package com.kooyeoung.hrbank.service;

import com.kooyeoung.hrbank.dto.command.employee.EmployeeCreateCommand;
import com.kooyeoung.hrbank.dto.command.employee.EmployeeUpdateCommand;
import com.kooyeoung.hrbank.dto.repository.employee.EmployeeSearchCondition;
import com.kooyeoung.hrbank.dto.repository.employee.EmployeeSummary;
import com.kooyeoung.hrbank.dto.request.employee.EmployeeCreateRequest;
import com.kooyeoung.hrbank.dto.request.employee.EmployeeSearchRequest;
import com.kooyeoung.hrbank.dto.request.employee.EmployeeUpdateRequest;
import com.kooyeoung.hrbank.dto.response.EmployeeDto;
import com.kooyeoung.hrbank.dto.response.PageResponse;
import com.kooyeoung.hrbank.entity.Department;
import com.kooyeoung.hrbank.entity.Employee;
import com.kooyeoung.hrbank.entity.FileInfo;
import com.kooyeoung.hrbank.entity.FileType;
import com.kooyeoung.hrbank.entity.snapshot.EmployeeSnapshot;
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

    @Transactional
    public EmployeeDto create(EmployeeCreateRequest request, MultipartFile file){
        validateEmailUniqueness(request.email());

        Department department = departmentReader.getDepartmentOrThrow(request.departmentId());
        String employeeNumber = numberGenerator.generate(request.hireDate());

        FileInfo profileFile = fileInfoService.save(file, FileType.PROFILE_IMAGE);

        EmployeeCreateCommand command = request.toCommand(department, profileFile);

        Employee employee = new Employee(command);
        employee.assignEmployeeNumber(employeeNumber);

        Employee savedEmployee = repository.save(employee);

        // TODO 히스토리 저장 로직
        // TODO 스냅샷 필요 필드 추가후 사용.
        // TODO 2차 수정 이벤트 관리 방식으로 변환 예정.
        EmployeeSnapshot snapshot = savedEmployee.snapshot();

        /**
         * 만약 파일 저장은 됐는데 직원 저장에서 예외가 나면 실제 파일이 디스크에 남을 수 있습니다.
         * 이것도 나중에 보상 처리나 afterCommit 이벤트로 정리
         */


        // dto 반환
        return EmployeeDto.from(snapshot);
    }

    private void validateEmailUniqueness(String email) {
        boolean emailExists = repository.existsByEmail(email);
        if(emailExists) throw new IllegalArgumentException("이미 존재하는 이메일 입니다.");
    }

    public EmployeeDto detail(Long id){

        Employee employee = repository.findDetailById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재 하지 않는 사원 입니다."));

        EmployeeSnapshot snapshot = employee.snapshot();

        return EmployeeDto.from(snapshot);
    }

    public PageResponse<EmployeeDto> list(EmployeeSearchRequest request){

        EmployeeSearchCondition condition = new EmployeeSearchCondition(
                request.nameOrEmail()
                ,request.employeeNumber()
                ,request.departmentName()
                ,request.position()
                ,request.hireDateFrom()
                ,request.hireDateTo()
                ,request.getStatusOrDefault()
                ,request.sortField()
                ,request.cursor()
                ,request.idAfter()
                ,request.hasCursor()
                ,request.isDesc()
                ,request.getSizeOrDefault()
        );

        int size = condition.size();
        List<EmployeeSummary> employeeSummaries = repository.searchEmployee(condition);

        boolean hasNext = employeeSummaries.size() >size;

        List<EmployeeSummary> pageContent = hasNext ? employeeSummaries.subList(0,size) : employeeSummaries;

        List<EmployeeDto> content = pageContent.stream()
                .map(EmployeeDto::from)
                .toList();

        String nextCursor = null;
        Long nextIdAfter = null;

        if(hasNext && !pageContent.isEmpty()){
            EmployeeSummary last = pageContent.get(pageContent.size() - 1);

            nextCursor = getNextCursor(condition.sortField(), last);
            nextIdAfter = last.id();
        }
        long totalCounts = repository.countEmployee(condition);

        return new PageResponse<>(
                content
                ,nextCursor
                ,nextIdAfter
                ,size
                ,totalCounts
                ,hasNext
        );

    }

    private String getNextCursor(String sortFiled, EmployeeSummary last) {
        if("hireDate".equals(sortFiled)){
            return last.hireDate().toString();
        }

        if("employeeNumber".equals(sortFiled)){
            return last.employeeNumber();
        }

        return last.name();
    }


    @Transactional
    public EmployeeDto update(Long id,EmployeeUpdateRequest request, MultipartFile profileImage){
        Employee foundEmployee = getEmployeeDetailOrThrow(id);

        if(foundEmployee.isEmailChanged(request.email())){
            validateEmailUniqueness(request.email());
        }

        Department department = departmentReader.getDepartmentOrThrow(request.departmentId());

        EmployeeSnapshot prevSnapshot = foundEmployee.snapshot();

        FileInfo beforeProfileInfo = foundEmployee.getProfileImage();
        FileInfo currentProfileInfo = fileInfoService.save(profileImage, FileType.PROFILE_IMAGE);

        FileInfo profileInfo = currentProfileInfo != null
                ? currentProfileInfo
                : beforeProfileInfo;

        EmployeeUpdateCommand command = request.toCommand(department, profileInfo);
        foundEmployee.updateInfo(command);
        foundEmployee.changeProfileImage(command.profileImage());

        if (currentProfileInfo != null && beforeProfileInfo != null){
            fileInfoService.delete(beforeProfileInfo.getId());
        }

        EmployeeSnapshot afterSnapshot = foundEmployee.snapshot();
        // TODO 히스토리 저장 로직
        // TODO 스냅샷 필요 필드 추가후 사용.
        // TODO 2차 수정 이벤트 관리 방식으로 변환 예정.

        /**
         * 만약 파일 저장은 됐는데 직원 저장에서 예외가 나면 실제 파일이 디스크에 남을 수 있습니다.
         * 이것도 나중에 보상 처리나 afterCommit 이벤트로 정리
         */


        return EmployeeDto.from(afterSnapshot);
    }

    @Transactional
    public void delete(Long id){
        Employee foundEmployee = getEmployeeDetailOrThrow(id);

        EmployeeSnapshot snapshot = foundEmployee.snapshot();

        if(foundEmployee.getProfileImage() != null){
            fileInfoService.delete(foundEmployee.getProfileImage().getId());
        }

        // TODO 히스토리 저장 로직
        // TODO 스냅샷 필요 필드 추가후 사용.
        // TODO 2차 수정 이벤트 관리 방식으로 변환 예정.

        repository.delete(foundEmployee);
    }

    @NonNull
    private Employee getEmployeeDetailOrThrow(Long id) {
        return repository.findDetailById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원입니다."));
    }

}
