package com.kooyeoung.hrbank.exception.fileInfo;

import com.kooyeoung.hrbank.exception.CustomBadRequestException;

public class FileDeleteNotAllowedException extends CustomBadRequestException {
    public FileDeleteNotAllowedException() {
        super("프로필 이미지 파일만 삭제할 수 있습니다.");
    }
}
