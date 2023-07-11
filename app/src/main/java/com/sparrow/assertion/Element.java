package com.sparrow.assertion;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import static java.util.Optional.ofNullable;

class Element {

    private Element() {
    }

    static JsonElement ofPath(JsonElement root, Path path) {
        return elementByPath(root, path);
    }

    private static JsonElement elementByPath(JsonElement element, Path path) {
        if (element.isJsonObject()) {
            JsonElement childElement = childElement(element.getAsJsonObject(), path.value());
            return isDestinationPath(path) ? childElement : elementByPath(childElement, path.descendant());
        }
        return element;
    }

    private static boolean isDestinationPath(Path path) {
        return !path.hasDescendant();
    }

    private static JsonElement childElement(JsonObject parent, String childName) {
        return ofNullable(parent.get(childName))
                .orElseThrow(() -> new CouldNotFindElement(childName));
    }
}
