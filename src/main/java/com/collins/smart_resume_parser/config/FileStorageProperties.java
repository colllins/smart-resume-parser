package com.collins.smart_resume_parser.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix="app.upload")
public record FileStorageProperties(List<String> allowedContentTypes,
                                    List<String> allowedExtensions) {
}
