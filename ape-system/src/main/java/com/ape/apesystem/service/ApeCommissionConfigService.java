package com.ape.apesystem.service;

import com.ape.apesystem.domain.ApeCommissionConfig;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
 * @author 系统
 * @version 1.0
 * @description: 佣金配置Service接口
 * @date 2024/01/26
 */
public interface ApeCommissionConfigService extends IService<ApeCommissionConfig> {

    /**
     * 分页查询佣金配置
     */
    Page<ApeCommissionConfig> getConfigPage(ApeCommissionConfig config);

    /**
     * 获取适用的佣金比例
     * @param supplierId 供应商ID
     * @param typeId 商品分类ID
     * @return 佣金比例
     */
    BigDecimal getCommissionRate(String supplierId, String typeId);

    /**
     * 获取默认佣金比例
     * @return 默认比例
     */
    BigDecimal getDefaultCommissionRate();
}
