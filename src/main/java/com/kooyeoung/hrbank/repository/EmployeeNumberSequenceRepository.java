package com.kooyeoung.hrbank.repository;

import com.kooyeoung.hrbank.entity.EmployeeNumberSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeNumberSequenceRepository extends JpaRepository<EmployeeNumberSequence, Long> {

    /**
     * 사원번호 채번 시 동시성 문제를 방지하기 위해
     * 해당 년월(yyyyMM)의 채번 행을 배타적으로 잠근 상태로 조회합니다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ENS from EmployeeNumberSequence ENS where ENS.yyyyMM = :yyyyMM")
    Optional<EmployeeNumberSequence> findByYyyyMMForUpdate(@Param("yyyyMM") String yyyyMM);
}
