package com.sparrow.assertion;

import static java.lang.String.format;

public class CouldNotFindElement extends RuntimeException {
    CouldNotFindElement(String path) {
        super(format("Could not find element %s", path));
    }
}
