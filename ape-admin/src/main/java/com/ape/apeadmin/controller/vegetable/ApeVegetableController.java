package com.ape.apeadmin.controller.vegetable;

import com.alibaba.fastjson2.JSONObject;
import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeCar;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.domain.ApeVegetable;
import com.ape.apesystem.domain.ApeVegetableFavor;
import com.ape.apesystem.domain.ApeVegetableOrder;
import com.ape.apesystem.service.ApeCarService;
import com.ape.apesystem.service.ApeStockService;
import com.ape.apesystem.service.ApeVegetableFavorService;
import com.ape.apesystem.service.ApeVegetableOrderService;
import com.ape.apesystem.service.ApeVegetableService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 蔬菜controller
 * @date 2024/03/28 10:39
 * 
 * 审核状态说明：
 * 0-待审核, 1-已通过, 2-已拒绝
 */
@Controller
@ResponseBody
@RequestMapping("vegetable")
public class ApeVegetableController {

    @Autowired
    private ApeVegetableService apeVegetableService;
    @Autowired
    private ApeVegetableFavorService apeVegetableFavorService;
    @Autowired
    private ApeVegetableOrderService apeVegetableOrderService;
    @Autowired
    private ApeCarService apeCarService;
    @Autowired
    private ApeStockService apeStockService;

    /** 分页获取蔬菜 */
    @Log(name = "分页获取蔬菜", type = BusinessType.OTHER)
    @PostMapping("getApeVegetablePage")
    public Result getApeVegetablePage(@RequestBody ApeVegetable apeVegetable) {
        // 处理分页参数，如果为null则使用默认值
        Integer pageNumber = apeVegetable.getPageNumber();
        Integer pageSize = apeVegetable.getPageSize();
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        
        Page<ApeVegetable> page = new Page<>(pageNumber, pageSize);
        QueryWrapper<ApeVegetable> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(apeVegetable.getState() != null,ApeVegetable::getState,apeVegetable.getState())
                .like(StringUtils.isNotBlank(apeVegetable.getName()),ApeVegetable::getName,apeVegetable.getName())
                .eq(StringUtils.isNotBlank(apeVegetable.getType()),ApeVegetable::getType,apeVegetable.getType());
        
        // 处理排序
        Integer sort = apeVegetable.getSort();
        if (sort != null) {
            if (sort == 1) {
                queryWrapper.lambda().orderByDesc(ApeVegetable::getCreateTime);
            } else if (sort == 2) {
                queryWrapper.lambda().orderByAsc(ApeVegetable::getPrice);
            } else if (sort == 3) {
                queryWrapper.lambda().orderByDesc(ApeVegetable::getPrice);
            }
        } else {
            // 默认按创建时间倒序
            queryWrapper.lambda().orderByDesc(ApeVegetable::getCreateTime);
        }
        Page<ApeVegetable> apeVegetablePage = apeVegetableService.page(page, queryWrapper);
        return Result.success(apeVegetablePage);
    }

