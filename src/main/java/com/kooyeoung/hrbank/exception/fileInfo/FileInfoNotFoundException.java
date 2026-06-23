package com.kooyeoung.hrbank.exception.fileInfo;

import com.kooyeoung.hrbank.exception.CustomNotFoundException;

public class FileInfoNotFoundException extends CustomNotFoundException {
    public FileInfoNotFoundException(Long fileId) {
        super("파일 정보를 찾을 수 없습니다. fileId=" + fileId);
    }
}
