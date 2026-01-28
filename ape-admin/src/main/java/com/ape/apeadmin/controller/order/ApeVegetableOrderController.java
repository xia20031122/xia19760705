package com.ape.apeadmin.controller.order;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.*;
import com.ape.apesystem.service.ApeCarService;
import com.ape.apesystem.service.ApeDeliveryService;
import com.ape.apesystem.service.ApeStockService;
import com.ape.apesystem.service.ApeVegetableOrderService;
import com.ape.apesystem.service.ApeVegetableService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 订单controller
 * @date 2024/03/28 03:37
 * 
 * 订单状态说明：
 * 0-待付款, 1-待发货, 2-待收货, 3-已完成, 4-已取消, 5-退款中, 6-已退款
 */
@Controller
@ResponseBody
@RequestMapping("order")
public class ApeVegetableOrderController {

    @Autowired
    private ApeVegetableOrderService apeVegetableOrderService;
    @Autowired
    private ApeVegetableService apeVegetableService;
    @Autowired
    private ApeDeliveryService apeDeliveryService;
    @Autowired
    private ApeStockService apeStockService;
    @Autowired
    private ApeCarService apeCarService;

    /** 分页获取订单 */
    @Log(name = "分页获取订单", type = BusinessType.OTHER)
    @PostMapping("getApeVegetableOrderPage")
    public Result getApeVegetableOrderPage(@RequestBody ApeVegetableOrder apeVegetableOrder) {
        // 处理分页参数，如果为null则使用默认值
        Integer pageNumber = apeVegetableOrder.getPageNumber();
        Integer pageSize = apeVegetableOrder.getPageSize();
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        
        Page<ApeVegetableOrder> page = new Page<>(pageNumber, pageSize);
        QueryWrapper<ApeVegetableOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeVegetableOrder.getUserId()),ApeVegetableOrder::getUserId,apeVegetableOrder.getUserId())
                .like(StringUtils.isNotBlank(apeVegetableOrder.getOrderNumber()),ApeVegetableOrder::getOrderNumber,apeVegetableOrder.getOrderNumber())
                .like(StringUtils.isNotBlank(apeVegetableOrder.getName()),ApeVegetableOrder::getName,apeVegetableOrder.getName())
                .eq(apeVegetableOrder.getState() != null,ApeVegetableOrder::getState,apeVegetableOrder.getState())
                .eq(StringUtils.isNotBlank(apeVegetableOrder.getCreateBy()),ApeVegetableOrder::getCreateBy,apeVegetableOrder.getCreateBy())
                .eq(apeVegetableOrder.getCreateTime() != null,ApeVegetableOrder::getCreateTime,apeVegetableOrder.getCreateTime())
                .orderByDesc(ApeVegetableOrder::getCreateTime);
        Page<ApeVegetableOrder> apeVegetableOrderPage = apeVegetableOrderService.page(page, queryWrapper);
        return Result.success(apeVegetableOrderPage);
    }

    /** 根据id获取订单 */
    @Log(name = "根据id获取订单", type = BusinessType.OTHER)
    @GetMapping("getApeVegetableOrderById")
    public Result getApeVegetableOrderById(@RequestParam("id")String id) {
        ApeVegetableOrder apeVegetableOrder = apeVegetableOrderService.getById(id);
        return Result.success(apeVegetableOrder);
    }

    /** 保存订单（带库存检查） */
    @Log(name = "保存订单", type = BusinessType.INSERT)
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("saveApeVegetableOrder")
    public Result saveApeVegetableOrder(@RequestBody ApeVegetableOrder apeVegetableOrder) {
        ApeVegetable vegetable = apeVegetableService.getById(apeVegetableOrder.getVegetableId());
        if (vegetable == null) {
            return Result.fail("商品不存在！");
        }
        
        Integer num = apeVegetableOrder.getNum() != null ? apeVegetableOrder.getNum() : 1;
        
        // 检查库存是否充足
        if (!apeStockService.checkStock(vegetable.getId(), num)) {
            ApeStock stock = apeStockService.getByVegetableId(vegetable.getId());
            int available = stock != null ? stock.getAvailableQuantity() : 0;
            return Result.fail("库存不足！当前可用库存：" + available);
        }
        
        String orderNumber = IdWorker.getMillisecond();
        apeVegetableOrder.setName(vegetable.getName());
        apeVegetableOrder.setOrderNumber(orderNumber);
        apeVegetableOrder.setUnit(vegetable.getUnit());
        apeVegetableOrder.setImages(vegetable.getImages());
        apeVegetableOrder.setPrice(vegetable.getPrice()); // 单价
        apeVegetableOrder.setTotalPrice(vegetable.getPrice() * num); // 总价
        apeVegetableOrder.setNum(num);
        apeVegetableOrder.setState(0); // 待付款
        ApeUser user = ShiroUtils.getUserInfo();
        apeVegetableOrder.setUserId(user.getId());
        
        boolean save = apeVegetableOrderService.save(apeVegetableOrder);
        if (save) {
            // 锁定库存
            apeStockService.lockStock(vegetable.getId(), num, apeVegetableOrder.getId());
            return Result.success(apeVegetableOrder);
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 从购物车创建订单（带库存检查） */
    @Log(name = "购物车下单", type = BusinessType.INSERT)
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("saveApeVegetableCarOrder")
    public Result saveApeVegetableCarOrder(@RequestBody JSONObject jsonObject) {
        String address = jsonObject.getString("address");
        String tel = jsonObject.getString("tel");
        String realName = jsonObject.getString("realName");
        Date arrivalTime = jsonObject.getDate("arrivalTime");
        JSONArray array = jsonObject.getJSONArray("arr");
        
        if (array == null || array.isEmpty()) {
            return Result.fail("购物车为空！");
        }
        
        // 第一步：检查所有商品库存
        List<Map<String, Object>> stockCheckList = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);
            ApeCar apeCar = object.toJavaObject(ApeCar.class);
            ApeVegetable vegetable = apeVegetableService.getById(apeCar.getVegetableId());
            
            if (vegetable == null) {
                return Result.fail("商品[" + apeCar.getName() + "]已下架！");
            }
            
            if (!apeStockService.checkStock(vegetable.getId(), apeCar.getNum())) {
                ApeStock stock = apeStockService.getByVegetableId(vegetable.getId());
                int available = stock != null ? stock.getAvailableQuantity() : 0;
                return Result.fail("商品[" + vegetable.getName() + "]库存不足！当前可用：" + available);
            }
            
            Map<String, Object> item = new HashMap<>();
            item.put("car", apeCar);
            item.put("vegetable", vegetable);
            stockCheckList.add(item);
        }
        
        // 第二步：创建订单并锁定库存
        List<String> orderIds = new ArrayList<>();
        List<String> carIds = new ArrayList<>();
        for (Map<String, Object> item : stockCheckList) {
            ApeCar apeCar = (ApeCar) item.get("car");
            ApeVegetable vegetable = (ApeVegetable) item.get("vegetable");
            
            ApeVegetableOrder apeVegetableOrder = new ApeVegetableOrder();
            apeVegetableOrder.setVegetableId(vegetable.getId());
            apeVegetableOrder.setOrderNumber(IdWorker.getMillisecond());
            apeVegetableOrder.setName(vegetable.getName());
            apeVegetableOrder.setNum(apeCar.getNum());
            apeVegetableOrder.setPrice(vegetable.getPrice()); // 单价
            apeVegetableOrder.setTotalPrice(vegetable.getPrice() * apeCar.getNum()); // 总价
            apeVegetableOrder.setUnit(vegetable.getUnit());
            apeVegetableOrder.setImages(vegetable.getImages());
            apeVegetableOrder.setUserId(ShiroUtils.getUserInfo().getId());
            apeVegetableOrder.setRealName(realName);
            apeVegetableOrder.setTel(tel);
            apeVegetableOrder.setArrivalTime(arrivalTime);
            apeVegetableOrder.setAddress(address);
            apeVegetableOrder.setState(0); // 待付款
            
            apeVegetableOrderService.save(apeVegetableOrder);
            orderIds.add(apeVegetableOrder.getId());
            carIds.add(apeCar.getId());
            
            // 锁定库存
            apeStockService.lockStock(vegetable.getId(), apeCar.getNum(), apeVegetableOrder.getId());
        }
        
        // 第三步：删除购物车中已下单的商品
        for (String carId : carIds) {
            apeCarService.removeById(carId);
        }
        
        JSONObject result = new JSONObject();
        result.put("orderIds", orderIds);
        result.put("count", orderIds.size());
        return Result.success(result);
    }

    /** 订单支付（模拟） */
    @Log(name = "订单支付", type = BusinessType.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("payOrder")
    public Result payOrder(@RequestBody JSONObject json) {
        String orderId = json.getString("orderId");
        if (StringUtils.isBlank(orderId)) {
            return Result.fail("订单ID不能为空！");
        }
        
        ApeVegetableOrder order = apeVegetableOrderService.getById(orderId);
        if (order == null) {
            return Result.fail("订单不存在！");
        }
        if (order.getState() != 0) {
            return Result.fail("订单状态异常，无法支付！");
        }
        
        // 更新订单状态为待发货
        order.setState(1);
        boolean update = apeVegetableOrderService.updateById(order);
        if (update) {
            return Result.success("支付成功");
        }
        return Result.fail("支付失败");
    }

    /** 订单发货 */
    @Log(name = "订单发货", type = BusinessType.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("shipOrder")
    public Result shipOrder(@RequestBody JSONObject json) {
        String orderId = json.getString("orderId");
        String deliveryId = json.getString("deliveryId");
        
        if (StringUtils.isBlank(orderId)) {
            return Result.fail("订单ID不能为空！");
        }
        
        ApeVegetableOrder order = apeVegetableOrderService.getById(orderId);
        if (order == null) {
            return Result.fail("订单不存在！");
        }
        if (order.getState() != 1) {
            return Result.fail("订单状态异常，无法发货！");
        }
        
        // 设置配送员信息
        if (StringUtils.isNotBlank(deliveryId)) {
            ApeDelivery delivery = apeDeliveryService.getById(deliveryId);
            if (delivery != null) {
                order.setDeliveryId(deliveryId);
                order.setDeliveryName(delivery.getName());
                order.setDeliveryTel(delivery.getTel());
            }
        }
        
        // 更新订单状态为待收货
        order.setState(2);
        boolean update = apeVegetableOrderService.updateById(order);
        
        if (update) {
            // 确认扣减库存（将锁定库存转为实际出库）
            apeStockService.confirmDeduct(order.getVegetableId(), order.getNum(), orderId);
            return Result.success("发货成功");
        }
        return Result.fail("发货失败");
    }

    /** 确认收货 */
    @Log(name = "确认收货", type = BusinessType.UPDATE)
    @PostMapping("confirmReceive")
    public Result confirmReceive(@RequestBody JSONObject json) {
        String orderId = json.getString("orderId");
        if (StringUtils.isBlank(orderId)) {
            return Result.fail("订单ID不能为空！");
        }
        
        ApeVegetableOrder order = apeVegetableOrderService.getById(orderId);
        if (order == null) {
            return Result.fail("订单不存在！");
        }
        if (order.getState() != 2) {
            return Result.fail("订单状态异常，无法确认收货！");
        }
        
        // 检查是否是本人的订单
        ApeUser user = ShiroUtils.getUserInfo();
        if (!order.getUserId().equals(user.getId()) && user.getUserType() != 0) {
            return Result.fail("无权操作此订单！");
        }
        
        order.setState(3); // 已完成
        boolean update = apeVegetableOrderService.updateById(order);
        if (update) {
            return Result.success("确认收货成功");
        }
        return Result.fail("操作失败");
    }

    /** 取消订单 */
    @Log(name = "取消订单", type = BusinessType.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("cancelOrder")
    public Result cancelOrder(@RequestBody JSONObject json) {
        String orderId = json.getString("orderId");
        String reason = json.getString("reason");
        
        if (StringUtils.isBlank(orderId)) {
            return Result.fail("订单ID不能为空！");
        }
        
        ApeVegetableOrder order = apeVegetableOrderService.getById(orderId);
        if (order == null) {
            return Result.fail("订单不存在！");
        }
        
        // 只有待付款和待发货的订单可以取消
        if (order.getState() != 0 && order.getState() != 1) {
            return Result.fail("当前订单状态不可取消！");
        }
        
        // 检查是否是本人的订单或管理员
        ApeUser user = ShiroUtils.getUserInfo();
        if (!order.getUserId().equals(user.getId()) && user.getUserType() != 0) {
            return Result.fail("无权操作此订单！");
        }
        
        order.setState(4); // 已取消
        order.setRemark(reason);
        boolean update = apeVegetableOrderService.updateById(order);
        
        if (update) {
            // 解锁库存
            apeStockService.unlockStock(order.getVegetableId(), order.getNum(), orderId);
            return Result.success("订单已取消");
        }
        return Result.fail("取消失败");
    }

    /** 获取订单统计 */
    @GetMapping("getOrderStats")
    public Result getOrderStats() {
        ApeUser user = ShiroUtils.getUserInfo();
        JSONObject stats = new JSONObject();
        
        QueryWrapper<ApeVegetableOrder> baseQuery = new QueryWrapper<>();
        // 非管理员只能看自己的订单
        if (user.getUserType() != 0) {
            baseQuery.lambda().eq(ApeVegetableOrder::getUserId, user.getId());
        }
        
        // 统计各状态订单数量
        for (int i = 0; i <= 6; i++) {
            QueryWrapper<ApeVegetableOrder> query = new QueryWrapper<>();
            if (user.getUserType() != 0) {
                query.lambda().eq(ApeVegetableOrder::getUserId, user.getId());
            }
            query.lambda().eq(ApeVegetableOrder::getState, i);
            int count = apeVegetableOrderService.count(query);
            stats.put("state" + i, count);
        }
        
        // 统计总订单数
        QueryWrapper<ApeVegetableOrder> totalQuery = new QueryWrapper<>();
        if (user.getUserType() != 0) {
            totalQuery.lambda().eq(ApeVegetableOrder::getUserId, user.getId());
        }
        stats.put("total", apeVegetableOrderService.count(totalQuery));
        
        return Result.success(stats);
    }

    /** 编辑订单 */
    @Log(name = "编辑订单", type = BusinessType.UPDATE)
    @PostMapping("editApeVegetableOrder")
    public Result editApeVegetableOrder(@RequestBody ApeVegetableOrder apeVegetableOrder) {
        if (StringUtils.isNotBlank(apeVegetableOrder.getDeliveryId())) {
            ApeDelivery delivery = apeDeliveryService.getById(apeVegetableOrder.getDeliveryId());
            apeVegetableOrder.setDeliveryName(delivery.getName());
            apeVegetableOrder.setDeliveryTel(delivery.getTel());
        }
        boolean save = apeVegetableOrderService.updateById(apeVegetableOrder);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除订单 */
    @GetMapping("removeApeVegetableOrder")
    @Log(name = "删除订单", type = BusinessType.DELETE)
    public Result removeApeVegetableOrder(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeVegetableOrderService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("订单id不能为空！");
        }
    }

}