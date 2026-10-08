package com.example;

import com.example.entity.ProductSalesVO;
import com.example.entity.Supplier;
import com.example.mapper.ProductMapper;
import com.example.mapper.SupplierMapper;
import com.example.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * 供应商-商品-客户 多表关联查询测试
 *
 * 关系：
 *   供应商 多对多 商品   (supplier_product)
 *   客户 一对多 商品      (product.customer_id)
 *   供应商 多对多 客户   (supplier_customer，服务/维修记录)
 */
public class SupplyChainTest {

    private SqlSession session;

    @Before
    public void init() {
        initDatabase();
        session = MyBatisUtil.openSession();
    }

    @After
    public void destroy() {
        if (session != null) session.close();
    }

    /** 建表 + 插入测试数据 */
    private void initDatabase() {
        try (SqlSession s = MyBatisUtil.openSession(true);
             java.sql.Connection c = s.getConnection();
             java.sql.Statement st = c.createStatement()) {

            // 删表（注意外键顺序）
            st.executeUpdate("DROP TABLE IF EXISTS supplier_customer");
            st.executeUpdate("DROP TABLE IF EXISTS supplier_product");
            st.executeUpdate("DROP TABLE IF EXISTS product");
            st.executeUpdate("DROP TABLE IF EXISTS customer");
            st.executeUpdate("DROP TABLE IF EXISTS supplier");

            // 建表
            st.executeUpdate("CREATE TABLE supplier (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(100) NOT NULL, " +
                    "phone VARCHAR(20), address VARCHAR(200))");
            st.executeUpdate("CREATE TABLE customer (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(100) NOT NULL, " +
                    "phone VARCHAR(20), address VARCHAR(200))");
            st.executeUpdate("CREATE TABLE product (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(100) NOT NULL, " +
                    "price DECIMAL(10,2), stock INT DEFAULT 0, " +
                    "customer_id INT, FOREIGN KEY (customer_id) REFERENCES customer(id))");
            st.executeUpdate("CREATE TABLE supplier_product (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, supplier_id INT NOT NULL, " +
                    "product_id INT NOT NULL, supply_date DATE, " +
                    "FOREIGN KEY (supplier_id) REFERENCES supplier(id), " +
                    "FOREIGN KEY (product_id) REFERENCES product(id))");
            st.executeUpdate("CREATE TABLE supplier_customer (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, supplier_id INT NOT NULL, " +
                    "customer_id INT NOT NULL, service_type VARCHAR(50), " +
                    "service_date DATE, service_result VARCHAR(200), " +
                    "FOREIGN KEY (supplier_id) REFERENCES supplier(id), " +
                    "FOREIGN KEY (customer_id) REFERENCES customer(id))");

            // 供应商
            st.executeUpdate("INSERT INTO supplier (name, phone, address) VALUES " +
                    "('华东电子有限公司','021-12345678','上海市浦东新区')," +
                    "('北方科技股份','010-87654321','北京市海淀区')," +
                    "('南方数码配件厂','020-55667788','广州市天河区')");

            // 客户
            st.executeUpdate("INSERT INTO customer (name, phone, address) VALUES " +
                    "('张三','13800138001','北京市朝阳区')," +
                    "('李四','13800138002','上海市徐汇区')," +
                    "('王五','13800138003','广州市越秀区')");

            // 商品（customer_id 非空=已售出）
            st.executeUpdate("INSERT INTO product (name, price, stock, customer_id) VALUES " +
                    "('笔记本电脑',5999.00,50,1)," +
                    "('无线鼠标',129.00,200,2)," +
                    "('机械键盘',399.00,100,NULL)," +
                    "('显示器',1499.00,80,3)," +
                    "('耳机',299.00,150,1)");

            // 供应商-商品
            st.executeUpdate("INSERT INTO supplier_product (supplier_id, product_id, supply_date) VALUES " +
                    "(1,1,'2026-01-10'),(1,4,'2026-01-12')," +
                    "(2,2,'2026-02-05'),(2,5,'2026-02-08')," +
                    "(3,3,'2026-03-01')");

            // 供应商-客户 服务记录
            st.executeUpdate("INSERT INTO supplier_customer (supplier_id, customer_id, service_type, service_date, service_result) VALUES " +
                    "(1,1,'维修','2026-05-01','已更换主板，维修完成')," +
                    "(2,2,'安装','2026-05-15','鼠标驱动安装完成')," +
                    "(1,3,'咨询','2026-06-01','已解答显示器使用问题')");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询1：已售商品 → 哪个供应商提供 → 哪个客户买走 → 是否享受服务
     */
    @Test
    public void testFindSoldProducts() {
        ProductMapper mapper = session.getMapper(ProductMapper.class);
        List<ProductSalesVO> list = mapper.findSoldProducts();

        System.out.println("===== 已售商品查询结果 =====");
        for (ProductSalesVO vo : list) {
            System.out.printf("商品[%s] 供应商[%s] 客户[%s] 享受服务:%s%n",
                    vo.getProductName(), vo.getSupplierName(),
                    vo.getCustomerName(), vo.getHasService());
            if (vo.getHasService()) {
                System.out.printf("  服务类型: %s, 结果: %s%n",
                        vo.getServiceType(), vo.getServiceResult());
            }
        }

        // 已售出的商品有4个（笔记本、鼠标、显示器、耳机），每个对应一个供应商
        // 注意：笔记本和耳机都被张三买走，但来自不同供应商
        assertFalse("应有已售商品记录", list.isEmpty());
        System.out.println("共 " + list.size() + " 条已售商品记录");
    }

    /**
     * 查询2：供应商 → 卖过哪些商品 → 被哪些客户买走 → 有无提供服务
     */
    @Test
    public void testFindSupplierDetail() {
        SupplierMapper mapper = session.getMapper(SupplierMapper.class);
        List<Supplier> suppliers = mapper.findSupplierDetail();

        System.out.println("===== 供应商详情查询结果 =====");
        for (Supplier sup : suppliers) {
            System.out.println("\n供应商: " + sup.getName());
            System.out.println("  供应的商品:");
            if (sup.getProducts() != null) {
                for (com.example.entity.Product p : sup.getProducts()) {
                    String buyer = (p.getCustomer() != null) ? p.getCustomer().getName() : "未售出";
                    System.out.printf("    - %s (买走客户: %s)%n", p.getName(), buyer);
                }
            }
            System.out.println("  服务过的客户:");
            if (sup.getCustomers() != null && !sup.getCustomers().isEmpty()) {
                for (com.example.entity.Customer cust : sup.getCustomers()) {
                    System.out.println("    - " + cust.getName());
                }
            } else {
                System.out.println("    （无服务记录）");
            }
        }

        assertFalse("应有供应商数据", suppliers.isEmpty());
        System.out.println("\n共 " + suppliers.size() + " 个供应商");
    }
}
