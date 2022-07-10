package com.sparrow

import spock.lang.Specification

import java.util.function.Consumer

class ResultTest extends Specification {

    def "should create result of success"() {
        given: "any success"
        String success = "any success"

        when:
        def result = Result.success(success)

        then:
        result.success().get() == success
        !result.failure().isPresent()
    }

    def "should create result of failure"() {
        given: "any failure"
        String failure = "any failure"

        when:
        def result = Result.failure(failure)

        then:
        result.failure().get() == failure
        !result.success().isPresent()
    }

    def "should return success transformed to value"() {
        given: "value"
        String value = "any value"

        when:
        def result = Result.success(value)

        then:
        result.get((val) -> "success", (val) -> "failure") == "success"
    }

    def "should return failure transformed to value"() {
        given: "value"
        String value = "any value"

        when:
        def result = Result.failure(value)

        then:
        result.get((val) -> "success", (val) -> "failure") == "failure"
    }

    def "should determine if success"() {
        given: "any success"
        String success = "any success"

        when:
        def result = Result.success(success)

        then:
        result.isSuccess()
    }

    def "should execute consumer if success"() {
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

    def "should execute consumer if failure"() {
        given:
        def anyFailure = MutableValue.of("any failure")
        def result = Result.failure(anyFailure)

        when:
        result.ifFailure((failure) -> failure.change("failure indeed"))

        then:
        anyFailure.get() == "failure indeed"
    }

    def "should map to new result ignoring current result and if current is success"() {
        when:
        def result = Result.success("any success")
                .map(() -> Result.success("another success"))

        then:
        result.success().get() == "another success"
    }

    def "should not map to new result if current is failure"() {
        when:
        def result = Result.failure("any failure")
                .map(() -> Result.success("any success"))

        then:
        result.failure().get() == "any failure"
    }

    def "should map to new result mapping previous success only if current is success"() {
        given:
        def successA = "success A"
        def successB = "success B"
        def successC = "success C"

        when:
        def result = Result.success(successA)
                .map(() -> Result.success(successB), (oldSuccess, newSuccess) -> oldSuccess + "," + newSuccess)
                .map(() -> Result.success(successC), (oldSuccess, newSuccess) -> oldSuccess + "," + newSuccess)

        then:
        result.success().get() == "success A,success B,success C"
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
