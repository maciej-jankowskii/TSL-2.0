package com.tsl.service;

import com.tsl.exceptions.EmployeeNotFoundException;
import com.tsl.model.employee.*;
import com.tsl.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EmployeeSalaryService {

    private final UserRepository userRepository;
    private final SalaryBonusCalculator salaryBonusCalculator;

    public EmployeeSalaryService(UserRepository userRepository, SalaryBonusCalculator salaryBonusCalculator) {
        this.userRepository = userRepository;
        this.salaryBonusCalculator = salaryBonusCalculator;
    }

    public BigDecimal calculateSalaryForEmployee(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

        if (user instanceof TransportPlanner) {
            BigDecimal basicGrossSalary = user.getBasicGrossSalary();
            Double bonus = salaryBonusCalculator.calculateSalaryBonusForPlanners((TransportPlanner) user);
            return basicGrossSalary.add(BigDecimal.valueOf(bonus));
        } else if (user instanceof Forwarder) {
            BigDecimal basicGrossSalary = user.getBasicGrossSalary();
            Double bonus = salaryBonusCalculator.calculateSalaryBonusForForwarders((Forwarder) user);
            return basicGrossSalary.add(BigDecimal.valueOf(bonus));
        } else if (user instanceof Accountant || user instanceof Driver || user instanceof WarehouseWorker) {
            return user.getBasicGrossSalary();

        }
        return BigDecimal.valueOf(0.0);
    }
}
