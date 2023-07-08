package com.sparrow.assertion;

import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;

class JsonPath {
    private static final String PATH_SEPARATOR = ".";
    private final String value;
    private JsonPath descendant;

    private JsonPath(String value, Iterator<String> descendants) {
        this.value = value;
        if (descendants.hasNext()) {
            this.descendant = new JsonPath(descendants.next(), descendants);
        }
    }

    static JsonPath of(String fullPath) {
        Iterator<String> pathParts = splitIntoParts(fullPath);
        return new JsonPath(pathParts.next(), pathParts);
    }

    private static Iterator<String> splitIntoParts(String fullPath) {
        String[] pathParts = fullPath.split(Pattern.quote(PATH_SEPARATOR));
        return Arrays.asList(pathParts).iterator();
    }

    JsonPath descendant() {
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
