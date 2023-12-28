package com.tsl.exceptions;

public class CannotEditCargo extends RuntimeException {
    public CannotEditCargo(String message) {
        super(message);
    }
}
