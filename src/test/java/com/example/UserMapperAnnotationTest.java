package com.example;

import com.example.entity.User;
import com.example.mapper.UserMapperAnnotation;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 注解方式 Mapper 测试类
 *
 * 测试目标：
 *   - 验证 UserMapperAnnotation 接口里的注解 SQL 全部生效
 *   - 观察 Log4j 日志输出（注解 SQL 也会打日志）
 *   - 观察 ${} 与 #{} 的区别
 */
public class UserMapperAnnotationTest {

    private InputStream is;
    private SqlSession session;
    private UserMapperAnnotation userMapper;

    @Before
    public void init() throws IOException {
        // 每个测试方法执行前，重置数据库为初始3条数据，保证测试互相独立
        resetDatabase();

        is = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
        session = factory.openSession(true); // 自动提交
        userMapper = session.getMapper(UserMapperAnnotation.class);
    }

    /**
     * 重置 user 表为初始3条数据（张三/李四/王五）
     */
    private void resetDatabase() {
        String url = "jdbc:mysql://localhost:3306/mybatis_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        try (Connection c = DriverManager.getConnection(url, "root", "wyyxhxmy0416");
             Statement s = c.createStatement()) {
            s.executeUpdate("DELETE FROM user");
            s.executeUpdate("ALTER TABLE user AUTO_INCREMENT = 1");
            s.executeUpdate("INSERT INTO user(username, password, email) VALUES('张三','123','zhangsan@qq.com')");
            s.executeUpdate("INSERT INTO user(username, password, email) VALUES('李四','456','lisi@qq.com')");
            s.executeUpdate("INSERT INTO user(username, password, email) VALUES('王五','789','wangwu@qq.com')");
        } catch (Exception e) {
            throw new RuntimeException("重置数据库失败: " + e.getMessage(), e);
        }
    }

    @After
    public void destroy() throws IOException {
        if (session != null) session.close();
        if (is != null) is.close();
    }

    @Test
    public void testFindAll() {
        List<User> users = userMapper.findAll();
        for (User user : users) {
            System.out.println(user);
        }
        assert users != null && users.size() > 0 : "查询结果不能为空";
    }

    @Test
    public void testFindById() {
        User user = userMapper.findById(1);
        System.out.println(user);
        assert user != null && user.getId() == 1 : "应该查到 id=1 的用户";
    }

    @Test
    public void testAddUser() {
        User user = new User();
        user.setUsername("注解测试用户");
        user.setPassword("123456");
        user.setEmail("ann@qq.com");
        int rows = userMapper.addUser(user);
        System.out.println("影响行数：" + rows + "，自增主键：" + user.getId());
        assert rows > 0 : "添加失败";
    }

    @Test
    public void testUpdateUser() {
        User user = userMapper.findById(1);
        assert user != null : "要更新的用户不存在";
        user.setUsername("注解更新用户");
        user.setEmail("ann-update@qq.com");
        int rows = userMapper.updateUser(user);
        System.out.println("影响行数：" + rows);
        assert rows > 0 : "更新失败";
    }

    @Test
    public void testDeleteUser() {
        int rows = userMapper.deleteUser(4);
        System.out.println("影响行数：" + rows);
        // 不强制断言 > 0，因为可能 id=4 不存在
    }

    @Test
    public void testFindByNameAndEmail() {
        // 多参数 @Param 测试：name + email
        List<User> users = userMapper.findByNameAndEmail("张三", "zhangsan@qq.com");
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Test
    public void testFindByNameLike() {
        // 注意：这里使用 ${} 拼接，存在 SQL 注入风险（仅演示）
        List<User> users = userMapper.findByNameLike("张");
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Test
    public void testFindAllByTableName() {
        // 动态表名（${} 的正确用法：表名必须用 ${}，不能用 #{}）
        List<User> users = userMapper.findAllByTableName("user");
        System.out.println("动态表名查询到 " + users.size() + " 条");
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Test
    public void testLogin() {
        // @Param 注解测试：登录验证
        // 如果不加 @Param，会报错：Parameter 'username' not found
        User user = userMapper.login("张三", "123");
        System.out.println("登录结果：" + user);
        assert user != null : "登录失败，用户名或密码错误";
    }

    @Test
    public void testFindUserByMap() {
        // Map 传参测试：当参数较多时，用 Map 比加一堆 @Param 更方便
        Map<String, Object> map = new HashMap<>();
        map.put("username", "张三");
        map.put("email", "zhangsan@qq.com");
        map.put("password", "123");

        List<User> users = userMapper.findUserByMap(map);
        for (User user : users) {
            System.out.println(user);
        }
    }

    /**
     * #{} 占位符测试（安全）
     * 观察日志：Preparing 里有 ? 占位符，Parameters 里有参数值
     */
    @Test
    public void testFindByUsername() {
        User user = userMapper.findByUsername("张三");
        System.out.println("#{} 查询结果：" + user);
        // 看日志确认：Preparing: SELECT * FROM user WHERE username = ?
        //            Parameters: 张三(String)
    }

    /**
     * ${} 字符串拼接测试（不安全）
     * 观察日志：Preparing 里直接是拼好的 SQL，没有 Parameters 输出
     */
    @Test
    public void testFindByUsername2() {
        User user = userMapper.findByUsername2("张三");
        System.out.println("${} 查询结果：" + user);
        // 看日志确认：Preparing: SELECT * FROM user WHERE username = '张三'
        //            Parameters: (空)
    }

    /**
     * SQL 注入演示：使用 ${} 会被注入攻击
     * 攻击字符串 "张三' -- " 会把 password 条件注释掉
     */
    @Test
    public void testSqlInjection() {
        // 正常登录：用户名密码都正确
        User user1 = userMapper.loginUnsafe("张三", "123");
        System.out.println("正常登录(${}): " + user1);

        // SQL 注入攻击：密码随便填，用注释符绕过密码校验
        User user2 = userMapper.loginUnsafe("张三' -- ", "任意密码");
        System.out.println("SQL注入登录(${}): " + user2);
        // 预期：user2 不为 null（攻击成功！）
        if (user2 != null) {
            System.out.println(">>> SQL 注入攻击成功！${} 方式不安全");
        }
    }

    /**
     * SQL 注入防御：使用 #{} 可以防御注入
     * 同样的攻击字符串，#{} 会把它当字符串值处理
     */
    @Test
    public void testSqlInjectionSafe() {
        // 正常登录
        User user1 = userMapper.loginSafe("张三", "123");
        System.out.println("正常登录(#{}): " + user1);

        // 同样的注入攻击，用 #{} 会失败
        User user2 = userMapper.loginSafe("张三' -- ", "任意密码");
        System.out.println("SQL注入登录(#{}): " + user2);
        // 预期：user2 为 null（攻击被防御！）
        if (user2 == null) {
            System.out.println(">>> #{} 方式防御了 SQL 注入攻击");
        }
    }
}
