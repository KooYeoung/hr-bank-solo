package com.kooyeoung.hrbank.service;

import com.kooyeoung.hrbank.entity.Department;
import com.kooyeoung.hrbank.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DepartmentReader {

    private final DepartmentRepository repository;

    @NonNull
    public Department getDepartmentOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부서 입니다."));
    }


}
