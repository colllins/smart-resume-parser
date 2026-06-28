package com.collins.smart_resume_parser.dto;

public record EducationDto(String institution,
                           String degree,
                           String fieldOfStudy,
                           String startDate,
                           String endDate) {
}
