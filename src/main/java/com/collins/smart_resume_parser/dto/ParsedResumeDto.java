package com.collins.smart_resume_parser.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ParsedResumeDto {
    private String fullName;
    private String email;
    private String phone;
    private List<SkillDto> skills;
    private List<EducationDto> educations;
    private List<WorkExperienceDto> workExperiences;
    private List<CertificationDto> certifications;
}
