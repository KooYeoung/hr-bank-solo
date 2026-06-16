package com.kooyeoung.hrbank.dto.request.employee;

import com.kooyeoung.hrbank.dto.repository.employee.EmployeeDistributionCondition;
import com.kooyeoung.hrbank.entity.EmployeeStatus;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public record EmployeeDistributionRequest(
        String groupBy,
        String status
) {

    private static String DEFAULT_GROUP_VALUE = "department";
    private static EmployeeStatus DEFAULT_STATUS_VALUE = EmployeeStatus.ACTIVE;
    private static Set<String> AVAILABLE_GROUP = Set.of(DEFAULT_GROUP_VALUE, "position");
    private static Set<String> AVAILABLE_STATUS =   Arrays.stream(EmployeeStatus.values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    // department: 부서별, position: 직무별, 기본값: department
    public String getGroupByOrDefault(){
        if(groupBy == null || groupBy.isBlank()) return DEFAULT_GROUP_VALUE;

        return AVAILABLE_GROUP.contains(groupBy) ? groupBy : DEFAULT_GROUP_VALUE;
    }

    // ACTIVE, ON_LEAVE, RESIGNED
    public EmployeeStatus getStatusOrDefault(){
        if(status == null || status.isBlank()) return DEFAULT_STATUS_VALUE;

        String normalizedStatus = status.toUpperCase();

        return AVAILABLE_STATUS.contains(normalizedStatus) ? EmployeeStatus.valueOf(normalizedStatus) : DEFAULT_STATUS_VALUE;
    }

    public EmployeeDistributionCondition toCondition(){
        return new EmployeeDistributionCondition(
                getGroupByOrDefault(),
                getStatusOrDefault()
        );
    }
}
