package com.sparrow.control

import spock.lang.Specification

import java.util.function.Consumer
import java.util.function.Function

class ResultTest extends Specification {

    def "create result of success"() {
        given: "any success"
        String success = "any success"

        when:
        def result = Result.success(success)

        then:
        result.success() == success
        result.isSuccess()
    }

    def "create result of failure"() {
        given: "any failure"
        String failure = "any failure"

        when:
        def result = Result.failure(failure)

        then:
        result.failure() == failure
        result.isFailure()
    }

    def "return success transformed to value"() {
        given: "value"
        String value = "any value"

        when:
        def result = Result.success(value)

        then:
        result.get((val) -> "success", (val) -> "failure") == "success"
    }

    def "return failure transformed to value"() {
        given: "value"
        String value = "any value"

        when:
        def result = Result.failure(value)

        then:
        result.get((val) -> "success", (val) -> "failure") == "failure"
    }

    def "determine if success"() {
        given: "any success"
        String success = "any success"

        when:
        def result = Result.success(success)

        then:
        result.isSuccess()
    }

    def "execute consumer if success"() {
        given:
        def anySuccess = MutableValue.of("any success")
        def result = Result.success(anySuccess)

        when:
        result.ifSuccess(new Consumer<MutableValue>() {
            @Override
            void accept(MutableValue success) {
                success.change("success indeed")
            }
        })

        then:
        anySuccess.get() == "success indeed"
    }

    def "execute consumer if failure"() {
        given:
        def anyFailure = MutableValue.of("any failure")
        def result = Result.failure(anyFailure)

        when:
        result.ifFailure((failure) -> failure.change("failure indeed"))

        then:
        anyFailure.get() == "failure indeed"
    }

    def "map success to new one only if it is a success"() {
        when:
        def result = Result.success("any success")
                .mapSuccess { "another success" }

        then:
        result.success() == "another success"
    }

    def "does not map success to new one if it is failure"() {
        when:
        def result = Result.failure("any failure")
                .mapSuccess { "another success" }

        then:
        result.failure() == "any failure"
    }

    def "map failure to new one only if it is a failure"() {
        when:
        def result = Result.failure("any failure")
                .mapFailure { "another failure" }

        then:
        result.failure() == "another failure"
    }

    def "does not map failure to new one if it is success"() {
        when:
        def result = Result.success("any success")
                .mapFailure { "another failure" }

        then:
        result.success() == "any success"
    }

    def "map only success to new one if it is a success"() {
        when:
        def result = Result.success("any success")
                .map(new Function<String, String>() {
                    @Override
                    String apply(String success) { "another success" }
                }, new Function<String, String>() {
                    @Override
                    String apply(String failure) { "another failure" }
                })

        then:
        result.success() == "another success"
    }

    def "map only failure to new one if it is a failure"() {
        when:
        def result = Result.failure("any failure")
                .map(new Function<String, String>() {
                    @Override
                    String apply(String success) { "another success" }
                }, new Function<String, String>() {
                    @Override
                    String apply(String failure) { "another failure" }
                })

        then:
        result.failure() == "another failure"
    }

    def "flat success only if it is a success"() {
        when:
        def result = Result.success("any success")
                .flatSuccess{Result.success("another success")}

        then:
        result.success() == "another success"
    }

    def "flat failure only if it is a failure"() {
        when:
        def result = Result.failure("any failure")
                .flatFailure{Result.failure("another failure")}

        then:
        result.failure() == "another failure"
    }

    private static class MutableValue {
        private String value

        private MutableValue(String value) {
            this.value = value
        }

        private static MutableValue of(String value) {
            return new MutableValue(value)
        }

        private MutableValue change(String newValue) {
            value = newValue
            return this
        }

        private String get() {
            return value
        }
    }
}
