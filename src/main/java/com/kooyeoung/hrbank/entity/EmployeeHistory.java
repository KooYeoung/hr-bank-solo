package com.kooyeoung.hrbank.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmployeeHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private HistoryType type;

    private String employeeNumber;

    private String memo;

    private String ipAddress;

    @Getter
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "history")
    List<EmployeeHistoryDetail> details = new ArrayList<>();

    public EmployeeHistory(HistoryType type, String employeeNumber, String memo, String ipAddress) {
        this.type = type;
        this.employeeNumber = employeeNumber;
        this.memo = memo;
        this.ipAddress = ipAddress;
        this.createdAt = LocalDateTime.now();
    }
}
