package com.sparrow.assertion;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

import java.util.NoSuchElementException;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

public class AssertJson {
    private AssertJson() {
    }

    public static DefinePath assertThat(String json) {
        JsonObject actual = new Gson().fromJson(json, JsonObject.class);
        return new DefinePath(actual);
    }

    public static class DefinePath {
        private final JsonObject actual;

        private DefinePath(JsonObject actual) {
            this.actual = actual;
        }

        public Assertion<String> on(String path) {
            return new Assertion<>(actual, JsonPath.of(path), String.class);
        }

        public <T> Assertion<T> on(String path, Class<T> type) {
            return new Assertion<>(actual, JsonPath.of(path), type);
        }
    }

    public static class Assertion<T> {
        private static final Gson GSON = new Gson();
        private final JsonObject actual;
        private final JsonPath path;
        private final Class<T> type;

        private Assertion(JsonObject actual, JsonPath of, Class<T> type) {
            this.actual = actual;
            this.path = of;
            this.type = type;
        }

        public DefinePath satisfies(Matcher<T> assertion) {
            T value = value(elementByPath(actual, path));
            MatcherAssert.assertThat(value, assertion);
            return new DefinePath(actual);
        }

        private T value(JsonElement element) {
            return element.isJsonNull() ? null : GSON.fromJson(element.getAsString(), type);
        }

        private JsonElement elementByPath(JsonElement parent, JsonPath path) {
            if (parent.isJsonObject()) {
                JsonElement childElement = childElement(parent.getAsJsonObject(), path.value());
                return isDestinationPath(path) ? childElement : elementByPath(childElement, path.descendant());
            }
            return parent;
        }

        private static boolean isDestinationPath(JsonPath path) {
            return !path.hasDescendant();
        }

        private JsonElement childElement(JsonObject parent, String childName) {
            return ofNullable(parent.get(childName))
                    .orElseThrow(() -> new NoSuchElementException(format("%s does not exist", this.path)));
        }
    }
}
