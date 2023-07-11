package com.sparrow.assertion

import spock.lang.Specification
import spock.lang.Unroll

import static com.sparrow.assertion.AssertJsonTest.EnumType.NESTED_VALUE
import static com.sparrow.assertion.AssertJsonTest.EnumType.TOP_LEVEL_VALUE
import static org.hamcrest.Matchers.*

class AssertJsonTest extends Specification {

    def "string value is asserted"() {
        given:
        def json = """
            {
                "string": "any top level",
                "nested": {
                    "nested": {
                    "string": "any nested"
                    }
                }
            }
           """
        expect:
        AssertJson.assertThat(json)
                .string("string").satisfies(equalTo("any top level"))
                .string("nested.nested.string").satisfies(equalTo("any nested"))
    }

    def "integer value is asserted"() {
        given:
        def json = """
            {
                "integer": 15,
                "nested": {
                    "nested": {
                    "integer": 100
                    }
                }
            }
           """
        expect:
        AssertJson.assertThat(json)
                .intNumber("integer").satisfies(equalTo(15))
                .intNumber("nested.nested.integer").satisfies(equalTo(100))
    }

    def "double value is asserted"() {
        given:
        def json = """
            {
                "double": 15.5,
                "nested": {
                    "nested": {
                    "double": 100.0
                    }
                }
            }
           """
        expect:
        AssertJson.assertThat(json)
                .doubleNumber("double").satisfies(equalTo(15.5d))
                .doubleNumber("nested.nested.double").satisfies(equalTo(100.0d))
    }

    def "enum value is asserted"() {
        given:
        def json = """
            {
                "enum": TOP_LEVEL_VALUE,
                "nested": {
                    "nested": {
                    "enum": NESTED_VALUE
                    }
                }
            }
           """
        expect:
        AssertJson.assertThat(json)
                .enumerated("enum", EnumType.class).satisfies(equalTo(TOP_LEVEL_VALUE))
                .enumerated("nested.nested.enum", EnumType.class).satisfies(equalTo(NESTED_VALUE))
    }

    def "custom object is asserted"() {
        given:
        def json = """
            {
                "custom-object": {
                    "propertyA": "A",
                    "propertyB": "B",
                    "propertyC": 100
                }
            }
           """
        expect:
        AssertJson.assertThat(json)
                .object("custom-object", CustomType.class).satisfies(equalTo(new CustomType("A", "B", 100)))
    }

    def "null value is asserted"() {
        given:
        def json = """
            {
                "nullable": null
            }
           """
        expect:
        AssertJson.assertThat(json)
                .string("nullable").satisfies(nullValue(String.class))
    }

    def "exception is thrown if JSON is null"() {
        when:
        AssertJson.assertThat(null)

        then:
        thrown(InvalidArgument)
    }

    def "exception is thrown if JSON is empty"() {
        when:
        AssertJson.assertThat("")

        then:
        thrown(InvalidArgument)
    }

    @Unroll
    def "exception is thrown if input is not a valid Json"() {
        when:
        AssertJson.assertThat(json).element("any").satisfies(is(equalTo("value")))

        then:
        thrown(JsonHasInvalidFormat)

        where:
        json << ["{invalid: json,}", "invalid: json,"]
    }

    def "exception is thrown if condition is null"() {
        given:
        def json = """
            {
                "name": "John"
            }
           """

        when:
        AssertJson.assertThat(json).string("name").satisfies(null)

        then:
        thrown(InvalidArgument)
    }

    def "exception is thrown if property is null"() {
        given:
        def json = """
            {
                "name": "John"
            }
           """

        when:
        AssertJson.assertThat(json).string(null)

        then:
        thrown(InvalidArgument)
    }

    def "exception is thrown if object type is null"() {
        given:
        def json = """
            {
                "name": "John"
            }
           """

        when:
        AssertJson.assertThat(json).object("name", null)

        then:
        thrown(InvalidArgument)
    }

    def "exception is thrown if enum type is null"() {
        given:
        def json = """
            {
                "name": "John"
            }
           """

        when:
        AssertJson.assertThat(json).enumerated("name", null)

        then:
        thrown(InvalidArgument)
    }

    def "exception is thrown if path is empty"() {
        given:
        def json = """
            {
                "name": "John"
            }
           """

        when:
        AssertJson.assertThat(json).string("").satisfies(is(emptyString()))

        then:
        thrown(InvalidArgument)
    }

    def "exception is thrown if path leads to non-existing top level property"() {
        given:
        def json = """
            {
                "name": "John"
            }
           """

        when:
        AssertJson.assertThat(json).string("non-existing").satisfies(is(equalTo("John")))

        then:
        thrown(CouldNotFindElement)
    }

    def "exception is thrown if path leads to non-existing nested property"() {
        given:
        def json = """
            {
                "address": {
                    "street" : "any street"
                }
            }
           """

        when:
        AssertJson.assertThat(json).string("address.non-existing").satisfies(is(equalTo("any street")))

        then:
        thrown(CouldNotFindElement)
    }

    static enum EnumType {
        TOP_LEVEL_VALUE, NESTED_VALUE
    }

    static record CustomType(String propertyA, String propertyB, int propertyC) {
    }
}
