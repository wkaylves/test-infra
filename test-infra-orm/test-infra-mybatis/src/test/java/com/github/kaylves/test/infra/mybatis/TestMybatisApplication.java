package com.github.kaylves.test.infra.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringBootConfiguration;

@SpringBootConfiguration
@MapperScan("com.github.kaylves.test.infra.mybatis.mapper")
class TestMybatisApplication {
}
