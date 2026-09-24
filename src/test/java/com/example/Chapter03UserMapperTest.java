package com.example;

import com.example.entity.User;
import com.example.mapper.Chapter03UserMapper;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 第3节测试类：ResultMap + 动态 SQL
 */
public class Chapter03UserMapperTest {

    private InputStream is;
    private SqlSession session;
    private Chapter03UserMapper userMapper;

    @Before
    public void init() throws IOException {
        // 重置数据库：保留张三/李四/王五 + 加一个 admin 用户用于测试
        resetDatabase();

        is = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
        session = factory.openSession(true);
        userMapper = session.getMapper(Chapter03UserMapper.class);
    }

    @After
    public void destroy() throws IOException {
        if (session != null) session.close();
        if (is != null) is.close();
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
            s.executeUpdate("INSERT INTO user(username, password, email) VALUES('admin','123456','admin@qq.com')");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ═══════════════════════════════════════════════════
    // 第一部分：ResultMap（注解方式）
    // ═══════════════════════════════════════════════════

    @Test
    public void testAnnoAnonymousResultMap() {
        System.out.println("========== ResultMap 注解方式 1: 匿名 @Results ==========");
        User user = userMapper.findByIdAnno(1);
        System.out.println(user);
        assert user != null;
    }

    @Test
    public void testAnnoNamedResultMap() {
        System.out.println("========== ResultMap 注解方式 2: 命名 @Results ==========");
        User user = userMapper.findByIdWithAnnoMap(2);
        System.out.println(user);
        assert user != null;
    }

    @Test
    public void testAnnoRefAnnoResultMap() {
        System.out.println("========== ResultMap 注解方式 3: @ResultMap 引用注解定义的 ==========");
        List<User> users = userMapper.findAllByAnnoMap();
        users.forEach(System.out::println);
        assert users.size() > 0;
    }

    @Test
    public void testAnnoRefXmlResultMap() {
        System.out.println("========== ResultMap 注解方式 4: @ResultMap 引用 XML 定义的 ==========");
        List<User> users = userMapper.findLikeByXmlMap("admin");
        users.forEach(System.out::println);
        assert users.size() > 0;
    }

    // ═══════════════════════════════════════════════════
    // 第二部分：动态 SQL（两种方式对比）
    // ═══════════════════════════════════════════════════

    @Test
    public void testAnnoDynamicScript() {
        System.out.println("========== 动态 SQL（注解方式）: <script> + <if> + <where> ==========");

        // 场景1: 只传 username
        User q1 = new User();
        q1.setUsername("admin");
        System.out.println("--- 只按 username 查 ---");
        userMapper.findUsersAnnoDynamic(q1).forEach(System.out::println);

        // 场景2: username + email
        User q2 = new User();
        q2.setUsername("admin");
        q2.setEmail("admin@qq.com");
        System.out.println("--- 按 username + email 查 ---");
        userMapper.findUsersAnnoDynamic(q2).forEach(System.out::println);

        // 场景3: 什么都不传
        User q3 = new User();
        System.out.println("--- 无条件（<where> 自动处理空条件） ---");
        userMapper.findUsersAnnoDynamic(q3).forEach(System.out::println);
    }

    @Test
    public void testDynamicIfWhere() {
        System.out.println("========== 动态 SQL 1: <if> + <where> ==========");

        User q1 = new User();
        q1.setUsername("admin");
        System.out.println("--- 只按 username 查 ---");
        userMapper.findUsersByCondition(q1).forEach(System.out::println);

        User q2 = new User();
        q2.setUsername("admin");
        q2.setEmail("admin@qq.com");
        System.out.println("--- 按 username + email 查 ---");
        userMapper.findUsersByCondition(q2).forEach(System.out::println);

        User q3 = new User();
        System.out.println("--- 无条件 ---");
        userMapper.findUsersByCondition(q3).forEach(System.out::println);
    }

    @Test
    public void testDynamicSet() {
        System.out.println("========== 动态 SQL 2: <set> 选择性更新 ==========");

        User user = new User();
        user.setId(1);
        user.setPassword("newpass_789");
        user.setUpdateTime(new Date());

        int rows = userMapper.updateUserSelective(user);
        System.out.println("更新影响行数：" + rows);
        assert rows > 0;
    }

    @Test
    public void testDynamicChoose() {
        System.out.println("========== 动态 SQL 3: <choose>/<when>/<otherwise> ==========");

        // 同时传 id 和 username → id 优先
        User q1 = new User();
        q1.setId(1);
        q1.setUsername("admin");
        System.out.println("--- id 优先 ---");
        userMapper.findUserPriority(q1).forEach(System.out::println);

        // 只传 username
        User q2 = new User();
        q2.setUsername("admin");
        System.out.println("--- 只传 username ---");
        userMapper.findUserPriority(q2).forEach(System.out::println);

        // 什么都不传 → otherwise 兜底
        User q3 = new User();
        System.out.println("--- 什么都不传 → otherwise 兜底 ---");
        System.out.println("命中数量：" + userMapper.findUserPriority(q3).size());
    }

    @Test
    public void testDynamicForeachIn() {
        System.out.println("========== 动态 SQL 4: <foreach> 批量查询（IN） ==========");
        List<User> users = userMapper.findByIds(Arrays.asList(1, 2, 3));
        users.forEach(System.out::println);
        assert users.size() == 3;
    }

    @Test
    public void testDynamicForeachBatchInsert() {
        System.out.println("========== 动态 SQL 5: <foreach> 批量插入 ==========");

        User u1 = new User();
        u1.setUsername("batch_p_" + System.currentTimeMillis());
        u1.setPassword("88888");
        u1.setEmail("66@test.com");
        u1.setCreateTime(new Date());
        u1.setUpdateTime(new Date());

        User u2 = new User();
        u2.setUsername("batch_q_" + System.currentTimeMillis());
        u2.setPassword("6666");
        u2.setEmail("77@test.com");
        u2.setCreateTime(new Date());
        u2.setUpdateTime(new Date());

        int rows = userMapper.batchInsert(Arrays.asList(u1, u2));
        System.out.println("批量插入影响行数：" + rows);
        assert rows == 2;
    }

    @Test
    public void testDynamicTrim() {
        System.out.println("========== 动态 SQL 6: <trim> 自定义裁剪 ==========");

        User q = new User();
        q.setUsername("admin");
        userMapper.findUsersByTrim(q).forEach(System.out::println);
    }

    @Test
    public void testSqlInclude() {
        System.out.println("========== 动态 SQL 7: <sql>/<include> 复用片段 ==========");

        User q = new User();
        q.setUsername("admin");
        userMapper.findUsersWithInclude(q).forEach(System.out::println);
    }
}
