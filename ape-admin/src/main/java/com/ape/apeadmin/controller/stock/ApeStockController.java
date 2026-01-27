package com.ape.apeadmin.controller.stock;

import com.alibaba.fastjson2.JSONObject;
import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeStock;
import com.ape.apesystem.domain.ApeStockRecord;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.service.ApeStockRecordService;
import com.ape.apesystem.service.ApeStockService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 系统
 * @version 1.0
 * @description: 库存管理Controller
 * @date 2024/01/26
 */
@Controller
@ResponseBody
@RequestMapping("stock")
public class ApeStockController {

    @Autowired
    private ApeStockService apeStockService;

    @Autowired
    private ApeStockRecordService apeStockRecordService;

    /**
     * 分页查询库存列表
     */
    @Log(name = "分页查询库存列表", type = BusinessType.OTHER)
    @PostMapping("getStockPage")
    public Result getStockPage(@RequestBody ApeStock apeStock) {
        Page<ApeStock> page = apeStockService.getStockPage(apeStock);
        return Result.success(page);
    }

    /**
     * 获取库存预警列表
     */
    @Log(name = "获取库存预警列表", type = BusinessType.OTHER)
    @GetMapping("getWarningList")
    public Result getWarningList() {
        List<ApeStock> list = apeStockService.getWarningList();
        return Result.success(list);
    }

    /**
     * 根据商品ID获取库存信息
     */
    @Log(name = "根据商品ID获取库存", type = BusinessType.OTHER)
    @GetMapping("getStockByVegetableId")
    public Result getStockByVegetableId(@RequestParam("vegetableId") String vegetableId) {
        ApeStock stock = apeStockService.getByVegetableId(vegetableId);
        return Result.success(stock);
    }

    /**
     * 检查库存是否充足
     */
    @GetMapping("checkStock")
    public Result checkStock(@RequestParam("vegetableId") String vegetableId,
                             @RequestParam("quantity") Integer quantity) {
        boolean sufficient = apeStockService.checkStock(vegetableId, quantity);
        JSONObject json = new JSONObject();
        json.put("sufficient", sufficient);
        if (!sufficient) {
            ApeStock stock = apeStockService.getByVegetableId(vegetableId);
            json.put("available", stock != null ? stock.getAvailableQuantity() : 0);
        }
        return Result.success(json);
    }

    /**
     * 初始化/设置库存
     */
    @Log(name = "初始化库存", type = BusinessType.INSERT)
    @PostMapping("initStock")
    public Result initStock(@RequestBody JSONObject json) {
        String vegetableId = json.getString("vegetableId");
        Integer quantity = json.getInteger("quantity");
        Integer warningQuantity = json.getInteger("warningQuantity");

        if (StringUtils.isBlank(vegetableId) || quantity == null) {
            return Result.fail("商品ID和库存数量不能为空");
        }

        boolean result = apeStockService.initStock(vegetableId, quantity, warningQuantity);
        if (result) {
            return Result.success();
        }
        return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
    }

    /**
     * 入库操作
     */
    @Log(name = "入库操作", type = BusinessType.INSERT)
    @PostMapping("stockIn")
    public Result stockIn(@RequestBody JSONObject json) {
        String vegetableId = json.getString("vegetableId");
        Integer quantity = json.getInteger("quantity");
        String remark = json.getString("remark");

        if (StringUtils.isBlank(vegetableId) || quantity == null || quantity <= 0) {
            return Result.fail("商品ID和入库数量不能为空，且数量必须大于0");
        }

        ApeUser user = ShiroUtils.getUserInfo();
        boolean result = apeStockService.stockIn(vegetableId, quantity, user.getId(), user.getUserName(), remark);
        if (result) {
            return Result.success();
        }
        return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
    }

    /**
     * 出库操作
     */
    @Log(name = "出库操作", type = BusinessType.UPDATE)
    @PostMapping("stockOut")
    public Result stockOut(@RequestBody JSONObject json) {
        String vegetableId = json.getString("vegetableId");
        Integer quantity = json.getInteger("quantity");
        String orderId = json.getString("orderId");
        String remark = json.getString("remark");

        if (StringUtils.isBlank(vegetableId) || quantity == null || quantity <= 0) {
            return Result.fail("商品ID和出库数量不能为空，且数量必须大于0");
        }

        ApeUser user = ShiroUtils.getUserInfo();
        boolean result = apeStockService.stockOut(vegetableId, quantity, orderId, user.getId(), user.getUserName(), remark);
        if (result) {
            return Result.success();
        }
        return Result.fail("出库失败，库存不足！");
    }

    /**
     * 库存调整
     */
    @Log(name = "库存调整", type = BusinessType.UPDATE)
    @PostMapping("adjustStock")
    public Result adjustStock(@RequestBody JSONObject json) {
        String vegetableId = json.getString("vegetableId");
        Integer adjustQuantity = json.getInteger("adjustQuantity");
        String remark = json.getString("remark");

        if (StringUtils.isBlank(vegetableId) || adjustQuantity == null || adjustQuantity == 0) {
            return Result.fail("商品ID和调整数量不能为空，且调整数量不能为0");
        }

        ApeUser user = ShiroUtils.getUserInfo();
        boolean result = apeStockService.adjustStock(vegetableId, adjustQuantity, user.getId(), user.getUserName(), remark);
        if (result) {
            return Result.success();
        }
        return Result.fail("库存调整失败！");
    }

    /**
     * 更新预警库存
     */
    @Log(name = "更新预警库存", type = BusinessType.UPDATE)
    @PostMapping("updateWarningQuantity")
    public Result updateWarningQuantity(@RequestBody JSONObject json) {
        String vegetableId = json.getString("vegetableId");
        Integer warningQuantity = json.getInteger("warningQuantity");

        if (StringUtils.isBlank(vegetableId) || warningQuantity == null) {
            return Result.fail("商品ID和预警库存不能为空");
        }

        boolean result = apeStockService.updateWarningQuantity(vegetableId, warningQuantity);
        if (result) {
            return Result.success();
        }
        return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
    }

    /**
     * 分页查询库存变动记录
     */
    @Log(name = "分页查询库存变动记录", type = BusinessType.OTHER)
    @PostMapping("getStockRecordPage")
    public Result getStockRecordPage(@RequestBody ApeStockRecord record) {
        Page<ApeStockRecord> page = apeStockRecordService.getRecordPage(record);
        return Result.success(page);
    }

    /**
     * 批量入库
     */
    @Log(name = "批量入库", type = BusinessType.INSERT)
    @PostMapping("batchStockIn")
    public Result batchStockIn(@RequestBody JSONObject json) {
        List<JSONObject> items = json.getList("items", JSONObject.class);
        if (items == null || items.isEmpty()) {
            return Result.fail("入库列表不能为空");
        }

        ApeUser user = ShiroUtils.getUserInfo();
        int successCount = 0;
        for (JSONObject item : items) {
            String vegetableId = item.getString("vegetableId");
            Integer quantity = item.getInteger("quantity");
            String remark = item.getString("remark");
            if (StringUtils.isNotBlank(vegetableId) && quantity != null && quantity > 0) {
                boolean result = apeStockService.stockIn(vegetableId, quantity, user.getId(), user.getUserName(), remark);
                if (result) {
                    successCount++;
                }
            }
        }

        JSONObject result = new JSONObject();
        result.put("total", items.size());
        result.put("success", successCount);
        result.put("fail", items.size() - successCount);
        return Result.success(result);
    }
}
