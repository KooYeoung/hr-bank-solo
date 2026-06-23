package com.kooyeoung.hrbank.repository;

import com.kooyeoung.hrbank.dto.response.EmployeeDistributionCount;
import com.kooyeoung.hrbank.entity.Employee;
import com.kooyeoung.hrbank.entity.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeRepositoryCustom {

    boolean existsByDepartmentId(Long departmentId);

    Long countByDepartmentId(Long departmentId);

    /**
     * 직원 상세 조회 시 부서와 프로필 이미지를 함께 조회하여
     * 지연 로딩으로 인한 추가 쿼리 발생을 방지합니다.
     */
    @Query("""
            select e
            from Employee e
            join fetch e.department
            left join fetch e.profileImage
            where e.id = :id
            """)
    Optional<Employee> findDetailById(@Param("id") Long id);

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = {"department"})
    Page<Employee> findAllBy(Pageable pageable);

    @Query("""
                select e.hireDate
                from Employee e
                where e.hireDate < :toExclusive
                  and e.status <> :resignedStatus
                order by e.hireDate asc
            """)
    List<LocalDate> findActiveHireDatesBefore(
            LocalDate toExclusive,
            EmployeeStatus resignedStatus
    );

    @Query("""
                select new com.kooyeoung.hrbank.dto.response.EmployeeDistributionCount(d.name , count(d.name) )
                from Employee e
                join e.department d
                where e.status = :status
                group by d.name
                order by count(d.name)  desc
            """)
    List<EmployeeDistributionCount> countGroupByDepartment(@Param("status") EmployeeStatus status);

    @Query("""
                select new com.kooyeoung.hrbank.dto.response.EmployeeDistributionCount( e.position , count(e.position) )
                from Employee e
                where e.status = :status
                group by e.position
                order by count(e.position) desc
            """)
    List<EmployeeDistributionCount> countGroupByPosition(@Param("status") EmployeeStatus status);

}
