package com.collins.smart_resume_parser.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3StorageService {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

   private final S3Client s3Client;

    public String uploadFile(MultipartFile file) throws IOException {

        String fileName = file.getOriginalFilename();

        String objectKey = "resumes/"+ UUID.randomUUID()+"-"+fileName;


        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(file.getContentType())
                .build();

         s3Client.putObject(
                 request,
                 RequestBody.fromInputStream(
                         file.getInputStream(),
                         file.getSize()));

         return objectKey;
    }

    public void deleteFile(String objectKey){
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        s3Client.deleteObject(request);
    }
}
