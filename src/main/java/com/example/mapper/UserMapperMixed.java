package com.example.mapper;

import com.example.entity.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 混合开发 Mapper 接口
 *
 * 原则：
 *   - 简单 SQL（单表 CRUD、简单条件查询）→ 用注解，代码简洁
 *   - 复杂 SQL（动态 SQL、多表关联、复杂条件）→ 用 XML，便于维护
 *
 * 本接口中：
 *   - findById：简单查询，用 @Select 注解
 *   - findByCondition：动态条件查询，用 XML 配置（见 UserMapperMixed.xml）
 */
public interface UserMapperMixed {

    /**
     * 简单查询：使用注解（@Select）
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Integer id);

    /**
     * 复杂查询：使用 XML 配置
     * 根据 User 对象的非空字段动态拼接查询条件
     * 具体 SQL 写在 UserMapperMixed.xml 的 <select id="findByCondition"> 里
     */
    List<User> findByCondition(User user);
}
