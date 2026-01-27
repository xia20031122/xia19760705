package com.ape.apesystem.mapper;

import com.ape.apesystem.domain.ApeSettlement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 系统
 * @version 1.0
 * @description: 结算单Mapper
 * @date 2024/01/26
 */
public interface ApeSettlementMapper extends BaseMapper<ApeSettlement> {

    /**
     * 统计供应商待结算金额
     */
    Map<String, Object> sumPendingSettlement(@Param("supplierId") String supplierId);

    /**
     * 统计日/月/年销售额
     */
    List<Map<String, Object>> statisticsSales(@Param("type") String type, 
                                               @Param("startDate") Date startDate,
                                               @Param("endDate") Date endDate);

    /**
     * 统计各供应商销售额排行
     */
    List<Map<String, Object>> supplierSalesRank(@Param("startDate") Date startDate,
                                                 @Param("endDate") Date endDate,
                                                 @Param("limit") Integer limit);
}
