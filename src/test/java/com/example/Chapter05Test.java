package com.example;

import com.example.entity.Dept;
import com.example.entity.Emp;
import com.example.entity.Skill;
import com.example.mapper.Chapter05DeptMapper;
import com.example.mapper.Chapter05EmpMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

/**
 * 第05章测试类：MyBatis 关联映射（一对一 / 多对一 / 一对多 / 多对多）
 *
 * 覆盖：
 *   1. 一对一/多对一：emp -> Dept（XML 嵌套结果 + 注解 @One 嵌套 select）
 *   2. 一对多：dept -> List<Emp>（XML 嵌套结果 + 注解 @Many 嵌套 select）
 *   3. 多对多：emp -> List<Skill>（XML 两次 JOIN + 注解 @Many 中间表查询）
 *
 * @Before 会自动创建 skill + emp_skill 表并插入测试数据，无需手动执行 SQL。
 */
public class Chapter05Test {

    private InputStream is;
    private SqlSession session;
    private Chapter05EmpMapper empMapper;
    private Chapter05DeptMapper deptMapper;

    @Before
    public void init() throws IOException {
        // 初始化 skill + emp_skill 表（create if not exists + 清空 + 插入测试数据）
        initSkillTables();

        is = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
        session = factory.openSession(true);
        empMapper = session.getMapper(Chapter05EmpMapper.class);
        deptMapper = session.getMapper(Chapter05DeptMapper.class);
    }

    @After
    public void destroy() throws IOException {
        if (session != null) session.close();
        if (is != null) is.close();
    }

