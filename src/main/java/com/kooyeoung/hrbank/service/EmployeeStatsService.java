package com.kooyeoung.hrbank.service;

import com.kooyeoung.hrbank.dto.repository.employee.EmployeeDistributionCondition;
import com.kooyeoung.hrbank.dto.repository.employee.EmployeeTrendCondition;
import com.kooyeoung.hrbank.dto.response.EmployeeDistributionCount;
import com.kooyeoung.hrbank.dto.response.EmployeeDistributionDto;
import com.kooyeoung.hrbank.dto.response.EmployeeTrendDto;
import com.kooyeoung.hrbank.entity.EmployeeStatus;
import com.kooyeoung.hrbank.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeStatsService {

    private final EmployeeRepository employeeRepository;

    public List<EmployeeDistributionDto> statsDistribution(EmployeeDistributionCondition condition) {
        List<EmployeeDistributionCount> counts = switch (condition.groupBy()) {
            case "position" -> employeeRepository.countGroupByPosition(condition.status());
            default -> employeeRepository.countGroupByDepartment(condition.status());
        };

        long total = counts.stream()
                .mapToLong(EmployeeDistributionCount::count)
                .sum();

        return counts.stream()
                .map(count -> new EmployeeDistributionDto(
                        count.groupKey(),
                        count.count(),
                        calculatePercentage(count.count(), total)
                ))
                .toList();
    }

    public List<EmployeeTrendDto> statsTrend(EmployeeTrendCondition condition) {
        String unit = condition.unit();

        LocalDate from = truncate(condition.from(), unit);
        LocalDate to = truncate(condition.to(), unit);

        // to가 속한 기간까지 포함하기 위해 다음 기간 시작일을 구함
        LocalDate toExclusive = next(to, unit);

        List<LocalDate> hireDates = employeeRepository.findActiveHireDatesBefore(
                toExclusive,
                EmployeeStatus.RESIGNED
        );

        List<LocalDate> periods = createPeriods(from, to, unit);

        List<EmployeeTrendDto> result = new ArrayList<>();

        int hireIndex = 0;
        Long previousCount = null;

        for (LocalDate periodStart : periods) {
            LocalDate periodEnd = next(periodStart, unit);

            while (hireIndex < hireDates.size()
                    && hireDates.get(hireIndex).isBefore(periodEnd)) {
                hireIndex++;
            }

            long count = hireIndex;

            long change = previousCount == null
                    ? 0
                    : count - previousCount;

            double changeRate = 0.0;

            if (previousCount != null && previousCount != 0) {
                changeRate = round2(change * 100.0 / previousCount);
            }

            result.add(new EmployeeTrendDto(
                    periodStart,
                    count,
                    change,
                    changeRate
            ));

            previousCount = count;
        }

        return result;
    }

    private List<LocalDate> createPeriods(LocalDate from, LocalDate to, String unit) {
        List<LocalDate> periods = new ArrayList<>();

        LocalDate current = from;

        while (!current.isAfter(to)) {
            periods.add(current);
            current = next(current, unit);
        }

        return periods;
    }

    private LocalDate next(LocalDate date, String unit) {
        return switch (unit) {
            case "day" -> date.plusDays(1);
            case "week" -> date.plusWeeks(1);
            case "quarter" -> date.plusMonths(3);
            case "year" -> date.plusYears(1);
            default -> date.plusMonths(1);
        };
    }

    private LocalDate truncate(LocalDate date, String unit) {
        return switch (unit) {
            case "day" -> date;

            // 월요일 기준 주 시작일
            case "week" -> date.minusDays(date.getDayOfWeek().getValue() - 1);

            case "month" -> date.withDayOfMonth(1);

            case "quarter" -> {
                int month = date.getMonthValue();
                int quarterStartMonth = ((month - 1) / 3) * 3 + 1;
                yield LocalDate.of(date.getYear(), quarterStartMonth, 1);
            }

            case "year" -> LocalDate.of(date.getYear(), 1, 1);

            default -> date.withDayOfMonth(1);
        };
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private double calculatePercentage(long count, long total) {
        if (total == 0) {
            return 0.0;
        }

        return Math.round((count * 10000.0 / total)) / 100.0;
    }


}
