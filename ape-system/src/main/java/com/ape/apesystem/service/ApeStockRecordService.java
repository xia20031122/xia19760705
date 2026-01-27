package com.ape.apesystem.service;

import com.ape.apesystem.domain.ApeStockRecord;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author 系统
 * @version 1.0
 * @description: 库存变动记录Service接口
 * @date 2024/01/26
 */
public interface ApeStockRecordService extends IService<ApeStockRecord> {

    /**
     * 分页查询库存变动记录
     */
    Page<ApeStockRecord> getRecordPage(ApeStockRecord record);

    /**
     * 添加库存变动记录
     */
    boolean addRecord(String stockId, String vegetableId, Integer type, Integer quantity,
                      Integer beforeQuantity, Integer afterQuantity, String orderId,
                      String operatorId, String operatorName, String remark);
}
