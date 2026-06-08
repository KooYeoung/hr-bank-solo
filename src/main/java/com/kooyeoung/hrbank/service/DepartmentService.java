package com.kooyeoung.hrbank.service;

import com.kooyeoung.hrbank.dto.repository.department.DepartmentSearchCondition;
import com.kooyeoung.hrbank.dto.repository.department.DepartmentSummary;
import com.kooyeoung.hrbank.dto.request.department.DepartmentCreateRequest;
import com.kooyeoung.hrbank.dto.request.department.DepartmentSearchRequest;
import com.kooyeoung.hrbank.dto.request.department.DepartmentUpdateRequest;
import com.kooyeoung.hrbank.dto.response.DepartmentDto;
import com.kooyeoung.hrbank.dto.response.PageResponse;
import com.kooyeoung.hrbank.entity.Department;
import com.kooyeoung.hrbank.repository.DepartmentRepository;
import com.kooyeoung.hrbank.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final DepartmentReader departmentReader;
    private final EmployeeReader employeeReader;

    @Transactional
    public DepartmentDto save(DepartmentCreateRequest request){
        existByNameThrow(request.name());

        Department department = request.toDepartment();

        Department savedDepartment = departmentRepository.save(department);

        return DepartmentDto.from(savedDepartment, 0L);
    }

    @Transactional
    public DepartmentDto update(Long id, DepartmentUpdateRequest request){
        Department department = departmentReader.getDepartmentOrThrow(id);

        if(department.isNameChanged(request.name())) existByNameThrow(request.name());

        department.updateInfo(
                request.name()
                ,request.description()
                ,request.establishedDate()
        );

        Long employeeCount = employeeReader.countByDepartment_Id(id);

        return  DepartmentDto.from(department, employeeCount);
    }

    public DepartmentDto findById(Long id){
        DepartmentSummary summaryResult = departmentRepository.findSummaryById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부서 입니다."));

        return DepartmentDto.from(summaryResult);
    }

    @Transactional
    public void delete(Long id){
        Department department = departmentReader.getDepartmentOrThrow(id);

        if(employeeReader.existsByDepartment_Id(id)) throw new IllegalArgumentException("소속된 직원이 없는 경우에만 부서를 삭제할 수 있습니다.");

        departmentRepository.delete(department);
    }

    public PageResponse<DepartmentDto> list(DepartmentSearchRequest request){

        // dto 생명주기 분리 하기 위해 사용.
        DepartmentSearchCondition condition = new DepartmentSearchCondition(
                request.nameOrDescription()
                , request.getSortFieldOrDefault()
                , request.cursor()
                , request.idAfter()
                , request.hasCursor()
                , request.isDesc()
                , request.getSizeOrDefault()
        );

        int size = condition.size();
        List<DepartmentSummary> departmentList = departmentRepository.searchDepartment(condition);

        boolean hasNext = departmentList.size() > size;

        List<DepartmentSummary> pageContent = hasNext ? departmentList.subList(0, size) : departmentList;

        List<DepartmentDto> content = pageContent.stream()
                .map(DepartmentDto::from)
                .toList();

        String nextCursor = null;
        Long nextIdAfter = null;

        if(hasNext && !pageContent.isEmpty()){
            DepartmentSummary last = pageContent.get(pageContent.size()-1);

            nextCursor = getNextCursor(condition.sortField(), last);
            nextIdAfter = last.id();

        }

        long totalDepartmentCount = departmentRepository.countDepartment(condition);

        return new PageResponse<>(
                content
                , nextCursor
                ,nextIdAfter
                ,size
                ,totalDepartmentCount
                ,hasNext
        );
    }

    private String getNextCursor(String sortFiled, DepartmentSummary last) {
        if("establishedDate".equals(sortFiled)){
            return last.establishedDate().toString();
        }
        return last.name();
    }

    private void existByNameThrow(String name){
        if(departmentRepository.existsByName(name)) throw new IllegalArgumentException("이미 존재하는 부서명칭 입니다.");
    }

}
