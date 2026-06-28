package com.collins.smart_resume_parser.service;

import com.collins.smart_resume_parser.config.FileStorageProperties;
import com.collins.smart_resume_parser.dto.ParsedResumeDto;
import com.collins.smart_resume_parser.entity.ProcessingStatus;
import com.collins.smart_resume_parser.entity.Resume;
import com.collins.smart_resume_parser.repository.ResumeRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final S3StorageService s3StorageService;
    private final TextractService textractService;
    private final AIResumeParsingService aiResumeParsingService;
    private final ResumeMapperService resumeMapperService;

    private final List<String> allowedContentTypes;
    private final List<String> allowedExtensions;

    public ResumeService(ResumeRepository resumeRepository, S3StorageService s3StorageService, TextractService textractService, AIResumeParsingService aiResumeParsingService, ResumeMapperService resumeMapperService, FileStorageProperties properties){
        this.resumeRepository = resumeRepository;
        this.s3StorageService = s3StorageService;
        this.textractService = textractService;
        this.aiResumeParsingService = aiResumeParsingService;
        this.resumeMapperService = resumeMapperService;
        this.allowedContentTypes = properties.allowedContentTypes() == null ? List.of() : properties.allowedContentTypes();
        this.allowedExtensions = properties.allowedExtensions() == null ? List.of() : properties.allowedExtensions();
    }


    public Resume parseAndSaveResume(MultipartFile file){
        validateFile(file);
        Resume resume = new Resume();
        String s3Object =null;
        try{
            s3Object = s3StorageService.uploadFile(file);
            String fileName = file.getOriginalFilename();
            String fileType = file.getContentType();

           resume.setOriginalFileName(fileName);
           resume.setFileType(fileType);
           resume.setS3ObjectKey(s3Object);
           resume.setProcessingStatus(ProcessingStatus.UPLOADED);
           resume.setUploadedAt(LocalDateTime.now());

           resumeRepository.save(resume);

            resume.setProcessingStatus(ProcessingStatus.PROCESSING);
            resumeRepository.save(resume);

           String extractedText = textractService.extractText(s3Object);

           resume.setRawText(extractedText);

          ParsedResumeDto parsedText = aiResumeParsingService.parseResume(extractedText);

          resumeMapperService.mapToResume(resume, parsedText);
          resume.setProcessingStatus(ProcessingStatus.COMPLETED);
            return resumeRepository.save(resume);

        }catch (Exception e){
            e.printStackTrace();
            resume.setProcessingStatus(ProcessingStatus.FAILED);
            resumeRepository.save(resume);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage(),
                    e
            );
        }
    }

    private void validateFile(MultipartFile file) {
        if(file==null || file.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "EMPTY_FILE, Uploaded file must not be empty");
        }

        String fileName = file.getOriginalFilename();
        if(!StringUtils.hasText(fileName)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "MISSING_FILE_NMAE, Uploaded file is missing name");
        }

        String cleanedFileName = StringUtils.cleanPath(fileName);

        if(cleanedFileName.contains("..")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_FILE_NAME, File name contains invalid path sequence");
        }

        String contentType = file.getContentType();
        if(!StringUtils.hasText(contentType)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "MISSING_CONTENT_TYPE, File content type is missing");
        }

        if ("application/pdf".equalsIgnoreCase(contentType)) {
            validatePdfPageCount(file);
        }

        if(!allowedContentTypes.isEmpty() && !allowedContentTypes.contains(contentType)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UNSUPPORTED_FILE_TYPE, Unsupported file type: "+contentType);
        }

        String extension = getExtension(cleanedFileName);
        if(!allowedExtensions.isEmpty() && !allowedExtensions.contains(extension)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UNSUPPORTED_FILE_EXTENSION, Unsupported file extension: "+extension);
        }
    }

    private String getExtension (String fileName){
        int lastIndex = fileName.lastIndexOf('.');
        if(lastIndex<0){
            return "";
        }
        return fileName.substring(lastIndex).toLowerCase();
    }

    private void validatePdfPageCount(MultipartFile file) {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {

            if (document.getNumberOfPages() > 1) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "MULTI_PAGE_RESUME_NOT_SUPPORTED, Resume must contain only one page"
                );
            }

        } catch (ResponseStatusException exception) {
            throw exception;

        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_PDF, Unable to read the uploaded PDF",
                    exception
            );
        }
    }
}
