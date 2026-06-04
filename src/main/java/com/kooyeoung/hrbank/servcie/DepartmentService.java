package com.kooyeoung.hrbank.servcie;

import com.kooyeoung.hrbank.dto.repository.DepartmentSearchCondition;
import com.kooyeoung.hrbank.dto.request.DepartmentCreateRequest;
import com.kooyeoung.hrbank.dto.request.DepartmentSearchRequest;
import com.kooyeoung.hrbank.dto.request.DepartmentUpdateRequest;
import com.kooyeoung.hrbank.dto.response.DepartmentDto;
import com.kooyeoung.hrbank.entity.Department;
import com.kooyeoung.hrbank.repository.DepartmentRepository;
import com.kooyeoung.hrbank.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public DepartmentDto save(DepartmentCreateRequest request){
        existByNameThrow(request.name());

        Department department = request.toDepartment();

        Department savedDepartment = departmentRepository.save(department);

        return DepartmentDto.from(savedDepartment, 0L);
    }

    @Transactional
    public DepartmentDto update(Long id, DepartmentUpdateRequest request){
        Department department = getDepartmentOrThrow(id);

        if(department.isNameChanged(request.name())) existByNameThrow(request.name());

        department.updateInfo(
                request.name()
                ,request.description()
                ,LocalDate.parse(request.establishedDate())
        );

        Long employeeCount = employeeRepository.countByDepartment_Id(id);

        return  DepartmentDto.from(department, employeeCount);
    }

    public DepartmentDto findById(Long id){
        Department department = getDepartmentOrThrow(id);

        Long employeeCount = employeeRepository.countByDepartment_Id(id);

        return DepartmentDto.from(department, employeeCount);
    }

    @Transactional
    public void delete(Long id){
        Department department = getDepartmentOrThrow(id);

        if(employeeRepository.existsByDepartment_Id(id)) throw new IllegalArgumentException("소속된 직원이 없는 경우에만 부서를 삭제할 수 있습니다.");

        departmentRepository.delete(department);
    }

    public List<DepartmentDto> list(DepartmentSearchRequest request){

        // dto 생명주기 분리 하기 위해 사용.
        DepartmentSearchCondition condition = new DepartmentSearchCondition(
                request.nameOrDescription()
                , request.getSortFiledOrDefault()
                , request.cursor()
                , request.idAfter()
                , request.hasCursor()
                , request.isDesc()
                , request.getSizeOrDefault()
        );

        return departmentRepository.searchDepartment(condition)
                .stream()
                .map(DepartmentDto::from)
                .toList();
    }

    private void existByNameThrow(String name){
        if(departmentRepository.existsByName(name)) throw new IllegalArgumentException("이미 존재하는 부서명칭 입니다.");
    }

    @NonNull
    private Department getDepartmentOrThrow(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부서 입니다."));
    }

}
