package com.ape.apesystem.service;

import com.ape.apesystem.domain.ApeStock;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author 系统
 * @version 1.0
 * @description: 库存Service接口
 * @date 2024/01/26
 */
public interface ApeStockService extends IService<ApeStock> {

    /**
     * 分页查询库存
     */
    Page<ApeStock> getStockPage(ApeStock stock);

    /**
     * 查询库存预警列表
     */
    List<ApeStock> getWarningList();

    /**
     * 根据商品ID查询库存
     */
    ApeStock getByVegetableId(String vegetableId);

    /**
     * 初始化商品库存
     */
    boolean initStock(String vegetableId, Integer quantity, Integer warningQuantity);

    /**
     * 入库操作
     * @param vegetableId 商品ID
     * @param quantity 入库数量
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @param remark 备注
     */
    boolean stockIn(String vegetableId, Integer quantity, String operatorId, String operatorName, String remark);

    /**
     * 出库操作
     * @param vegetableId 商品ID
     * @param quantity 出库数量
     * @param orderId 关联订单ID
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @param remark 备注
     */
    boolean stockOut(String vegetableId, Integer quantity, String orderId, String operatorId, String operatorName, String remark);

    /**
     * 库存调整
     * @param vegetableId 商品ID
     * @param adjustQuantity 调整数量（正数增加，负数减少）
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @param remark 备注
     */
    boolean adjustStock(String vegetableId, Integer adjustQuantity, String operatorId, String operatorName, String remark);

    /**
     * 锁定库存（下单时调用）
     * @param vegetableId 商品ID
     * @param quantity 锁定数量
     * @param orderId 订单ID
     * @return true锁定成功，false库存不足
     */
    boolean lockStock(String vegetableId, Integer quantity, String orderId);

    /**
     * 解锁库存（取消订单时调用）
     * @param vegetableId 商品ID
     * @param quantity 解锁数量
     * @param orderId 订单ID
     */
    boolean unlockStock(String vegetableId, Integer quantity, String orderId);

    /**
     * 确认扣减库存（发货时调用）
     * @param vegetableId 商品ID
     * @param quantity 扣减数量
     * @param orderId 订单ID
     */
    boolean confirmDeduct(String vegetableId, Integer quantity, String orderId);

    /**
     * 检查库存是否充足
     * @param vegetableId 商品ID
     * @param quantity 需求数量
     * @return true库存充足，false库存不足
     */
    boolean checkStock(String vegetableId, Integer quantity);

    /**
     * 更新预警库存
     */
    boolean updateWarningQuantity(String vegetableId, Integer warningQuantity);
}
