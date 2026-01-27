package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeFreshnessConfig;
import com.ape.apesystem.domain.ApeFreshnessInfo;
import com.ape.apesystem.domain.ApeVegetable;
import com.ape.apesystem.mapper.ApeFreshnessConfigMapper;
import com.ape.apesystem.service.ApeFreshnessService;
import com.ape.apesystem.service.ApeVegetableService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 系统
 * @version 1.0
 * @description: 保鲜期管理Service实现类
 * @date 2024/01/26
 */
@Service
public class ApeFreshnessServiceImpl extends ServiceImpl<ApeFreshnessConfigMapper, ApeFreshnessConfig> 
        implements ApeFreshnessService {

    /** 默认保鲜期7天 */
    private static final int DEFAULT_SHELF_LIFE = 7;
    /** 默认预警天数2天 */
    private static final int DEFAULT_WARNING_DAYS = 2;

    @Autowired
    private ApeVegetableService vegetableService;

    @Override
    public Page<ApeFreshnessConfig> getConfigPage(ApeFreshnessConfig config) {
        Page<ApeFreshnessConfig> page = new Page<>(config.getPageNumber(), config.getPageSize());
        QueryWrapper<ApeFreshnessConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(config.getTypeName()), ApeFreshnessConfig::getTypeName, config.getTypeName())
                .eq(StringUtils.isNotBlank(config.getTypeId()), ApeFreshnessConfig::getTypeId, config.getTypeId())
                .eq(config.getStatus() != null, ApeFreshnessConfig::getStatus, config.getStatus())
                .orderByDesc(ApeFreshnessConfig::getCreateTime);
        return page(page, queryWrapper);
    }

    @Override
    public ApeFreshnessConfig getConfigByTypeId(String typeId) {
        if (StringUtils.isBlank(typeId)) {
            return null;
        }
        QueryWrapper<ApeFreshnessConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(ApeFreshnessConfig::getTypeId, typeId)
                .eq(ApeFreshnessConfig::getStatus, 1)
                .last("LIMIT 1");
        return getOne(queryWrapper);
    }

    @Override
    public ApeFreshnessInfo calculateFreshness(ApeVegetable vegetable) {
        if (vegetable == null) {
            return null;
        }

        ApeFreshnessInfo info = new ApeFreshnessInfo();
        info.setVegetableId(vegetable.getId());
        info.setVegetableName(vegetable.getName());
        info.setHarvestDate(vegetable.getHarvestDate());

        // 获取保鲜期配置
        ApeFreshnessConfig config = getConfigByTypeId(vegetable.getType());
        int shelfLife = config != null ? config.getShelfLife() : DEFAULT_SHELF_LIFE;
        int warningDays = config != null ? config.getWarningDays() : DEFAULT_WARNING_DAYS;
        
        info.setShelfLife(shelfLife);
        info.setStorageAdvice(config != null ? config.getFreshnessAdvice() : "请冷藏保存");

        // 计算新鲜度
        if (vegetable.getHarvestDate() == null) {
            // 没有采摘日期，默认使用创建时间
            Date baseDate = vegetable.getCreateTime() != null ? vegetable.getCreateTime() : new Date();
            info.setHarvestDate(baseDate);
        }

        Date harvestDate = info.getHarvestDate();
        Date now = new Date();

        // 计算过期日期
        Calendar cal = Calendar.getInstance();
        cal.setTime(harvestDate);
        cal.add(Calendar.DAY_OF_MONTH, shelfLife);
        Date expiryDate = cal.getTime();
        info.setExpiryDate(expiryDate);

        // 计算剩余天数
        long diffInMillis = expiryDate.getTime() - now.getTime();
        int remainingDays = (int) TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        info.setRemainingDays(remainingDays);

        // 计算新鲜度百分比
        int freshnessPercent;
        if (remainingDays <= 0) {
            freshnessPercent = 0;
        } else {
            freshnessPercent = (int) ((double) remainingDays / shelfLife * 100);
            freshnessPercent = Math.min(100, Math.max(0, freshnessPercent));
        }
        info.setFreshnessPercent(freshnessPercent);

        // 计算状态
        int status = ApeFreshnessInfo.calculateStatus(freshnessPercent);
        info.setFreshnessStatus(status);
        info.setFreshnessLabel(ApeFreshnessInfo.getStatusLabel(status));
        info.setFreshnessColor(ApeFreshnessInfo.getStatusColor(status));

        // 是否预警
        info.setIsWarning(remainingDays <= warningDays && remainingDays > 0);

        return info;
    }

    @Override
    public List<ApeFreshnessInfo> batchCalculateFreshness(List<ApeVegetable> vegetables) {
        if (vegetables == null || vegetables.isEmpty()) {
            return Collections.emptyList();
        }
        return vegetables.stream()
                .map(this::calculateFreshness)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApeFreshnessInfo> getNearExpiryList(Integer warningDays) {
        if (warningDays == null) {
            warningDays = DEFAULT_WARNING_DAYS;
        }

        // 获取所有上架商品
        QueryWrapper<ApeVegetable> query = new QueryWrapper<>();
        query.lambda()
                .eq(ApeVegetable::getState, 1)
                .isNotNull(ApeVegetable::getHarvestDate);
        List<ApeVegetable> vegetables = vegetableService.list(query);

        // 计算新鲜度并筛选临期商品
        List<ApeFreshnessInfo> allInfo = batchCalculateFreshness(vegetables);
        
        final int finalWarningDays = warningDays;
        return allInfo.stream()
                .filter(info -> info.getRemainingDays() > 0 && info.getRemainingDays() <= finalWarningDays)
                .sorted(Comparator.comparing(ApeFreshnessInfo::getRemainingDays))
                .collect(Collectors.toList());
    }

    @Override
    public List<ApeFreshnessInfo> getExpiredList() {
        // 获取所有上架商品
        QueryWrapper<ApeVegetable> query = new QueryWrapper<>();
        query.lambda()
                .eq(ApeVegetable::getState, 1)
                .isNotNull(ApeVegetable::getHarvestDate);
        List<ApeVegetable> vegetables = vegetableService.list(query);

        // 计算新鲜度并筛选过期商品
        List<ApeFreshnessInfo> allInfo = batchCalculateFreshness(vegetables);
        
        return allInfo.stream()
                .filter(info -> info.getRemainingDays() <= 0)
                .sorted(Comparator.comparing(ApeFreshnessInfo::getRemainingDays))
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateHarvestDate(String vegetableId, Date harvestDate) {
        ApeVegetable vegetable = vegetableService.getById(vegetableId);
        if (vegetable == null) {
            return false;
        }
        vegetable.setHarvestDate(harvestDate);
        return vegetableService.updateById(vegetable);
    }

    @Override
    public int getDefaultShelfLife() {
        return DEFAULT_SHELF_LIFE;
    }
}
