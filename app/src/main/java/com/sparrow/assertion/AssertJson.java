package com.sparrow.assertion;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import static com.sparrow.assertion.InvalidArgument.throwIf;
import static java.util.Objects.isNull;

public class AssertJson {
    private static final Gson GSON = new Gson();

    private AssertJson() {
    }

    public static DefinePath assertThat(String json) {
        assertNotEmpty(json);
        return new DefinePath(deserialize(json));
    }

    private static void assertNotEmpty(String json) {
        throwIf(() -> isNull(json), "Json must not be null");
        throwIf(json::isBlank, "Json must not be empty");
    }

    private static JsonObject deserialize(String json) {
        try {
            return GSON.fromJson(json, JsonObject.class);
        } catch (JsonSyntaxException e) {
            throw new JsonHasInvalidFormat(e);
        }
    }

    public static class DefinePath {
        private final JsonObject actual;

        DefinePath(JsonObject actual) {
            this.actual = actual;
        }

        public Assertion<Integer> intNumber(String path) {
            return new SingleValueAssertion<>(actual, Path.of(path), Integer.class);
        }

        public Assertion<Double> doubleNumber(String path) {
            return new SingleValueAssertion<>(actual, Path.of(path), Double.class);
        }

        public Assertion<String> string(String path) {
            return new SingleValueAssertion<>(actual, Path.of(path), String.class);
        }

        public <T extends Enum<T>> Assertion<T> enumerated(String path, Class<T> type) {
            return object(path, type);
        }

        public <T> Assertion<T> object(String path, Class<T> type) {
            throwIf(() -> isNull(type), "Type must not be null");
            return new SingleValueAssertion<>(actual, Path.of(path), type);
        }
    }
}
