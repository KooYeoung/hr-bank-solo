package com.kooyeoung.hrbank.service;

import com.kooyeoung.hrbank.config.EmployeeNumberProperties;
import com.kooyeoung.hrbank.entity.EmployeeNumberSequence;
import com.kooyeoung.hrbank.repository.EmployeeNumberSequenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmployeeNumberGenerator {

    private final EmployeeNumberSequenceRepository repository;
    private final EmployeeNumberProperties properties;

    public String generate(LocalDate hireDate) {

        String yyyyMM = hireDate.format(DateTimeFormatter.ofPattern("yyyyMM"));

        EmployeeNumberSequence currentSequence = repository.findByYyyyMMForUpdate(yyyyMM)
                .orElseGet(() -> repository.save(new EmployeeNumberSequence(yyyyMM)));

        return currentSequence.issueEmployeeNumber(properties.sequenceWidth());
    }
}
