package com.example.mapper;

import com.example.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 用户 Mapper 接口（注解方式）
 *
 * 特点：
 *   - SQL 直接写在接口方法的注解里（@Select / @Insert / @Update / @Delete）
 *   - 不需要对应的 XML 文件
 *   - 多参数必须用 @Param 指定参数名
 */
public interface UserMapperAnnotation {

    /**
     * 查询所有用户（@Select 注解）
     */
    @Select("SELECT * FROM user")
    List<User> findAll();

    /**
     * 根据 ID 查询用户
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Integer id);

    /**
     * 添加用户（@Insert 注解 + 获取自增主键）
     * useGeneratedKeys=true：让数据库生成主键
     * keyProperty="id"：把生成的主键回填到 user 对象的 id 字段
     */
    @Insert("INSERT INTO user(username, password, email) VALUES(#{username}, #{password}, #{email})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int addUser(User user);

    /**
     * 更新用户（@Update 注解）
     */
    @Update("UPDATE user SET username=#{username}, email=#{email} WHERE id=#{id}")
    int updateUser(User user);

    /**
     * 删除用户（@Delete 注解）
     */
    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteUser(Integer id);

    /**
     * 多条件查询（@Param 注解）
     * 多个参数必须用 @Param 指定参数名，SQL 里用 #{name} #{email} 取值
     * 如果不加 @Param，MyBatis 默认用 arg0/arg1 或 param1/param2，容易混淆
     */
    @Select("SELECT * FROM user WHERE username=#{name} AND email=#{email}")
    List<User> findByNameAndEmail(@Param("name") String username, @Param("email") String email);

    /**
     * 模糊查询（${} 用法，注意 SQL 注入风险）
     * ${} 会把参数值直接拼进 SQL，这里参数不带引号才能正常工作
     * 实际开发应该用 #{} 配合 LIKE CONCAT('%', #{username}, '%')
     */
    @Select("SELECT * FROM user WHERE username LIKE '%${username}%'")
    List<User> findByNameLike(@Param("username") String username);

    /**
     * 动态表名查询（${} 的正确用法）
     * 表名不能用 #{}（会被加引号变成字符串），必须用 ${}
     * 但表名绝对不能来自用户输入，否则会被注入
     */
    @Select("SELECT * FROM ${tableName}")
    List<User> findAllByTableName(@Param("tableName") String tableName);

    /**
     * 登录验证（@Param 注解的正确用法）
     *
     * 错误示例（不加 @Param）：
     *   User login(String username, String password);
     *   报错：Parameter 'username' not found. Available parameters are [arg0, arg1, param1, param2]
     *   原因：MyBatis 找不到 #{username}，因为它默认用 arg0/arg1 命名参数
     *
     * 正确写法（加 @Param）：
     *   @Param("username") 把参数名指定为 "username"，SQL 里 #{username} 就能取到
     */
    @Select("SELECT * FROM user WHERE username=#{username} AND password=#{password}")
    User login(@Param("username") String username, @Param("password") String password);

    /**
     * 使用 Map 传递多个参数（当参数较多时推荐）
     *
     * SQL 里 #{key} 会去 Map 里取对应 key 的 value
     * Map 的 key 必须和 SQL 里的 #{} 占位符名称一致
     */
    @Select("SELECT * FROM user WHERE username=#{username} AND email=#{email} AND password=#{password}")
    List<User> findUserByMap(Map<String, Object> map);

    /**
     * ==================== 第3学时：#{} vs ${} 对比实验 ====================
     */

    /**
     * #{} 占位符（安全，推荐）
     * 底层用 PreparedStatement，参数用 ? 占位，防 SQL 注入
     * 日志：Preparing: SELECT * FROM user WHERE username = ?
     *      Parameters: 张三(String)
     */
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    /**
     * ${} 字符串拼接（不安全，仅演示）
     * 底层用 Statement，参数直接拼进 SQL，有注入风险
     * 日志：Preparing: SELECT * FROM user WHERE username = '张三'
     *      Parameters: (空，没有参数输出)
     */
    @Select("SELECT * FROM user WHERE username = '${username}'")
    User findByUsername2(@Param("username") String username);

    /**
     * SQL 注入演示：使用 ${}（危险！）
     * 攻击方式：username 传 "admin' -- "，会把后面的 password 条件注释掉
     * 拼出的 SQL：SELECT * FROM user WHERE username = 'admin' -- ' AND password = '任意密码'
     * 结果：只要 admin 用户存在，无论密码是什么都能登录成功
     */
    @Select("SELECT * FROM user WHERE username = '${username}' AND password = '${password}'")
    User loginUnsafe(@Param("username") String username, @Param("password") String password);

    /**
     * SQL 注入防御：使用 #{}（安全！）
     * 攻击方式：username 传 "admin' -- "，但 #{} 会把整个字符串当参数值处理
     * 实际 SQL：SELECT * FROM user WHERE username = ? AND password = ?
     * 参数：'admin'' -- '（作为字符串值，不是 SQL 语法）
     * 结果：查不到用户，登录失败，攻击被防御
     */
    @Select("SELECT * FROM user WHERE username = #{username} AND password = #{password}")
    User loginSafe(@Param("username") String username, @Param("password") String password);
}
