package com.github.kaylves.test.infra.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    List<Map<String, Object>> selectAll();

    Map<String, Object> selectById(@Param("id") Long id);

    int insert(@Param("name") String name, @Param("email") String email);

    int deleteById(@Param("id") Long id);
}
