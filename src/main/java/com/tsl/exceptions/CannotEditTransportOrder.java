package com.tsl.exceptions;

public class CannotEditTransportOrder extends RuntimeException{
    public CannotEditTransportOrder(String message) {
        super(message);
    }
}
