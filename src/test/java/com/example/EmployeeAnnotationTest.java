package com.example;

import com.example.entity.Employee;
import com.example.mapper.EmployeeMapperAnnotation;
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
 * 注解版 EmployeeMapper 测试 + SQL 注入防御演示
 *
 * 重点验证：
 *   1. 注解方式 CRUD 功能正常
 *   2. #{} 安全查询能防注入
 *   3. ${} 拼接查询存在注入风险（演示用）
 */
public class EmployeeAnnotationTest {

    private SqlSession session;
    private EmployeeMapperAnnotation mapper;

    @Before
    public void init() {
        ensureTableAndData();
        session = MyBatisUtil.openSession(true);
        mapper = session.getMapper(EmployeeMapperAnnotation.class);
    }

    @After
    public void destroy() {
        if (session != null) {
            session.close();
        }
    }

    /** 确保 employee 表存在且有基础数据 */
    private void ensureTableAndData() {
        try (SqlSession s = MyBatisUtil.openSession(true);
             java.sql.Connection c = s.getConnection();
             java.sql.Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS employee (" +
                    "emp_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "emp_name VARCHAR(50) NOT NULL," +
                    "gender CHAR(1) DEFAULT '男'," +
                    "dept VARCHAR(50), post VARCHAR(50)," +
                    "salary DECIMAL(10,2), hire_date DATE," +
                    "status TINYINT DEFAULT 1)");
            st.executeUpdate("DELETE FROM employee");
            st.executeUpdate("ALTER TABLE employee AUTO_INCREMENT = 1");
            st.executeUpdate("INSERT INTO employee (emp_name, gender, dept, post, salary, hire_date, status) " +
                    "VALUES ('张伟','男','研发部','Java工程师',12000.00,'2024-07-01',1)," +
                    "('李娜','女','研发部','前端工程师',10000.00,'2024-08-15',1)," +
                    "('王强','男','市场部','市场专员',8000.00,'2023-03-10',1)," +
                    "('赵敏','女','人事部','人事专员',7500.00,'2022-06-20',0)");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetById() {
        Employee emp = mapper.getById(1);
        assertNotNull(emp);
        assertEquals("张伟", emp.getEmpName());
    }

    @Test
    public void testListAll() {
        List<Employee> list = mapper.listAll();
        assertTrue(list.size() >= 4);
    }

    @Test
    public void testSave() {
        Employee emp = new Employee("测试员工", "男", "测试部", "测试岗",
                new BigDecimal("5000"), new Date(), 1);
        int rows = mapper.save(emp);
        assertEquals(1, rows);
        assertNotNull(emp.getEmpId());
        // 清理
        mapper.removeById(emp.getEmpId());
    }

    @Test
    public void testModify() {
        Employee emp = mapper.getById(1);
        emp.setPost("架构师");
        int rows = mapper.modify(emp);
        assertEquals(1, rows);
        assertEquals("架构师", mapper.getById(1).getPost());
    }

    @Test
    public void testRemoveById() {
        // 先插入一条再删除，避免影响其他测试
        Employee emp = new Employee("待删除", "男", "测试部", "测试岗",
                new BigDecimal("5000"), new Date(), 1);
        mapper.save(emp);
        int rows = mapper.removeById(emp.getEmpId());
        assertEquals(1, rows);
    }

    // ==================== SQL 注入防御演示 ====================

    /**
     * 安全查询：#{} 预编译
     * 传入注入字符串 " ' OR '1'='1 " 时，只会被当作 emp_name 的值，
     * 查询结果为空（没有叫这个名字的员工），不会泄露全部数据。
     */
    @Test
    public void testSafeQuery() {
        String injection = "'' OR 1=1 #";
        List<Employee> result = mapper.findByNameSafe(injection);
        // 安全：不会返回全部数据
        assertTrue("安全查询不应被注入", result.isEmpty());
        System.out.println("[安全查询 #{}] 注入输入返回 " + result.size() + " 条（预期0）");
    }

    /**
     * 危险查询：${} 字符串拼接（仅演示，不要在生产使用）
     * 注入字符串 "' OR 1=1 --" 拼入 SQL 后，WHERE 条件变为永真，
     * 返回全部员工数据——这就是 SQL 注入。
     */
    @Test
    public void testUnsafeQuery() {
        String injection = "'' OR 1=1 #";
        List<Employee> result = mapper.findByNameUnsafe(injection);
        // 危险：注入成功，返回了全部数据
        System.out.println("[危险查询 ${}] 注入输入返回 " + result.size() + " 条（被注入！）");
        assertTrue("${} 查询被注入，返回了数据", result.size() > 0);
    }
}
