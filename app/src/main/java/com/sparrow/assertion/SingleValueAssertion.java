package com.sparrow.assertion;

import com.google.gson.JsonObject;
import com.sparrow.assertion.AssertJson.DefinePath;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

import static com.sparrow.assertion.InvalidArgument.throwIf;
import static java.util.Objects.isNull;

class SingleValueAssertion<T> implements Assertion<T> {
    private final JsonObject actual;
    private final Path path;
    private final Class<T> type;

    SingleValueAssertion(JsonObject actual, Path path, Class<T> type) {
        this.actual = actual;
        this.path = path;
        this.type = type;
    }

    @Override
    public DefinePath satisfies(Matcher<T> condition) {
        throwIf(() -> isNull(condition), "Condition must not be null");
        T value = Value.readValueAsType(Element.ofPath(actual, path), type);
        MatcherAssert.assertThat(value, condition);
        return new DefinePath(actual);
    }
}
