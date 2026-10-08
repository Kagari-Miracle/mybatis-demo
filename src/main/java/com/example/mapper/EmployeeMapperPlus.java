package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Employee;

/**
 * MyBatis-Plus 版员工 Mapper（实验一 任务4）
 *
 * 继承 BaseMapper<Employee> 后自动获得以下通用方法（零 SQL）：
 *   - insert(entity)              插入一条记录
 *   - deleteById(id)              根据主键删除
 *   - updateById(entity)          根据主键更新
 *   - selectById(id)              根据主键查询
 *   - selectList(wrapper)         条件查询列表
 *   - selectPage(page, wrapper)   分页查询
 *   - selectCount(wrapper)        条件计数
 *   - ... 等十余个通用 CRUD 方法
 *
 * 与 EmployeeMapper（XML 版）并存：BaseMapper 提供通用方法，
 * 复杂 SQL 仍可通过 XML 或注解自定义。
 */
public interface EmployeeMapperPlus extends BaseMapper<Employee> {
    // 无需写任何方法，BaseMapper 已提供全部通用 CRUD
}
