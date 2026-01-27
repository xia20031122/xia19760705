package com.ape.apesystem.mapper;

import com.ape.apesystem.domain.ApeStock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 系统
 * @version 1.0
 * @description: 库存Mapper
 * @date 2024/01/26
 */
public interface ApeStockMapper extends BaseMapper<ApeStock> {

    /**
     * 分页查询库存（关联商品信息）
     */
    Page<ApeStock> selectStockPage(Page<ApeStock> page, @Param("stock") ApeStock stock);

    /**
     * 查询库存预警列表
     */
    List<ApeStock> selectWarningList();

    /**
     * 根据商品ID查询库存
     */
    ApeStock selectByVegetableId(@Param("vegetableId") String vegetableId);

    /**
     * 扣减库存（乐观锁防超卖）
     * @return 影响行数，0表示库存不足
     */
    int deductStock(@Param("vegetableId") String vegetableId, @Param("quantity") Integer quantity);

    /**
     * 增加库存
     */
    int increaseStock(@Param("vegetableId") String vegetableId, @Param("quantity") Integer quantity);

    /**
     * 锁定库存
     */
    int lockStock(@Param("vegetableId") String vegetableId, @Param("quantity") Integer quantity);

    /**
     * 解锁库存
     */
    int unlockStock(@Param("vegetableId") String vegetableId, @Param("quantity") Integer quantity);

    /**
     * 确认扣减库存（将锁定库存转为实际出库）
     */
    int confirmDeduct(@Param("vegetableId") String vegetableId, @Param("quantity") Integer quantity);
}
