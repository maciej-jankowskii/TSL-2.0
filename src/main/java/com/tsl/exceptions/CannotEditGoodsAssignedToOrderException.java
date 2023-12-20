package com.tsl.exceptions;

public class CannotEditGoodsAssignedToOrderException extends RuntimeException {
    public CannotEditGoodsAssignedToOrderException(String message) {
        super(message);
    }
}
