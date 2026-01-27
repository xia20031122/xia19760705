package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeSettlement;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.domain.ApeVegetableOrder;
import com.ape.apesystem.mapper.ApeSettlementMapper;
import com.ape.apesystem.service.ApeCommissionConfigService;
import com.ape.apesystem.service.ApeSettlementService;
import com.ape.apesystem.service.ApeUserService;
import com.ape.apesystem.service.ApeVegetableOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author 系统
 * @version 1.0
 * @description: 结算单Service实现类
 * @date 2024/01/26
 */
@Service
public class ApeSettlementServiceImpl extends ServiceImpl<ApeSettlementMapper, ApeSettlement> 
        implements ApeSettlementService {

    @Autowired
    private ApeVegetableOrderService orderService;

    @Autowired
    private ApeUserService userService;

    @Autowired
    private ApeCommissionConfigService commissionConfigService;

    @Override
    public Page<ApeSettlement> getSettlementPage(ApeSettlement settlement) {
        Page<ApeSettlement> page = new Page<>(settlement.getPageNumber(), settlement.getPageSize());
        QueryWrapper<ApeSettlement> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(settlement.getSettlementNo()), ApeSettlement::getSettlementNo, settlement.getSettlementNo())
                .eq(StringUtils.isNotBlank(settlement.getSupplierId()), ApeSettlement::getSupplierId, settlement.getSupplierId())
                .eq(settlement.getStatus() != null, ApeSettlement::getStatus, settlement.getStatus())
                .orderByDesc(ApeSettlement::getCreateTime);
        return page(page, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApeSettlement generateSettlement(String supplierId, Date startDate, Date endDate) {
        // 获取供应商信息
        ApeUser supplier = userService.getById(supplierId);
        if (supplier == null) {
            return null;
        }

        // 查询该供应商在指定时间段内的已完成订单
        QueryWrapper<ApeVegetableOrder> orderQuery = new QueryWrapper<>();
        orderQuery.lambda()
                .eq(ApeVegetableOrder::getCreateBy, supplierId)
                .eq(ApeVegetableOrder::getState, 3) // 已完成
                .ge(ApeVegetableOrder::getCreateTime, startDate)
                .le(ApeVegetableOrder::getCreateTime, endDate);
        
        List<ApeVegetableOrder> orders = orderService.list(orderQuery);
        
        if (orders.isEmpty()) {
            return null;
        }

        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (ApeVegetableOrder order : orders) {
            totalAmount = totalAmount.add(BigDecimal.valueOf(order.getPrice()));
        }

        // 获取佣金比例
        BigDecimal commissionRate = commissionConfigService.getDefaultCommissionRate();

        // 计算佣金和结算金额
        BigDecimal commissionAmount = totalAmount.multiply(commissionRate)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal settlementAmount = totalAmount.subtract(commissionAmount);

        // 创建结算单
        ApeSettlement settlement = new ApeSettlement();
        settlement.setSettlementNo("ST" + IdWorker.getMillisecond());
        settlement.setSupplierId(supplierId);
        settlement.setSupplierName(supplier.getUserName());
        settlement.setTotalAmount(totalAmount);
        settlement.setOrderCount(orders.size());
        settlement.setCommissionRate(commissionRate);
        settlement.setCommissionAmount(commissionAmount);
        settlement.setSettlementAmount(settlementAmount);
        settlement.setStatus(ApeSettlement.SettlementStatus.PENDING);
        settlement.setStartDate(startDate);
        settlement.setEndDate(endDate);
        settlement.setCreateTime(new Date());
        settlement.setUpdateTime(new Date());

        save(settlement);
        return settlement;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchGenerateSettlement(Date startDate, Date endDate) {
        // 获取所有供应商（userType = 2 表示供应商）
        QueryWrapper<ApeUser> userQuery = new QueryWrapper<>();
        userQuery.lambda().eq(ApeUser::getUserType, 2);
        List<ApeUser> suppliers = userService.list(userQuery);

        int count = 0;
        for (ApeUser supplier : suppliers) {
            ApeSettlement settlement = generateSettlement(supplier.getId(), startDate, endDate);
            if (settlement != null) {
                count++;
            }
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmSettlement(String settlementId) {
        ApeSettlement settlement = getById(settlementId);
        if (settlement == null || settlement.getStatus() != ApeSettlement.SettlementStatus.PENDING) {
            return false;
        }

        settlement.setStatus(ApeSettlement.SettlementStatus.SETTLED);
        settlement.setSettlementTime(new Date());
        settlement.setUpdateTime(new Date());

        return updateById(settlement);
    }

    @Override
    public boolean cancelSettlement(String settlementId) {
        ApeSettlement settlement = getById(settlementId);
        if (settlement == null || settlement.getStatus() != ApeSettlement.SettlementStatus.PENDING) {
            return false;
        }

        settlement.setStatus(ApeSettlement.SettlementStatus.CANCELLED);
        settlement.setUpdateTime(new Date());

        return updateById(settlement);
    }

    @Override
    public Map<String, Object> getPendingSettlement(String supplierId) {
        Map<String, Object> result = new HashMap<>();
        
        // 查询待结算金额
        QueryWrapper<ApeSettlement> query = new QueryWrapper<>();
        query.lambda()
                .eq(ApeSettlement::getSupplierId, supplierId)
                .eq(ApeSettlement::getStatus, ApeSettlement.SettlementStatus.PENDING);
        
        List<ApeSettlement> pendingList = list(query);
        
        BigDecimal totalPending = BigDecimal.ZERO;
        int pendingCount = 0;
        for (ApeSettlement settlement : pendingList) {
            totalPending = totalPending.add(settlement.getSettlementAmount());
            pendingCount++;
        }

        result.put("pendingAmount", totalPending);
        result.put("pendingCount", pendingCount);
        
        // 查询已结算金额
        QueryWrapper<ApeSettlement> settledQuery = new QueryWrapper<>();
        settledQuery.lambda()
                .eq(ApeSettlement::getSupplierId, supplierId)
                .eq(ApeSettlement::getStatus, ApeSettlement.SettlementStatus.SETTLED);
        
        List<ApeSettlement> settledList = list(settledQuery);
        
        BigDecimal totalSettled = BigDecimal.ZERO;
        for (ApeSettlement settlement : settledList) {
            totalSettled = totalSettled.add(settlement.getSettlementAmount());
        }

        result.put("settledAmount", totalSettled);
        result.put("settledCount", settledList.size());

        return result;
    }

    @Override
    public BigDecimal calculateCommission(BigDecimal amount, String supplierId, String typeId) {
        BigDecimal rate = commissionConfigService.getCommissionRate(supplierId, typeId);
        return amount.multiply(rate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }
}
