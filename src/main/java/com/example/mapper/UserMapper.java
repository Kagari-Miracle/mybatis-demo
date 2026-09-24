package com.example.mapper;

import com.example.entity.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 用户 Mapper 接口
 * 对应 Mapper XML：UserMapper.xml
 */
public interface UserMapper {

    // ==================== 知识点4：注解 vs XML ====================
    // 同一个功能可以用两种方式实现：
    //   findAllByAnnotation  → 用 @Select 注解（直接写 SQL 在接口上）
    //   findAllByxml         → SQL 写在 XML 文件里（下方别名方法）
    // 两种方式可以共存，MyBatis 都能识别

    /**
     * 注解方式查询所有用户（@Select）
     */
    @Select("SELECT * FROM user")
    List<User> findAllByAnnotation();

    /**
     * XML 方式查询所有用户（对应 findAllByxml，也叫 findAll）
     * @return 用户列表
     */
    List<User> findAll();

    /**
     * 根据 ID 查询用户
     * @param id 用户ID
     * @return 用户对象
     */
    User findById(Integer id);

    /**
     * 根据用户名查询用户（精确匹配）
     * @param username 用户名
     * @return 用户对象
     */
    User testfind(String username);

    /**
     * 添加用户
     * @param user 用户对象
     * @return 影响行数
     */
    int addUser(User user);

    /**
     * 更新用户
     * @param user 用户对象
     * @return 影响行数
     */
    int updateUser(User user);

    /**
     * 根据 ID 删除用户
     * @param id 用户ID
     * @return 影响行数
     */
    int deleteUser(Integer id);

    /**
     * 根据用户名模糊查询用户
     * @param username 用户名关键字
     * @return 用户列表
     */
    List<User> findByUsernameLike(String username);

    // ==================== 知识点1：#{} vs ${} ====================

    /**
     * #{} 预编译查询（安全，推荐）
     */
    User findByUsernamePre(String username);

    /**
     * ${} 直接拼接查询（危险！仅演示区别）
     * 注意：参数值不需要加引号，${} 会直接拼进去
     * 实际开发绝对不要用 ${} 接用户输入
     */
    User findByUsernameXML(String username);

    // ==================== 知识点2：Map 传参 ====================

    /**
     * Map 参数查询：password = #{abc} OR username = #{username}
     * 调用方传入 Map.put("abc", "xxx"); Map.put("username", "yyy")
     */
    List<User> findByMap(Map<String, Object> params);

    // ==================== 知识点3：聚合函数 ====================

    /**
     * 查询 user 表总条数
     */
    Integer findCount();

    /**
     * 查询 COUNT(*) 和 SUM(id)，返回 Map 列表
     * Map 的 key 是别名（count / sum），value 是数值
     */
    List<Map<String, Object>> findCountAndSum();
}
