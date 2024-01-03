package com.tsl.exceptions;

public class TransportOrderNotFoundException extends RuntimeException{
    public TransportOrderNotFoundException(String message) {
        super(message);
    }
}
