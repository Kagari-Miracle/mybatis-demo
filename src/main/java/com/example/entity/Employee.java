package com.example.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 员工信息实体（对应数据库 emp 表）
 *
 * 字段说明：
 *   empId    员工编号（主键，自增）
 *   empName  姓名
 *   gender   性别
 *   dept     所属部门
 *   post     岗位
 *   salary   薪资
 *   hireDate 入职日期
 *   status   在职状态（1=在职，0=离职）
 */
public class Employee implements Serializable {

    private Integer empId;
    private String empName;
    private String gender;
    private String dept;
    private String post;
    private BigDecimal salary;
    private Date hireDate;
    private Integer status;

    public Employee() {
    }

    public Employee(String empName, String gender, String dept, String post,
                    BigDecimal salary, Date hireDate, Integer status) {
        this.empName = empName;
        this.gender = gender;
        this.dept = dept;
        this.post = post;
        this.salary = salary;
        this.hireDate = hireDate;
        this.status = status;
    }

    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "empId=" + empId +
                ", empName='" + empName + '\'' +
                ", gender='" + gender + '\'' +
                ", dept='" + dept + '\'' +
                ", post='" + post + '\'' +
                ", salary=" + salary +
                ", hireDate=" + hireDate +
                ", status=" + status +
                '}';
    }
}
