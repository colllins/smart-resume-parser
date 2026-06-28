package com.collins.smart_resume_parser.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TextractService {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private final TextractClient textractClient;

    public String extractText(String objectKey){

        //creates an s3object which is basically an address pointing textract to the file
        S3Object s3Object = S3Object.builder()
                .bucket(bucketName)
                .name(objectKey)
                .build();

        //textract expects a Document object as input which can contain file bytes directly or s3 reference. using s3 reference in this case
        Document document = Document.builder()
                .s3Object(s3Object)
                .build();

        /*creates the actual request to send to textract so it can run text detection on the document
        the document field contains the s3 location created previously
         */
        DetectDocumentTextRequest request = DetectDocumentTextRequest.builder()
                .document(document)
                .build();

        /*request is sent to aws textract
        textract reads the file from s3
        analyzes doc
        aws returns result as a DetectDocumentResponse
         */
        DetectDocumentTextResponse response = textractClient.detectDocumentText(request);

        //converts textract response into one normal string
        return response.blocks()                                                            //gets every detected block from textract
                .stream()                                                                   //lets you process blocks one by one
                .filter(block -> block.blockType()== BlockType.LINE)                  //keeps only full text lines and ignores blocks suck as pages and individual words
                .map(Block::text)                                                           //takes actual text from each line block
                .collect(Collectors.joining("\n"));                                 //joins all lines with a new line between them

    }
}
