package com.sparrow.assertion;

import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;

import static com.sparrow.assertion.InvalidArgument.throwIf;
import static java.util.Objects.isNull;

class Path {
    private static final String PATH_SEPARATOR = ".";
    private final String value;
    private Path descendant;

    private Path(String value, Iterator<String> descendants) {
        this.value = value;
        if (descendants.hasNext()) {
            this.descendant = new Path(descendants.next(), descendants);
        }
    }

    static Path of(String fullPath) {
        throwIf(()-> isNull(fullPath), "Path must not be null");
        throwIf(fullPath::isBlank, "Path must not be empty");
        Iterator<String> pathParts = splitIntoParts(fullPath);
        return new Path(pathParts.next(), pathParts);
    }

    private static Iterator<String> splitIntoParts(String fullPath) {
        String[] pathParts = fullPath.split(Pattern.quote(PATH_SEPARATOR));
        return Arrays.asList(pathParts).iterator();
    }

    Path descendant() {
        return descendant;
    }

    String value() {
        return value;
    }

    boolean hasDescendant() {
        return descendant != null;
    }

    @Override
    public String toString() {
        return value;
    }
}
