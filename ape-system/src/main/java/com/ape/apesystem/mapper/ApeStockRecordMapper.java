package com.ape.apesystem.mapper;

import com.ape.apesystem.domain.ApeStockRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author 系统
 * @version 1.0
 * @description: 库存变动记录Mapper
 * @date 2024/01/26
 */
public interface ApeStockRecordMapper extends BaseMapper<ApeStockRecord> {

    /**
     * 分页查询库存变动记录（关联商品信息）
     */
    Page<ApeStockRecord> selectRecordPage(Page<ApeStockRecord> page, @Param("record") ApeStockRecord record);
}
