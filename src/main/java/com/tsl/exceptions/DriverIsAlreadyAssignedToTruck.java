package com.tsl.exceptions;

public class DriverIsAlreadyAssignedToTruck extends RuntimeException{
    public DriverIsAlreadyAssignedToTruck(String message) {
        super(message);
    }
}