    @GetMapping("getApeVegetableIndex")
    public Result getApeVegetableIndex() {
        QueryWrapper<ApeVegetable> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeVegetable::getState,1).orderByDesc(ApeVegetable::getCreateTime).last("limit 8");
        List<ApeVegetable> vegetableList = apeVegetableService.list(queryWrapper);
        return Result.success(vegetableList);
    }

    @GetMapping("/getApeVegetableList")
    public Result getApeVegetableList() {
        List<ApeVegetable> vegetableList = apeVegetableService.list();
        return Result.success(vegetableList);
    }

    /** 根据id获取蔬菜 */
    @Log(name = "根据id获取蔬菜", type = BusinessType.OTHER)
    @GetMapping("getApeVegetableById")
    public Result getApeVegetableById(@RequestParam("id")String id) {
        ApeVegetable apeVegetable = apeVegetableService.getById(id);
        return Result.success(apeVegetable);
    }

    /** 保存蔬菜 */
    @Log(name = "保存蔬菜", type = BusinessType.INSERT)
    @PostMapping("saveApeVegetable")
    public Result saveApeVegetable(@RequestBody ApeVegetable apeVegetable) {
        boolean save = apeVegetableService.save(apeVegetable);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑蔬菜 */
    @Log(name = "编辑蔬菜", type = BusinessType.UPDATE)
    @PostMapping("editApeVegetable")
    public Result editApeVegetable(@RequestBody ApeVegetable apeVegetable) {
        boolean save = apeVegetableService.updateById(apeVegetable);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除蔬菜 */
    @Transactional(rollbackFor = Exception.class)
    @GetMapping("removeApeVegetable")
    @Log(name = "删除蔬菜", type = BusinessType.DELETE)
    public Result removeApeVegetable(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeVegetableService.removeById(id);
                QueryWrapper<ApeVegetableFavor> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(ApeVegetableFavor::getVegetableId,id);
                apeVegetableFavorService.remove(queryWrapper);

                QueryWrapper<ApeVegetableOrder> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.lambda().eq(ApeVegetableOrder::getVegetableId,id);
                apeVegetableOrderService.remove(queryWrapper1);

                QueryWrapper<ApeCar> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.lambda().eq(ApeCar::getVegetableId,id);
                apeCarService.remove(queryWrapper2);
            }
            return Result.success();
        } else {
            return Result.fail("蔬菜id不能为空！");
        }
    }

    // ==================== 商品审核功能 ====================

    /** 供应商提交商品（待审核） */
    @Log(name = "供应商提交商品", type = BusinessType.INSERT)
    @PostMapping("submitVegetable")
    public Result submitVegetable(@RequestBody ApeVegetable apeVegetable) {
        ApeUser user = ShiroUtils.getUserInfo();
        
        // 设置供应商ID
        apeVegetable.setSupplierId(user.getId());
        // 设置为待审核状态
        apeVegetable.setAuditStatus(0);
        // 默认下架状态
        apeVegetable.setState(0);
        
        boolean save = apeVegetableService.save(apeVegetable);
        if (save) {
            return Result.success(apeVegetable);
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 获取待审核商品列表 */
    @Log(name = "获取待审核商品", type = BusinessType.OTHER)
    @PostMapping("getPendingAuditPage")
    public Result getPendingAuditPage(@RequestBody ApeVegetable apeVegetable) {
        ApeUser user = ShiroUtils.getUserInfo();
        // 只有管理员可以查看待审核列表
        if (user.getUserType() != 0) {
            return Result.fail("无权操作");
        }

        // 处理分页参数，如果为null则使用默认值
        Integer pageNumber = apeVegetable.getPageNumber();
        Integer pageSize = apeVegetable.getPageSize();
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        Page<ApeVegetable> page = new Page<>(pageNumber, pageSize);
        QueryWrapper<ApeVegetable> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(ApeVegetable::getAuditStatus, 0)
                .like(StringUtils.isNotBlank(apeVegetable.getName()), ApeVegetable::getName, apeVegetable.getName())
                .orderByAsc(ApeVegetable::getCreateTime);
        
        Page<ApeVegetable> resultPage = apeVegetableService.page(page, queryWrapper);
        return Result.success(resultPage);
    }

    /** 获取供应商自己的商品列表 */
    @Log(name = "获取我的商品", type = BusinessType.OTHER)
    @PostMapping("getMyVegetablePage")
    public Result getMyVegetablePage(@RequestBody ApeVegetable apeVegetable) {
        ApeUser user = ShiroUtils.getUserInfo();

        // 处理分页参数，如果为null则使用默认值
        Integer pageNumber = apeVegetable.getPageNumber();
        Integer pageSize = apeVegetable.getPageSize();
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        Page<ApeVegetable> page = new Page<>(pageNumber, pageSize);
        QueryWrapper<ApeVegetable> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(ApeVegetable::getSupplierId, user.getId())
                .eq(apeVegetable.getAuditStatus() != null, ApeVegetable::getAuditStatus, apeVegetable.getAuditStatus())
                .like(StringUtils.isNotBlank(apeVegetable.getName()), ApeVegetable::getName, apeVegetable.getName())
                .orderByDesc(ApeVegetable::getCreateTime);

        Page<ApeVegetable> resultPage = apeVegetableService.page(page, queryWrapper);
        return Result.success(resultPage);
    }

    /** 审核商品 */
    @Log(name = "审核商品", type = BusinessType.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("auditVegetable")
    public Result auditVegetable(@RequestBody JSONObject json) {
        String vegetableId = json.getString("vegetableId");
        Boolean approved = json.getBoolean("approved");
        String auditRemark = json.getString("auditRemark");

        if (StringUtils.isBlank(vegetableId) || approved == null) {
            return Result.fail("参数不完整");
        }

        ApeUser user = ShiroUtils.getUserInfo();
        // 只有管理员可以审核
        if (user.getUserType() != 0) {
            return Result.fail("无权审核");
        }

        ApeVegetable vegetable = apeVegetableService.getById(vegetableId);
        if (vegetable == null) {
            return Result.fail("商品不存在");
        }

        if (vegetable.getAuditStatus() != null && vegetable.getAuditStatus() != 0) {
            return Result.fail("该商品已审核");
        }

        if (approved) {
            vegetable.setAuditStatus(1); // 审核通过
            vegetable.setState(1); // 自动上架
            
            // 初始化库存
            Integer initStock = json.getInteger("initStock");
            if (initStock != null && initStock > 0) {
                apeStockService.initStock(vegetableId, initStock, 10);
            }
        } else {
            vegetable.setAuditStatus(2); // 审核拒绝
            vegetable.setState(0); // 保持下架
        }
        
        vegetable.setAuditRemark(auditRemark);
        vegetable.setUpdateTime(new Date());

        boolean result = apeVegetableService.updateById(vegetable);
        if (result) {
            return Result.success(approved ? "审核通过" : "审核拒绝");
        }
        return Result.fail("审核失败");
    }

    /** 批量审核通过 */
    @Log(name = "批量审核通过", type = BusinessType.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("batchApprove")
    public Result batchApprove(@RequestBody JSONObject json) {
        List<String> ids = json.getList("ids", String.class);
        if (ids == null || ids.isEmpty()) {
            return Result.fail("商品ID列表不能为空");
        }

        ApeUser user = ShiroUtils.getUserInfo();
        if (user.getUserType() != 0) {
            return Result.fail("无权操作");
        }

        int successCount = 0;
        for (String id : ids) {
            ApeVegetable vegetable = apeVegetableService.getById(id);
            if (vegetable != null && (vegetable.getAuditStatus() == null || vegetable.getAuditStatus() == 0)) {
                vegetable.setAuditStatus(1);
                vegetable.setState(1);
                vegetable.setUpdateTime(new Date());
                if (apeVegetableService.updateById(vegetable)) {
                    successCount++;
                }
            }
        }

        JSONObject result = new JSONObject();
        result.put("total", ids.size());
        result.put("success", successCount);
        return Result.success(result);
    }

    /** 重新提交审核（被拒绝后修改重新提交） */
    @Log(name = "重新提交审核", type = BusinessType.UPDATE)
    @PostMapping("resubmitVegetable")
    public Result resubmitVegetable(@RequestBody ApeVegetable apeVegetable) {
        ApeUser user = ShiroUtils.getUserInfo();

        ApeVegetable exist = apeVegetableService.getById(apeVegetable.getId());
        if (exist == null) {
            return Result.fail("商品不存在");
        }

        // 检查是否是自己的商品
        if (!exist.getSupplierId().equals(user.getId()) && user.getUserType() != 0) {
            return Result.fail("无权操作");
        }

        // 只有被拒绝的商品才能重新提交
        if (exist.getAuditStatus() != 2) {
            return Result.fail("只有被拒绝的商品才能重新提交");
        }

        // 更新商品信息
        apeVegetable.setAuditStatus(0); // 重置为待审核
        apeVegetable.setAuditRemark(null);
        apeVegetable.setUpdateTime(new Date());

        boolean result = apeVegetableService.updateById(apeVegetable);
        if (result) {
            return Result.success("已重新提交审核");
        }
        return Result.fail("提交失败");
    }

    /** 获取审核统计 */
    @GetMapping("getAuditStats")
    public Result getAuditStats() {
        ApeUser user = ShiroUtils.getUserInfo();
        JSONObject stats = new JSONObject();

        // 管理员查看全部
        if (user.getUserType() == 0) {
            QueryWrapper<ApeVegetable> pendingQuery = new QueryWrapper<>();
            pendingQuery.lambda().eq(ApeVegetable::getAuditStatus, 0);
            stats.put("pending", apeVegetableService.count(pendingQuery));

            QueryWrapper<ApeVegetable> approvedQuery = new QueryWrapper<>();
            approvedQuery.lambda().eq(ApeVegetable::getAuditStatus, 1);
            stats.put("approved", apeVegetableService.count(approvedQuery));

            QueryWrapper<ApeVegetable> rejectedQuery = new QueryWrapper<>();
            rejectedQuery.lambda().eq(ApeVegetable::getAuditStatus, 2);
            stats.put("rejected", apeVegetableService.count(rejectedQuery));
        } else {
            // 供应商只看自己的
            QueryWrapper<ApeVegetable> pendingQuery = new QueryWrapper<>();
            pendingQuery.lambda().eq(ApeVegetable::getSupplierId, user.getId()).eq(ApeVegetable::getAuditStatus, 0);
            stats.put("pending", apeVegetableService.count(pendingQuery));

            QueryWrapper<ApeVegetable> approvedQuery = new QueryWrapper<>();
            approvedQuery.lambda().eq(ApeVegetable::getSupplierId, user.getId()).eq(ApeVegetable::getAuditStatus, 1);
            stats.put("approved", apeVegetableService.count(approvedQuery));

            QueryWrapper<ApeVegetable> rejectedQuery = new QueryWrapper<>();
            rejectedQuery.lambda().eq(ApeVegetable::getSupplierId, user.getId()).eq(ApeVegetable::getAuditStatus, 2);
            stats.put("rejected", apeVegetableService.count(rejectedQuery));
        }

        return Result.success(stats);
    }

}