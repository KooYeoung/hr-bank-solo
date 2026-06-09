package com.kooyeoung.hrbank.repository;

import com.kooyeoung.hrbank.entity.EmployeeHistoryDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeHistoryDetailRepository extends JpaRepository<EmployeeHistoryDetail, Long> {
}
