package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeUserBehavior;
import com.ape.apesystem.domain.ApeVegetable;
import com.ape.apesystem.domain.ApeVegetableFavor;
import com.ape.apesystem.domain.ApeVegetableOrder;
import com.ape.apesystem.mapper.ApeUserBehaviorMapper;
import com.ape.apesystem.service.ApeRecommendService;
import com.ape.apesystem.service.ApeVegetableFavorService;
import com.ape.apesystem.service.ApeVegetableOrderService;
import com.ape.apesystem.service.ApeVegetableService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 系统
 * @version 1.0
 * @description: 智能推荐Service实现类
 * @date 2024/01/26
 */
@Service
public class ApeRecommendServiceImpl implements ApeRecommendService {

    @Autowired
    private ApeVegetableService vegetableService;

    @Autowired
    private ApeVegetableOrderService orderService;

    @Autowired
    private ApeVegetableFavorService favorService;

    @Autowired
    private ApeUserBehaviorMapper behaviorMapper;

    @Override
    public List<ApeVegetable> getPersonalizedRecommend(String userId, Integer limit) {
        if (StringUtils.isBlank(userId)) {
            return getHotRecommend(limit);
        }

        Set<String> recommendIds = new LinkedHashSet<>();

        // 1. 获取用户偏好分类
        List<Map<String, Object>> preferredTypes = behaviorMapper.getUserPreferredTypes(userId, 5);
        List<String> typeIds = preferredTypes.stream()
                .map(m -> (String) m.get("type_id"))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 2. 基于偏好分类推荐
        if (!typeIds.isEmpty()) {
            QueryWrapper<ApeVegetable> typeQuery = new QueryWrapper<>();
            typeQuery.lambda()
                    .in(ApeVegetable::getType, typeIds)
                    .eq(ApeVegetable::getState, 1)
                    .orderByDesc(ApeVegetable::getStar)
                    .last("LIMIT " + (limit * 2));
            List<ApeVegetable> typeBasedList = vegetableService.list(typeQuery);
            recommendIds.addAll(typeBasedList.stream().map(ApeVegetable::getId).collect(Collectors.toList()));
        }

        // 3. 获取用户收藏但未购买的商品
        QueryWrapper<ApeVegetableFavor> favorQuery = new QueryWrapper<>();
        favorQuery.lambda().eq(ApeVegetableFavor::getUserId, userId);
        List<ApeVegetableFavor> favors = favorService.list(favorQuery);
        Set<String> favorIds = favors.stream().map(ApeVegetableFavor::getVegetableId).collect(Collectors.toSet());

        // 获取已购买的商品
        QueryWrapper<ApeVegetableOrder> orderQuery = new QueryWrapper<>();
        orderQuery.lambda()
                .eq(ApeVegetableOrder::getUserId, userId)
                .eq(ApeVegetableOrder::getState, 3);
        List<ApeVegetableOrder> orders = orderService.list(orderQuery);
        Set<String> purchasedIds = orders.stream().map(ApeVegetableOrder::getVegetableId).collect(Collectors.toSet());

        // 收藏但未购买
        favorIds.removeAll(purchasedIds);
        recommendIds.addAll(favorIds);

        // 4. 如果推荐不足，补充热门商品
        if (recommendIds.size() < limit) {
            List<ApeVegetable> hotList = getHotRecommend(limit);
            recommendIds.addAll(hotList.stream().map(ApeVegetable::getId).collect(Collectors.toList()));
        }

        // 5. 获取最终推荐列表
        List<String> finalIds = new ArrayList<>(recommendIds).subList(0, Math.min(recommendIds.size(), limit));
        if (finalIds.isEmpty()) {
            return getHotRecommend(limit);
        }

        QueryWrapper<ApeVegetable> finalQuery = new QueryWrapper<>();
        finalQuery.lambda()
                .in(ApeVegetable::getId, finalIds)
                .eq(ApeVegetable::getState, 1);
        return vegetableService.list(finalQuery);
    }

