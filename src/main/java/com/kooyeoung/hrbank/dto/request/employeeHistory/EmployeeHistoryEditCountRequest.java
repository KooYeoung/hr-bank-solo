package com.kooyeoung.hrbank.dto.request.employeeHistory;

import com.kooyeoung.hrbank.dto.repository.employeeHistory.EmployeeHistoryEditCountCondition;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record EmployeeHistoryEditCountRequest(

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime fromDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime toDate

) {

    public EmployeeHistoryEditCountRequest {
        LocalDateTime now = LocalDateTime.now();

        if (fromDate == null) {
            fromDate = now.minusDays(7);
        }

        if (toDate == null) {
            toDate = now;
        }

        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("fromDate는 toDate보다 이후일 수 없습니다.");
        }
    }

    public EmployeeHistoryEditCountCondition toCondition(){
        return new EmployeeHistoryEditCountCondition(
                fromDate,
                toDate
        );
    }
}