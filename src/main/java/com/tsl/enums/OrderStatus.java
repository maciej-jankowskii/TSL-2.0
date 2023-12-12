package com.tsl.enums;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum OrderStatus {
    ASSIGNED_TO_CARRIER,
    ON_LOADING,
    ON_THE_WAY_TO_UNLOADING,
    ON_UNLOADING,
    UNLOADED,
    CANCELED,
}
