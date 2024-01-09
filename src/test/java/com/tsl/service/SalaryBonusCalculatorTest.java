package com.tsl.service;

import com.tsl.model.employee.Forwarder;
import com.tsl.model.employee.TransportPlanner;
import com.tsl.model.order.ForwardingOrder;
import com.tsl.model.truck.Truck;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SalaryBonusCalculatorTest {

    @Test
    @DisplayName("Should calculate salary bonus for planners correctly")
    public void testCalculateSalaryBonusForPlanners() {
        SalaryBonusCalculator calculator = new SalaryBonusCalculator();
        TransportPlanner plannerWith2Trucks = prepareTransportPlanner(2);
        TransportPlanner plannerWith5Trucks = prepareTransportPlanner(5);
        TransportPlanner plannerWith8Trucks = prepareTransportPlanner(8);
        TransportPlanner plannerWith10Trucks = prepareTransportPlanner(10);

        assertEquals(600.0, calculator.calculateSalaryBonusForPlanners(plannerWith2Trucks));
        assertEquals(2250.0, calculator.calculateSalaryBonusForPlanners(plannerWith5Trucks));
        assertEquals(5200.0, calculator.calculateSalaryBonusForPlanners(plannerWith8Trucks));
        assertEquals(0.0, calculator.calculateSalaryBonusForPlanners(plannerWith10Trucks));
    }

    @Test
    @DisplayName("Should calculate salary bonus for forwarders correctly")
    public void testCalculateSalaryBonusForForwarders() {
        SalaryBonusCalculator calculator = new SalaryBonusCalculator();
        Forwarder forwarderWithMarginLessThan2000 = prepareForwarder(BigDecimal.valueOf(1500));
        Forwarder forwarderWithMarginBetween2000And3000 = prepareForwarder(BigDecimal.valueOf(2500));
        Forwarder forwarderWithMarginBetween3000And4500 = prepareForwarder(BigDecimal.valueOf(4000));
        Forwarder forwarderWithMarginBetween4500And6500 = prepareForwarder(BigDecimal.valueOf(6000));
        Forwarder forwarderWithMarginOver6500 = prepareForwarder(BigDecimal.valueOf(8000));

        assertEquals(0.0, calculator.calculateSalaryBonusForForwarders(forwarderWithMarginLessThan2000));
        assertEquals(2000.0, calculator.calculateSalaryBonusForForwarders(forwarderWithMarginBetween2000And3000));
        assertEquals(3500.0, calculator.calculateSalaryBonusForForwarders(forwarderWithMarginBetween3000And4500));
        assertEquals(5000.0, calculator.calculateSalaryBonusForForwarders(forwarderWithMarginBetween4500And6500));
        assertEquals(7000.0, calculator.calculateSalaryBonusForForwarders(forwarderWithMarginOver6500));
    }

    private TransportPlanner prepareTransportPlanner(int numberOfTrucks) {
        TransportPlanner planner = new TransportPlanner();
        List<Truck> trucks = new ArrayList<>();
        for (int i = 0; i < numberOfTrucks ; i++) {
            trucks.add(new Truck());
        }
        planner.setCompanyTrucks(trucks);
        return planner;
    }

    private Forwarder prepareForwarder(BigDecimal margin) {
        Forwarder forwarder = new Forwarder();
        ForwardingOrder order = new ForwardingOrder();
        order.setMargin(margin);
        order.setDateAdded(LocalDate.now());
        List<ForwardingOrder> orders = new ArrayList<>();
        orders.add(order);
        forwarder.setForwardingOrders(orders);
        return forwarder;
    }

}