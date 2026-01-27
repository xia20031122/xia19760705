package com.ape.apeadmin.controller.aftersale;

import com.alibaba.fastjson2.JSONObject;
import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeAfterSale;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.domain.ApeVegetableOrder;
import com.ape.apesystem.service.ApeAfterSaleService;
import com.ape.apesystem.service.ApeVegetableOrderService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * @author 系统
 * @version 1.0
 * @description: 售后管理Controller
 * @date 2024/01/26
 */
@Controller
@ResponseBody
@RequestMapping("afterSale")
public class ApeAfterSaleController {

    @Autowired
    private ApeAfterSaleService apeAfterSaleService;

    @Autowired
    private ApeVegetableOrderService orderService;

    /**
     * 分页查询售后申请
     */
    @Log(name = "分页查询售后申请", type = BusinessType.OTHER)
    @PostMapping("getAfterSalePage")
    public Result getAfterSalePage(@RequestBody ApeAfterSale afterSale) {
        ApeUser user = ShiroUtils.getUserInfo();
        // 非管理员只能查看自己的售后申请
        if (user.getUserType() != 0) {
            afterSale.setUserId(user.getId());
        }
        Page<ApeAfterSale> page = apeAfterSaleService.getAfterSalePage(afterSale);
        return Result.success(page);
    }

    /**
     * 根据ID获取售后详情
     */
    @Log(name = "获取售后详情", type = BusinessType.OTHER)
    @GetMapping("getAfterSaleById")
    public Result getAfterSaleById(@RequestParam("id") String id) {
        ApeAfterSale afterSale = apeAfterSaleService.getById(id);
        if (afterSale == null) {
            return Result.fail("售后申请不存在");
        }
        
        // 检查权限
        ApeUser user = ShiroUtils.getUserInfo();
        if (user.getUserType() != 0 && !afterSale.getUserId().equals(user.getId())) {
            return Result.fail("无权查看此售后申请");
        }
        
        return Result.success(afterSale);
    }

    /**
     * 提交售后申请
     */
    @Log(name = "提交售后申请", type = BusinessType.INSERT)
    @PostMapping("submitAfterSale")
    public Result submitAfterSale(@RequestBody ApeAfterSale afterSale) {
        if (StringUtils.isBlank(afterSale.getOrderId())) {
            return Result.fail("订单ID不能为空");
        }
        if (afterSale.getType() == null) {
            return Result.fail("售后类型不能为空");
        }
        if (StringUtils.isBlank(afterSale.getReason())) {
            return Result.fail("申请原因不能为空");
        }

        // 获取订单信息
        ApeVegetableOrder order = orderService.getById(afterSale.getOrderId());
        if (order == null) {
            return Result.fail("订单不存在");
        }

        // 检查是否是本人的订单
        ApeUser user = ShiroUtils.getUserInfo();
        if (!order.getUserId().equals(user.getId())) {
            return Result.fail("无权对此订单申请售后");
        }

        // 设置用户信息
        afterSale.setUserId(user.getId());
        afterSale.setUserName(user.getUserName());
        
        // 设置退款金额（默认全额退款）
        if (afterSale.getRefundAmount() == null) {
            afterSale.setRefundAmount(BigDecimal.valueOf(order.getPrice()));
        }

        // 检查是否已有进行中的售后
        if (apeAfterSaleService.hasActiveAfterSale(afterSale.getOrderId())) {
            return Result.fail("该订单已有进行中的售后申请");
        }

        boolean result = apeAfterSaleService.submitAfterSale(afterSale);
        if (result) {
            return Result.success(afterSale);
        }
        return Result.fail("提交售后申请失败");
    }

    /**
     * 审核售后申请
     */
    @Log(name = "审核售后申请", type = BusinessType.UPDATE)
    @PostMapping("auditAfterSale")
    public Result auditAfterSale(@RequestBody JSONObject json) {
        String afterSaleId = json.getString("afterSaleId");
        Boolean approved = json.getBoolean("approved");
        String auditRemark = json.getString("auditRemark");

        if (StringUtils.isBlank(afterSaleId) || approved == null) {
            return Result.fail("参数不完整");
        }

        ApeUser user = ShiroUtils.getUserInfo();
        // 只有管理员可以审核
        if (user.getUserType() != 0) {
            return Result.fail("无权审核售后申请");
        }

        boolean result = apeAfterSaleService.auditAfterSale(afterSaleId, approved, auditRemark,
                user.getId(), user.getUserName());
        if (result) {
            return Result.success(approved ? "审核通过" : "审核拒绝");
        }
        return Result.fail("审核失败，售后申请状态异常");
    }

