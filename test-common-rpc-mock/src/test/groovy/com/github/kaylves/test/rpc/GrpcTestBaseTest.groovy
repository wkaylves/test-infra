package com.github.kaylves.test.rpc

import spock.lang.Specification

import static org.mockito.Mockito.when

class GrpcTestBaseTest extends Specification {

    interface SampleGrpcStub {
        String sendRequest(String input)
        int getStatus()
    }

    def testBase = new GrpcTestBase()

    def "mockGrpcStub should return non-null mock"() {
        when:
        def stub = testBase.mockGrpcStub(SampleGrpcStub)

        then:
        stub != null
    }

    def "mockGrpcStub should return mock of correct type"() {
        when:
        def stub = testBase.mockGrpcStub(SampleGrpcStub)

        then:
        stub instanceof SampleGrpcStub
    }

    def "mockGrpcStub should allow stubbing sendRequest"() {
        given:
        def stub = testBase.mockGrpcStub(SampleGrpcStub)
        when(stub.sendRequest("hello")).thenReturn("response")

        when:
        def result = stub.sendRequest("hello")

        then:
        result == "response"
    }

    def "mockGrpcStub should return null for unstubbed methods"() {
        given:
        def stub = testBase.mockGrpcStub(SampleGrpcStub)

        when:
        def result = stub.sendRequest("hello")

        then:
        result == null
    }

    def "mockGrpcStub should allow stubbing getStatus"() {
        given:
        def stub = testBase.mockGrpcStub(SampleGrpcStub)
        when(stub.getStatus()).thenReturn(200)

        when:
        def result = stub.getStatus()

        then:
        result == 200
    }
}
