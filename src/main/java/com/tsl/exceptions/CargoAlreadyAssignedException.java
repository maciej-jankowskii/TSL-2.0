package com.tsl.exceptions;

public class CargoAlreadyAssignedException extends RuntimeException {
    public CargoAlreadyAssignedException(String message) {
        super(message);
    }
}
