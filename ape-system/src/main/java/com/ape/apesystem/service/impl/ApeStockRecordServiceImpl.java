package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeStockRecord;
import com.ape.apesystem.mapper.ApeStockRecordMapper;
import com.ape.apesystem.service.ApeStockRecordService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author 系统
 * @version 1.0
 * @description: 库存变动记录Service实现类
 * @date 2024/01/26
 */
@Service
public class ApeStockRecordServiceImpl extends ServiceImpl<ApeStockRecordMapper, ApeStockRecord> implements ApeStockRecordService {

    @Override
    public Page<ApeStockRecord> getRecordPage(ApeStockRecord record) {
        Page<ApeStockRecord> page = new Page<>(record.getPageNumber(), record.getPageSize());
        return baseMapper.selectRecordPage(page, record);
    }

    @Override
    public boolean addRecord(String stockId, String vegetableId, Integer type, Integer quantity,
                             Integer beforeQuantity, Integer afterQuantity, String orderId,
                             String operatorId, String operatorName, String remark) {
        ApeStockRecord record = new ApeStockRecord();
        record.setStockId(stockId);
        record.setVegetableId(vegetableId);
        record.setType(type);
        record.setQuantity(quantity);
        record.setBeforeQuantity(beforeQuantity);
        record.setAfterQuantity(afterQuantity);
        record.setOrderId(orderId);
        record.setOperatorId(operatorId);
        record.setOperatorName(operatorName);
        record.setRemark(remark);
        record.setCreateTime(new Date());
        return save(record);
    }
}
