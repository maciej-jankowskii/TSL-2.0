package com.tsl.enums;

import lombok.Getter;

@Getter
public enum PaymentRating {
    PAYMENTS_ON_TIME,
    SMALL_DELAYS,
    LONG_DELAYS,
    BLACK_LIST,
    NONE
}
