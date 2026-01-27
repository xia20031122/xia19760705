package com.ape.apeadmin.controller.finance;

import com.alibaba.fastjson2.JSONObject;
import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeCommissionConfig;
import com.ape.apesystem.domain.ApeSettlement;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.domain.ApeVegetableOrder;
import com.ape.apesystem.service.ApeCommissionConfigService;
import com.ape.apesystem.service.ApeSettlementService;
import com.ape.apesystem.service.ApeVegetableOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 系统
 * @version 1.0
 * @description: 财务管理Controller
 * @date 2024/01/26
 */
@Controller
@ResponseBody
@RequestMapping("finance")
public class ApeFinanceController {

    @Autowired
    private ApeSettlementService settlementService;

    @Autowired
    private ApeCommissionConfigService commissionConfigService;

    @Autowired
    private ApeVegetableOrderService orderService;

    // ==================== 结算单管理 ====================

    /**
     * 分页查询结算单
     */
    @Log(name = "分页查询结算单", type = BusinessType.OTHER)
    @PostMapping("getSettlementPage")
    public Result getSettlementPage(@RequestBody ApeSettlement settlement) {
        ApeUser user = ShiroUtils.getUserInfo();
        // 供应商只能查看自己的结算单
        if (user.getUserType() == 2) {
            settlement.setSupplierId(user.getId());
        }
        Page<ApeSettlement> page = settlementService.getSettlementPage(settlement);
        return Result.success(page);
    }

    /**
     * 根据ID获取结算单详情
     */
    @GetMapping("getSettlementById")
    public Result getSettlementById(@RequestParam("id") String id) {
        ApeSettlement settlement = settlementService.getById(id);
        return Result.success(settlement);
    }

    /**
     * 生成结算单
     */
    @Log(name = "生成结算单", type = BusinessType.INSERT)
    @PostMapping("generateSettlement")
    public Result generateSettlement(@RequestBody JSONObject json) {
        String supplierId = json.getString("supplierId");
        Date startDate = json.getDate("startDate");
        Date endDate = json.getDate("endDate");

        if (StringUtils.isBlank(supplierId) || startDate == null || endDate == null) {
            return Result.fail("参数不完整");
        }

        ApeUser user = ShiroUtils.getUserInfo();
        // 只有管理员可以生成结算单
        if (user.getUserType() != 0) {
            return Result.fail("无权操作");
        }

        ApeSettlement settlement = settlementService.generateSettlement(supplierId, startDate, endDate);
        if (settlement != null) {
            return Result.success(settlement);
        }
        return Result.fail("该时间段内没有可结算的订单");
    }

    /**
     * 批量生成结算单
     */
    @Log(name = "批量生成结算单", type = BusinessType.INSERT)
    @PostMapping("batchGenerateSettlement")
    public Result batchGenerateSettlement(@RequestBody JSONObject json) {
        Date startDate = json.getDate("startDate");
        Date endDate = json.getDate("endDate");

        if (startDate == null || endDate == null) {
            return Result.fail("请选择结算周期");
        }

        ApeUser user = ShiroUtils.getUserInfo();
        if (user.getUserType() != 0) {
            return Result.fail("无权操作");
        }

        int count = settlementService.batchGenerateSettlement(startDate, endDate);
        JSONObject result = new JSONObject();
        result.put("count", count);
        return Result.success(result);
    }

    /**
     * 确认结算
     */
    @Log(name = "确认结算", type = BusinessType.UPDATE)
    @PostMapping("confirmSettlement")
    public Result confirmSettlement(@RequestBody JSONObject json) {
        String settlementId = json.getString("settlementId");
        if (StringUtils.isBlank(settlementId)) {
            return Result.fail("结算单ID不能为空");
        }

        ApeUser user = ShiroUtils.getUserInfo();
        if (user.getUserType() != 0) {
            return Result.fail("无权操作");
        }

        boolean result = settlementService.confirmSettlement(settlementId);
        if (result) {
            return Result.success("结算成功");
        }
        return Result.fail("结算失败，状态异常");
    }

