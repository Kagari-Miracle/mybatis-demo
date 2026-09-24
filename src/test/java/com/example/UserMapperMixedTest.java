package com.example;

import com.example.entity.User;
import com.example.mapper.UserMapperMixed;
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
 * 混合开发测试类
 *
 * 验证同一个 Mapper 接口里，注解方法和 XML 方法都能正常工作
 */
public class UserMapperMixedTest {

    private InputStream is;
    private SqlSession session;
    private UserMapperMixed userMapper;

    @Before
    public void init() throws IOException {
        resetDatabase();
        is = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
        session = factory.openSession(true);
        userMapper = session.getMapper(UserMapperMixed.class);
    }

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
            throw new RuntimeException(e);
        }
    }

    @After
    public void destroy() throws IOException {
        if (session != null) session.close();
        if (is != null) is.close();
    }

    @Test
    public void testFindById() {
        // 简单查询：用 @Select 注解实现
        User user = userMapper.findById(1);
        System.out.println("注解方式查询结果：" + user);
        assert user != null && user.getId() == 1;
    }

    @Test
    public void testFindByCondition() {
        // 复杂查询：用 XML 里的动态 SQL 实现
        // 场景1：只传 username
        User query1 = new User();
        query1.setUsername("张三");
        List<User> users1 = userMapper.findByCondition(query1);
        System.out.println("按用户名查询，找到 " + users1.size() + " 条：");
        for (User u : users1) {
            System.out.println("  " + u);
        }

        // 场景2：传 username + email
        User query2 = new User();
        query2.setUsername("李四");
        query2.setEmail("lisi@qq.com");
        List<User> users2 = userMapper.findByCondition(query2);
        System.out.println("按用户名+邮箱查询，找到 " + users2.size() + " 条：");
        for (User u : users2) {
            System.out.println("  " + u);
        }

        // 场景3：什么都不传（查全部）
        User query3 = new User();
        List<User> users3 = userMapper.findByCondition(query3);
        System.out.println("无条件查询，找到 " + users3.size() + " 条");
    }
}
