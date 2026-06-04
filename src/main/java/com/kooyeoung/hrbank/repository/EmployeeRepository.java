package com.kooyeoung.hrbank.repository;

import com.kooyeoung.hrbank.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByDepartment_Id(Long departmentId);
    Long countByDepartment_Id(Long departmentId);
}
