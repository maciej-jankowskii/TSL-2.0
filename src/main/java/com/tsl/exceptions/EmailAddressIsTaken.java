package com.tsl.exceptions;

public class EmailAddressIsTaken extends RuntimeException{
    public EmailAddressIsTaken(String message) {
        super(message);
    }
}
