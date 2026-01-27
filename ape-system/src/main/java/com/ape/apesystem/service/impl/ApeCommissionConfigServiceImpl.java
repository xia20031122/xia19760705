package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeCommissionConfig;
import com.ape.apesystem.mapper.ApeCommissionConfigMapper;
import com.ape.apesystem.service.ApeCommissionConfigService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author 系统
 * @version 1.0
 * @description: 佣金配置Service实现类
 * @date 2024/01/26
 */
@Service
public class ApeCommissionConfigServiceImpl extends ServiceImpl<ApeCommissionConfigMapper, ApeCommissionConfig> 
        implements ApeCommissionConfigService {

    /** 默认佣金比例 5% */
    private static final BigDecimal DEFAULT_COMMISSION_RATE = new BigDecimal("5.00");

    @Override
    public Page<ApeCommissionConfig> getConfigPage(ApeCommissionConfig config) {
        Page<ApeCommissionConfig> page = new Page<>(config.getPageNumber(), config.getPageSize());
        QueryWrapper<ApeCommissionConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(config.getName()), ApeCommissionConfig::getName, config.getName())
                .eq(StringUtils.isNotBlank(config.getTypeId()), ApeCommissionConfig::getTypeId, config.getTypeId())
                .eq(StringUtils.isNotBlank(config.getSupplierId()), ApeCommissionConfig::getSupplierId, config.getSupplierId())
                .eq(config.getStatus() != null, ApeCommissionConfig::getStatus, config.getStatus())
                .orderByDesc(ApeCommissionConfig::getPriority)
                .orderByDesc(ApeCommissionConfig::getCreateTime);
        return page(page, queryWrapper);
    }

    @Override
    public BigDecimal getCommissionRate(String supplierId, String typeId) {
        Date now = new Date();
        QueryWrapper<ApeCommissionConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(ApeCommissionConfig::getStatus, 1)
                .and(wrapper -> wrapper
                        .isNull(ApeCommissionConfig::getEffectiveStartDate)
                        .or()
                        .le(ApeCommissionConfig::getEffectiveStartDate, now)
                )
                .and(wrapper -> wrapper
                        .isNull(ApeCommissionConfig::getEffectiveEndDate)
                        .or()
                        .ge(ApeCommissionConfig::getEffectiveEndDate, now)
                )
                .orderByDesc(ApeCommissionConfig::getPriority);

        List<ApeCommissionConfig> configs = list(queryWrapper);

        // 按优先级匹配规则
        for (ApeCommissionConfig config : configs) {
            // 1. 完全匹配（供应商+分类）
            if (StringUtils.isNotBlank(config.getSupplierId()) && StringUtils.isNotBlank(config.getTypeId())) {
                if (config.getSupplierId().equals(supplierId) && config.getTypeId().equals(typeId)) {
                    return config.getCommissionRate();
                }
            }
        }

        for (ApeCommissionConfig config : configs) {
            // 2. 匹配供应商（任意分类）
            if (StringUtils.isNotBlank(config.getSupplierId()) && StringUtils.isBlank(config.getTypeId())) {
                if (config.getSupplierId().equals(supplierId)) {
                    return config.getCommissionRate();
                }
            }
        }

        for (ApeCommissionConfig config : configs) {
            // 3. 匹配分类（任意供应商）
            if (StringUtils.isBlank(config.getSupplierId()) && StringUtils.isNotBlank(config.getTypeId())) {
                if (config.getTypeId().equals(typeId)) {
                    return config.getCommissionRate();
                }
            }
        }

        for (ApeCommissionConfig config : configs) {
            // 4. 全局配置
            if (StringUtils.isBlank(config.getSupplierId()) && StringUtils.isBlank(config.getTypeId())) {
                return config.getCommissionRate();
            }
        }

        // 5. 返回默认比例
        return DEFAULT_COMMISSION_RATE;
    }

    @Override
    public BigDecimal getDefaultCommissionRate() {
        return DEFAULT_COMMISSION_RATE;
    }
}
