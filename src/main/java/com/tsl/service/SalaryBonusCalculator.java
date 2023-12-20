package com.tsl.service;

import com.tsl.model.employee.TransportPlanner;
import org.springframework.stereotype.Service;

@Service
public class SalaryBonusCalculator implements SalaryBonusForEmployeesCalculator{
    @Override
    public Double calculateSalaryBonusForPlanners(TransportPlanner transportPlanner) {

        int numberOfTrucks = transportPlanner.getCompanyTrucks().size();

        if (numberOfTrucks >= 2 && numberOfTrucks <= 3) {
            return 300.0 * numberOfTrucks;
        } else if (numberOfTrucks >= 4 && numberOfTrucks <= 6) {
            return 450.0 * numberOfTrucks;
        } else if (numberOfTrucks >= 7 && numberOfTrucks <= 9) {
            return 650.0 * numberOfTrucks;
        } else {
            return 0.0;
        }
    }
}
