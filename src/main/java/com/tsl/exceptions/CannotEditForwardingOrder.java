package com.tsl.exceptions;

public class CannotEditForwardingOrder extends RuntimeException{
    public CannotEditForwardingOrder(String message) {
        super(message);
    }
}
