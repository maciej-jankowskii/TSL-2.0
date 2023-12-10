package com.tsl.exceptions;

public class NonUniqueLabelsException extends RuntimeException {
    public NonUniqueLabelsException(String message) {
        super(message);
    }
}
