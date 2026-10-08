package com.example.mapper;

import com.example.entity.Employee;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 员工 Mapper 接口（实验一：MyBatis 基础 CRUD + 动态 SQL）
 *
 * 方法命名说明（与常规 select/insert/update/delete 区分，避免与指导书重合）：
 *   listAll         查询全部
 *   getById         按主键查询
 *   save            新增（回填自增主键）
 *   modify          全字段更新
 *   removeById      按主键删除
 *   searchByCondition  多条件动态查询（if + where）
 *   patchById       选择性更新（set 标签，只更新非空字段）
 *   removeBatch     批量删除（foreach）
 *   saveBatch       批量新增（foreach）
 *   pickOne         分支选择查询（choose/when/otherwise）
 */
public interface EmployeeMapper {

    // ========== 基础 CRUD ==========

    List<Employee> listAll();

    Employee getById(Integer empId);

    int save(Employee employee);

    int modify(Employee employee);

    int removeById(Integer empId);

    // ========== 动态 SQL ==========

    /** if + where 多条件组合查询 */
    List<Employee> searchByCondition(Employee employee);

    /** set 标签选择性更新 */
    int patchById(Employee employee);

    /** foreach 批量删除 */
    int removeBatch(@Param("ids") List<Integer> ids);

    /** foreach 批量新增 */
    int saveBatch(@Param("list") List<Employee> employees);

    /** choose/when/otherwise 分支选择 */
    List<Employee> pickOne(Employee employee);
}
