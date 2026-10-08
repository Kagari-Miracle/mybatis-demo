package com.example.mapper;

import com.example.entity.Supplier;

import java.util.List;

/**
 * 供应商 Mapper
 *
 * 核心查询：
 *   findSupplierDetail - 查供应商卖过哪些商品、被哪些客户买走、有无提供服务
 */
public interface SupplierMapper {

    /**
     * 查询供应商详情：卖过的商品 + 买走商品的客户 + 服务记录
     * 通过嵌套结果（JOIN）一次性查出供应商-商品-客户-服务的完整链路
     */
    List<Supplier> findSupplierDetail();
}
