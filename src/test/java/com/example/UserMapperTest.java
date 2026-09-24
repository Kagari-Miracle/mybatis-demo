package com.example;

import com.example.entity.User;
import com.example.mapper.UserMapper;
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
 * 用户 Mapper 测试类（第01章第2节 标准化测试类）
 *
 * 特点：
 *   - @Before 初始化 SqlSessionFactory + SqlSession（autoCommit=true，自动提交）
 *   - @After 关闭 SqlSession 和 InputStream
 *   - assert 断言验证结果（assert 关键字 + -ea JVM 参数才生效）
 *   - 每个 @Test 方法独立执行，互不影响
 */
public class UserMapperTest {

    private InputStream is;
    private SqlSession session;
    private UserMapper userMapper;

    /**
     * 初始化方法（在每个 @Test 方法执行前执行）
     */
    @Before
    public void init() throws IOException {
        System.out.println("初始化方法执行...");

        // 0. 重置数据库（保证每个测试方法的数据一致）
        resetDatabase();

        // 1. 加载配置文件
        is = Resources.getResourceAsStream("mybatis-config.xml");

        // 2. 创建 SqlSessionFactory
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);

        // 3. 创建 SqlSession（true：自动提交事务，不需要手动 commit）
        session = factory.openSession(true);

        // 4. 获取 Mapper 代理对象
        userMapper = session.getMapper(UserMapper.class);
    }

    /**
     * 销毁方法（在每个 @Test 方法执行后执行）
     */
    @After
    public void destroy() throws IOException {
        System.out.println("销毁方法执行...");

        // 关闭资源（try-with-resources 也可以，但这里显式写）
        if (session != null) {
            session.close();
        }
        if (is != null) {
            is.close();
        }
    }

    /**
     * 重置 user 表数据（每个测试方法执行前调用，保证数据一致性）
     * 插入：张三(1)/李四(2)/王五(3)/admin(4)
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
            s.executeUpdate("INSERT INTO user(username, password, email) VALUES('admin','123456','admin@qq.com')");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 测试查询所有用户
     */
    @Test
    public void testFindAll() {
        System.out.println("testFindAll 方法执行...");

        List<User> users = userMapper.findAll();

        for (User user : users) {
            System.out.println(user);
        }

        // 断言（注意：IDEA 运行 assert 需要 VM options 加 -ea）
        assert users != null : "用户列表不能为空";
        assert users.size() > 0 : "用户列表必须有数据";
    }

    /**
     * 测试根据 ID 查询用户
     */
    @Test
    public void testFindById() {
        System.out.println("testFindById 方法执行...");

        User user = userMapper.findById(1);
        System.out.println(user);

        assert user != null : "用户不能为空";
        assert user.getId() == 1 : "用户ID必须是1";
    }

    /**
     * 测试添加用户
     */
    @Test
    public void testAddUser() {
        System.out.println("testAddUser 方法执行...");

        User user = new User();
        user.setUsername("测试用户Add");
        user.setPassword("123456");
        user.setEmail("testadd@qq.com");

        int rows = userMapper.addUser(user);

        assert rows > 0 : "添加失败";
        System.out.println("添加成功，影响行数：" + rows + "，自增主键：" + user.getId());
    }

    /**
     * 测试更新用户
     */
    @Test
    public void testUpdateUser() {
        System.out.println("testUpdateUser 方法执行...");

        // 先查一个存在的用户
        User user = userMapper.findById(1);
        assert user != null : "要更新的用户不存在";

        user.setUsername("批量测试更新");
        user.setEmail("batch@qq.com");

        int rows = userMapper.updateUser(user);
        assert rows > 0 : "更新失败";
        System.out.println("更新成功，影响行数：" + rows);
    }

    /**
     * 测试删除用户
     */
    @Test
    public void testDeleteUser() {
        System.out.println("testDeleteUser 方法执行...");

        // 先查一个存在的用户 id=3（王五），避免影响 id=1 的张三
        User user = userMapper.findById(3);
        if (user != null) {
            int rows = userMapper.deleteUser(3);
            assert rows > 0 : "删除失败";
            System.out.println("删除成功，影响行数：" + rows);
        } else {
            System.out.println("id=3 的用户不存在，跳过删除测试");
        }
    }

    /**
     * 测试根据用户名模糊查询
     */
    @Test
    public void testFindByUsernameLike() {
        System.out.println("testFindByUsernameLike 方法执行...");

        List<User> users = userMapper.findByUsernameLike("张");
        for (User user : users) {
            System.out.println(user);
        }

        assert users != null : "模糊查询结果不能为空";
        assert users.size() > 0 : "应该能查到姓张的用户";
    }
}
