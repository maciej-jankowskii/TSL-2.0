package com.tsl.exceptions;

public class NoTrucksException extends RuntimeException{
    public NoTrucksException(String message) {
        super(message);
    }
}
