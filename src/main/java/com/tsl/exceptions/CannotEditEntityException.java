package com.tsl.exceptions;

public class CannotEditEntityException extends RuntimeException {
    public CannotEditEntityException(String message) {
        super(message);
    }
}