    /**
     * 初始化 skill + emp_skill 表：建表（如不存在）+ 插入测试数据
     * 注意：不重置 emp/dept 表，它们保持原有 Scott 样本数据。
     */
    private void initSkillTables() {
        String url = "jdbc:mysql://localhost:3306/mybatis_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        try (Connection c = DriverManager.getConnection(url, "root", "wyyxhxmy0416");
             Statement s = c.createStatement()) {

            // 建表（IF NOT EXISTS）
            s.executeUpdate(
                "CREATE TABLE IF NOT EXISTS skill (" +
                "  id INT PRIMARY KEY AUTO_INCREMENT," +
                "  name VARCHAR(50) NOT NULL," +
                "  description VARCHAR(255)" +
                ")"
            );
            s.executeUpdate(
                "CREATE TABLE IF NOT EXISTS emp_skill (" +
                "  empno INT NOT NULL," +
                "  skill_id INT NOT NULL," +
                "  PRIMARY KEY (empno, skill_id)," +
                "  FOREIGN KEY (empno) REFERENCES emp(empno)," +
                "  FOREIGN KEY (skill_id) REFERENCES skill(id)" +
                ")"
            );

            // 清空旧数据（先中间表，再技能表）
            s.executeUpdate("DELETE FROM emp_skill");
            s.executeUpdate("DELETE FROM skill");
            s.executeUpdate("ALTER TABLE skill AUTO_INCREMENT = 1");

            // 插入技能数据
            s.executeUpdate("INSERT INTO skill(name, description) VALUES('Java','后端开发')");
            s.executeUpdate("INSERT INTO skill(name, description) VALUES('MySQL','数据库')");
            s.executeUpdate("INSERT INTO skill(name, description) VALUES('Spring','框架')");
            s.executeUpdate("INSERT INTO skill(name, description) VALUES('Vue','前端开发')");
            s.executeUpdate("INSERT INTO skill(name, description) VALUES('Docker','容器化')");

            // 插入员工-技能关联数据（empno 对应 emp 表的实际主键）
            // JONES(4) 掌握 Java + MySQL
            s.executeUpdate("INSERT INTO emp_skill(empno, skill_id) VALUES(4, 1)");
            s.executeUpdate("INSERT INTO emp_skill(empno, skill_id) VALUES(4, 2)");
            // SCOTT(8) 掌握 Java
            s.executeUpdate("INSERT INTO emp_skill(empno, skill_id) VALUES(8, 1)");
            // FORD(13) 掌握 Java + Spring
            s.executeUpdate("INSERT INTO emp_skill(empno, skill_id) VALUES(13, 1)");
            s.executeUpdate("INSERT INTO emp_skill(empno, skill_id) VALUES(13, 3)");
            // SMITH(1) 掌握 MySQL
            s.executeUpdate("INSERT INTO emp_skill(empno, skill_id) VALUES(1, 2)");

        } catch (Exception e) {
            throw new RuntimeException("初始化 skill 表失败: " + e.getMessage(), e);
        }
    }

    // ==================== 一对一 / 多对一 ====================

    @Test
    public void testOne2oneByXml() {
        System.out.println("========== 一对一（XML方式）==========");
        System.out.println("知识点：<association> + 嵌套结果映射（一条 JOIN）");

        Emp emp = empMapper.one2oneByXml(1);
        if (emp != null) {
            System.out.println("员工：" + emp.getEname() + " (编号:" + emp.getEmpno() + ")");
            if (emp.getDept() != null) {
                System.out.println("部门：" + emp.getDept().getDname());
                System.out.println("位置：" + emp.getDept().getLoc());
            }
        }
    }

    @Test
    public void testOne2oneByAnn() {
        System.out.println("========== 一对一（注解方式）==========");
        System.out.println("知识点：@Results + @One(select = ...) 嵌套 select");

        Emp emp = empMapper.one2oneByAnn(2);
        if (emp != null) {
            System.out.println("员工：" + emp.getEname() + " (编号:" + emp.getEmpno() + ")");
            if (emp.getDept() != null) {
                System.out.println("部门：" + emp.getDept().getDname());
                System.out.println("位置：" + emp.getDept().getLoc());
            }
        }
    }

    @Test
    public void testMany2oneByXml() {
        System.out.println("========== 多对一（XML方式）==========");
        System.out.println("知识点：复用 empWithDeptMap，一次查一批员工");

        List<Emp> list = empMapper.many2oneByXml();
        System.out.println("员工总数：" + (list != null ? list.size() : 0));
        if (list != null) {
            for (Emp emp : list) {
                String deptName = (emp.getDept() != null) ? emp.getDept().getDname() : "无";
                System.out.println("  " + emp.getEmpno() + " - " + emp.getEname() + " -> " + deptName);
            }
        }
    }

    @Test
    public void testMany2oneByAnn() {
        System.out.println("========== 多对一（注解方式）==========");

        List<Emp> list = empMapper.many2oneByAnn();
        System.out.println("员工总数：" + (list != null ? list.size() : 0));
        if (list != null) {
            for (Emp emp : list) {
                String deptName = (emp.getDept() != null) ? emp.getDept().getDname() : "无";
                System.out.println("  " + emp.getEmpno() + " - " + emp.getEname() + " -> " + deptName);
            }
        }
    }

    // ==================== 一对多 ====================

    @Test
    public void testOne2manyByXml() {
        System.out.println("========== 一对多（XML方式）==========");
        System.out.println("知识点：<collection> + ofType 指定元素类型 + 嵌套结果映射");

        Dept dept = deptMapper.one2manyByXml(2);
        if (dept != null) {
            System.out.println("部门：" + dept.getDname() + " (编号:" + dept.getDeptno() + ")");
            System.out.println("位置：" + dept.getLoc());
            System.out.println("员工数量：" + (dept.getEmps() != null ? dept.getEmps().size() : 0));
            if (dept.getEmps() != null) {
                for (Emp emp : dept.getEmps()) {
                    System.out.println("  - " + emp.getEname() + " (" + emp.getJob() + ")");
                }
            }
        }
    }

    @Test
    public void testOne2manyByAnn() {
        System.out.println("========== 一对多（注解方式）==========");
        System.out.println("知识点：@Many(select = ...) 嵌套 select");

        Dept dept = deptMapper.one2manyByAnn(3);
        if (dept != null) {
            System.out.println("部门：" + dept.getDname() + " (编号:" + dept.getDeptno() + ")");
            System.out.println("位置：" + dept.getLoc());
            System.out.println("员工数量：" + (dept.getEmps() != null ? dept.getEmps().size() : 0));
            if (dept.getEmps() != null) {
                for (Emp emp : dept.getEmps()) {
                    System.out.println("  - " + emp.getEname() + " (" + emp.getJob() + ")");
                }
            }
        }
    }

    // ==================== 多对多 ====================

    @Test
    public void testMany2manyByXml() {
        System.out.println("========== 多对多（XML方式）==========");
        System.out.println("知识点：中间表 emp_skill + 两次 LEFT JOIN + <collection> + DISTINCT 去重");

        Emp emp = empMapper.many2manyByXml(4);
        if (emp != null) {
            System.out.println("员工：" + emp.getEname() + " (编号:" + emp.getEmpno() + ")");
            System.out.println("技能数量：" + (emp.getSkills() != null ? emp.getSkills().size() : 0));
            if (emp.getSkills() != null) {
                for (Skill skill : emp.getSkills()) {
                    System.out.println("  - " + skill.getName() + ": " + skill.getDescription());
                }
            }
        }
    }

    @Test
    public void testMany2manyByAnn() {
        System.out.println("========== 多对多（注解方式）==========");
        System.out.println("知识点：@Many(select = ...) 调用中间表查询方法");

        Emp emp = empMapper.many2manyByAnn(8);
        if (emp != null) {
            System.out.println("员工：" + emp.getEname() + " (编号:" + emp.getEmpno() + ")");
            System.out.println("技能数量：" + (emp.getSkills() != null ? emp.getSkills().size() : 0));
            if (emp.getSkills() != null) {
                for (Skill skill : emp.getSkills()) {
                    System.out.println("  - " + skill.getName() + ": " + skill.getDescription());
                }
            }
        }
    }
}
