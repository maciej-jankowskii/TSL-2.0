package com.tsl.exceptions;

public class WarehouseWorkerNotFoundException extends RuntimeException{
    public WarehouseWorkerNotFoundException(String message) {
        super(message);
    }
}
