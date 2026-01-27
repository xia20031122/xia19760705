package com.ape.apesystem.service;

import com.ape.apesystem.domain.ApeAfterSale;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author 系统
 * @version 1.0
 * @description: 售后申请Service接口
 * @date 2024/01/26
 */
public interface ApeAfterSaleService extends IService<ApeAfterSale> {

    /**
     * 分页查询售后申请
     */
    Page<ApeAfterSale> getAfterSalePage(ApeAfterSale afterSale);

    /**
     * 提交售后申请
     * @param afterSale 售后申请信息
     * @return 是否成功
     */
    boolean submitAfterSale(ApeAfterSale afterSale);

    /**
     * 审核售后申请
     * @param afterSaleId 售后申请ID
     * @param approved 是否同意
     * @param auditRemark 审核备注
     * @param auditorId 审核人ID
     * @param auditorName 审核人姓名
     * @return 是否成功
     */
    boolean auditAfterSale(String afterSaleId, boolean approved, String auditRemark, 
                           String auditorId, String auditorName);

    /**
     * 填写退货物流单号
     * @param afterSaleId 售后申请ID
     * @param logisticsNo 物流单号
     * @return 是否成功
     */
    boolean fillReturnLogistics(String afterSaleId, String logisticsNo);

    /**
     * 完成售后（确认收货/退款完成）
     * @param afterSaleId 售后申请ID
     * @return 是否成功
     */
    boolean completeAfterSale(String afterSaleId);

    /**
     * 取消售后申请
     * @param afterSaleId 售后申请ID
     * @param userId 用户ID（校验权限）
     * @return 是否成功
     */
    boolean cancelAfterSale(String afterSaleId, String userId);

    /**
     * 检查订单是否存在进行中的售后
     * @param orderId 订单ID
     * @return true存在 false不存在
     */
    boolean hasActiveAfterSale(String orderId);
}
