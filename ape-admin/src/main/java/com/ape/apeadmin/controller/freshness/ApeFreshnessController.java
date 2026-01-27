package com.ape.apeadmin.controller.freshness;

import com.alibaba.fastjson2.JSONObject;
import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeFreshnessConfig;
import com.ape.apesystem.domain.ApeFreshnessInfo;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.domain.ApeVegetable;
import com.ape.apesystem.service.ApeFreshnessService;
import com.ape.apesystem.service.ApeVegetableService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author 系统
 * @version 1.0
 * @description: 保鲜期管理Controller
 * @date 2024/01/26
 */
@Controller
@ResponseBody
@RequestMapping("freshness")
public class ApeFreshnessController {

    @Autowired
    private ApeFreshnessService freshnessService;

    @Autowired
    private ApeVegetableService vegetableService;

    // ==================== 保鲜期配置管理 ====================

    /**
     * 分页查询保鲜期配置
     */
    @Log(name = "分页查询保鲜期配置", type = BusinessType.OTHER)
    @PostMapping("getConfigPage")
    public Result getConfigPage(@RequestBody ApeFreshnessConfig config) {
        Page<ApeFreshnessConfig> page = freshnessService.getConfigPage(config);
        return Result.success(page);
    }

    /**
     * 根据ID获取保鲜期配置
     */
    @GetMapping("getConfigById")
    public Result getConfigById(@RequestParam("id") String id) {
        ApeFreshnessConfig config = freshnessService.getById(id);
        return Result.success(config);
    }

    /**
     * 根据分类ID获取保鲜期配置
     */
    @GetMapping("getConfigByTypeId")
    public Result getConfigByTypeId(@RequestParam("typeId") String typeId) {
        ApeFreshnessConfig config = freshnessService.getConfigByTypeId(typeId);
        return Result.success(config);
    }

    /**
     * 保存保鲜期配置
     */
    @Log(name = "保存保鲜期配置", type = BusinessType.INSERT)
    @PostMapping("saveConfig")
    public Result saveConfig(@RequestBody ApeFreshnessConfig config) {
        ApeUser user = ShiroUtils.getUserInfo();
        if (user.getUserType() != 0) {
            return Result.fail("无权操作");
        }

        if (StringUtils.isBlank(config.getTypeId()) || config.getShelfLife() == null) {
            return Result.fail("分类ID和保鲜期不能为空");
        }

        // 检查是否已存在该分类的配置
        ApeFreshnessConfig exist = freshnessService.getConfigByTypeId(config.getTypeId());
        if (exist != null) {
            return Result.fail("该分类已存在保鲜期配置");
        }

        config.setCreateTime(new Date());
        config.setUpdateTime(new Date());
        boolean result = freshnessService.save(config);
        if (result) {
            return Result.success();
        }
        return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
    }

    /**
     * 更新保鲜期配置
     */
    @Log(name = "更新保鲜期配置", type = BusinessType.UPDATE)
    @PostMapping("updateConfig")
    public Result updateConfig(@RequestBody ApeFreshnessConfig config) {
        ApeUser user = ShiroUtils.getUserInfo();
        if (user.getUserType() != 0) {
            return Result.fail("无权操作");
        }

        config.setUpdateTime(new Date());
        boolean result = freshnessService.updateById(config);
        if (result) {
            return Result.success();
        }
        return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
    }

    /**
     * 删除保鲜期配置
     */
    @Log(name = "删除保鲜期配置", type = BusinessType.DELETE)
    @GetMapping("removeConfig")
    public Result removeConfig(@RequestParam("id") String id) {
        ApeUser user = ShiroUtils.getUserInfo();
        if (user.getUserType() != 0) {
            return Result.fail("无权操作");
        }

        boolean result = freshnessService.removeById(id);
        if (result) {
            return Result.success();
        }
        return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
    }

    // ==================== 新鲜度计算 ====================

    /**
     * 计算商品新鲜度
     */
    @GetMapping("calculateFreshness")
    public Result calculateFreshness(@RequestParam("vegetableId") String vegetableId) {
        ApeVegetable vegetable = vegetableService.getById(vegetableId);
        if (vegetable == null) {
            return Result.fail("商品不存在");
        }

        ApeFreshnessInfo info = freshnessService.calculateFreshness(vegetable);
        return Result.success(info);
    }

