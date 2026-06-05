package com.kooyeoung.hrbank.service;

import com.kooyeoung.hrbank.dto.command.employee.EmployeeCreateCommand;
import com.kooyeoung.hrbank.dto.command.employee.EmployeeUpdateCommand;
import com.kooyeoung.hrbank.dto.request.employee.EmployeeCreateRequest;
import com.kooyeoung.hrbank.dto.request.employee.EmployeeUpdateRequest;
import com.kooyeoung.hrbank.dto.response.EmployeeDto;
import com.kooyeoung.hrbank.entity.Department;
import com.kooyeoung.hrbank.entity.Employee;
import com.kooyeoung.hrbank.entity.FileInfo;
import com.kooyeoung.hrbank.entity.snapshot.EmployeeSnapshot;
import com.kooyeoung.hrbank.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EmployeeService {
    private final EmployeeRepository repository;
    private final EmployeeNumberGenerator numberGenerator;
    private final DepartmentReader departmentReader;

    @Transactional
    public EmployeeDto create(EmployeeCreateRequest request, MultipartFile file){
        validateEmailUniqueness(request.email());

        Department department = departmentReader.getDepartmentOrThrow(request.departmentId());
        String employeeNumber = numberGenerator.generate(request.hireDate());

        FileInfo profileFile = null;
        if(file != null && !file.isEmpty()){
            // TODO 파일 저장 로직.
        }
        EmployeeCreateCommand command = request.toCommand(department, profileFile);

        Employee employee = new Employee(command);
        employee.assignEmployeeNumber(employeeNumber);

        Employee savedEmployee = repository.save(employee);

        // TODO 히스토리 저장 로직
        // TODO 스냅샷 필요 필드 추가후 사용.
        // TODO 2차 수정 이벤트 관리 방식으로 변환 예정.
        EmployeeSnapshot snapshot = savedEmployee.snapshot();

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

    public void list(){

    }

    @Transactional
    public EmployeeDto update(Long id,EmployeeUpdateRequest request, MultipartFile profileImage){
        Employee foundEmployee = repository.findDetailById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원입니다."));

        if(foundEmployee.isEmailChanged(request.email())){
            validateEmailUniqueness(request.email());
        }

        Department department = departmentReader.getDepartmentOrThrow(request.departmentId());

        EmployeeSnapshot prevSnapshot = foundEmployee.snapshot();
        FileInfo profileInfo = foundEmployee.getProfileImage();
        if(profileImage != null && !profileImage.isEmpty()){
            // TODO
            // 기존 이미지 삭제 해야함..?

            // 파일 저장.

            // 기존 등록되어 있던 파일 삭제 후 기존 db 정보 삭제?

            // 저장 성공시 db 정보 저장.
            profileInfo = null;
        }

        EmployeeUpdateCommand command = request.toCommand(department, profileInfo);
        foundEmployee.updateInfo(command);
        foundEmployee.changeProfileImage(command.profileImage());

        EmployeeSnapshot afterSnapshot = foundEmployee.snapshot();

        return EmployeeDto.from(afterSnapshot);
    }

    public void delete(){

    }

}
