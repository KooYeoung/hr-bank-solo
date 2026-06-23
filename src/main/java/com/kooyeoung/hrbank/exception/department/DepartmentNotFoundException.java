package com.kooyeoung.hrbank.exception.department;

import com.kooyeoung.hrbank.exception.CustomNotFoundException;

public class DepartmentNotFoundException extends CustomNotFoundException {
    public DepartmentNotFoundException(Long departmentId) {
        super("존재하지 않는 부서 입니다. departmentId = " + departmentId);
    }
}
