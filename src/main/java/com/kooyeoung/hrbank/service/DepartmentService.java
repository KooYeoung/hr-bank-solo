package com.kooyeoung.hrbank.service;

import com.kooyeoung.hrbank.dto.command.department.DepartmentCreateCommand;
import com.kooyeoung.hrbank.dto.command.department.DepartmentUpdateCommand;
import com.kooyeoung.hrbank.dto.repository.department.DepartmentSearchCondition;
import com.kooyeoung.hrbank.dto.repository.department.DepartmentSummary;
import com.kooyeoung.hrbank.dto.response.DepartmentDto;
import com.kooyeoung.hrbank.dto.response.PageResponse;
import com.kooyeoung.hrbank.entity.Department;
import com.kooyeoung.hrbank.exception.department.DepartmentHasEmployeesException;
import com.kooyeoung.hrbank.exception.department.DepartmentNameAlreadyExistsException;
import com.kooyeoung.hrbank.exception.department.DepartmentNotFoundException;
import com.kooyeoung.hrbank.repository.DepartmentRepository;
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
    private final EmployeeReader employeeReader;

    @Transactional
    public DepartmentDto save(DepartmentCreateCommand command) {
        existByNameThrow(command.name());

        Department department = new Department(command);

        Department savedDepartment = departmentRepository.save(department);

        return DepartmentDto.from(savedDepartment, 0L);
    }

    @Transactional
    public DepartmentDto update(Long id, DepartmentUpdateCommand command) {
        Department department = getDepartmentById(id);

        if (department.isNameChanged(command.name())) existByNameThrow(command.name());

        department.updateInfo(command);

        Long employeeCount = employeeReader.countByDepartmentId(id);

        return DepartmentDto.from(department, employeeCount);
    }

    @NonNull
    private Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));
    }

    public DepartmentDto findById(Long id) {
        DepartmentSummary summaryResult = departmentRepository.findSummaryById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));

        return DepartmentDto.from(summaryResult);
    }

    @Transactional
    public void delete(Long id) {
        Department department = getDepartmentById(id);

        if (employeeReader.existsByDepartmentId(id))
            throw new DepartmentHasEmployeesException(id);

        departmentRepository.delete(department);
    }

    public PageResponse<DepartmentDto> list(DepartmentSearchCondition condition) {

        int size = condition.size();
        List<DepartmentSummary> departmentList = departmentRepository.searchDepartment(condition);

        boolean hasNext = departmentList.size() > size;

        List<DepartmentSummary> pageContent = hasNext ? departmentList.subList(0, size) : departmentList;

        List<DepartmentDto> content = pageContent.stream()
                .map(DepartmentDto::from)
                .toList();

        String nextCursor = null;
        Long nextIdAfter = null;

        if (hasNext && !pageContent.isEmpty()) {
            DepartmentSummary last = pageContent.get(pageContent.size() - 1);

            nextCursor = getNextCursor(condition.sortField(), last);
            nextIdAfter = last.id();

        }

        long totalDepartmentCount = departmentRepository.countDepartment(condition);

        return new PageResponse<>(
                content,
                nextCursor,
                nextIdAfter,
                size,
                totalDepartmentCount,
                hasNext
        );
    }

    private String getNextCursor(String sortFiled, DepartmentSummary last) {
        if ("establishedDate".equals(sortFiled)) {
            return last.establishedDate().toString();
        }
        return last.name();
    }

    private void existByNameThrow(String name) {
        if (departmentRepository.existsByName(name))
            throw new DepartmentNameAlreadyExistsException(name);
    }

}
