package com.tsl.exceptions;

public class AccountantNotFoundException extends RuntimeException{
    public AccountantNotFoundException(String message) {
        super(message);
    }
}
