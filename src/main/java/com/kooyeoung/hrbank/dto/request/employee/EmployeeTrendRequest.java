package com.kooyeoung.hrbank.dto.request.employee;

import com.kooyeoung.hrbank.dto.repository.employee.EmployeeTrendCondition;

import java.time.LocalDate;
import java.util.Set;

public record EmployeeTrendRequest(
        LocalDate from,
        LocalDate to,
        String unit
) {

    private final static Set<String> AVAILABLE_UNIT = Set.of("day","week","month","quarter","year");
    private final static Long SUBTRACT_NUMBER = 12L;
    private final static String DEFAULT_UNIT ="month";

    public String getUnitOrDefault(){
        if(unit == null) return DEFAULT_UNIT;

        return  AVAILABLE_UNIT.contains(unit) ? unit : DEFAULT_UNIT;
    }

    public LocalDate getToOrDefault(){
        if (to == null) return LocalDate.now();

        return to;
    }

    public LocalDate getFromOrDefault(){
        if(from != null) return from;

        LocalDate toOrDefault = getToOrDefault();
        String unitOrDefault = getUnitOrDefault();

       return switch(unitOrDefault){
            case "day" -> toOrDefault.minusDays(SUBTRACT_NUMBER);
            case "week" -> toOrDefault.minusWeeks(SUBTRACT_NUMBER);
            case "quarter" -> toOrDefault.minusMonths(SUBTRACT_NUMBER * 3);
            case "year" -> toOrDefault.minusYears(SUBTRACT_NUMBER);
           default -> toOrDefault.minusMonths(SUBTRACT_NUMBER);
        };
    }

    public EmployeeTrendCondition toCondition() {
        return new EmployeeTrendCondition(
                getFromOrDefault(), getToOrDefault(), getUnitOrDefault()
        );
    }
}
