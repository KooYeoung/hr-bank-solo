package com.kooyeoung.hrbank.controller;

import com.kooyeoung.hrbank.dto.request.department.DepartmentCreateRequest;
import com.kooyeoung.hrbank.dto.request.department.DepartmentSearchRequest;
import com.kooyeoung.hrbank.dto.request.department.DepartmentUpdateRequest;
import com.kooyeoung.hrbank.dto.response.DepartmentDto;
import com.kooyeoung.hrbank.dto.response.PageResponse;
import com.kooyeoung.hrbank.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Slf4j
public class DepartmentController {

    private final DepartmentService service;

    @GetMapping
    public ResponseEntity<PageResponse<DepartmentDto>> list(@ModelAttribute DepartmentSearchRequest request){
        PageResponse<DepartmentDto> page = service.list(request);

        return ResponseEntity.ok().body(page);
    }

    @PostMapping
    public ResponseEntity<DepartmentDto> create(@RequestBody DepartmentCreateRequest request){
        DepartmentDto savedDepartment = service.save(request);

        return ResponseEntity.ok().body(savedDepartment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDto> detail(@PathVariable Long id){
        DepartmentDto foundDepartment = service.findById(id);

        return ResponseEntity.ok().body(foundDepartment);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DepartmentDto> update(@PathVariable Long id,@RequestBody DepartmentUpdateRequest request){
        DepartmentDto updatedDepartment = service.update(id, request);

        return ResponseEntity.ok().body(updatedDepartment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);

        return ResponseEntity.ok().build();
    }

}
