package com.tsl.exceptions;

public class CannotEditInvoice extends RuntimeException{
    public CannotEditInvoice(String message) {
        super(message);
    }
}
