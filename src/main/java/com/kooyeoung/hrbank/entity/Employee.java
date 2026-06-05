package com.kooyeoung.hrbank.entity;

import com.kooyeoung.hrbank.dto.command.employee.EmployeeCreateCommand;
import com.kooyeoung.hrbank.dto.command.employee.EmployeeUpdateCommand;
import com.kooyeoung.hrbank.entity.snapshot.EmployeeSnapshot;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/*
### 원 정보 관리


**직원 등록**

- **{이름}**, **{이메일}**, **{부서}**, **{직함}**, **{입사일}**, **{프로필 이미지}**를 통해 직원을 등록할 수 있습니다.
    - **{이메일}**은 다른 직원과 중복되면 안됩니다.
    - **{프로필 이미지}**는 선택적으로 등록할 수 있습니다.
    - **{프로필 이미지}**는 이어지는 **파일 관리 요구사항**에 따라 등록합니다.
    - **{사원 번호}**는 자동으로 부여되어야 합니다. 규칙은 자유롭게 정의하세요.
    - **{상태}**는 자동으로 `재직중`상태로 초기화합니다.

**직원 정보 수정**

- **{사원 번호}**를 제외한 다른 속성은 모두 수정할 수 있습니다.
- **{이메일}**은 다른 직원과 중복되면 안됩니다.

**직원 정보 삭제**

- 직원을 삭제하면 프로필 이미지도 같이 삭제되어야합니다.
- `퇴사`는 삭제가 아닌 수정으로 처리해야합니다.

-> 생성, 수정, 삭제 할 경우 히스토리에 남아야함. 이런경우 이벤트 발행으로 구성?

**직원 정보 목록 조회**

- **{이름 또는 이메일}**, **{부서}**, **{직함}**, **{사원번호}**, **{입사일}**, **{상태}**로 직원 목록을 조회할 수 있습니다.
    - **{이름 또는 이메일}**, **{부서}**, **{직함}**, **{사원번호}**는 부분 일치 조건입니다.
    - **{입사일}**은 범위 조건입니다.
    - **{상태}**는 완전 일치 조건입니다.
    - 조회 조건이 여러 개인 경우 모든 조건을 만족한 결과로 조회합니다.
- **{이름}**, **{입사일}**, **{사원번호}**로 정렬 및 페이지네이션을 구현합니다.
    - 여러 개의 정렬 조건 중 선택적으로 1개의 정렬 조건만 가질 수 있습니다.
    - 정확한 페이지네이션을 위해 **{이전 페이지의 마지막 요소 ID}**를 활용합니다.
    - 화면을 고려해 적절한 페이지네이션 전략을 선택합니다.

**직원 정보 상세 조회**

- **{id}**로 직원의 상세 정보를 조회할 수 있습니다.
 */

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"department", "profileImage"})
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(nullable = false, length = 100)
    private String name;
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "employee_number", nullable = false, unique = true, updatable = false, length = 20)
    private String employeeNumber;
    private String position;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EmployeeStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_image_id")
    @Getter
    private FileInfo profileImage;


    public boolean isEmailChanged(String email){
        return !this.email.equals(email);
    }

    private Employee(Department department, String name, String email, String position
    , LocalDate hireDate,EmployeeStatus status ,FileInfo profileImage){
        this.department = department;
        this.name = name;
        this.email = email;
        this.position = position;
        this.hireDate = hireDate;
        this.status = status;
        this.profileImage = profileImage;
    }

    public Employee(EmployeeCreateCommand command){
        this(command.department(), command.name(), command.email()
                , command.position(), command.hireDate(), EmployeeStatus.EMPLOYED
                , command.profileImage());
    }

    public void assignEmployeeNumber(String employeeNumber) {
        if (this.employeeNumber != null) {
            throw new IllegalStateException("사원번호는 최초 등록 후 수정할 수 없습니다.");
        }

        this.employeeNumber = employeeNumber;
    }

    public void updateInfo(EmployeeUpdateCommand command){
        this.department =command.department();
        this.name = command.name();
        this.email = command.email();
        this.position  = command.position();
        this.hireDate = command.hireDate();
        this.status = command.status();
    }

    public void changeProfileImage(FileInfo profileImage) {
        this.profileImage = profileImage;
    }

    public void removeProfileImage() {
        this.profileImage = null;
    }

    public EmployeeSnapshot snapshot(){
        Long departmentId = null;
        String departmentName = null;
        if(department != null){
            departmentId = department.getId();
            departmentName = department.getName();
        }

        Long profileImageId = null;
        if(profileImage != null){
            profileImageId = profileImage.getId();
        }

        return new EmployeeSnapshot(
                id
                ,name
                ,email
                ,employeeNumber
                ,departmentId
                ,departmentName
                ,position
                ,hireDate
                ,status.getDescription()
                ,profileImageId
        );
    }

}
