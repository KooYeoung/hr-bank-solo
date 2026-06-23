package com.kooyeoung.hrbank.controller;

import com.kooyeoung.hrbank.dto.request.employee.EmployeeDistributionRequest;
import com.kooyeoung.hrbank.dto.request.employee.*;
import com.kooyeoung.hrbank.dto.response.EmployeeDistributionDto;
import com.kooyeoung.hrbank.dto.response.EmployeeDto;
import com.kooyeoung.hrbank.dto.response.EmployeeTrendDto;
import com.kooyeoung.hrbank.dto.response.PageResponse;
import com.kooyeoung.hrbank.service.EmployeeService;
import com.kooyeoung.hrbank.service.EmployeeStatsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {
    private final EmployeeService service;
    private final EmployeeStatsService employeeStatsService;

    @GetMapping
    public ResponseEntity<PageResponse<EmployeeDto>> list(@ModelAttribute EmployeeSearchRequest request){
        PageResponse<EmployeeDto> list = service.list(request.toCondition());
        return ResponseEntity.ok().body(list);
    }

    @PostMapping
    public ResponseEntity<EmployeeDto> create(@Valid @RequestPart EmployeeCreateRequest employee
    , @RequestPart(required = false) MultipartFile profile){
        EmployeeDto employeeDto = service.create(employee.toCommand(), employee.departmentId(), profile, employee.memo());
        return ResponseEntity.ok().body(employeeDto);
    }

    @PatchMapping("/{employeeId}")
    public ResponseEntity<EmployeeDto> update(@PathVariable Long employeeId, @Valid @RequestPart EmployeeUpdateRequest employee
                                              ,@RequestPart(required = false) MultipartFile profile){

        EmployeeDto employeeDto = service.update(employeeId, employee.toCommand(), employee.departmentId(), profile, employee.memo());

        return ResponseEntity.ok().body(employeeDto);
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeDto> detail(@PathVariable Long employeeId){
        EmployeeDto detail = service.detail(employeeId);
        return ResponseEntity.ok().body(detail);
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> delete(@PathVariable Long employeeId ){
        service.delete(employeeId);
        return ResponseEntity.ok().build();

    }

    // 직원 수 조회
    @GetMapping("/count")
    public ResponseEntity<Long> employeesCount(EmployeesCountRequest request){
        Long l = service.employeesCount(request.toCondition());

        return ResponseEntity.ok().body(l);
    }

    // 직원 수 추이 조회
    @GetMapping("/stats/trend")
    public ResponseEntity<List<EmployeeTrendDto>> employeesTrend(EmployeeTrendRequest request){
        List<EmployeeTrendDto> employeeTrendDtos = employeeStatsService.statsTrend(request.toCondition());

        return ResponseEntity.ok().body(employeeTrendDtos);
    }

    // 직원 분포 조회
    @GetMapping("/stats/distribution")
    public ResponseEntity<List<EmployeeDistributionDto>> employeesDistribution(EmployeeDistributionRequest request){
        List<EmployeeDistributionDto> employeeDistributionDtos = employeeStatsService.statsDistribution(request.toCondition());

        return ResponseEntity.ok().body(employeeDistributionDtos);
    }


}
