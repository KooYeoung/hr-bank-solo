package com.kooyeoung.hrbank.repository;

import com.kooyeoung.hrbank.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long>, DepartmentRepositoryCustom {

    boolean existsByName(String name);

    Page<Department> findByNameOrDescription(String name, String description, Pageable pageable);




}
