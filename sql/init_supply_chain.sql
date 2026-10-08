-- 供应商-商品-客户 多表关联
-- 关系：
--   供应商 多对多 商品  (supplier_product 中间表)
--   客户 一对多 商品     (product.customer_id 外键)
--   供应商 多对多 客户  (supplier_customer 中间表，记录服务/维修)

-- ==================== 供应商表 ====================
CREATE TABLE IF NOT EXISTS supplier (
    id      INT PRIMARY KEY AUTO_INCREMENT COMMENT '供应商编号',
    name    VARCHAR(100) NOT NULL COMMENT '供应商名称',
    phone   VARCHAR(20)  COMMENT '联系电话',
    address VARCHAR(200) COMMENT '地址'
);

-- ==================== 客户表 ====================
CREATE TABLE IF NOT EXISTS customer (
    id      INT PRIMARY KEY AUTO_INCREMENT COMMENT '客户编号',
    name    VARCHAR(100) NOT NULL COMMENT '客户姓名',
    phone   VARCHAR(20)  COMMENT '联系电话',
    address VARCHAR(200) COMMENT '地址'
);

-- ==================== 商品表 ====================
-- 客户一对多商品：customer_id 表示该商品被哪个客户买走（已售出）
-- 若 customer_id 为 NULL 表示未售出
CREATE TABLE IF NOT EXISTS product (
    id          INT PRIMARY KEY AUTO_INCREMENT COMMENT '商品编号',
    name        VARCHAR(100) NOT NULL COMMENT '商品名称',
    price       DECIMAL(10,2) COMMENT '价格',
    stock       INT DEFAULT 0 COMMENT '库存',
    customer_id INT COMMENT '购买客户编号（外键，NULL=未售出）',
    CONSTRAINT fk_product_customer FOREIGN KEY (customer_id) REFERENCES customer(id)
);

-- ==================== 供应商-商品 中间表（多对多）====================
CREATE TABLE IF NOT EXISTS supplier_product (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    supplier_id INT NOT NULL COMMENT '供应商编号',
    product_id  INT NOT NULL COMMENT '商品编号',
    supply_date DATE COMMENT '供货日期',
    CONSTRAINT fk_sp_supplier FOREIGN KEY (supplier_id) REFERENCES supplier(id),
    CONSTRAINT fk_sp_product  FOREIGN KEY (product_id)  REFERENCES product(id)
);

-- ==================== 供应商-客户 中间表（多对多，维修/服务记录）====================
CREATE TABLE IF NOT EXISTS supplier_customer (
    id              INT PRIMARY KEY AUTO_INCREMENT,
    supplier_id     INT NOT NULL COMMENT '供应商编号',
    customer_id     INT NOT NULL COMMENT '客户编号',
    service_type    VARCHAR(50) COMMENT '服务类型（维修/安装/咨询）',
    service_date    DATE COMMENT '服务日期',
    service_result  VARCHAR(200) COMMENT '服务结果',
    CONSTRAINT fk_sc_supplier FOREIGN KEY (supplier_id) REFERENCES supplier(id),
    CONSTRAINT fk_sc_customer FOREIGN KEY (customer_id) REFERENCES customer(id)
);

-- ==================== 测试数据 ====================
-- 清空
DELETE FROM supplier_customer;
DELETE FROM supplier_product;
DELETE FROM product;
DELETE FROM customer;
DELETE FROM supplier;

ALTER TABLE supplier AUTO_INCREMENT = 1;
ALTER TABLE customer AUTO_INCREMENT = 1;
ALTER TABLE product AUTO_INCREMENT = 1;

-- 供应商
INSERT INTO supplier (name, phone, address) VALUES
('华东电子有限公司', '021-12345678', '上海市浦东新区'),
('北方科技股份', '010-87654321', '北京市海淀区'),
('南方数码配件厂', '020-55667788', '广州市天河区');

-- 客户
INSERT INTO customer (name, phone, address) VALUES
('张三', '13800138001', '北京市朝阳区'),
('李四', '13800138002', '上海市徐汇区'),
('王五', '13800138003', '广州市越秀区');

-- 商品（customer_id 非 NULL 表示已售出）
INSERT INTO product (name, price, stock, customer_id) VALUES
('笔记本电脑', 5999.00, 50, 1),    -- 被张三买走
('无线鼠标',   129.00, 200, 2),    -- 被李四买走
('机械键盘',   399.00, 100, NULL),  -- 未售出
('显示器',     1499.00, 80, 3),    -- 被王五买走
('耳机',       299.00, 150, 1);    -- 被张三买走

-- 供应商-商品 供货关系
INSERT INTO supplier_product (supplier_id, product_id, supply_date) VALUES
(1, 1, '2026-01-10'),  -- 华东电子供笔记本
(1, 4, '2026-01-12'),  -- 华东电子供显示器
(2, 2, '2026-02-05'),  -- 北方科技供鼠标
(2, 5, '2026-02-08'),  -- 北方科技供耳机
(3, 3, '2026-03-01');  -- 南方数码供键盘

-- 供应商-客户 服务记录
INSERT INTO supplier_customer (supplier_id, customer_id, service_type, service_date, service_result) VALUES
(1, 1, '维修', '2026-05-01', '已更换主板，维修完成'),
(2, 2, '安装', '2026-05-15', '鼠标驱动安装完成'),
(1, 3, '咨询', '2026-06-01', '已解答显示器使用问题');
