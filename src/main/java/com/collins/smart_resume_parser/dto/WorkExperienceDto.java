package com.collins.smart_resume_parser.dto;

public record WorkExperienceDto(String company,
                                String jobTitle,
                                String location,
                                String startDate,
                                String endDate,
                                String description) {
}
