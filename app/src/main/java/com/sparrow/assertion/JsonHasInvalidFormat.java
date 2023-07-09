package com.sparrow.assertion;

public class JsonHasInvalidFormat extends RuntimeException {
    JsonHasInvalidFormat(Exception reason) {
        super("Json has invalid format", reason);
    }
}
