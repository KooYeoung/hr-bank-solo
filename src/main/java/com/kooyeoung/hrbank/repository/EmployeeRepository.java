package com.kooyeoung.hrbank.repository;

import com.kooyeoung.hrbank.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeRepositoryCustom {

    boolean existsByDepartment_Id(Long departmentId);
    Long countByDepartment_Id(Long departmentId);

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
}
