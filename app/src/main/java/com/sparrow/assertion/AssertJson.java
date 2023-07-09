package com.sparrow.assertion;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.bind.JsonTreeReader;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

public class AssertJson {
    private static final Gson GSON = new Gson();

    private AssertJson() {
    }

    public static DefinePath assertThat(String json) {
        assertNotEmpty(json);
        return new DefinePath(deserialize(json));
    }

    private static void assertNotEmpty(String json) {
        assertNotNull(json, "Json");
        if (json.isBlank()) {
            throw new IllegalArgumentException("Json must not be empty");
        }
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

        private DefinePath(JsonObject actual) {
            this.actual = actual;
        }

        public Assertion<String> on(String path) {
            assertPathIsNotEmpty(path);
            return new Assertion<>(actual, JsonPath.of(path), String.class);
        }

        public <T> Assertion<T> on(String path, Class<T> type) {
            assertPathIsNotEmpty(path);
            assertNotNull(type, "element type");
            return new Assertion<>(actual, JsonPath.of(path), type);
        }

        private static void assertPathIsNotEmpty(String path) {
            assertNotNull(path, "path to element");
            if (path.isBlank()) {
                throw new IllegalArgumentException(format("%s path must not be empty", path));
            }
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
            assertNotNull(assertion, "assertion");
            T value = value(elementByPath(actual, path));
            MatcherAssert.assertThat(value, assertion);
            return new DefinePath(actual);
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
                    .orElseThrow(() -> new CouldNotFindElement(this.path.value()));
        }

        private T value(JsonElement element) {
            return element.isJsonNull() ? null : readValue(element);
        }

        private T readValue(JsonElement element) {
            try (JsonTreeReader reader = new JsonTreeReader(element)) {
                reader.setLenient(true);
                return GSON.fromJson(reader, type);
            } catch (Exception e) {
                throw new CouldNotReadElement(element, e);
            }
        }
    }

    private static void assertNotNull(Object object, String description) {
        if (object == null) {
            throw new IllegalArgumentException(format("%s must not be null", description));
        }
    }

}
