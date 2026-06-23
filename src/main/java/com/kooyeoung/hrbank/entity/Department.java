package com.kooyeoung.hrbank.entity;

import com.kooyeoung.hrbank.dto.command.department.DepartmentCreateCommand;
import com.kooyeoung.hrbank.dto.command.department.DepartmentUpdateCommand;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String description;

    private LocalDate establishedDate;

    public boolean isNameChanged(String name) {
        return !this.name.equals(name);
    }

    public Department(DepartmentCreateCommand command) {
        this.name = command.name();
        this.description = command.description();
        this.establishedDate = command.establishedDate();
    }

    public void updateInfo(DepartmentUpdateCommand command) {
        this.name = command.name();
        this.description = command.description();
        this.establishedDate = command.establishedDate();
    }

}
