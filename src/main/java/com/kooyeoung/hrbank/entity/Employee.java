package com.kooyeoung.hrbank.entity;

import com.kooyeoung.hrbank.dto.command.employee.EmployeeCreateCommand;
import com.kooyeoung.hrbank.dto.command.employee.EmployeeUpdateCommand;
import com.kooyeoung.hrbank.entity.snapshot.EmployeeSnapshot;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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
