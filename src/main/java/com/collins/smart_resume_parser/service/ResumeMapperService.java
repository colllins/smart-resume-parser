package com.collins.smart_resume_parser.service;

import com.collins.smart_resume_parser.dto.ParsedResumeDto;
import com.collins.smart_resume_parser.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeMapperService {

    public void mapToResume(Resume resume, ParsedResumeDto parsedResumeDto){
        resume.setFullName(parsedResumeDto.getFullName());
        resume.setEmail(parsedResumeDto.getEmail());
        resume.setPhone(parsedResumeDto.getPhone());
        resume.setProcessedAt(LocalDateTime.now());

        List<Skill> skills = parsedResumeDto
                .getSkills()
                        .stream()
                                .map(skillDto -> {
                                    Skill skill = new Skill();
                                    skill.setName(skillDto.name());
                                    skill.setResume(resume);
                                    return skill;
                                }).collect(Collectors.toCollection(ArrayList::new));
        resume.setSkills(skills);

        List<Education> educations = parsedResumeDto
                .getEducations()
                        .stream()
                                .map(educationDto -> {
                                    Education education = new Education();
                                    education.setResume(resume);
                                    education.setDegree(educationDto.degree());
                                    education.setInstitution(educationDto.institution());
                                    education.setFieldOfStudy(educationDto.fieldOfStudy());
                                    education.setStartDate(educationDto.startDate());
                                    education.setEndDate(educationDto.endDate());
                                    return education;
                                }).collect(Collectors.toCollection(ArrayList::new));
        resume.setEducations(educations);

        List<WorkExperience> workExperiences = parsedResumeDto
                .getWorkExperiences()
                .stream()
                .map(workExperienceDto -> {
                    WorkExperience workExperience = new WorkExperience();
                    workExperience.setResume(resume);
                    workExperience.setCompany(workExperienceDto.company());
                    workExperience.setJobTitle(workExperienceDto.jobTitle());
                    workExperience.setLocation(workExperienceDto.location());
                    workExperience.setDescription(workExperienceDto.description());
                    workExperience.setStartDate(workExperienceDto.startDate());
                    workExperience.setEndDate(workExperienceDto.endDate());
                    return workExperience;
                }).collect(Collectors.toCollection(ArrayList::new));
        resume.setWorkExperiences(workExperiences);

        List<Certification> certifications = parsedResumeDto
                .getCertifications()
                .stream()
                .map(certificationDto -> {
                    Certification certification = new Certification();
                    certification.setResume(resume);
                    certification.setName(certificationDto.name());
                    certification.setIssuer(certificationDto.issuer());
                    certification.setDateEarned(certificationDto.dateEarned());
                    return certification;
                }).collect(Collectors.toCollection(ArrayList::new));
        resume.setCertifications(certifications);
    }
}
