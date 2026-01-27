package com.ape.apesystem.mapper;

import com.ape.apesystem.domain.ApeUserBehavior;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 系统
 * @version 1.0
 * @description: 用户行为记录Mapper
 * @date 2024/01/26
 */
public interface ApeUserBehaviorMapper extends BaseMapper<ApeUserBehavior> {

    /**
     * 获取用户偏好分类（按权重排序）
     */
    List<Map<String, Object>> getUserPreferredTypes(@Param("userId") String userId, 
                                                     @Param("limit") Integer limit);

    /**
     * 获取用户最近浏览的商品
     */
    List<String> getRecentViewedVegetables(@Param("userId") String userId, 
                                            @Param("limit") Integer limit);

    /**
     * 获取热门商品（按行为统计）
     */
    List<Map<String, Object>> getHotVegetables(@Param("startDate") Date startDate, 
                                                @Param("limit") Integer limit);

    /**
     * 获取用户购买过的商品分类
     */
    List<String> getUserPurchasedTypes(@Param("userId") String userId);
}
