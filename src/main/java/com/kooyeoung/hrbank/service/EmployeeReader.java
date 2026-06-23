package com.kooyeoung.hrbank.service;

import com.kooyeoung.hrbank.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EmployeeReader {

    private final EmployeeRepository repository;

    public Long countByDepartmentId(Long id){
        return repository.countByDepartment_Id(id);
    }

    public boolean existsByDepartmentId(Long id){
        return repository.existsByDepartment_Id(id);
    }
}
