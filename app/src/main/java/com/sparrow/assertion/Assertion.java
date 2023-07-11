package com.sparrow.assertion;

import com.sparrow.assertion.AssertJson.DefinePath;
import org.hamcrest.Matcher;

public interface Assertion<T> {
    DefinePath satisfies(Matcher<T> condition);
}