    @Override
    public List<ApeVegetable> getHotRecommend(Integer limit) {
        // 基于最近30天的销量统计热门商品
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date startDate = cal.getTime();

        // 统计销量
        QueryWrapper<ApeVegetableOrder> orderQuery = new QueryWrapper<>();
        orderQuery.lambda()
                .eq(ApeVegetableOrder::getState, 3)
                .ge(ApeVegetableOrder::getCreateTime, startDate)
                .select(ApeVegetableOrder::getVegetableId);
        
        List<ApeVegetableOrder> orders = orderService.list(orderQuery);
        
        // 按商品统计销量
        Map<String, Long> salesCount = orders.stream()
                .collect(Collectors.groupingBy(ApeVegetableOrder::getVegetableId, Collectors.counting()));
        
        // 按销量排序
        List<String> hotIds = salesCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (hotIds.isEmpty()) {
            // 没有销量数据，返回评分最高的商品
            QueryWrapper<ApeVegetable> query = new QueryWrapper<>();
            query.lambda()
                    .eq(ApeVegetable::getState, 1)
                    .orderByDesc(ApeVegetable::getStar)
                    .last("LIMIT " + limit);
            return vegetableService.list(query);
        }

        QueryWrapper<ApeVegetable> query = new QueryWrapper<>();
        query.lambda()
                .in(ApeVegetable::getId, hotIds)
                .eq(ApeVegetable::getState, 1);
        List<ApeVegetable> result = vegetableService.list(query);
        
        // 保持销量排序
        Map<String, ApeVegetable> vegetableMap = result.stream()
                .collect(Collectors.toMap(ApeVegetable::getId, v -> v));
        return hotIds.stream()
                .map(vegetableMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApeVegetable> getNewArrivalRecommend(Integer limit) {
        // 最近7天上架的新品
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        Date startDate = cal.getTime();

        QueryWrapper<ApeVegetable> query = new QueryWrapper<>();
        query.lambda()
                .eq(ApeVegetable::getState, 1)
                .ge(ApeVegetable::getCreateTime, startDate)
                .orderByDesc(ApeVegetable::getCreateTime)
                .last("LIMIT " + limit);
        
        List<ApeVegetable> newList = vegetableService.list(query);
        
        // 如果新品不足，扩大时间范围
        if (newList.size() < limit) {
            QueryWrapper<ApeVegetable> extendQuery = new QueryWrapper<>();
            extendQuery.lambda()
                    .eq(ApeVegetable::getState, 1)
                    .orderByDesc(ApeVegetable::getCreateTime)
                    .last("LIMIT " + limit);
            return vegetableService.list(extendQuery);
        }
        
        return newList;
    }

    @Override
    public List<ApeVegetable> getSimilarRecommend(String vegetableId, Integer limit) {
        // 获取当前商品
        ApeVegetable current = vegetableService.getById(vegetableId);
        if (current == null) {
            return Collections.emptyList();
        }

        // 基于同分类推荐
        QueryWrapper<ApeVegetable> query = new QueryWrapper<>();
        query.lambda()
                .eq(ApeVegetable::getType, current.getType())
                .eq(ApeVegetable::getState, 1)
                .ne(ApeVegetable::getId, vegetableId)
                .orderByDesc(ApeVegetable::getStar)
                .last("LIMIT " + limit);
        
        List<ApeVegetable> similarList = vegetableService.list(query);
        
        // 如果同分类不足，补充其他热门商品
        if (similarList.size() < limit) {
            int need = limit - similarList.size();
            Set<String> existIds = similarList.stream().map(ApeVegetable::getId).collect(Collectors.toSet());
            existIds.add(vegetableId);
            
            QueryWrapper<ApeVegetable> hotQuery = new QueryWrapper<>();
            hotQuery.lambda()
                    .eq(ApeVegetable::getState, 1)
                    .notIn(ApeVegetable::getId, existIds)
                    .orderByDesc(ApeVegetable::getStar)
                    .last("LIMIT " + need);
            similarList.addAll(vegetableService.list(hotQuery));
        }
        
        return similarList;
    }

    @Override
    public List<ApeVegetable> getGuessYouLike(String userId, Integer limit) {
        List<ApeVegetable> result = new ArrayList<>();
        int eachLimit = limit / 3;

        // 1. 个性化推荐
        if (StringUtils.isNotBlank(userId)) {
            result.addAll(getPersonalizedRecommend(userId, eachLimit));
        }

        // 2. 热门推荐
        List<ApeVegetable> hotList = getHotRecommend(eachLimit);
        for (ApeVegetable v : hotList) {
            if (result.stream().noneMatch(r -> r.getId().equals(v.getId()))) {
                result.add(v);
            }
        }

        // 3. 新品推荐
        List<ApeVegetable> newList = getNewArrivalRecommend(eachLimit);
        for (ApeVegetable v : newList) {
            if (result.stream().noneMatch(r -> r.getId().equals(v.getId()))) {
                result.add(v);
            }
        }

        // 打乱顺序，增加多样性
        Collections.shuffle(result);
        
        return result.subList(0, Math.min(result.size(), limit));
    }

    @Override
    public List<ApeVegetable> getTypeBasedRecommend(String typeId, String excludeId, Integer limit) {
        QueryWrapper<ApeVegetable> query = new QueryWrapper<>();
        query.lambda()
                .eq(ApeVegetable::getType, typeId)
                .eq(ApeVegetable::getState, 1);
        
        if (StringUtils.isNotBlank(excludeId)) {
            query.lambda().ne(ApeVegetable::getId, excludeId);
        }
        
        query.lambda()
                .orderByDesc(ApeVegetable::getStar)
                .last("LIMIT " + limit);
        
        return vegetableService.list(query);
    }
}
