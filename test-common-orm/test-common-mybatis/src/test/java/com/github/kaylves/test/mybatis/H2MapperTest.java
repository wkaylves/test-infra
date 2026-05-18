package com.github.kaylves.test.mybatis;

import com.github.kaylves.test.mybatis.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@BaseH2MapperTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
@Sql({"classpath:schema.sql", "classpath:data.sql"})
class H2MapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("selectAll should return seeded data")
    void selectAll() {
        List<Map<String, Object>> users = userMapper.selectAll();
        assertThat(users).hasSize(2);
    }

    @Test
    @DisplayName("selectById should return single user")
    void selectById() {
        List<Map<String, Object>> users = userMapper.selectAll();
        Long id = ((Number) users.get(0).get("ID")).longValue();
        Map<String, Object> user = userMapper.selectById(id);
        assertThat(user).isNotNull();
    }

    @Test
    @DisplayName("insert should add a row")
    void insert() {
        int before = userMapper.selectAll().size();
        userMapper.insert("charlie", "charlie@test.com");
        assertThat(userMapper.selectAll()).hasSize(before + 1);
    }
}
