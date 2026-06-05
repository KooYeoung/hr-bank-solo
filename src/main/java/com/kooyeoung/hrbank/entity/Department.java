package com.kooyeoung.hrbank.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Department {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    private String description;
    private LocalDate establishedDate;

    public boolean isNameChanged(String name){
        return !this.name.equals(name);
    }

    public Department(String name, String description, LocalDate establishedDate){
        this.name =name;
        this.description = description;
        this.establishedDate = establishedDate;
    }

    public void updateInfo(String name, String description, LocalDate establishedDate){
        this.name = name;
        this.description = description;
        this.establishedDate = establishedDate;
    }

}
