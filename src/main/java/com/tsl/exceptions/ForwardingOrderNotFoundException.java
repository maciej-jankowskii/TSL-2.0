package com.tsl.exceptions;

public class ForwardingOrderNotFoundException extends RuntimeException{
    public ForwardingOrderNotFoundException(String message) {
        super(message);
    }
}
