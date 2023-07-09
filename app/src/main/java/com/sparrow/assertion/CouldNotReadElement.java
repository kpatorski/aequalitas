package com.sparrow.assertion;

import com.google.gson.JsonElement;

import static java.lang.String.format;

public class CouldNotReadElement extends RuntimeException {
    CouldNotReadElement(JsonElement element, Exception reason) {
        super(format("Could not read element[%s]", element), reason);
    }
}
