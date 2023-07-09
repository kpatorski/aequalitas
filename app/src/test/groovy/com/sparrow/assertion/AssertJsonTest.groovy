package com.sparrow.assertion

import spock.lang.Specification
import spock.lang.Unroll

import static com.sparrow.assertion.AssertJson.Assertion.*
import static org.hamcrest.Matchers.*

class AssertJsonTest extends Specification {

    @Unroll
    def "[#value] value of top level property [#property] is asserted"() {
        given:
        def json = """
            {
                "name": "John",
                "age": 15
            }
           """
        expect:
        AssertJson.assertThat(json).on(property).satisfies(equalTo(value))

        where:
        property | value
        "name"   | "John"
        "age"    | "15"
    }

    def "value is casted to desired type"() {
        given:
        def json = """
            {
                "integer": 15,
                "double": 2.5,
                "string": "text",
                "enum": "VALUE_TWO",
                "custom-object": {
                    "propertyA": "A",
                    "propertyB": "B",
                    "propertyC": 100
                }
            }
           """
        expect:
        AssertJson.assertThat(json)
                .on("integer", Integer.class).satisfies(equalTo(15))
                .on("double", Double.class).satisfies(equalTo(2.5d))
                .on("string", String.class).satisfies(equalTo("text"))
                .on("enum", EnumType.class).satisfies(equalTo(EnumType.VALUE_TWO))
                .on("custom-object", CustomType.class).satisfies(equalTo(new CustomType("A", "B", 100)))
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
                .on("nullable").satisfies(nullValue(String.class))
    }

    @Unroll
    def "[#value] value of nested level property [#property] is asserted"() {
        given:
        def json = """
            {
                "address": {
                    "street": "Any street 10",
                    "postal-code": "555-000",
                    "owner": {
                        "name": "Max",
                        "id": 12355
                    }
                }
            }
           """
        expect:
        AssertJson.assertThat(json).on(property).satisfies(equalTo(value))

        where:
        property              | value
        "address.street"      | "Any street 10"
        "address.postal-code" | "555-000"
        "address.owner.name"  | "Max"
        "address.owner.id"    | "12355"
    }

    def "exception is thrown if JSON is null"() {
        when:
        AssertJson.assertThat(null)

        then:
        thrown(IllegalArgumentException)
    }

    def "exception is thrown if matcher is null"() {
        given:
        def json = """
            {
                "name": "John"
            }
           """

        when:
        AssertJson.assertThat(json).on("name").satisfies(null)

        then:
        thrown(IllegalArgumentException)
    }

    def "exception is thrown if property is null"() {
        given:
        def json = """
            {
                "name": "John"
            }
           """

        when:
        AssertJson.assertThat(json).on(null)

        then:
        thrown(IllegalArgumentException)
    }

    def "exception is thrown if property type is null"() {
        given:
        def json = """
            {
                "name": "John"
            }
           """

        when:
        AssertJson.assertThat(json).on("name", null)

        then:
        thrown(IllegalArgumentException)
    }

    def "exception is thrown if property is empty"() {
        given:
        def json = """
            {
                "name": "John"
            }
           """

        when:
        AssertJson.assertThat(json).on("").satisfies(is(emptyString()))

        then:
        thrown(IllegalArgumentException)
    }

    def "exception is thrown if path leads to non-existing top level property"() {
        given:
        def json = """
            {
                "name": "John"
            }
           """

        when:
        AssertJson.assertThat(json).on("non-existing").satisfies(is(equalTo("John")))

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
        AssertJson.assertThat(json).on("address.non-existing").satisfies(is(equalTo("any street")))

        then:
        thrown(CouldNotFindElement)
    }

    static enum EnumType {
        VALUE_ONE, VALUE_TWO, VALUE_THREE
    }

    static record CustomType(String propertyA, String propertyB, int propertyC) {
    }
}
