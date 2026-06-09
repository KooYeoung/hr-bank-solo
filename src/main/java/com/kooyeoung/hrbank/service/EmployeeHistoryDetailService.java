package com.kooyeoung.hrbank.service;

import com.kooyeoung.hrbank.dto.command.historyDetail.EmployeeHistoryDetailCommand;
import com.kooyeoung.hrbank.entity.EmployeeHistory;
import com.kooyeoung.hrbank.entity.EmployeeHistoryDetail;
import com.kooyeoung.hrbank.entity.EmployeeProperties;
import com.kooyeoung.hrbank.entity.snapshot.EmployeeSnapshot;
import com.kooyeoung.hrbank.repository.EmployeeHistoryDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeHistoryDetailService {
    
    private final EmployeeHistoryDetailRepository repository;

    private static final List<PropertyMapper> PROPERTY_MAPPERS = List.of(
            new PropertyMapper(EmployeeProperties.DEPARTMENT, EmployeeSnapshot::departmentName),
            new PropertyMapper(EmployeeProperties.NAME, EmployeeSnapshot::name),
            new PropertyMapper(EmployeeProperties.EMAIL, EmployeeSnapshot::email),
            new PropertyMapper(EmployeeProperties.EMPLOYEE_NUMBER, EmployeeSnapshot::employeeNumber),
            new PropertyMapper(EmployeeProperties.POSITION, EmployeeSnapshot::position),
            new PropertyMapper(EmployeeProperties.HIRE_DATE, EmployeeSnapshot::hireDate),
            new PropertyMapper(EmployeeProperties.EMPLOYEE_STATUS, EmployeeSnapshot::status)
    );


    public void save(EmployeeHistoryDetailCommand command){
        EmployeeSnapshot beforeSnapshot = command.beforeSnapshot();
        EmployeeSnapshot afterSnapshot = command.afterSnapshot();
        EmployeeHistory history = command.history();

        List<EmployeeHistoryDetail> details = PROPERTY_MAPPERS.stream()
                .map(mapper -> createDetailIfChanged(
                        mapper,
                        beforeSnapshot,
                        afterSnapshot,
                        history
                ))
                .filter(Objects::nonNull)
                .toList();

        if (details.isEmpty()) {
            return;
        }

        repository.saveAll(details);
    }

    private EmployeeHistoryDetail createDetailIfChanged(
            PropertyMapper mapper,
            EmployeeSnapshot beforeSnapshot,
            EmployeeSnapshot afterSnapshot,
            EmployeeHistory history
    ) {
        String beforeValue = getValue(beforeSnapshot, mapper.extractor());
        String afterValue = getValue(afterSnapshot, mapper.extractor());

        if (Objects.equals(beforeValue, afterValue)) {
            return null;
        }

        return EmployeeHistoryDetail.create(
                history,
                mapper.property(),
                beforeValue,
                afterValue
        );
    }

    private String getValue(
            EmployeeSnapshot snapshot,
            Function<EmployeeSnapshot, ?> extractor
    ) {
        if (snapshot == null) {
            return null;
        }

        Object value = extractor.apply(snapshot);

        if (value == null) {
            return null;
        }

        return String.valueOf(value);
    }

    private record PropertyMapper(
            EmployeeProperties property,
            Function<EmployeeSnapshot, ?> extractor
    ) {
    }
}
