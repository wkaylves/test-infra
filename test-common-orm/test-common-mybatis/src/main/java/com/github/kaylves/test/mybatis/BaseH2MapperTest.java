package com.github.kaylves.test.mybatis;

import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;

import java.lang.annotation.*;

/**
 * MyBatis slice test with H2 in-memory database.
 * Uses @MybatisTest to load only MyBatis beans (SqlSessionFactory, mappers, DataSource).
 * Fast startup, no Spring Boot auto-configuration overhead.
 *
 * <p>Usage:
 * <pre>
 * &#64;BaseH2MapperTest
 * &#64;Sql("classpath:schema.sql")
 * class MyMapperTest {
 *     &#64;Autowired
 *     private MyMapper mapper;
 * }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@MybatisTest
public @interface BaseH2MapperTest {
}
