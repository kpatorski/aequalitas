package com.sparrow.assertion;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.internal.bind.JsonTreeReader;

class Value {
    private static final Gson GSON = new Gson();

    private Value() {
    }

    static <T> T readValueAsType(JsonElement element, Class<T> type) {
        return element.isJsonNull() ? null : readObject(element, type);
    }

    private static <T> T readObject(JsonElement element, Class<T> type) {
        try (JsonTreeReader reader = new JsonTreeReader(element)) {
            reader.setLenient(true);
            return GSON.fromJson(reader, type);
        } catch (Exception e) {
            throw new CouldNotReadElement(element, e);
        }
    }
}
