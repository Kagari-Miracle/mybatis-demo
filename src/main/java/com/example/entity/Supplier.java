package com.example.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 供应商实体（supplier 表）
 *
 * 关联关系：
 *   - 供应的商品（多对多）：products
 *   - 服务过的客户（多对多）：customers
 */
public class Supplier implements Serializable {

    private Integer id;
    private String name;
    private String phone;
    private String address;

    // 多对多：供应的商品列表
    private List<Product> products;

    // 多对多：服务过的客户列表
    private List<Customer> customers;

    public Supplier() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }

    public List<Customer> getCustomers() { return customers; }
    public void setCustomers(List<Customer> customers) { this.customers = customers; }

    @Override
    public String toString() {
        return "Supplier{id=" + id + ", name='" + name + '\'' +
                ", phone='" + phone + '\'' + ", address='" + address + '\'' + '}';
    }
}
