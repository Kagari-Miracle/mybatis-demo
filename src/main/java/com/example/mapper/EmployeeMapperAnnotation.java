package com.example.mapper;

import com.example.entity.Employee;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 员工 Mapper 注解版（实验一：@Select/@Insert 等注解 + SQL 注入防御）
 *
 * 重点演示 #{} 与 ${} 的区别：
 *   - getByIdSafe   使用 #{}（预编译，防注入，推荐）
 *   - getByIdUnsafe 使用 ${}（字符串拼接，有注入风险，仅演示）
 */
public interface EmployeeMapperAnnotation {

    // ========== 注解版基础 CRUD ==========

    @Select("SELECT emp_id, emp_name, gender, dept, post, salary, hire_date, status " +
            "FROM employee WHERE emp_id = #{empId}")
    @Results({
            @Result(property = "empId",    column = "emp_id", id = true),
            @Result(property = "empName",  column = "emp_name"),
            @Result(property = "gender",   column = "gender"),
            @Result(property = "dept",     column = "dept"),
            @Result(property = "post",     column = "post"),
            @Result(property = "salary",   column = "salary"),
            @Result(property = "hireDate", column = "hire_date"),
            @Result(property = "status",   column = "status")
    })
    Employee getById(Integer empId);

    @Select("SELECT emp_id, emp_name, gender, dept, post, salary, hire_date, status FROM employee")
    @ResultMap("com.example.mapper.EmployeeMapper.empResultMap")
    List<Employee> listAll();

    @Insert("INSERT INTO employee (emp_name, gender, dept, post, salary, hire_date, status) " +
            "VALUES (#{empName}, #{gender}, #{dept}, #{post}, #{salary}, #{hireDate}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "empId")
    int save(Employee employee);

    @Update("UPDATE employee SET emp_name=#{empName}, gender=#{gender}, dept=#{dept}, " +
            "post=#{post}, salary=#{salary}, status=#{status} WHERE emp_id=#{empId}")
    int modify(Employee employee);

    @Delete("DELETE FROM employee WHERE emp_id = #{empId}")
    int removeById(Integer empId);

    // ========== SQL 注入防御演示 ==========

    /**
     * 安全查询：使用 #{} 预编译参数，用户输入会被当作字符串字面量处理，
     * 即使传入 "1 OR 1=1" 也只会被当作 emp_name 的值，不会改变 SQL 结构。
     */
    @Select("SELECT emp_id, emp_name, gender, dept FROM employee WHERE emp_name = #{name}")
    List<Employee> findByNameSafe(@Param("name") String name);

    /**
     * 危险查询：使用 ${} 直接拼接字符串，用户输入会直接拼入 SQL，
     * 传入 " ' OR '1'='1 " 等内容可导致 SQL 注入。
     * 仅用于教学演示，生产环境严禁使用！
     */
    @Select("SELECT emp_id, emp_name, gender, dept FROM employee WHERE emp_name = ${name}")
    List<Employee> findByNameUnsafe(@Param("name") String name);
}
