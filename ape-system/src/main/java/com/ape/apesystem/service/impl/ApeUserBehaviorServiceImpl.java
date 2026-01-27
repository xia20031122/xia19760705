package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeUserBehavior;
import com.ape.apesystem.mapper.ApeUserBehaviorMapper;
import com.ape.apesystem.service.ApeUserBehaviorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author 系统
 * @version 1.0
 * @description: 用户行为记录Service实现类
 * @date 2024/01/26
 */
@Service
public class ApeUserBehaviorServiceImpl extends ServiceImpl<ApeUserBehaviorMapper, ApeUserBehavior> 
        implements ApeUserBehaviorService {

    @Override
    @Async
    public void recordView(String userId, String vegetableId, String typeId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(vegetableId)) {
            return;
        }
        recordBehavior(userId, vegetableId, typeId, ApeUserBehavior.BehaviorType.VIEW, 
                ApeUserBehavior.BehaviorWeight.VIEW_WEIGHT, null);
    }

    @Override
    @Async
    public void recordFavorite(String userId, String vegetableId, String typeId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(vegetableId)) {
            return;
        }
        recordBehavior(userId, vegetableId, typeId, ApeUserBehavior.BehaviorType.FAVORITE, 
                ApeUserBehavior.BehaviorWeight.FAVORITE_WEIGHT, null);
    }

    @Override
    @Async
    public void recordAddCart(String userId, String vegetableId, String typeId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(vegetableId)) {
            return;
        }
        recordBehavior(userId, vegetableId, typeId, ApeUserBehavior.BehaviorType.ADD_CART, 
                ApeUserBehavior.BehaviorWeight.ADD_CART_WEIGHT, null);
    }

    @Override
    @Async
    public void recordPurchase(String userId, String vegetableId, String typeId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(vegetableId)) {
            return;
        }
        recordBehavior(userId, vegetableId, typeId, ApeUserBehavior.BehaviorType.PURCHASE, 
                ApeUserBehavior.BehaviorWeight.PURCHASE_WEIGHT, null);
    }

    @Override
    @Async
    public void recordSearch(String userId, String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return;
        }
        recordBehavior(userId, null, null, ApeUserBehavior.BehaviorType.SEARCH, 
                ApeUserBehavior.BehaviorWeight.SEARCH_WEIGHT, keyword);
    }

    private void recordBehavior(String userId, String vegetableId, String typeId, 
                                Integer behaviorType, Integer weight, String keyword) {
        ApeUserBehavior behavior = new ApeUserBehavior();
        behavior.setUserId(userId);
        behavior.setVegetableId(vegetableId);
        behavior.setTypeId(typeId);
        behavior.setBehaviorType(behaviorType);
        behavior.setWeight(weight);
        behavior.setKeyword(keyword);
        behavior.setCreateTime(new Date());
        save(behavior);
    }
}
