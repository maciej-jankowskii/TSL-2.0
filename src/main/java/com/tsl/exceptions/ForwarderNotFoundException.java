package com.tsl.exceptions;

public class ForwarderNotFoundException extends RuntimeException {
    public ForwarderNotFoundException(String message) {
        super(message);
    }
}
