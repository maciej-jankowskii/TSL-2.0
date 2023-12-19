package com.tsl.exceptions;

public class WarehouseOrderIsAlreadyCompletedException extends RuntimeException{
    public WarehouseOrderIsAlreadyCompletedException(String message) {
        super(message);
    }
}
