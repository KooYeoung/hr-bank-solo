package com.kooyeoung.hrbank.dto.response;

import org.springframework.core.io.Resource;

public record FileDownloadResponse(
        Resource resource,
        String originalFileName,
        String contentType,
        Long size
) {
}

