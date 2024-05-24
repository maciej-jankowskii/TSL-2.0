package com.tsl.controller;

import com.tsl.service.calculators.EmployeeSalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/salary")
@RequiredArgsConstructor
public class EmployeeSalaryController {

    private final EmployeeSalaryService employeeSalaryService;


    @GetMapping("/{id}")
    public ResponseEntity<?> calculateSalary(@PathVariable Long id) {
        BigDecimal salary = employeeSalaryService.calculateSalaryForEmployee(id);
        return ResponseEntity.ok(salary);
    }
}
