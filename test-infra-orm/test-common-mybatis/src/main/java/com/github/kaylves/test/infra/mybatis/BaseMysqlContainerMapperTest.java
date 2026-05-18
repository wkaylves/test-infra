package com.github.kaylves.test.infra.mybatis;

import com.github.kaylves.test.infra.storage.rdbms.SharedMySQLContainer;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

/**
 * MyBatis slice test with shared Testcontainers MySQL.
 * Uses {@link SharedMySQLContainer} singleton — container starts once, shared across all tests.
 *
 * <p>Usage:
 * <pre>
 * class MyMapperTest extends BaseMysqlContainerMapperTest {
 *     &#64;Autowired
 *     private MyMapper mapper;
 * }
 * </pre>
 */
@MybatisTest
public abstract class BaseMysqlContainerMapperTest {

    private static final MySQLContainer<?> MYSQL = SharedMySQLContainer.getInstance();

    static {
        MYSQL.start();
    }

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.datasource.driver-class-name", MYSQL::getDriverClassName);
    }
}
