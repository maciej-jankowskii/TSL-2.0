package com.tsl.exceptions;

public class ContactPersonNotFoundException extends RuntimeException{
    public ContactPersonNotFoundException(String message) {
        super(message);
    }
}
