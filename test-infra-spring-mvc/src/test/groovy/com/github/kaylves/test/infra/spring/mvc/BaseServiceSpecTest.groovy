package com.github.kaylves.test.infra.spring.mvc

class BaseServiceSpecTest extends BaseServiceSpec {

    private SampleRepository sampleRepository = Mock()
    private SampleService sampleService

    def setup() {
        sampleService = new SampleService(sampleRepository)
    }

    def "should initialize Mockito mocks"() {
        given:
        sampleRepository.findName() >> "test"

        expect:
        sampleService.getName() == "test"
    }

    static class SampleRepository {
        String findName() { "real" }
    }

    static class SampleService {
        private final SampleRepository repository
        SampleService(SampleRepository repository) { this.repository = repository }
        String getName() { repository.findName() }
    }
}
