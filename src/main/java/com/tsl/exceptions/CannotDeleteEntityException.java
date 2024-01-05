package com.tsl.exceptions;

public class CannotDeleteEntityException extends RuntimeException{
    public CannotDeleteEntityException(String message) {
        super(message);
    }
}
