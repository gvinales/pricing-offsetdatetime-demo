package org.acme.pricing.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CurrencyEnum {

    USD("USD"),

    EUR("EUR"),

    ABC("ABC"),

    XYZ("XYZ"),

    ETC("ETC");

    private final String value;

    CurrencyEnum(String value) {
        this.value = value;
    }

    @JsonCreator
    public static CurrencyEnum fromValue(String value) {
        for (CurrencyEnum b : CurrencyEnum.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