    /**
     * 批量计算商品新鲜度
     */
    @PostMapping("batchCalculateFreshness")
    public Result batchCalculateFreshness(@RequestBody JSONObject json) {
        List<String> vegetableIds = json.getList("vegetableIds", String.class);
        if (vegetableIds == null || vegetableIds.isEmpty()) {
            return Result.fail("商品ID列表不能为空");
        }

        QueryWrapper<ApeVegetable> query = new QueryWrapper<>();
        query.lambda().in(ApeVegetable::getId, vegetableIds);
        List<ApeVegetable> vegetables = vegetableService.list(query);

        List<ApeFreshnessInfo> infoList = freshnessService.batchCalculateFreshness(vegetables);
        return Result.success(infoList);
    }

    /**
     * 获取临期商品列表
     */
    @Log(name = "获取临期商品列表", type = BusinessType.OTHER)
    @GetMapping("getNearExpiryList")
    public Result getNearExpiryList(@RequestParam(value = "warningDays", required = false) Integer warningDays) {
        List<ApeFreshnessInfo> list = freshnessService.getNearExpiryList(warningDays);
        return Result.success(list);
    }

    /**
     * 获取已过期商品列表
     */
    @Log(name = "获取过期商品列表", type = BusinessType.OTHER)
    @GetMapping("getExpiredList")
    public Result getExpiredList() {
        List<ApeFreshnessInfo> list = freshnessService.getExpiredList();
        return Result.success(list);
    }

    /**
     * 更新商品采摘日期
     */
    @Log(name = "更新采摘日期", type = BusinessType.UPDATE)
    @PostMapping("updateHarvestDate")
    public Result updateHarvestDate(@RequestBody JSONObject json) {
        String vegetableId = json.getString("vegetableId");
        Date harvestDate = json.getDate("harvestDate");

        if (StringUtils.isBlank(vegetableId) || harvestDate == null) {
            return Result.fail("商品ID和采摘日期不能为空");
        }

        boolean result = freshnessService.updateHarvestDate(vegetableId, harvestDate);
        if (result) {
            return Result.success();
        }
        return Result.fail("更新失败");
    }

    /**
     * 批量更新采摘日期
     */
    @Log(name = "批量更新采摘日期", type = BusinessType.UPDATE)
    @PostMapping("batchUpdateHarvestDate")
    public Result batchUpdateHarvestDate(@RequestBody JSONObject json) {
        List<String> vegetableIds = json.getList("vegetableIds", String.class);
        Date harvestDate = json.getDate("harvestDate");

        if (vegetableIds == null || vegetableIds.isEmpty() || harvestDate == null) {
            return Result.fail("参数不完整");
        }

        int successCount = 0;
        for (String vegetableId : vegetableIds) {
            if (freshnessService.updateHarvestDate(vegetableId, harvestDate)) {
                successCount++;
            }
        }

        JSONObject result = new JSONObject();
        result.put("total", vegetableIds.size());
        result.put("success", successCount);
        return Result.success(result);
    }

    /**
     * 获取新鲜度统计概览
     */
    @GetMapping("getFreshnessOverview")
    public Result getFreshnessOverview() {
        // 获取所有上架商品
        QueryWrapper<ApeVegetable> query = new QueryWrapper<>();
        query.lambda().eq(ApeVegetable::getState, 1);
        List<ApeVegetable> vegetables = vegetableService.list(query);

        List<ApeFreshnessInfo> allInfo = freshnessService.batchCalculateFreshness(vegetables);

        // 统计各状态数量
        int freshCount = 0, goodCount = 0, nearExpiryCount = 0, expiredCount = 0, noDateCount = 0;

        for (ApeFreshnessInfo info : allInfo) {
            if (info.getHarvestDate() == null) {
                noDateCount++;
                continue;
            }
            switch (info.getFreshnessStatus()) {
                case ApeFreshnessInfo.FreshnessStatus.FRESH:
                    freshCount++;
                    break;
                case ApeFreshnessInfo.FreshnessStatus.GOOD:
                    goodCount++;
                    break;
                case ApeFreshnessInfo.FreshnessStatus.NEAR_EXPIRY:
                    nearExpiryCount++;
                    break;
                case ApeFreshnessInfo.FreshnessStatus.EXPIRED:
                    expiredCount++;
                    break;
            }
        }

        JSONObject overview = new JSONObject();
        overview.put("total", vegetables.size());
        overview.put("fresh", freshCount);
        overview.put("good", goodCount);
        overview.put("nearExpiry", nearExpiryCount);
        overview.put("expired", expiredCount);
        overview.put("noDate", noDateCount);

        return Result.success(overview);
    }
}
