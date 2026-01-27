package com.ape.apesystem.service;

import com.ape.apesystem.domain.ApeUserBehavior;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author 系统
 * @version 1.0
 * @description: 用户行为记录Service接口
 * @date 2024/01/26
 */
public interface ApeUserBehaviorService extends IService<ApeUserBehavior> {

    /**
     * 记录用户浏览行为
     */
    void recordView(String userId, String vegetableId, String typeId);

    /**
     * 记录用户收藏行为
     */
    void recordFavorite(String userId, String vegetableId, String typeId);

    /**
     * 记录用户加购行为
     */
    void recordAddCart(String userId, String vegetableId, String typeId);

    /**
     * 记录用户购买行为
     */
    void recordPurchase(String userId, String vegetableId, String typeId);

    /**
     * 记录用户搜索行为
     */
    void recordSearch(String userId, String keyword);
}
