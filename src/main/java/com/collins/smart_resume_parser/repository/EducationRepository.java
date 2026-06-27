package com.collins.smart_resume_parser.repository;

import com.collins.smart_resume_parser.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EducationRepository extends JpaRepository<Education, Long> {
}
