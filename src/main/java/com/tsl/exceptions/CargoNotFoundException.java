package com.tsl.exceptions;

public class CargoNotFoundException extends RuntimeException{
    public CargoNotFoundException(String message) {
        super(message);
    }
}
