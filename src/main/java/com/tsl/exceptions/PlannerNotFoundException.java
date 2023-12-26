package com.tsl.exceptions;

public class PlannerNotFoundException extends RuntimeException {
    public PlannerNotFoundException(String message) {
        super(message);
    }
}
