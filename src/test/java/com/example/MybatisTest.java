package com.example;

import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MyBatis 测试类（对应 UserMapper 全部 CRUD 方法）
 *
 * 运行方式（IDEA 里两种都可以）：
 *   1) 右键单个 @Test 方法 → Run            （JUnit 4 运行器）
 *   2) 右键 MybatisTest → Run 'main()'      （普通 Java 程序，不依赖 JUnit 框架）
 */
public class MybatisTest {

    private SqlSessionFactory sqlSessionFactory;

    @Before
    public void init() throws Exception {
        // 复用 MyBatisUtil 的单例工厂
        sqlSessionFactory = MyBatisUtil.getSqlSessionFactory();
    }

    @Test
    public void testFindAll() {
        System.out.println("========== 测试查询所有用户 ==========");
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            List<User> users = userMapper.findAll();
            for (User user : users) {
                System.out.println(user);
            }
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testFindByName() {
        System.out.println("========== 测试根据用户名查询 ==========");
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user = userMapper.testfind("张三");
            System.out.println(user);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testFindById() {
        System.out.println("========== 测试根据ID查询用户 ==========");
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user = userMapper.findById(1);
            System.out.println(user);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testAddUser() {
        System.out.println("========== 测试添加用户 ==========");
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user = new User();
            user.setUsername("测试用户");
            user.setPassword("123456");
            user.setEmail("test@qq.com");
            user.setCreateTime(new Date());
            int rows = userMapper.addUser(user);
            System.out.println("影响行数：" + rows);
            System.out.println("自增主键：" + user.getId());
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testUpdateUser() {
        System.out.println("========== 测试更新用户 ==========");
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user = userMapper.findById(1);
            user.setUsername("更新后的用户名");
            user.setEmail("update@qq.com");
            user.setUpdateTime(new Date());
            int rows = userMapper.updateUser(user);
            System.out.println("影响行数：" + rows);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testDeleteUser() {
        System.out.println("========== 测试删除用户 ==========");
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            int rows = userMapper.deleteUser(1);
            System.out.println("影响行数：" + rows);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testFindByUsernameLike() {
        System.out.println("========== 测试根据用户名模糊查询 ==========");
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            List<User> users = userMapper.findByUsernameLike("测");
            for (User user : users) {
                System.out.println(user);
            }
        } finally {
            sqlSession.close();
        }
    }

    // ==================== 知识点1：#{} vs ${} ====================

    @Test
    public void testFindByUsernamePre() {
        System.out.println("========== 知识点1：#{} 预编译查询（安全） ==========");
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            // 用 #{} 传参，MyBatis 会自动做预编译，防 SQL 注入
            User user = userMapper.findByUsernamePre("张三");
            System.out.println("查到：" + user);

            // 对比：如果输入 "张三' OR 1=1 --"
            // #{} 方式：会查不到，因为参数整体作为用户名，不会拼进 SQL 结构
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testFindByUsernameXML() {
        System.out.println("========== 知识点1：${} 直接拼接查询（危险，仅演示） ==========");
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            // ${} 会把参数值直接拼进 SQL 字符串
            // 注意：${} 不会自动加引号，所以这里参数值要带引号才能正常查询
            // 实际开发绝对不要用 ${} 接用户输入（有 SQL 注入风险）
            User user = userMapper.findByUsernameXML("'张三'");
            System.out.println("查到：" + user);

            // 演示：如果输入 "' OR 1=1 --"（经典注入 payload）
            // ${} 方式会把整个参数拼进去，变成 WHERE username = '' OR 1=1 --
            // 导致绕过条件查出所有数据！这就是为什么要优先用 #{}
        } finally {
            sqlSession.close();
        }
    }

    // ==================== 知识点2：Map 传参 ====================

    @Test
    public void testFindByMap() {
        System.out.println("========== 知识点2：Map 参数查询 ==========");
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            // 用 Map 传多个无关联的参数
            // SQL: SELECT * FROM user WHERE password = #{abc} OR username = #{username}
            Map<String, Object> params = new HashMap<>();
            params.put("abc", "123");      // 对应 #{abc}
            params.put("username", "张三"); // 对应 #{username}

            List<User> users = userMapper.findByMap(params);
            for (User user : users) {
                System.out.println(user);
            }
        } finally {
            sqlSession.close();
        }
    }

    // ==================== 知识点3：聚合函数 ====================

    @Test
    public void testFindCount() {
        System.out.println("========== 知识点3：聚合函数 - COUNT 查询总条数 ==========");
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            Integer count = userMapper.findCount();
            System.out.println("user 表总条数：" + count);
            // COUNT(*) 返回 Integer / Long 都可以，MyBatis 会自动转换
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testFindCountAndSum() {
        System.out.println("========== 知识点3：聚合函数 - COUNT + SUM ==========");
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            // SQL: SELECT COUNT(*) AS count, SUM(id) AS sum FROM user
            // 返回 List<Map>，每个 Map 的 key 是别名
            List<Map<String, Object>> result = userMapper.findCountAndSum();
            for (Map<String, Object> map : result) {
                System.out.println("count = " + map.get("count"));
                System.out.println("sum   = " + map.get("sum"));
            }
        } finally {
            sqlSession.close();
        }
    }

    // ==================== 知识点4：注解 vs XML ====================

    @Test
    public void testFindAllByAnnotation() {
        System.out.println("========== 知识点4：@Select 注解方式查询所有用户 ==========");
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            // 这个方法在接口上用 @Select("SELECT * FROM user") 写了 SQL
            // 不需要在 XML 里有对应 id，MyBatis 会直接用注解里的 SQL
            List<User> users = userMapper.findAllByAnnotation();
            System.out.println("注解方式查到 " + users.size() + " 条：");
            for (User user : users) {
                System.out.println(user);
            }

            // 对比：findAll() 是 XML 方式，结果一样
            List<User> users2 = userMapper.findAll();
            System.out.println("XML 方式查到 " + users2.size() + " 条（结果相同）");
        } finally {
            sqlSession.close();
        }
    }

    /**
     * main 方法：IDEA 里直接右键 MybatisTest 就能跑，依次执行全部测试。
     */
    public static void main(String[] args) {
        MybatisTest test = new MybatisTest();
        try {
            test.init();
            System.out.println("===== 原有 CRUD 测试 =====");
            test.testFindAll();
            test.testFindById();
            test.testFindByName();
            test.testFindByUsernameLike();
            System.out.println();
            System.out.println("===== 知识点1：#{} vs ${} =====");
            test.testFindByUsernamePre();
            test.testFindByUsernameXML();
            System.out.println();
            System.out.println("===== 知识点2：Map 传参 =====");
            test.testFindByMap();
            System.out.println();
            System.out.println("===== 知识点3：聚合函数 =====");
            test.testFindCount();
            test.testFindCountAndSum();
            System.out.println();
            System.out.println("===== 知识点4：注解 vs XML =====");
            test.testFindAllByAnnotation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
