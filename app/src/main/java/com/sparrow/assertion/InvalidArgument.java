package com.sparrow.assertion;

import java.util.function.BooleanSupplier;

public class InvalidArgument extends RuntimeException {
    private InvalidArgument(String message) {
        super(message);
    }

    static void throwIf(BooleanSupplier condition, String description) {
        if (condition.getAsBoolean()) {
            throw new InvalidArgument(description);
        }
    }
}
