package com.tsl.service;

import com.tsl.model.employee.Forwarder;
import com.tsl.model.employee.TransportPlanner;
import com.tsl.model.order.ForwardingOrder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class SalaryBonusCalculator implements SalaryBonusForEmployeesCalculator {
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

    @Override
    public Double calculateSalaryBonusForForwarders(Forwarder forwarder) {
        List<ForwardingOrder> forwardingOrders = forwarder.getForwardingOrders();
        BigDecimal totalMargin = BigDecimal.ZERO;

        for (ForwardingOrder order : forwardingOrders) {
            LocalDate orderDate = order.getDateAdded();
            LocalDate firstDayOfMonth = LocalDate.of(orderDate.getYear(), orderDate.getMonth(), 1);
            LocalDate lastDayOfMonth = firstDayOfMonth.plusMonths(1).minusDays(1);

            if (orderDate.isAfter(firstDayOfMonth) && orderDate.isBefore(lastDayOfMonth)) {
                totalMargin = totalMargin.add(order.getMargin());
            }
        }

        if (totalMargin.doubleValue() < 2000) {
            return 0.0;
        } else if (totalMargin.doubleValue() >= 2000 && totalMargin.doubleValue() < 3000) {
            return 2000.0;
        } else if (totalMargin.doubleValue() >= 3000 && totalMargin.doubleValue() < 4500) {
            return 3500.0;
        } else if (totalMargin.doubleValue() >= 4500 && totalMargin.doubleValue() < 6500) {
            return 5000.0;
        } else {
            return 7000.0;
        }
    }

}
