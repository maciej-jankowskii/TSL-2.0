package com.tsl.exceptions;

public class WarehouseOrderNotFoundException extends RuntimeException{
    public WarehouseOrderNotFoundException(String message) {
        super(message);
    }
}
