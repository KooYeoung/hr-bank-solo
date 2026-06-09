package com.kooyeoung.hrbank.controller;

import com.kooyeoung.hrbank.dto.request.employeeHistory.EmployeeHistoryEditCountRequest;
import com.kooyeoung.hrbank.dto.request.employeeHistory.EmployeeHistorySearchRequest;
import com.kooyeoung.hrbank.dto.response.ChangeLogDetailDto;
import com.kooyeoung.hrbank.dto.response.ChangeLogDto;
import com.kooyeoung.hrbank.dto.response.PageResponse;
import com.kooyeoung.hrbank.service.EmployeeHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/change-logs")
@RequiredArgsConstructor
@Slf4j
public class EmployeeHistoryController {

    private final EmployeeHistoryService service;

    @GetMapping
    public ResponseEntity<PageResponse<ChangeLogDto>> list(@ModelAttribute EmployeeHistorySearchRequest request){
        PageResponse<ChangeLogDto> list = service.list(request);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChangeLogDetailDto> detail(@PathVariable Long id){
        ChangeLogDetailDto detail = service.detail(id);
        return ResponseEntity.ok().body(detail);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count(EmployeeHistoryEditCountRequest request){
        Long editCountResult = service.editCount(request);

        return ResponseEntity.ok().body(editCountResult);
    }

}
