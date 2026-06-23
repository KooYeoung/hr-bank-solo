package com.kooyeoung.hrbank.exception.department;

import com.kooyeoung.hrbank.exception.CustomBadRequestException;

public class DepartmentNameAlreadyExistsException extends CustomBadRequestException {
    public DepartmentNameAlreadyExistsException(String name) {
        super("이미 존재하는 부서명칭입니다. name=" + name);
    }
}
