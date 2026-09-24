package com.example.mapper;

import com.example.entity.Emp;
import com.example.entity.Skill;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 第05章 员工 Mapper：
 *   - 一对一 / 多对一：emp -> Dept（<association> / @One）
 *   - 多对多：emp -> List<Skill>（<collection> / @Many，经中间表 emp_skill）
 */
public interface Chapter05EmpMapper {

    // ==================== 一对一 ====================

    /**
     * 一对一（XML方式）：嵌套结果，一条 LEFT JOIN + <association>
     * 映射在 Chapter05EmpMapper.xml 的 empWithDeptMap 中
     */
    Emp one2oneByXml(Integer empno);

    /**
     * 一对一（注解方式）：@Results + @One 嵌套 select
     * 注意 select 必须写接口全限定名
     */
    @Select("SELECT e.empno, e.ename, e.job, e.sal, e.deptno, " +
            "d.deptno AS d_deptno, d.dname, d.loc " +
            "FROM emp e LEFT JOIN dept d ON e.deptno = d.deptno " +
            "WHERE e.empno = #{empno}")
    @Results({
            @Result(property = "empno",  column = "empno"),
            @Result(property = "ename",  column = "ename"),
            @Result(property = "job",    column = "job"),
            @Result(property = "sal",    column = "sal"),
            @Result(property = "deptno", column = "deptno"),
            @Result(property = "dept",   column = "deptno",
                    one = @One(select = "com.example.mapper.Chapter05DeptMapper.findById"))
    })
    Emp one2oneByAnn(Integer empno);

    // ==================== 多对一 ====================

    /**
     * 多对一（XML方式）：一次查一批员工，每人带一个部门
     * 复用 empWithDeptMap
     */
    List<Emp> many2oneByXml();

    /**
     * 多对一（注解方式）：@Results + @One
     */
    @Select("SELECT e.empno, e.ename, e.job, e.sal, e.deptno, " +
            "d.deptno AS d_deptno, d.dname, d.loc " +
            "FROM emp e LEFT JOIN dept d ON e.deptno = d.deptno")
    @Results({
            @Result(property = "empno",  column = "empno"),
            @Result(property = "ename",  column = "ename"),
            @Result(property = "job",    column = "job"),
            @Result(property = "sal",    column = "sal"),
            @Result(property = "deptno", column = "deptno"),
            @Result(property = "dept",   column = "deptno",
                    one = @One(select = "com.example.mapper.Chapter05DeptMapper.findById"))
    })
    List<Emp> many2oneByAnn();

    // ==================== 多对多 ====================

    /**
     * 多对多（XML方式）：两次 LEFT JOIN + <collection> + DISTINCT 去重
     * 映射在 Chapter05EmpMapper.xml 的 empWithSkillsMap 中
     */
    Emp many2manyByXml(Integer empno);

    /**
     * 多对多（注解方式）：@Many 调用 selectSkillsByEmpno（中间表查询）
     */
    @Select("SELECT * FROM emp WHERE empno = #{empno}")
    @Results({
            @Result(property = "empno",  column = "empno"),
            @Result(property = "ename",  column = "ename"),
            @Result(property = "job",    column = "job"),
            @Result(property = "sal",    column = "sal"),
            @Result(property = "deptno", column = "deptno"),
            @Result(property = "skills", column = "empno",
                    many = @Many(select = "com.example.mapper.Chapter05EmpMapper.selectSkillsByEmpno"))
    })
    Emp many2manyByAnn(Integer empno);

    /**
     * 中间表查询：按员工编号查其掌握的技能
     * 被上面 @Many 引用，也供 Chapter05DeptMapper 的 @Many 引用
     */
    @Select("SELECT s.id, s.name, s.description " +
            "FROM skill s " +
            "INNER JOIN emp_skill es ON s.id = es.skill_id " +
            "WHERE es.empno = #{empno}")
    List<Skill> selectSkillsByEmpno(@Param("empno") Integer empno);

    /**
     * 按部门编号查员工列表（供 Chapter05DeptMapper 的 @Many 引用）
     */
    @Select("SELECT * FROM emp WHERE deptno = #{deptno}")
    List<Emp> selectListByDeptno(@Param("deptno") Integer deptno);
}
