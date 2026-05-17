package com.github.kaylves.test.storage.rdbms

import spock.lang.Specification

import static org.mockito.Mockito.when

class MockMyBatisUtilsTest extends Specification {

    interface SampleMapper {
        String selectById(Long id)
        List<String> selectAll()
        int insert(String name)
        int updateById(Long id, String name)
        int deleteById(Long id)
    }

    def "mockMapper should return a non-null mock"() {
        when:
        def mapper = MockMyBatisUtils.mockMapper(SampleMapper)

        then:
        mapper != null
    }

    def "mockMapper should return mock of correct type"() {
        when:
        def mapper = MockMyBatisUtils.mockMapper(SampleMapper)

        then:
        mapper instanceof SampleMapper
    }

    def "mockMapper mock should allow stubbing selectById"() {
        given:
        def mapper = MockMyBatisUtils.mockMapper(SampleMapper)
        when(mapper.selectById(1L)).thenReturn("Alice")

        when:
        def result = mapper.selectById(1L)

        then:
        result == "Alice"
    }

    def "mockMapper mock should return null for unstubbed methods"() {
        given:
        def mapper = MockMyBatisUtils.mockMapper(SampleMapper)

        when:
        def result = mapper.selectById(1L)

        then:
        result == null
    }

    def "mockMapper mock should allow stubbing selectAll"() {
        given:
        def mapper = MockMyBatisUtils.mockMapper(SampleMapper)
        when(mapper.selectAll()).thenReturn(["Alice", "Bob"])

        when:
        def result = mapper.selectAll()

        then:
        result.size() == 2
        result[0] == "Alice"
        result[1] == "Bob"
    }

    def "mockMapper mock should allow stubbing insert"() {
        given:
        def mapper = MockMyBatisUtils.mockMapper(SampleMapper)
        when(mapper.insert("Charlie")).thenReturn(1)

        when:
        def result = mapper.insert("Charlie")

        then:
        result == 1
    }

    def "mockMapper mock should allow stubbing updateById"() {
        given:
        def mapper = MockMyBatisUtils.mockMapper(SampleMapper)
        when(mapper.updateById(1L, "Updated")).thenReturn(1)

        when:
        def result = mapper.updateById(1L, "Updated")

        then:
        result == 1
    }

    def "mockMapper mock should allow stubbing deleteById"() {
        given:
        def mapper = MockMyBatisUtils.mockMapper(SampleMapper)
        when(mapper.deleteById(1L)).thenReturn(1)

        when:
        def result = mapper.deleteById(1L)

        then:
        result == 1
    }
}
