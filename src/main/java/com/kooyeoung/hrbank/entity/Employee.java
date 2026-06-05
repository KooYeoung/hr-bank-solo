package com.kooyeoung.hrbank.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Employee {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Department department;
}
