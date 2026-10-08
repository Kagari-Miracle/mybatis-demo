package com.example;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.Employee;
import com.example.mapper.EmployeeMapperPlus;
import com.example.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * MyBatis-Plus 通用 CRUD 测试（实验一 任务4）
 *
 * 测试内容：
 *   1. BaseMapper 通用 CRUD（零 SQL：insert / selectById / updateById / deleteById）
 *   2. QueryWrapper 条件构造器（like / eq / ge / orderByDesc）
 *   3. LambdaQueryWrapper 条件构造器（方法引用，防字段名写错）
 *   4. selectCount 条件计数
 *   5. selectPage 分页查询
 */
public class EmployeeMapperPlusTest {

    private SqlSession session;
    private EmployeeMapperPlus mapper;

    @Before
    public void init() {
        resetEmpTable();
        session = MyBatisUtil.openSession(true);
        mapper = session.getMapper(EmployeeMapperPlus.class);
    }

    @After
    public void destroy() {
        if (session != null) session.close();
    }

    /** 重置 employee 表为 4 条初始数据 */
    private void resetEmpTable() {
        try (SqlSession s = MyBatisUtil.openSession(true);
             java.sql.Connection c = s.getConnection();
             java.sql.Statement st = c.createStatement()) {
            st.executeUpdate("DROP TABLE IF EXISTS employee");
            st.executeUpdate("CREATE TABLE employee (" +
                    "emp_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '员工编号'," +
                    "emp_name VARCHAR(50) NOT NULL COMMENT '姓名'," +
                    "gender CHAR(1) DEFAULT '男' COMMENT '性别'," +
                    "dept VARCHAR(50) COMMENT '部门'," +
                    "post VARCHAR(50) COMMENT '岗位'," +
                    "salary DECIMAL(10,2) COMMENT '薪资'," +
                    "hire_date DATE COMMENT '入职时间'," +
                    "status TINYINT DEFAULT 1 COMMENT '状态：1在职 0离职')");
            st.executeUpdate("INSERT INTO employee (emp_name, gender, dept, post, salary, hire_date, status) VALUES " +
                    "('张伟','男','研发部','Java工程师',12000.00,'2024-07-01',1)," +
                    "('李娜','女','研发部','前端工程师',10000.00,'2024-08-15',1)," +
                    "('王强','男','市场部','市场专员',8000.00,'2023-03-10',1)," +
                    "('赵敏','女','人事部','人事专员',7500.00,'2022-06-20',0)");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ========== 1. BaseMapper 通用 CRUD ==========

    @Test
    public void testInsertAndSelectById() {
        Employee emp = new Employee();
        emp.setEmpName("陈晨");
        emp.setGender("女");
        emp.setDept("研发部");
        emp.setPost("测试工程师");
        emp.setSalary(new BigDecimal("9000"));
        emp.setHireDate(new Date());
        emp.setStatus(1);

        int rows = mapper.insert(emp);
        assertEquals(1, rows);
        assertNotNull("insert 后主键应自动回填", emp.getEmpId());
        System.out.println("BaseMapper.insert 回填主键 empId = " + emp.getEmpId());

        Employee found = mapper.selectById(emp.getEmpId());
        assertNotNull(found);
        assertEquals("陈晨", found.getEmpName());
    }

    @Test
    public void testUpdateById() {
        Employee emp = mapper.selectById(1);
        assertNotNull(emp);
        emp.setSalary(new BigDecimal("13000"));

        int rows = mapper.updateById(emp);
        assertEquals(1, rows);

        Employee updated = mapper.selectById(1);
        assertEquals(0, new BigDecimal("13000").compareTo(updated.getSalary()));
    }

    @Test
    public void testDeleteById() {
        int rows = mapper.deleteById(4);
        assertEquals(1, rows);
        assertNull(mapper.selectById(4));
    }

    // ========== 2. QueryWrapper 条件构造器 ==========

    @Test
    public void testQueryWrapper() {
        // 多条件：姓名模糊 + 部门等于 + 薪资大于等于 + 按薪资降序
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        wrapper.like("emp_name", "张")
                .eq("dept", "研发部")
                .ge("salary", 10000)
                .orderByDesc("salary");

        List<Employee> list = mapper.selectList(wrapper);
        System.out.println("QueryWrapper 查询结果数: " + list.size());
        for (Employee e : list) {
            System.out.println("  " + e.getEmpName() + " - " + e.getDept() + " - " + e.getSalary());
        }
        assertFalse(list.isEmpty());
        assertTrue(list.stream().allMatch(e -> e.getDept().equals("研发部")));
    }

    // ========== 3. LambdaQueryWrapper 条件构造器 ==========

    @Test
    public void testLambdaQueryWrapper() {
        // Lambda 写法：用方法引用代替字符串列名，编译期检查，避免字段名写错
        LambdaQueryWrapper<Employee> lw = new LambdaQueryWrapper<>();
        lw.like(Employee::getEmpName, "李")
                .eq(Employee::getStatus, 1)
                .orderByDesc(Employee::getSalary);

        List<Employee> list = mapper.selectList(lw);
        System.out.println("LambdaQueryWrapper 查询结果数: " + list.size());
        for (Employee e : list) {
            System.out.println("  " + e.getEmpName() + " - " + e.getPost() + " - " + e.getSalary());
        }
        assertFalse(list.isEmpty());
    }

    // ========== 4. selectCount 条件计数 ==========

    @Test
    public void testSelectCount() {
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);

        Long count = mapper.selectCount(wrapper);
        System.out.println("在职员工数: " + count);
        assertEquals(Long.valueOf(3), count);
    }

    // ========== 5. selectPage 分页查询 ==========

    @Test
    public void testSelectPage() {
        // 第 1 页，每页 2 条
        Page<Employee> page = new Page<>(1, 2);
        Page<Employee> result = mapper.selectPage(page, null);

        System.out.println("分页查询结果:");
        System.out.println("  总记录数 = " + result.getTotal());
        System.out.println("  总页数   = " + result.getPages());
        System.out.println("  当前页   = " + result.getCurrent());
        System.out.println("  每页条数 = " + result.getSize());
        System.out.println("  本页数据:");
        for (Employee e : result.getRecords()) {
            System.out.println("    " + e.getEmpId() + ": " + e.getEmpName());
        }

        assertEquals(4L, result.getTotal());
        assertEquals(2L, result.getPages());
        assertEquals(2, result.getRecords().size());
    }
}
