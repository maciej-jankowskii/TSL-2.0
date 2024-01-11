package com.tsl.exceptions;

public class TruckIsAlreadyAssignedToPlanner extends RuntimeException {
    public TruckIsAlreadyAssignedToPlanner(String message) {
        super(message);
    }
}
