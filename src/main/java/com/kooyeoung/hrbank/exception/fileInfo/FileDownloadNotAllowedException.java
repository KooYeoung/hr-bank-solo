package com.kooyeoung.hrbank.exception.fileInfo;

import com.kooyeoung.hrbank.exception.CustomBadRequestException;

public class FileDownloadNotAllowedException extends CustomBadRequestException {
    public FileDownloadNotAllowedException() {
        super("이미지 파일의 다운로드는 지원하지 않습니다.");
    }
}
