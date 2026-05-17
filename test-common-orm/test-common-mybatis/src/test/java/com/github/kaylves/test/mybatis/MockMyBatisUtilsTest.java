package com.github.kaylves.test.mybatis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class MockMyBatisUtilsTest {

    interface SampleMapper {
        String selectById(Long id);
        List<String> selectAll();
        int insert(String name);
    }

    @Test
    @DisplayName("mockMapper should return a non-null mock")
    void shouldReturnNonNullMock() {
        SampleMapper mapper = MockMyBatisUtils.mockMapper(SampleMapper.class);
        assertThat(mapper).isNotNull();
    }

    @Test
    @DisplayName("mockMapper should return mock of correct type")
    void shouldReturnCorrectType() {
        SampleMapper mapper = MockMyBatisUtils.mockMapper(SampleMapper.class);
        assertThat(mapper).isInstanceOf(SampleMapper.class);
    }

    @Test
    @DisplayName("mockMapper should allow stubbing")
    void shouldAllowStubbing() {
        SampleMapper mapper = MockMyBatisUtils.mockMapper(SampleMapper.class);
        when(mapper.selectById(1L)).thenReturn("Alice");

        assertThat(mapper.selectById(1L)).isEqualTo("Alice");
    }

    @Test
    @DisplayName("mockMapper should return null for unstubbed methods")
    void shouldReturnNullForUnstubbed() {
        SampleMapper mapper = MockMyBatisUtils.mockMapper(SampleMapper.class);
        assertThat(mapper.selectById(1L)).isNull();
    }

    @Test
    @DisplayName("mockMapper should allow stubbing selectAll")
    void shouldAllowStubbingList() {
        SampleMapper mapper = MockMyBatisUtils.mockMapper(SampleMapper.class);
        when(mapper.selectAll()).thenReturn(java.util.Arrays.asList("Alice", "Bob"));

        List<String> result = mapper.selectAll();
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo("Alice");
    }
}
