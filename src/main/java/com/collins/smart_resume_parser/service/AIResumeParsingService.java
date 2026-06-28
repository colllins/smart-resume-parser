package com.collins.smart_resume_parser.service;

import com.collins.smart_resume_parser.dto.ParsedResumeDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AIResumeParsingService {

    private final ChatClient chatClient;
    private ObjectMapper objectMapper;

    public AIResumeParsingService(ChatClient.Builder chatClientBuilder, ObjectMapper objectMapper){
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public ParsedResumeDto parseResume(String text) throws JsonProcessingException {
     String jsonResponse =  chatClient
                .prompt()
                .system("""
        You are a resume parsing assistant.

        Extract candidate information from the resume text and return only valid JSON.

        Rules:
        - Return only JSON.
        - Do not include markdown or explanations.
        - Do not invent information.
        - Use null for missing single values.
        - Use an empty array for missing lists.
        - Never return placeholder objects containing only null or empty values.
        - If the resume has no certifications, return "certifications": [].
        - If the resume has no education, skills, or work experience, return an empty array for that field.
        - Preserve dates as written.
        - Keep work descriptions concise but complete.
        - Do not treat projects as work experience.
        - Do not treat coursework as skills unless it is explicitly listed under technical skills.

        Return JSON matching exactly:

        {
          "fullName": null,
          "email": null,
          "phone": null,
          "skills": [
            {
              "name": ""
            }
          ],
          "educations": [
            {
              "institution": null,
              "degree": null,
              "fieldOfStudy": null,
              "startDate": null,
              "endDate": null
            }
          ],
          "workExperiences": [
            {
              "company": null,
              "jobTitle": null,
              "location": null,
              "startDate": null,
              "endDate": null,
              "description": null
            }
          ],
          "certifications": [
            {
              "name": null,
              "issuer": null,
              "dateEarned": null
            }
          ]
        }
        """)
             .user(text)
             .call()
             .content();

        return objectMapper.readValue(
                jsonResponse,
                ParsedResumeDto.class
        );
    }
}
