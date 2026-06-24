package com.kooyeoung.hrbank.service;

import com.kooyeoung.hrbank.dto.command.employee.EmployeeCreateCommand;
import com.kooyeoung.hrbank.dto.command.employee.EmployeeUpdateCommand;
import com.kooyeoung.hrbank.dto.command.history.EmployeeHistoryCreateCommand;
import com.kooyeoung.hrbank.dto.repository.employee.EmployeeSearchCondition;
import com.kooyeoung.hrbank.dto.repository.employee.EmployeeSummary;
import com.kooyeoung.hrbank.dto.repository.employee.EmployeesCountCondition;
import com.kooyeoung.hrbank.dto.response.EmployeeDto;
import com.kooyeoung.hrbank.dto.response.PageResponse;
import com.kooyeoung.hrbank.entity.Department;
import com.kooyeoung.hrbank.entity.Employee;
import com.kooyeoung.hrbank.entity.FileInfo;
import com.kooyeoung.hrbank.entity.FileType;
import com.kooyeoung.hrbank.entity.snapshot.EmployeeSnapshot;
import com.kooyeoung.hrbank.exception.employee.EmployeeEmailAlreadyExistsException;
import com.kooyeoung.hrbank.exception.employee.EmployeeNotFoundException;
import com.kooyeoung.hrbank.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EmployeeService {
    private final EmployeeRepository repository;
    private final EmployeeNumberGenerator numberGenerator;
    private final DepartmentReader departmentReader;
    private final FileInfoService fileInfoService;
    private final EmployeeHistoryService employeeHistoryService;

    @Transactional
    public EmployeeDto create(EmployeeCreateCommand command, Long departmentId, MultipartFile file, String memo) {
        validateEmailUniqueness(command.email());

        Department department = departmentReader.getDepartmentOrThrow(departmentId);
        String employeeNumber = numberGenerator.generate(command.hireDate());

        FileInfo profileFile = fileInfoService.save(file, FileType.PROFILE_IMAGE);

        Employee employee = new Employee(command, department, profileFile);
        employee.assignEmployeeNumber(employeeNumber);

        Employee savedEmployee = repository.save(employee);
        EmployeeSnapshot snapshot = savedEmployee.snapshot();

        employeeHistoryService.save(EmployeeHistoryCreateCommand.create(
                        null,
                        snapshot,
                        memo
                )
        );

        return EmployeeDto.from(snapshot);
    }

    private void validateEmailUniqueness(String email) {
        boolean emailExists = repository.existsByEmail(email);
        if (emailExists) throw new EmployeeEmailAlreadyExistsException(email);
    }

    public EmployeeDto detail(Long id) {

        Employee employee = getEmployeeDetailOrThrow(id);

        EmployeeSnapshot snapshot = employee.snapshot();

        return EmployeeDto.from(snapshot);
    }

    public PageResponse<EmployeeDto> list(EmployeeSearchCondition condition) {


        int size = condition.size();
        List<EmployeeSummary> employeeSummaries = repository.searchEmployee(condition);

        boolean hasNext = employeeSummaries.size() > size;

        List<EmployeeSummary> pageContent = hasNext ? employeeSummaries.subList(0, size) : employeeSummaries;

        List<EmployeeDto> content = pageContent.stream()
                .map(EmployeeDto::from)
                .toList();

        String nextCursor = null;
        Long nextIdAfter = null;

        if (hasNext && !pageContent.isEmpty()) {
            EmployeeSummary last = pageContent.get(pageContent.size() - 1);

            nextCursor = getNextCursor(condition.sortField(), last);
            nextIdAfter = last.id();
        }
        long totalCounts = repository.countEmployee(condition);

        return new PageResponse<>(
                content,
                nextCursor,
                nextIdAfter,
                size,
                totalCounts,
                hasNext
        );

    }

    private String getNextCursor(String sortFiled, EmployeeSummary last) {
        if ("hireDate".equals(sortFiled)) {
            return last.hireDate().toString();
        }

        if ("employeeNumber".equals(sortFiled)) {
            return last.employeeNumber();
        }

        return last.name();
    }


    @Transactional
    public EmployeeDto update(Long employeeId, EmployeeUpdateCommand command, Long departmentId, MultipartFile profileImage, String memo) {
        Employee foundEmployee = getEmployeeDetailOrThrow(employeeId);

        if (foundEmployee.isEmailChanged(command.email())) {
            validateEmailUniqueness(command.email());
        }

        Department department = departmentReader.getDepartmentOrThrow(departmentId);

        EmployeeSnapshot prevSnapshot = foundEmployee.snapshot();

        FileInfo beforeProfileInfo = foundEmployee.getProfileImage();
        FileInfo currentProfileInfo = fileInfoService.save(profileImage, FileType.PROFILE_IMAGE);

        FileInfo profileInfo = currentProfileInfo != null
                ? currentProfileInfo
                : beforeProfileInfo;

        foundEmployee.updateInfo(command, department);
        foundEmployee.changeProfileImage(profileInfo);

        if (currentProfileInfo != null && beforeProfileInfo != null) {
            fileInfoService.delete(beforeProfileInfo.getId());
        }

        EmployeeSnapshot afterSnapshot = foundEmployee.snapshot();

        employeeHistoryService.save(EmployeeHistoryCreateCommand.create(
                prevSnapshot, afterSnapshot, memo
        ));

        return EmployeeDto.from(afterSnapshot);
    }

    @Transactional
    public void delete(Long id) {
        Employee foundEmployee = getEmployeeDetailOrThrow(id);

        EmployeeSnapshot snapshot = foundEmployee.snapshot();

        if (foundEmployee.getProfileImage() != null) {
            fileInfoService.delete(foundEmployee.getProfileImage().getId());
        }

        employeeHistoryService.save(EmployeeHistoryCreateCommand.create(
                snapshot, null, ""
        ));

        repository.delete(foundEmployee);
    }

    public Long employeesCount(EmployeesCountCondition condition) {

        return repository.countEmployee(condition);
    }

    @NonNull
    private Employee getEmployeeDetailOrThrow(Long id) {
        return repository.findDetailById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

}
