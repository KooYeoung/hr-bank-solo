package com.kooyeoung.hrbank.repository;

import com.kooyeoung.hrbank.dto.response.ChangeLogDetailRowDto;
import com.kooyeoung.hrbank.entity.EmployeeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmployeeHistoryRepository extends JpaRepository<EmployeeHistory, Long>, EmployeeHistoryRepositoryCustom {

    @Query("""
                select new com.kooyeoung.hrbank.dto.response.ChangeLogDetailRowDto(
                    eh.id,
                    eh.type,
                    eh.employeeNumber,
                    eh.memo,
                    eh.ipAddress,
                    eh.createdAt,
                    e.name,
                    fi.id,
                    d.properties,
                    d.beforeValue,
                    d.afterValue
                )
                from EmployeeHistory eh
                left join Employee e on e.employeeNumber = eh.employeeNumber
                left join e.profileImage fi
                left join eh.details d
                where eh.id = :id
            """)
    List<ChangeLogDetailRowDto> findDetailRowsById(@Param("id") Long id);

    Optional<Long> countByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    Optional<EmployeeHistory> findTopByOrderByCreatedAtDesc();
}
