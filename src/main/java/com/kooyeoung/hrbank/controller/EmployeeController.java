package com.kooyeoung.hrbank.controller;

import com.kooyeoung.hrbank.dto.request.employee.EmployeeCreateRequest;
import com.kooyeoung.hrbank.dto.request.employee.EmployeeSearchRequest;
import com.kooyeoung.hrbank.dto.request.employee.EmployeeUpdateRequest;
import com.kooyeoung.hrbank.dto.response.EmployeeDto;
import com.kooyeoung.hrbank.dto.response.PageResponse;
import com.kooyeoung.hrbank.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {
    private final EmployeeService service;

    @GetMapping
    public ResponseEntity<PageResponse<EmployeeDto>> list(@ModelAttribute EmployeeSearchRequest request){
        PageResponse<EmployeeDto> list = service.list(request);
        return ResponseEntity.ok().body(list);
    }

    @PostMapping
    public ResponseEntity<EmployeeDto> create(@Valid @RequestPart EmployeeCreateRequest request
    , @RequestPart(required = false) MultipartFile profile){
        EmployeeDto employeeDto = service.create(request, profile);
        return ResponseEntity.ok().body(employeeDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeDto> update(@PathVariable Long id, @Valid @RequestPart EmployeeUpdateRequest request
                                              ,@RequestPart(required = false) MultipartFile profile){

        EmployeeDto employeeDto = service.update(id, request, profile);

        return ResponseEntity.ok().body(employeeDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> detail(@PathVariable Long id){
        EmployeeDto detail = service.detail(id);
        return ResponseEntity.ok().body(detail);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id ){
        service.delete(id);
        return ResponseEntity.ok().build();

    }


}
