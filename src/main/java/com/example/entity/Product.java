package com.example.entity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品实体（product 表）
 *
 * 关联关系：
 *   - 被客户买走（多对一）：customer
 *   - 由供应商提供（多对多）：suppliers
 *   - 维修记录（通过供应商-客户中间表）
 */
public class Product implements Serializable {

    private Integer id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private Integer customerId;

    // 多对一：购买该商品的客户
    private Customer customer;

    public Product() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + '\'' +
                ", price=" + price + ", stock=" + stock +
                ", customerId=" + customerId + '}';
    }
}
