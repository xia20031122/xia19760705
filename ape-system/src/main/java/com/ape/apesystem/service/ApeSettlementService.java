package com.ape.apesystem.service;

import com.ape.apesystem.domain.ApeSettlement;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 系统
 * @version 1.0
 * @description: 结算单Service接口
 * @date 2024/01/26
 */
public interface ApeSettlementService extends IService<ApeSettlement> {

    /**
     * 分页查询结算单
     */
    Page<ApeSettlement> getSettlementPage(ApeSettlement settlement);

    /**
     * 生成结算单
     * @param supplierId 供应商ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 生成的结算单
     */
    ApeSettlement generateSettlement(String supplierId, Date startDate, Date endDate);

    /**
     * 批量生成所有供应商结算单
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 生成数量
     */
    int batchGenerateSettlement(Date startDate, Date endDate);

    /**
     * 确认结算
     * @param settlementId 结算单ID
     * @return 是否成功
     */
    boolean confirmSettlement(String settlementId);

    /**
     * 取消结算单
     * @param settlementId 结算单ID
     * @return 是否成功
     */
    boolean cancelSettlement(String settlementId);

    /**
     * 获取供应商待结算金额
     * @param supplierId 供应商ID
     * @return 待结算金额信息
     */
    Map<String, Object> getPendingSettlement(String supplierId);

    /**
     * 计算佣金
     * @param amount 订单金额
     * @param supplierId 供应商ID
     * @param typeId 商品分类ID
     * @return 佣金金额
     */
    BigDecimal calculateCommission(BigDecimal amount, String supplierId, String typeId);
}
