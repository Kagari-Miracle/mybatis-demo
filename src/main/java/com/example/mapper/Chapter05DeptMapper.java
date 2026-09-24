package com.example.mapper;

import com.example.entity.Dept;
import org.apache.ibatis.annotations.*;

/**
 * 第05章 部门 Mapper：一对多映射（dept -> List<Emp>）
 *
 * 同时提供 findById 方法，供 Chapter05EmpMapper 的 @One 嵌套 select 调用。
 */
public interface Chapter05DeptMapper {

    /**
     * 根据部门编号查询部门（注解版，供 @One 嵌套 select 引用）
     * 全限定名: com.example.mapper.Chapter05DeptMapper.findById
     */
    @Select("SELECT * FROM dept WHERE deptno = #{deptno}")
    Dept findById(Integer deptno);

    // ==================== 一对多 ====================

    /**
     * 一对多（XML方式）：嵌套结果，一条 JOIN 拿到部门与全部员工
     * 映射在 Chapter05DeptMapper.xml 的 deptWithEmpsMap 中
     */
    Dept one2manyByXml(Integer deptno);

    /**
     * 一对多（注解方式）：@Many 嵌套 select，部门查一次，员工查一次
     */
    @Select("SELECT * FROM dept WHERE deptno = #{deptno}")
    @Results({
            @Result(property = "deptno", column = "deptno"),
            @Result(property = "dname",  column = "dname"),
            @Result(property = "loc",    column = "loc"),
            @Result(property = "emps",   column = "deptno",
                    many = @Many(select = "com.example.mapper.Chapter05EmpMapper.selectListByDeptno"))
    })
    Dept one2manyByAnn(Integer deptno);
}
