package com.github.kaylves.test.infra.spring.mvc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class BaseServiceTestTest extends BaseServiceTest {

    @Mock
    private SampleRepository sampleRepository;

    @InjectMocks
    private SampleService sampleService;

    @Test
    @DisplayName("BaseServiceTest should initialize Mockito mocks")
    void shouldInitializeMocks() {
        when(sampleRepository.findName()).thenReturn("test");
        String result = sampleService.getName();
        assertThat(result).isEqualTo("test");
    }

    static class SampleRepository {
        String findName() {
            return "real";
        }
    }

    static class SampleService {
        private final SampleRepository repository;

        SampleService(SampleRepository repository) {
            this.repository = repository;
        }

        String getName() {
            return repository.findName();
        }
    }
}