    /**
     * 填写退货物流单号
     */
    @Log(name = "填写退货物流", type = BusinessType.UPDATE)
    @PostMapping("fillReturnLogistics")
    public Result fillReturnLogistics(@RequestBody JSONObject json) {
        String afterSaleId = json.getString("afterSaleId");
        String logisticsNo = json.getString("logisticsNo");

        if (StringUtils.isBlank(afterSaleId) || StringUtils.isBlank(logisticsNo)) {
            return Result.fail("参数不完整");
        }

        // 检查是否是本人的售后申请
        ApeAfterSale afterSale = apeAfterSaleService.getById(afterSaleId);
        if (afterSale == null) {
            return Result.fail("售后申请不存在");
        }

        ApeUser user = ShiroUtils.getUserInfo();
        if (!afterSale.getUserId().equals(user.getId())) {
            return Result.fail("无权操作此售后申请");
        }

        boolean result = apeAfterSaleService.fillReturnLogistics(afterSaleId, logisticsNo);
        if (result) {
            return Result.success("物流单号填写成功");
        }
        return Result.fail("填写失败，售后状态异常");
    }

    /**
     * 完成售后（管理员确认收货/完成退款）
     */
    @Log(name = "完成售后", type = BusinessType.UPDATE)
    @PostMapping("completeAfterSale")
    public Result completeAfterSale(@RequestBody JSONObject json) {
        String afterSaleId = json.getString("afterSaleId");

        if (StringUtils.isBlank(afterSaleId)) {
            return Result.fail("售后ID不能为空");
        }

        ApeUser user = ShiroUtils.getUserInfo();
        // 只有管理员可以完成售后
        if (user.getUserType() != 0) {
            return Result.fail("无权操作");
        }

        boolean result = apeAfterSaleService.completeAfterSale(afterSaleId);
        if (result) {
            return Result.success("售后已完成");
        }
        return Result.fail("操作失败，售后状态异常");
    }

    /**
     * 取消售后申请
     */
    @Log(name = "取消售后申请", type = BusinessType.UPDATE)
    @PostMapping("cancelAfterSale")
    public Result cancelAfterSale(@RequestBody JSONObject json) {
        String afterSaleId = json.getString("afterSaleId");

        if (StringUtils.isBlank(afterSaleId)) {
            return Result.fail("售后ID不能为空");
        }

        ApeUser user = ShiroUtils.getUserInfo();
        boolean result = apeAfterSaleService.cancelAfterSale(afterSaleId, user.getId());
        if (result) {
            return Result.success("售后申请已取消");
        }
        return Result.fail("取消失败，可能无权操作或状态不允许取消");
    }

    /**
     * 检查订单是否可以申请售后
     */
    @GetMapping("checkCanApply")
    public Result checkCanApply(@RequestParam("orderId") String orderId) {
        ApeVegetableOrder order = orderService.getById(orderId);
        if (order == null) {
            return Result.fail("订单不存在");
        }

        // 检查是否是本人的订单
        ApeUser user = ShiroUtils.getUserInfo();
        if (!order.getUserId().equals(user.getId())) {
            return Result.fail("无权操作此订单");
        }

        JSONObject result = new JSONObject();
        // 只有已完成的订单可以申请售后
        boolean canApply = order.getState() == 3;
        // 检查是否已有进行中的售后
        boolean hasActive = apeAfterSaleService.hasActiveAfterSale(orderId);
        
        result.put("canApply", canApply && !hasActive);
        result.put("orderState", order.getState());
        result.put("hasActiveAfterSale", hasActive);
        
        if (!canApply) {
            result.put("reason", "只有已完成的订单可以申请售后");
        } else if (hasActive) {
            result.put("reason", "该订单已有进行中的售后申请");
        }
        
        return Result.success(result);
    }
}
