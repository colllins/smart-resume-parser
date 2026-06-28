package com.collins.smart_resume_parser;

import com.collins.smart_resume_parser.config.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(FileStorageProperties.class)
@SpringBootApplication
public class SmartResumeParserApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartResumeParserApplication.class, args);
	}

}
