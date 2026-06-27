package com.collins.smart_resume_parser.repository;

import com.collins.smart_resume_parser.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificationRepository extends JpaRepository<Certification, Long> {
}
