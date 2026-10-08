package com.example;

import com.example.entity.Employee;
import com.example.mapper.EmployeeMapper;
import com.example.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 员工 Mapper 测试类（实验一）
 *
 * 采用 @Before / @After 标准化结构：
 *   - @Before init()     每个测试前重置 emp 表数据 + 开启 SqlSession
 *   - @After  destroy()  每个测试后关闭 SqlSession
 *
 * 所有增删改操作默认 autoCommit=true，无需手动 commit。
 */
public class EmployeeMapperTest {

    private SqlSession session;
    private EmployeeMapper mapper;

    @Before
    public void init() {
        resetEmpTable();
        session = MyBatisUtil.openSession(true);
        mapper = session.getMapper(EmployeeMapper.class);
    }

    @After
    public void destroy() {
        if (session != null) {
            session.close();
        }
    }

    /** 重置 emp 表为 4 条初始数据 */
    private void resetEmpTable() {
        try (SqlSession s = MyBatisUtil.openSession(true);
             java.sql.Connection c = s.getConnection();
             java.sql.Statement st = c.createStatement()) {
            // 确保表存在
            st.executeUpdate("CREATE TABLE IF NOT EXISTS employee (" +
                    "emp_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '员工编号'," +
                    "emp_name VARCHAR(50) NOT NULL COMMENT '姓名'," +
                    "gender CHAR(1) DEFAULT '男' COMMENT '性别'," +
                    "dept VARCHAR(50) COMMENT '部门'," +
                    "post VARCHAR(50) COMMENT '岗位'," +
                    "salary DECIMAL(10,2) COMMENT '薪资'," +
                    "hire_date DATE COMMENT '入职时间'," +
                    "status TINYINT DEFAULT 1 COMMENT '状态：1在职 0离职')");
            st.executeUpdate("DELETE FROM employee");
            st.executeUpdate("ALTER TABLE employee AUTO_INCREMENT = 1");
            st.executeUpdate("INSERT INTO employee (emp_name, gender, dept, post, salary, hire_date, status) " +
                    "VALUES ('张伟','男','研发部','Java工程师',12000.00,'2024-07-01',1)");
            st.executeUpdate("INSERT INTO employee (emp_name, gender, dept, post, salary, hire_date, status) " +
                    "VALUES ('李娜','女','研发部','前端工程师',10000.00,'2024-08-15',1)");
            st.executeUpdate("INSERT INTO employee (emp_name, gender, dept, post, salary, hire_date, status) " +
                    "VALUES ('王强','男','市场部','市场专员',8000.00,'2023-03-10',1)");
            st.executeUpdate("INSERT INTO employee (emp_name, gender, dept, post, salary, hire_date, status) " +
                    "VALUES ('赵敏','女','人事部','人事专员',7500.00,'2022-06-20',0)");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ==================== 基础 CRUD 测试 ====================

    @Test
    public void testListAll() {
        List<Employee> list = mapper.listAll();
        assertEquals(4, list.size());
        System.out.println("查询全部：" + list);
    }

    @Test
    public void testGetById() {
        Employee emp = mapper.getById(1);
        assertNotNull(emp);
        assertEquals("张伟", emp.getEmpName());
        System.out.println("按ID查询：" + emp);
    }

    @Test
    public void testSave() {
        Employee emp = new Employee("陈晨", "女", "研发部", "测试工程师",
                new BigDecimal("9000"), new Date(), 1);
        int rows = mapper.save(emp);
        assertEquals(1, rows);
        assertNotNull(emp.getEmpId());
        System.out.println("新增成功，回填主键 empId=" + emp.getEmpId());
    }

    @Test
    public void testModify() {
        Employee emp = mapper.getById(1);
        emp.setSalary(new BigDecimal("13000"));
        int rows = mapper.modify(emp);
        assertEquals(1, rows);
        Employee updated = mapper.getById(1);
        assertEquals(0, new BigDecimal("13000").compareTo(updated.getSalary()));
        System.out.println("更新后薪资：" + updated.getSalary());
    }

    @Test
    public void testRemoveById() {
        int rows = mapper.removeById(4);
        assertEquals(1, rows);
        assertNull(mapper.getById(4));
        System.out.println("删除 emp_id=4 成功");
    }

    // ==================== 动态 SQL 测试 ====================

    @Test
    public void testSearchByCondition() {
        Employee query = new Employee();
        query.setDept("研发部");
        query.setStatus(1);
        List<Employee> list = mapper.searchByCondition(query);
        assertEquals(2, list.size());
        System.out.println("研发部在职员工：" + list);
    }

    @Test
    public void testPatchById() {
        Employee patch = new Employee();
        patch.setEmpId(1);
        patch.setPost("高级Java工程师");
        int rows = mapper.patchById(patch);
        assertEquals(1, rows);
        Employee updated = mapper.getById(1);
        assertEquals("高级Java工程师", updated.getPost());
        // 其他字段应保持不变
        assertEquals("张伟", updated.getEmpName());
        System.out.println("选择性更新后岗位：" + updated.getPost());
    }

    @Test
    public void testRemoveBatch() {
        int rows = mapper.removeBatch(Arrays.asList(3, 4));
        assertEquals(2, rows);
        assertEquals(2, mapper.listAll().size());
        System.out.println("批量删除 3,4 成功");
    }

    @Test
    public void testSaveBatch() {
        List<Employee> batch = Arrays.asList(
                new Employee("孙磊", "男", "财务部", "会计", new BigDecimal("8500"), new Date(), 1),
                new Employee("周婷", "女", "研发部", "运维工程师", new BigDecimal("11000"), new Date(), 1)
        );
        int rows = mapper.saveBatch(batch);
        assertEquals(2, rows);
        assertEquals(6, mapper.listAll().size());
        System.out.println("批量新增 2 条成功");
    }

    @Test
    public void testPickOne() {
        // 有 empId 时按 ID 查
        Employee q1 = new Employee();
        q1.setEmpId(2);
        List<Employee> r1 = mapper.pickOne(q1);
        assertEquals(1, r1.size());
        assertEquals("李娜", r1.get(0).getEmpName());

        // 无 empId 但有 empName 时按姓名查
        Employee q2 = new Employee();
        q2.setEmpName("王强");
        List<Employee> r2 = mapper.pickOne(q2);
        assertEquals(1, r2.size());
        assertEquals("市场部", r2.get(0).getDept());

        // 都没有时走 otherwise：查全部在职
        Employee q3 = new Employee();
        List<Employee> r3 = mapper.pickOne(q3);
        assertEquals(3, r3.size());
        System.out.println("choose 分支查询测试通过");
    }
}
