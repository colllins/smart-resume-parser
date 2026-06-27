package com.collins.smart_resume_parser.repository;

import com.collins.smart_resume_parser.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
}
