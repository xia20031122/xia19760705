package com.ape.apesystem.service;

import com.ape.apesystem.domain.ApeFreshnessConfig;
import com.ape.apesystem.domain.ApeFreshnessInfo;
import com.ape.apesystem.domain.ApeVegetable;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;

/**
 * @author 系统
 * @version 1.0
 * @description: 保鲜期管理Service接口
 * @date 2024/01/26
 */
public interface ApeFreshnessService extends IService<ApeFreshnessConfig> {

    /**
     * 分页查询保鲜期配置
     */
    Page<ApeFreshnessConfig> getConfigPage(ApeFreshnessConfig config);

    /**
     * 根据分类ID获取保鲜期配置
     */
    ApeFreshnessConfig getConfigByTypeId(String typeId);

    /**
     * 计算商品新鲜度信息
     * @param vegetable 商品
     * @return 新鲜度信息
     */
    ApeFreshnessInfo calculateFreshness(ApeVegetable vegetable);

    /**
     * 批量计算商品新鲜度
     * @param vegetables 商品列表
     * @return 新鲜度信息列表
     */
    List<ApeFreshnessInfo> batchCalculateFreshness(List<ApeVegetable> vegetables);

    /**
     * 获取临期商品列表
     * @param warningDays 预警天数
     * @return 临期商品列表
     */
    List<ApeFreshnessInfo> getNearExpiryList(Integer warningDays);

    /**
     * 获取已过期商品列表
     * @return 过期商品列表
     */
    List<ApeFreshnessInfo> getExpiredList();

    /**
     * 更新商品采摘日期
     * @param vegetableId 商品ID
     * @param harvestDate 采摘日期
     * @return 是否成功
     */
    boolean updateHarvestDate(String vegetableId, Date harvestDate);

    /**
     * 获取默认保鲜期（天）
     */
    int getDefaultShelfLife();
}
