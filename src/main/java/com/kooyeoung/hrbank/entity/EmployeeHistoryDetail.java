package com.kooyeoung.hrbank.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmployeeHistoryDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_history_id")
    private EmployeeHistory history;

    @Enumerated(EnumType.STRING)
    private EmployeeProperties properties;

    private String beforeValue;

    private String afterValue;

    private EmployeeHistoryDetail(
            EmployeeHistory history,
            EmployeeProperties properties,
            String beforeValue,
            String afterValue
    ) {
        this.history = history;
        this.properties = properties;
        this.beforeValue = beforeValue;
        this.afterValue = afterValue;
    }

    public static EmployeeHistoryDetail create(
            EmployeeHistory history,
            EmployeeProperties properties,
            String beforeValue,
            String afterValue
    ) {
        return new EmployeeHistoryDetail(
                history,
                properties,
                beforeValue,
                afterValue
        );
    }
}
