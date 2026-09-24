-- ================================================================
-- 第05章 MyBatis 关联映射：skill 表 + emp_skill 中间表
-- 多对多关系：一个员工掌握多项技能，一项技能被多个员工掌握
-- ================================================================

-- 技能表
CREATE TABLE IF NOT EXISTS skill (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255)
);

-- 中间表：联合主键，两个外键分别指向 emp 与 skill
CREATE TABLE IF NOT EXISTS emp_skill (
    empno    INT NOT NULL,
    skill_id INT NOT NULL,
    PRIMARY KEY (empno, skill_id),
    FOREIGN KEY (empno)    REFERENCES emp(empno),
    FOREIGN KEY (skill_id) REFERENCES skill(id)
);

-- 清空旧数据（先清中间表，再清技能表）
DELETE FROM emp_skill;
DELETE FROM skill;
ALTER TABLE skill AUTO_INCREMENT = 1;

-- 技能数据
INSERT INTO skill(name, description) VALUES('Java', '后端开发');
INSERT INTO skill(name, description) VALUES('MySQL', '数据库');
INSERT INTO skill(name, description) VALUES('Spring', '框架');
INSERT INTO skill(name, description) VALUES('Vue', '前端开发');
INSERT INTO skill(name, description) VALUES('Docker', '容器化');

-- 员工-技能关联数据（empno 对应 emp 表的自增主键）
-- JONES(4) 掌握 Java + MySQL
INSERT INTO emp_skill(empno, skill_id) VALUES(4, 1);
INSERT INTO emp_skill(empno, skill_id) VALUES(4, 2);
-- SCOTT(8) 掌握 Java
INSERT INTO emp_skill(empno, skill_id) VALUES(8, 1);
-- FORD(13) 掌握 Java + Spring
INSERT INTO emp_skill(empno, skill_id) VALUES(13, 1);
INSERT INTO emp_skill(empno, skill_id) VALUES(13, 3);
-- SMITH(1) 掌握 MySQL
INSERT INTO emp_skill(empno, skill_id) VALUES(1, 2);
