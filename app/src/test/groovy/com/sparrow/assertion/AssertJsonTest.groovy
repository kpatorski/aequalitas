package com.sparrow.assertion

import spock.lang.Specification
import spock.lang.Unroll

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
                "enum": "VALUE_TWO"
            }
           """
        expect:
        AssertJson.assertThat(json)
                .on("integer", Integer.class).satisfies(equalTo(15))
                .on("double", Double.class).satisfies(equalTo(2.5d))
                .on("string", String.class).satisfies(equalTo("text"))
                .on("enum", EnumType.class).satisfies(equalTo(EnumType.VALUE_TWO))
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

    static enum EnumType {
        VALUE_ONE, VALUE_TWO, VALUE_THREE
    }
}
