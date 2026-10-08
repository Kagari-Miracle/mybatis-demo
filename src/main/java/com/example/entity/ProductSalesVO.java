package com.example.entity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品销售详情 VO
 * 用于查询：已售商品 → 哪个供应商提供 → 哪个客户买走 → 是否享受服务
 */
public class ProductSalesVO implements Serializable {

    private Integer productId;
    private String productName;
    private BigDecimal price;

    // 购买客户
    private Integer customerId;
    private String customerName;

    // 提供该商品的供应商
    private Integer supplierId;
    private String supplierName;

    // 是否享受供应商服务（维修记录）
    private Boolean hasService;
    private String serviceType;
    private String serviceResult;

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Integer getSupplierId() { return supplierId; }
    public void setSupplierId(Integer supplierId) { this.supplierId = supplierId; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public Boolean getHasService() { return hasService; }
    public void setHasService(Boolean hasService) { this.hasService = hasService; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String getServiceResult() { return serviceResult; }
    public void setServiceResult(String serviceResult) { this.serviceResult = serviceResult; }

    @Override
    public String toString() {
        return "ProductSalesVO{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", supplierId=" + supplierId +
                ", supplierName='" + supplierName + '\'' +
                ", hasService=" + hasService +
                ", serviceType='" + serviceType + '\'' +
                ", serviceResult='" + serviceResult + '\'' +
                '}';
    }
}
