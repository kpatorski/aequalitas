package com.sparrow

import spock.lang.Specification 

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
}
