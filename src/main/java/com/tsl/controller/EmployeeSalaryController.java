package com.tsl.controller;

import com.tsl.service.EmployeeSalaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/salary")
public class EmployeeSalaryController {

    private final EmployeeSalaryService employeeSalaryService;


    public EmployeeSalaryController(EmployeeSalaryService employeeSalaryService) {
        this.employeeSalaryService = employeeSalaryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> calculateSalary(@PathVariable Long id) {
        BigDecimal salary = employeeSalaryService.calculateSalaryForEmployee(id);
        return ResponseEntity.ok(salary);
    }
}
