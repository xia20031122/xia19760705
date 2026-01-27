package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeStock;
import com.ape.apesystem.domain.ApeStockRecord;
import com.ape.apesystem.mapper.ApeStockMapper;
import com.ape.apesystem.service.ApeStockRecordService;
import com.ape.apesystem.service.ApeStockService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author 系统
 * @version 1.0
 * @description: 库存Service实现类
 * @date 2024/01/26
 */
@Service
public class ApeStockServiceImpl extends ServiceImpl<ApeStockMapper, ApeStock> implements ApeStockService {

    @Autowired
    private ApeStockRecordService stockRecordService;

    @Override
    public Page<ApeStock> getStockPage(ApeStock stock) {
        Page<ApeStock> page = new Page<>(stock.getPageNumber(), stock.getPageSize());
        return baseMapper.selectStockPage(page, stock);
    }

    @Override
    public List<ApeStock> getWarningList() {
        return baseMapper.selectWarningList();
    }

    @Override
    public ApeStock getByVegetableId(String vegetableId) {
        return baseMapper.selectByVegetableId(vegetableId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initStock(String vegetableId, Integer quantity, Integer warningQuantity) {
        ApeStock existStock = getByVegetableId(vegetableId);
        if (existStock != null) {
            // 已存在则更新
            existStock.setQuantity(quantity);
            existStock.setWarningQuantity(warningQuantity);
            existStock.setUpdateTime(new Date());
            return updateById(existStock);
        }
        // 新建库存记录
        ApeStock stock = new ApeStock();
        stock.setVegetableId(vegetableId);
        stock.setQuantity(quantity);
        stock.setWarningQuantity(warningQuantity != null ? warningQuantity : 10);
        stock.setLockedQuantity(0);
        stock.setCreateTime(new Date());
        stock.setUpdateTime(new Date());
        return save(stock);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean stockIn(String vegetableId, Integer quantity, String operatorId, String operatorName, String remark) {
        ApeStock stock = getByVegetableId(vegetableId);
        if (stock == null) {
            // 库存不存在，初始化
            initStock(vegetableId, quantity, 10);
            stock = getByVegetableId(vegetableId);
            // 记录入库日志
            stockRecordService.addRecord(stock.getId(), vegetableId, ApeStockRecord.StockRecordType.STOCK_IN,
                    quantity, 0, quantity, null, operatorId, operatorName,
                    remark != null ? remark : "初始入库");
            return true;
        }

        Integer beforeQuantity = stock.getQuantity();
        int result = baseMapper.increaseStock(vegetableId, quantity);
        if (result > 0) {
            // 记录入库日志
            stockRecordService.addRecord(stock.getId(), vegetableId, ApeStockRecord.StockRecordType.STOCK_IN,
                    quantity, beforeQuantity, beforeQuantity + quantity, null, operatorId, operatorName,
                    remark != null ? remark : "入库");
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean stockOut(String vegetableId, Integer quantity, String orderId, String operatorId, String operatorName, String remark) {
        ApeStock stock = getByVegetableId(vegetableId);
        if (stock == null || stock.getQuantity() < quantity) {
            return false;
        }

        Integer beforeQuantity = stock.getQuantity();
        int result = baseMapper.deductStock(vegetableId, quantity);
        if (result > 0) {
            // 记录出库日志
            stockRecordService.addRecord(stock.getId(), vegetableId, ApeStockRecord.StockRecordType.STOCK_OUT,
                    quantity, beforeQuantity, beforeQuantity - quantity, orderId, operatorId, operatorName,
                    remark != null ? remark : "出库");
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean adjustStock(String vegetableId, Integer adjustQuantity, String operatorId, String operatorName, String remark) {
        ApeStock stock = getByVegetableId(vegetableId);
        if (stock == null) {
            return false;
        }

        Integer beforeQuantity = stock.getQuantity();
        Integer afterQuantity = beforeQuantity + adjustQuantity;
        
        if (afterQuantity < 0) {
            return false; // 调整后库存不能为负
        }

        stock.setQuantity(afterQuantity);
        stock.setUpdateTime(new Date());
        boolean result = updateById(stock);
        
        if (result) {
            // 记录调整日志
            stockRecordService.addRecord(stock.getId(), vegetableId, ApeStockRecord.StockRecordType.ADJUST,
                    Math.abs(adjustQuantity), beforeQuantity, afterQuantity, null, operatorId, operatorName,
                    remark != null ? remark : "库存调整");
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean lockStock(String vegetableId, Integer quantity, String orderId) {
        ApeStock stock = getByVegetableId(vegetableId);
        if (stock == null) {
            return false;
        }

        // 检查可用库存是否足够
        Integer availableQuantity = stock.getQuantity() - stock.getLockedQuantity();
        if (availableQuantity < quantity) {
            return false;
        }

        int result = baseMapper.lockStock(vegetableId, quantity);
        if (result > 0) {
            // 记录锁定日志
            stockRecordService.addRecord(stock.getId(), vegetableId, ApeStockRecord.StockRecordType.LOCK,
                    quantity, stock.getLockedQuantity(), stock.getLockedQuantity() + quantity,
                    orderId, null, "系统", "下单锁定库存");
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unlockStock(String vegetableId, Integer quantity, String orderId) {
        ApeStock stock = getByVegetableId(vegetableId);
        if (stock == null || stock.getLockedQuantity() < quantity) {
            return false;
        }

        int result = baseMapper.unlockStock(vegetableId, quantity);
        if (result > 0) {
            // 记录解锁日志
            stockRecordService.addRecord(stock.getId(), vegetableId, ApeStockRecord.StockRecordType.UNLOCK,
                    quantity, stock.getLockedQuantity(), stock.getLockedQuantity() - quantity,
                    orderId, null, "系统", "取消订单解锁库存");
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmDeduct(String vegetableId, Integer quantity, String orderId) {
        ApeStock stock = getByVegetableId(vegetableId);
        if (stock == null) {
            return false;
        }

        Integer beforeQuantity = stock.getQuantity();
        int result = baseMapper.confirmDeduct(vegetableId, quantity);
        if (result > 0) {
            // 记录出库日志
            stockRecordService.addRecord(stock.getId(), vegetableId, ApeStockRecord.StockRecordType.STOCK_OUT,
                    quantity, beforeQuantity, beforeQuantity - quantity, orderId, null, "系统", "发货确认出库");
            return true;
        }
        return false;
    }

    @Override
    public boolean checkStock(String vegetableId, Integer quantity) {
        ApeStock stock = getByVegetableId(vegetableId);
        if (stock == null) {
            return false;
        }
        // 可用库存 = 总库存 - 锁定库存
        Integer availableQuantity = stock.getQuantity() - (stock.getLockedQuantity() != null ? stock.getLockedQuantity() : 0);
        return availableQuantity >= quantity;
    }

    @Override
    public boolean updateWarningQuantity(String vegetableId, Integer warningQuantity) {
        ApeStock stock = getByVegetableId(vegetableId);
        if (stock == null) {
            return false;
        }
        stock.setWarningQuantity(warningQuantity);
        stock.setUpdateTime(new Date());
        return updateById(stock);
    }
}
