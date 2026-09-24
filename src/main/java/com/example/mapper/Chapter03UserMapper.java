package com.example.mapper;

import com.example.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 第3节 Mapper 接口：ResultMap 与 动态 SQL
 *
 * 本节聚焦两大核心知识点：
 *
 * ── ResultMap（XML 方式 & 注解方式）──
 *   · XML:  在 Chapter03UserMapper.xml 中定义 <resultMap id="userResultMap">
 *   · 注解: @Results 直接在方法上定义 / @ResultMap 引用已有 ResultMap
 *   · 跨方式: 注解 SQL 通过 @ResultMap("userResultMap") 引用 XML ResultMap
 *
 * ── 动态 SQL（两种方式）──
 *   · 注解方式：<script> 标签包裹 XML 动态标签（MyBatis 3.x 支持）
 *   · XML 方式：完整的 6 种动态标签（<if> <where> <set> <choose> <foreach> <trim>）
 */
public interface Chapter03UserMapper {

    // ==================== ResultMap（注解方式）====================

    /** 注解方式 1: 匿名 @Results（只给当前方法用） */
    @Select("SELECT * FROM user WHERE id = #{id}")
    @Results({
            @Result(property = "id",       column = "id",           id = true),
            @Result(property = "username", column = "username"),
            @Result(property = "password", column = "password"),
            @Result(property = "email",    column = "email"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    User findByIdAnno(Integer id);

    /** 注解方式 2: 命名 @Results(id="annoUserMap")，供 @ResultMap 重复引用 */
    @Select("SELECT * FROM user WHERE id = #{id}")
    @Results(id = "annoUserMap", value = {
            @Result(property = "id",       column = "id",           id = true),
            @Result(property = "username", column = "username"),
            @Result(property = "password", column = "password"),
            @Result(property = "email",    column = "email"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    User findByIdWithAnnoMap(Integer id);

    /** 注解方式 3: @ResultMap 引用【注解定义的命名 ResultMap】 */
    @Select("SELECT * FROM user")
    @ResultMap("annoUserMap")
    List<User> findAllByAnnoMap();

    /** 注解方式 4: @ResultMap 引用【XML 中定义的 ResultMap】（跨方式引用） */
    @Select("SELECT * FROM user WHERE username LIKE CONCAT('%', #{kw}, '%')")
    @ResultMap("userResultMap")
    List<User> findLikeByXmlMap(@Param("kw") String keyword);

    // ==================== 动态 SQL（注解方式 —— <script> 包裹）====================

    /**
     * 注解版动态 SQL：<script> + <if> + <where>
     * 对比 XML 版 findUsersByCondition，功能完全一致
     */
    @Select("<script>" +
            "SELECT * FROM user " +
            "<where>" +
            "  <if test='username != null and username != \"\"'>" +
            "    AND username LIKE CONCAT('%', #{username}, '%')" +
            "  </if>" +
            "  <if test='email != null and email != \"\"'>" +
            "    AND email = #{email}" +
            "  </if>" +
            "  <if test='id != null'>" +
            "    AND id = #{id}" +
            "  </if>" +
            "</where>" +
            "</script>")
    @ResultMap("userResultMap")
    List<User> findUsersAnnoDynamic(User user);

    // ==================== 动态 SQL（XML 方式）====================

    /** 1. <if> + <where> 多条件任意组合 */
    List<User> findUsersByCondition(User user);

    /** 2. <set> 选择性更新（只更新非空字段） */
    int updateUserSelective(User user);

    /** 3. <choose>/<when>/<otherwise> 互斥分支 */
    List<User> findUserPriority(User user);

    /** 4. <foreach> 批量查询（IN 条件） */
    List<User> findByIds(@Param("ids") List<Integer> ids);

    /** 5. <foreach> 批量插入 */
    int batchInsert(List<User> users);

    /** 6. <trim> 自定义裁剪 */
    List<User> findUsersByTrim(User user);

    /** 7. <sql>/<include> 复用 SQL 片段 */
    List<User> findUsersWithInclude(User user);
}
