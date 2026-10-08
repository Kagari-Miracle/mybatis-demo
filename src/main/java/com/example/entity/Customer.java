package com.example.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 客户实体（customer 表）
 *
 * 关联关系：
 *   - 购买的商品（一对多）：products
 *   - 服务过的供应商（多对多）：suppliers
 */
public class Customer implements Serializable {

    private Integer id;
    private String name;
    private String phone;
    private String address;

    // 一对多：购买的商品列表
    private List<Product> products;

    // 多对多：服务过该客户的供应商列表
    private List<Supplier> suppliers;

    public Customer() {
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

    public List<Supplier> getSuppliers() { return suppliers; }
    public void setSuppliers(List<Supplier> suppliers) { this.suppliers = suppliers; }

    @Override
    public String toString() {
        return "Customer{id=" + id + ", name='" + name + '\'' +
                ", phone='" + phone + '\'' + ", address='" + address + '\'' + '}';
    }
}
