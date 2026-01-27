package com.ape.apesystem.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 系统
 * @version 1.0
 * @description: 售后申请实体
 * @date 2024/01/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("ape_after_sale")
public class ApeAfterSale implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 售后单号
     */
    private String afterSaleNo;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 订单号
     */
    private String orderNumber;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 商品ID
     */
    private String vegetableId;

    /**
     * 商品名称
     */
    private String vegetableName;

    /**
     * 商品图片
     */
    private String vegetableImages;

    /**
     * 售后类型: 1退货退款 2仅退款 3换货
     */
    private Integer type;

    /**
     * 申请原因
     */
    private String reason;

    /**
     * 详细描述
     */
    private String description;

    /**
     * 图片凭证（多张用逗号分隔）
     */
    private String images;

    /**
     * 申请数量
     */
    private Integer quantity;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 状态: 0待审核 1已同意 2已拒绝 3处理中 4已完成 5已取消
     */
    private Integer status;

    /**
     * 审核备注
     */
    private String auditRemark;

    /**
     * 审核人ID
     */
    private String auditorId;

    /**
     * 审核人姓名
     */
    private String auditorName;

    /**
     * 审核时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;

    /**
     * 退货物流单号
     */
    private String returnLogisticsNo;

    /**
     * 换货物流单号
     */
    private String exchangeLogisticsNo;

    /**
     * 完成时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date completeTime;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(exist = false)
    private Integer pageNumber;

    @TableField(exist = false)
    private Integer pageSize;

    /**
     * 售后类型枚举
     */
    public static class AfterSaleType {
        /** 退货退款 */
        public static final int RETURN_REFUND = 1;
        /** 仅退款 */
        public static final int REFUND_ONLY = 2;
        /** 换货 */
        public static final int EXCHANGE = 3;
    }

    /**
     * 售后状态枚举
     */
    public static class AfterSaleStatus {
        /** 待审核 */
        public static final int PENDING = 0;
        /** 已同意 */
        public static final int APPROVED = 1;
        /** 已拒绝 */
        public static final int REJECTED = 2;
        /** 处理中 */
        public static final int PROCESSING = 3;
        /** 已完成 */
        public static final int COMPLETED = 4;
        /** 已取消 */
        public static final int CANCELLED = 5;
    }
}
