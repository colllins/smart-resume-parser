package com.collins.smart_resume_parser.repository;

import com.collins.smart_resume_parser.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Long> {
}
