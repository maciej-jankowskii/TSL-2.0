package com.tsl.service;

import com.tsl.model.employee.Forwarder;
import com.tsl.model.employee.TransportPlanner;

public interface SalaryBonusForEmployeesCalculator {

    Double calculateSalaryBonusForPlanners(TransportPlanner transportPlanner);

    Double calculateSalaryBonusForForwarders(Forwarder forwarder);
}
