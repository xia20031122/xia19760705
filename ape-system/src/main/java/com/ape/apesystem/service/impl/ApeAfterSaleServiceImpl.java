package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeAfterSale;
import com.ape.apesystem.domain.ApeStock;
import com.ape.apesystem.domain.ApeVegetableOrder;
import com.ape.apesystem.mapper.ApeAfterSaleMapper;
import com.ape.apesystem.service.ApeAfterSaleService;
import com.ape.apesystem.service.ApeStockService;
import com.ape.apesystem.service.ApeVegetableOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author 系统
 * @version 1.0
 * @description: 售后申请Service实现类
 * @date 2024/01/26
 */
@Service
public class ApeAfterSaleServiceImpl extends ServiceImpl<ApeAfterSaleMapper, ApeAfterSale> implements ApeAfterSaleService {

    @Autowired
    private ApeVegetableOrderService orderService;

    @Autowired
    private ApeStockService stockService;

    @Override
    public Page<ApeAfterSale> getAfterSalePage(ApeAfterSale afterSale) {
        Page<ApeAfterSale> page = new Page<>(afterSale.getPageNumber(), afterSale.getPageSize());
        QueryWrapper<ApeAfterSale> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(afterSale.getUserId()), ApeAfterSale::getUserId, afterSale.getUserId())
                .eq(StringUtils.isNotBlank(afterSale.getOrderId()), ApeAfterSale::getOrderId, afterSale.getOrderId())
                .like(StringUtils.isNotBlank(afterSale.getAfterSaleNo()), ApeAfterSale::getAfterSaleNo, afterSale.getAfterSaleNo())
                .like(StringUtils.isNotBlank(afterSale.getOrderNumber()), ApeAfterSale::getOrderNumber, afterSale.getOrderNumber())
                .eq(afterSale.getType() != null, ApeAfterSale::getType, afterSale.getType())
                .eq(afterSale.getStatus() != null, ApeAfterSale::getStatus, afterSale.getStatus())
                .orderByDesc(ApeAfterSale::getCreateTime);
        return page(page, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitAfterSale(ApeAfterSale afterSale) {
        // 检查订单是否存在
        ApeVegetableOrder order = orderService.getById(afterSale.getOrderId());
        if (order == null) {
            return false;
        }

        // 检查订单状态是否可以申请售后（已完成的订单可以申请）
        if (order.getState() != 3) {
            return false;
        }

        // 检查是否已有进行中的售后
        if (hasActiveAfterSale(afterSale.getOrderId())) {
            return false;
        }

        // 生成售后单号
        afterSale.setAfterSaleNo("AS" + IdWorker.getMillisecond());
        afterSale.setOrderNumber(order.getOrderNumber());
        afterSale.setVegetableId(order.getVegetableId());
        afterSale.setVegetableName(order.getName());
        afterSale.setVegetableImages(order.getImages());
        afterSale.setQuantity(order.getNum());
        afterSale.setStatus(ApeAfterSale.AfterSaleStatus.PENDING);
        afterSale.setCreateTime(new Date());
        afterSale.setUpdateTime(new Date());

        // 更新订单状态为退款中
        order.setState(5);
        orderService.updateById(order);

        return save(afterSale);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditAfterSale(String afterSaleId, boolean approved, String auditRemark,
                                   String auditorId, String auditorName) {
        ApeAfterSale afterSale = getById(afterSaleId);
        if (afterSale == null || afterSale.getStatus() != ApeAfterSale.AfterSaleStatus.PENDING) {
            return false;
        }

        afterSale.setAuditRemark(auditRemark);
        afterSale.setAuditorId(auditorId);
        afterSale.setAuditorName(auditorName);
        afterSale.setAuditTime(new Date());
        afterSale.setUpdateTime(new Date());

        if (approved) {
            afterSale.setStatus(ApeAfterSale.AfterSaleStatus.APPROVED);
            
            // 如果是仅退款，直接进入处理中状态
            if (afterSale.getType() == ApeAfterSale.AfterSaleType.REFUND_ONLY) {
                afterSale.setStatus(ApeAfterSale.AfterSaleStatus.PROCESSING);
            }
        } else {
            afterSale.setStatus(ApeAfterSale.AfterSaleStatus.REJECTED);
            
            // 拒绝后恢复订单状态为已完成
            ApeVegetableOrder order = orderService.getById(afterSale.getOrderId());
            if (order != null) {
                order.setState(3);
                orderService.updateById(order);
            }
        }

        return updateById(afterSale);
    }

    @Override
    public boolean fillReturnLogistics(String afterSaleId, String logisticsNo) {
        ApeAfterSale afterSale = getById(afterSaleId);
        if (afterSale == null || afterSale.getStatus() != ApeAfterSale.AfterSaleStatus.APPROVED) {
            return false;
        }

        // 只有退货退款和换货需要填写物流
        if (afterSale.getType() != ApeAfterSale.AfterSaleType.RETURN_REFUND 
            && afterSale.getType() != ApeAfterSale.AfterSaleType.EXCHANGE) {
            return false;
        }

        afterSale.setReturnLogisticsNo(logisticsNo);
        afterSale.setStatus(ApeAfterSale.AfterSaleStatus.PROCESSING);
        afterSale.setUpdateTime(new Date());

        return updateById(afterSale);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completeAfterSale(String afterSaleId) {
        ApeAfterSale afterSale = getById(afterSaleId);
        if (afterSale == null || afterSale.getStatus() != ApeAfterSale.AfterSaleStatus.PROCESSING) {
            return false;
        }

        afterSale.setStatus(ApeAfterSale.AfterSaleStatus.COMPLETED);
        afterSale.setCompleteTime(new Date());
        afterSale.setUpdateTime(new Date());

        boolean result = updateById(afterSale);

        if (result) {
            // 更新订单状态
            ApeVegetableOrder order = orderService.getById(afterSale.getOrderId());
            if (order != null) {
                if (afterSale.getType() == ApeAfterSale.AfterSaleType.EXCHANGE) {
                    // 换货完成，订单状态恢复为已完成
                    order.setState(3);
                } else {
                    // 退款完成，订单状态更新为已退款
                    order.setState(6);
                    
                    // 退货退款需要归还库存
                    if (afterSale.getType() == ApeAfterSale.AfterSaleType.RETURN_REFUND) {
                        stockService.stockIn(afterSale.getVegetableId(), afterSale.getQuantity(),
                                null, "系统", "售后退货入库，售后单号：" + afterSale.getAfterSaleNo());
                    }
                }
                orderService.updateById(order);
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelAfterSale(String afterSaleId, String userId) {
        ApeAfterSale afterSale = getById(afterSaleId);
        if (afterSale == null) {
            return false;
        }

        // 检查是否是申请人
        if (!afterSale.getUserId().equals(userId)) {
            return false;
        }

        // 只有待审核和已同意状态可以取消
        List<Integer> cancelableStatus = Arrays.asList(
                ApeAfterSale.AfterSaleStatus.PENDING,
                ApeAfterSale.AfterSaleStatus.APPROVED
        );
        if (!cancelableStatus.contains(afterSale.getStatus())) {
            return false;
        }

        afterSale.setStatus(ApeAfterSale.AfterSaleStatus.CANCELLED);
        afterSale.setUpdateTime(new Date());

        boolean result = updateById(afterSale);

        if (result) {
            // 恢复订单状态为已完成
            ApeVegetableOrder order = orderService.getById(afterSale.getOrderId());
            if (order != null && order.getState() == 5) {
                order.setState(3);
                orderService.updateById(order);
            }
        }

        return result;
    }

    @Override
    public boolean hasActiveAfterSale(String orderId) {
        QueryWrapper<ApeAfterSale> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(ApeAfterSale::getOrderId, orderId)
                .in(ApeAfterSale::getStatus, Arrays.asList(
                        ApeAfterSale.AfterSaleStatus.PENDING,
                        ApeAfterSale.AfterSaleStatus.APPROVED,
                        ApeAfterSale.AfterSaleStatus.PROCESSING
                ));
        return count(queryWrapper) > 0;
    }
}
