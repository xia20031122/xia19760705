package com.ape.apesystem.service;

import com.ape.apesystem.domain.ApeVegetable;

import java.util.List;

/**
 * @author 系统
 * @version 1.0
 * @description: 智能推荐Service接口
 * @date 2024/01/26
 */
public interface ApeRecommendService {

    /**
     * 基于用户历史行为的个性化推荐
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐商品列表
     */
    List<ApeVegetable> getPersonalizedRecommend(String userId, Integer limit);

    /**
     * 热门商品推荐
     * @param limit 推荐数量
     * @return 热门商品列表
     */
    List<ApeVegetable> getHotRecommend(Integer limit);

    /**
     * 新品推荐
     * @param limit 推荐数量
     * @return 新品列表
     */
    List<ApeVegetable> getNewArrivalRecommend(Integer limit);

    /**
     * 相似商品推荐
     * @param vegetableId 当前商品ID
     * @param limit 推荐数量
     * @return 相似商品列表
     */
    List<ApeVegetable> getSimilarRecommend(String vegetableId, Integer limit);

    /**
     * 猜你喜欢（综合推荐）
     * @param userId 用户ID（可为空）
     * @param limit 推荐数量
     * @return 推荐商品列表
     */
    List<ApeVegetable> getGuessYouLike(String userId, Integer limit);

    /**
     * 基于分类的推荐
     * @param typeId 分类ID
     * @param excludeId 排除的商品ID
     * @param limit 推荐数量
     * @return 推荐商品列表
     */
    List<ApeVegetable> getTypeBasedRecommend(String typeId, String excludeId, Integer limit);
}
