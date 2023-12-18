package com.tsl.exceptions;

public class InvoiceNotFound extends RuntimeException{
    public InvoiceNotFound(String message) {
        super(message);
    }
}
