package com.collins.smart_resume_parser.controller;

import com.collins.smart_resume_parser.entity.Resume;
import com.collins.smart_resume_parser.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/parse")
    public Resume parseResume(@RequestParam("file") MultipartFile file){
        return resumeService.parseAndSaveResume(file);
    }
}
