package com.ape.apeadmin.controller.recommend;

import com.alibaba.fastjson2.JSONObject;
import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.domain.ApeVegetable;
import com.ape.apesystem.service.ApeRecommendService;
import com.ape.apesystem.service.ApeUserBehaviorService;
import com.ape.apesystem.service.ApeVegetableService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 系统
 * @version 1.0
 * @description: 智能推荐Controller
 * @date 2024/01/26
 */
@Controller
@ResponseBody
@RequestMapping("recommend")
public class ApeRecommendController {

    @Autowired
    private ApeRecommendService recommendService;

    @Autowired
    private ApeUserBehaviorService behaviorService;

    @Autowired
    private ApeVegetableService vegetableService;

    /**
     * 获取个性化推荐
     */
    @GetMapping("personalized")
    public Result getPersonalizedRecommend(@RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        String userId = null;
        try {
            ApeUser user = ShiroUtils.getUserInfo();
            userId = user != null ? user.getId() : null;
        } catch (Exception e) {
            // 未登录用户
        }
        
        List<ApeVegetable> list = recommendService.getPersonalizedRecommend(userId, limit);
        return Result.success(list);
    }

    /**
     * 获取热门商品推荐
     */
    @GetMapping("hot")
    public Result getHotRecommend(@RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        List<ApeVegetable> list = recommendService.getHotRecommend(limit);
        return Result.success(list);
    }

    /**
     * 获取新品推荐
     */
    @GetMapping("newArrival")
    public Result getNewArrivalRecommend(@RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        List<ApeVegetable> list = recommendService.getNewArrivalRecommend(limit);
        return Result.success(list);
    }

    /**
     * 获取相似商品推荐
     */
    @GetMapping("similar")
    public Result getSimilarRecommend(@RequestParam("vegetableId") String vegetableId,
                                       @RequestParam(value = "limit", defaultValue = "6") Integer limit) {
        List<ApeVegetable> list = recommendService.getSimilarRecommend(vegetableId, limit);
        return Result.success(list);
    }

    /**
     * 猜你喜欢（综合推荐）
     */
    @GetMapping("guessYouLike")
    public Result getGuessYouLike(@RequestParam(value = "limit", defaultValue = "12") Integer limit) {
        String userId = null;
        try {
            ApeUser user = ShiroUtils.getUserInfo();
            userId = user != null ? user.getId() : null;
        } catch (Exception e) {
            // 未登录用户
        }
        
        List<ApeVegetable> list = recommendService.getGuessYouLike(userId, limit);
        return Result.success(list);
    }

    /**
     * 基于分类的推荐
     */
    @GetMapping("byType")
    public Result getTypeBasedRecommend(@RequestParam("typeId") String typeId,
                                         @RequestParam(value = "excludeId", required = false) String excludeId,
                                         @RequestParam(value = "limit", defaultValue = "8") Integer limit) {
        List<ApeVegetable> list = recommendService.getTypeBasedRecommend(typeId, excludeId, limit);
        return Result.success(list);
    }

    /**
     * 记录用户浏览行为
     */
    @Log(name = "记录浏览行为", type = BusinessType.OTHER)
    @PostMapping("recordView")
    public Result recordView(@RequestParam("vegetableId") String vegetableId) {
        try {
            ApeUser user = ShiroUtils.getUserInfo();
            if (user != null) {
                ApeVegetable vegetable = vegetableService.getById(vegetableId);
                if (vegetable != null) {
                    behaviorService.recordView(user.getId(), vegetableId, vegetable.getType());
                }
            }
        } catch (Exception e) {
            // 未登录用户不记录
        }
        return Result.success();
    }

    /**
     * 记录用户搜索行为
     */
    @PostMapping("recordSearch")
    public Result recordSearch(@RequestParam("keyword") String keyword) {
        try {
            ApeUser user = ShiroUtils.getUserInfo();
            if (user != null && StringUtils.isNotBlank(keyword)) {
                behaviorService.recordSearch(user.getId(), keyword);
            }
        } catch (Exception e) {
            // 未登录用户不记录
        }
        return Result.success();
    }

    /**
     * 记录用户行为（通用接口）
     * @param jsonObject 请求参数，包含：
     *                   - vegetableId: 商品ID（可选）
     *                   - type: 行为类型: 1浏览 2收藏 3加购 4购买 5搜索
     *                   - keyword: 搜索关键词（仅搜索行为需要，可选）
     */
    @PostMapping("recordBehavior")
    public Result recordBehavior(@RequestBody JSONObject jsonObject) {
        try {
            ApeUser user = ShiroUtils.getUserInfo();
            if (user == null) {
                // 未登录用户不记录
                return Result.success();
            }

            String userId = user.getId();
            String vegetableId = jsonObject.getString("vegetableId");
            Integer type = jsonObject.getInteger("type");
            String keyword = jsonObject.getString("keyword");

            if (type == null) {
                return Result.success();
            }

            // 根据行为类型调用相应的记录方法
            switch (type) {
                case 1: // 浏览
                    if (StringUtils.isNotBlank(vegetableId)) {
                        ApeVegetable vegetable = vegetableService.getById(vegetableId);
                        if (vegetable != null) {
                            behaviorService.recordView(userId, vegetableId, vegetable.getType());
                        }
                    }
                    break;
                case 2: // 收藏
                    if (StringUtils.isNotBlank(vegetableId)) {
                        ApeVegetable vegetable = vegetableService.getById(vegetableId);
                        if (vegetable != null) {
                            behaviorService.recordFavorite(userId, vegetableId, vegetable.getType());
                        }
                    }
                    break;
                case 3: // 加购
                    if (StringUtils.isNotBlank(vegetableId)) {
                        ApeVegetable vegetable = vegetableService.getById(vegetableId);
                        if (vegetable != null) {
                            behaviorService.recordAddCart(userId, vegetableId, vegetable.getType());
                        }
                    }
                    break;
                case 4: // 购买
                    if (StringUtils.isNotBlank(vegetableId)) {
                        ApeVegetable vegetable = vegetableService.getById(vegetableId);
                        if (vegetable != null) {
                            behaviorService.recordPurchase(userId, vegetableId, vegetable.getType());
                        }
                    }
                    break;
                case 5: // 搜索
                    if (StringUtils.isNotBlank(keyword)) {
                        behaviorService.recordSearch(userId, keyword);
                    }
                    break;
                default:
                    // 未知行为类型，忽略
                    break;
            }
        } catch (Exception e) {
            // 记录失败不影响主流程，静默处理
        }
        return Result.success();
    }
}
