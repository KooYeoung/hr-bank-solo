package com.kooyeoung.hrbank.exception.department;

import com.kooyeoung.hrbank.exception.CustomBadRequestException;

public class DepartmentHasEmployeesException extends CustomBadRequestException {
    public DepartmentHasEmployeesException(Long departmentId) {
        super("소속된 직원이 없는 경우에만 부서를 삭제할 수 있습니다. departmentId = " + departmentId);
    }
}
