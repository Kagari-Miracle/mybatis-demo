package com.example.mapper;

import com.example.entity.ProductSalesVO;

import java.util.List;

/**
 * 商品 Mapper
 *
 * 核心查询：
 *   findSoldProducts - 查已售商品：哪个供应商提供、哪个客户买走、是否享受服务
 */
public interface ProductMapper {

    /**
     * 查询已售出的商品详情：
     *   - 商品信息
     *   - 提供该商品的供应商
     *   - 买走该商品的客户
     *   - 该供应商是否为该客户提供过服务（维修记录）
     */
    List<ProductSalesVO> findSoldProducts();
}
