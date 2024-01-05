package com.tsl.exceptions;

public class NoDriverOnTheTruckException extends RuntimeException{
    public NoDriverOnTheTruckException(String message) {
        super(message);
    }
}