    /**
     * 取消结算单
     */
    @Log(name = "取消结算单", type = BusinessType.UPDATE)
    @PostMapping("cancelSettlement")
    public Result cancelSettlement(@RequestBody JSONObject json) {
        String settlementId = json.getString("settlementId");
        if (StringUtils.isBlank(settlementId)) {
            return Result.fail("结算单ID不能为空");
        }

        ApeUser user = ShiroUtils.getUserInfo();
        if (user.getUserType() != 0) {
            return Result.fail("无权操作");
        }

        boolean result = settlementService.cancelSettlement(settlementId);
        if (result) {
            return Result.success("已取消");
        }
        return Result.fail("取消失败");
    }

    /**
     * 获取供应商结算概况
     */
    @GetMapping("getSettlementOverview")
    public Result getSettlementOverview(@RequestParam(value = "supplierId", required = false) String supplierId) {
        ApeUser user = ShiroUtils.getUserInfo();
        // 供应商只能查看自己的
        if (user.getUserType() == 2) {
            supplierId = user.getId();
        }
        if (StringUtils.isBlank(supplierId)) {
            return Result.fail("供应商ID不能为空");
        }

        Map<String, Object> overview = settlementService.getPendingSettlement(supplierId);
        return Result.success(overview);
    }

    // ==================== 佣金配置 ====================

    /**
     * 分页查询佣金配置
     */
    @Log(name = "分页查询佣金配置", type = BusinessType.OTHER)
    @PostMapping("getCommissionConfigPage")
    public Result getCommissionConfigPage(@RequestBody ApeCommissionConfig config) {
        Page<ApeCommissionConfig> page = commissionConfigService.getConfigPage(config);
        return Result.success(page);
    }

    /**
     * 保存佣金配置
     */
    @Log(name = "保存佣金配置", type = BusinessType.INSERT)
    @PostMapping("saveCommissionConfig")
    public Result saveCommissionConfig(@RequestBody ApeCommissionConfig config) {
        ApeUser user = ShiroUtils.getUserInfo();
        if (user.getUserType() != 0) {
            return Result.fail("无权操作");
        }

        if (config.getCommissionRate() == null) {
            return Result.fail("佣金比例不能为空");
        }

        config.setCreateTime(new Date());
        config.setUpdateTime(new Date());
        boolean result = commissionConfigService.save(config);
        if (result) {
            return Result.success();
        }
        return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
    }

    /**
     * 更新佣金配置
     */
    @Log(name = "更新佣金配置", type = BusinessType.UPDATE)
    @PostMapping("updateCommissionConfig")
    public Result updateCommissionConfig(@RequestBody ApeCommissionConfig config) {
        ApeUser user = ShiroUtils.getUserInfo();
        if (user.getUserType() != 0) {
            return Result.fail("无权操作");
        }

        config.setUpdateTime(new Date());
        boolean result = commissionConfigService.updateById(config);
        if (result) {
            return Result.success();
        }
        return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
    }

    /**
     * 删除佣金配置
     */
    @Log(name = "删除佣金配置", type = BusinessType.DELETE)
    @GetMapping("removeCommissionConfig")
    public Result removeCommissionConfig(@RequestParam("id") String id) {
        ApeUser user = ShiroUtils.getUserInfo();
        if (user.getUserType() != 0) {
            return Result.fail("无权操作");
        }

        boolean result = commissionConfigService.removeById(id);
        if (result) {
            return Result.success();
        }
        return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
    }

    // ==================== 财务报表 ====================

