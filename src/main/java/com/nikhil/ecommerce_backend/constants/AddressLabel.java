package com.nikhil.ecommerce_backend.constants;


import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum AddressLabel
{
    OFFICE(1),
    HOME(2);
    private final int value;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AddressLabel fromValue(String value) {
        for (AddressLabel label : AddressLabel.values()) {
            if (label.name().equalsIgnoreCase(value)) {
                return label;
            }
        }
        throw new IllegalArgumentException(
        );
    }
}
