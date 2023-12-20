package com.tsl.exceptions;

public class CannotEditCompletedWarehouseOrder extends RuntimeException{
    public CannotEditCompletedWarehouseOrder(String message) {
        super(message);
    }
}