    /**
     * 获取销售统计报表
     */
    @GetMapping("getSalesStatistics")
    public Result getSalesStatistics(
            @RequestParam(value = "type", defaultValue = "day") String type,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        
        // 默认查询最近7天/30天
        if (startDate == null || endDate == null) {
            Calendar cal = Calendar.getInstance();
            endDate = cal.getTime();
            if ("day".equals(type)) {
                cal.add(Calendar.DAY_OF_MONTH, -7);
            } else if ("month".equals(type)) {
                cal.add(Calendar.MONTH, -6);
            } else {
                cal.add(Calendar.YEAR, -3);
            }
            startDate = cal.getTime();
        }

        // 查询已完成订单
        QueryWrapper<ApeVegetableOrder> query = new QueryWrapper<>();
        query.lambda()
                .eq(ApeVegetableOrder::getState, 3)
                .ge(ApeVegetableOrder::getCreateTime, startDate)
                .le(ApeVegetableOrder::getCreateTime, endDate)
                .orderByAsc(ApeVegetableOrder::getCreateTime);
        
        List<ApeVegetableOrder> orders = orderService.list(query);

        // 按日期分组统计
        Map<String, BigDecimal> salesByDate = new LinkedHashMap<>();
        Map<String, Integer> countByDate = new LinkedHashMap<>();
        SimpleDateFormat sdf;
        if ("day".equals(type)) {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        } else if ("month".equals(type)) {
            sdf = new SimpleDateFormat("yyyy-MM");
        } else {
            sdf = new SimpleDateFormat("yyyy");
        }

        for (ApeVegetableOrder order : orders) {
            String dateKey = sdf.format(order.getCreateTime());
            salesByDate.merge(dateKey, BigDecimal.valueOf(order.getPrice()), BigDecimal::add);
            countByDate.merge(dateKey, 1, Integer::sum);
        }

        // 构建返回数据
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : salesByDate.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", entry.getKey());
            item.put("sales", entry.getValue());
            item.put("orders", countByDate.get(entry.getKey()));
            result.add(item);
        }

        // 计算汇总
        BigDecimal totalSales = BigDecimal.ZERO;
        int totalOrders = 0;
        for (ApeVegetableOrder order : orders) {
            totalSales = totalSales.add(BigDecimal.valueOf(order.getPrice()));
            totalOrders++;
        }

        JSONObject response = new JSONObject();
        response.put("list", result);
        response.put("totalSales", totalSales);
        response.put("totalOrders", totalOrders);
        response.put("avgOrderAmount", totalOrders > 0 ? 
                totalSales.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        return Result.success(response);
    }

    /**
     * 获取财务概览
     */
    @GetMapping("getFinanceOverview")
    public Result getFinanceOverview() {
        ApeUser user = ShiroUtils.getUserInfo();
        JSONObject overview = new JSONObject();

        // 今日销售额
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        Date todayStart = today.getTime();
        
        QueryWrapper<ApeVegetableOrder> todayQuery = new QueryWrapper<>();
        todayQuery.lambda()
                .eq(ApeVegetableOrder::getState, 3)
                .ge(ApeVegetableOrder::getCreateTime, todayStart);
        List<ApeVegetableOrder> todayOrders = orderService.list(todayQuery);
        BigDecimal todaySales = BigDecimal.ZERO;
        for (ApeVegetableOrder order : todayOrders) {
            todaySales = todaySales.add(BigDecimal.valueOf(order.getPrice()));
        }
        overview.put("todaySales", todaySales);
        overview.put("todayOrders", todayOrders.size());

        // 本月销售额
        Calendar monthStart = Calendar.getInstance();
        monthStart.set(Calendar.DAY_OF_MONTH, 1);
        monthStart.set(Calendar.HOUR_OF_DAY, 0);
        monthStart.set(Calendar.MINUTE, 0);
        monthStart.set(Calendar.SECOND, 0);
        
        QueryWrapper<ApeVegetableOrder> monthQuery = new QueryWrapper<>();
        monthQuery.lambda()
                .eq(ApeVegetableOrder::getState, 3)
                .ge(ApeVegetableOrder::getCreateTime, monthStart.getTime());
        List<ApeVegetableOrder> monthOrders = orderService.list(monthQuery);
        BigDecimal monthSales = BigDecimal.ZERO;
        for (ApeVegetableOrder order : monthOrders) {
            monthSales = monthSales.add(BigDecimal.valueOf(order.getPrice()));
        }
        overview.put("monthSales", monthSales);
        overview.put("monthOrders", monthOrders.size());

        // 待结算金额
        QueryWrapper<ApeSettlement> pendingQuery = new QueryWrapper<>();
        pendingQuery.lambda().eq(ApeSettlement::getStatus, ApeSettlement.SettlementStatus.PENDING);
        List<ApeSettlement> pendingSettlements = settlementService.list(pendingQuery);
        BigDecimal pendingAmount = BigDecimal.ZERO;
        for (ApeSettlement settlement : pendingSettlements) {
            pendingAmount = pendingAmount.add(settlement.getSettlementAmount());
        }
        overview.put("pendingSettlement", pendingAmount);
        overview.put("pendingCount", pendingSettlements.size());

        return Result.success(overview);
    }
}
