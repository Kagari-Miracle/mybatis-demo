-- 实验一：员工表初始化脚本
-- 数据库：mybatis_db（沿用项目现有库）
-- 表名：employee

CREATE TABLE IF NOT EXISTS employee (
    emp_id     INT PRIMARY KEY AUTO_INCREMENT COMMENT '员工编号',
    emp_name   VARCHAR(50)  NOT NULL COMMENT '姓名',
    gender     CHAR(1)      DEFAULT '男' COMMENT '性别',
    dept       VARCHAR(50)  COMMENT '部门',
    post       VARCHAR(50)  COMMENT '岗位',
    salary     DECIMAL(10,2) COMMENT '薪资',
    hire_date  DATE         COMMENT '入职时间',
    status     TINYINT      DEFAULT 1 COMMENT '状态：1在职 0离职'
);

-- 清空旧数据
DELETE FROM employee;

-- 插入测试数据
INSERT INTO employee (emp_name, gender, dept, post, salary, hire_date, status) VALUES
('张伟', '男', '研发部', 'Java工程师', 12000.00, '2024-07-01', 1),
('李娜', '女', '研发部', '前端工程师', 10000.00, '2024-08-15', 1),
('王强', '男', '市场部', '市场专员', 8000.00, '2023-03-10', 1),
('赵敏', '女', '人事部', '人事专员', 7500.00, '2022-06-20', 0);
