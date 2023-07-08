package com.sparrow.assertion

import spock.lang.Specification

import static org.hamcrest.Matchers.*

class AssertJsonTest extends Specification {

    def "primitive value of top level property is asserted"() {
        given:
        def json = """
            {
                "name": "John",
                "age": 15
            }
           """
        expect:
        AssertJson.assertThat(json)
                .on("name").satisfies(equalTo("John"))
                .on("age").satisfies(equalTo("15"))
                .on("age", Integer.class).satisfies(equalTo(15))
    }

    def "null value of top level property is asserted"() {
        given:
        def json = """
            {
                "name": "John",
                "kids": null
            }
           """
        expect:
        AssertJson.assertThat(json)
                .on("kids").satisfies(nullValue(String.class))
    }
}
