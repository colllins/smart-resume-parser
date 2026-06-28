package com.collins.smart_resume_parser.repository;

import com.collins.smart_resume_parser.entity.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Long> {
}
