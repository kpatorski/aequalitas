package com.sparrow.control

import spock.lang.Specification

import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

class ResultTest extends Specification {

    def "should create result of success"() {
        given: "any success"
        String success = "any success"

        when:
        def result = Result.success(success)

        then:
        result.success() == success
        result.isSuccess()
    }

    def "should create result of failure"() {
        given: "any failure"
        String failure = "any failure"

        when:
        def result = Result.failure(failure)

        then:
        result.failure() == failure
        result.isFailure()
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
                .map(new Supplier<Result<String, Object>>() {
                    @Override
                    Result<String, Object> get() {
                        Result.success("another success")
                    }
                })

        then:
        result.success() == "another success"
    }

    def "should map to new result mapping previous success only if current is success"() {
        given:
        def successA = "success A"
        def successB = 100

        when:
        def result = Result.success(successA)
                .map(new Function<String, Result<Integer, Object>>() {
                    @Override
                    Result<Integer, Object> apply(String s) {
                        Result.success(successB)
                    }
                })

        then:
        result.success() == successB
    }

    def "should not map to new result if current is failure"() {
        when:
        def result = Result.failure("any failure")
                .map(new Function<Object, Result<String, String>>() {
                    @Override
                    Result<String, String> apply(Object o) {
                        Result.success("any success")
                    }
                })

        then:
        result.failure() == "any failure"
    }

    def "should map to new result type mapping previous success only if current is success"() {
        given:
        def successA = "success A"
        def successB = "success B"
        def successC = "success C"

        when:
        def result = Result.success(successA)
                .map(() -> Result.success(successB), (oldSuccess, newSuccess) -> oldSuccess + "," + newSuccess)
                .map(() -> Result.success(successC), (oldSuccess, newSuccess) -> oldSuccess + "," + newSuccess)

        then:
        result.success() == "success A,success B,success C"
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
