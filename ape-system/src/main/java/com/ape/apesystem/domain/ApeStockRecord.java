package com.ape.apesystem.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 系统
 * @version 1.0
 * @description: 库存变动记录实体
 * @date 2024/01/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("ape_stock_record")
public class ApeStockRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 库存ID
     */
    private String stockId;

    /**
     * 商品ID
     */
    private String vegetableId;

    /**
     * 变动类型: 1入库 2出库 3调整 4锁定 5解锁
     */
    private Integer type;

    /**
     * 变动数量
     */
    private Integer quantity;

    /**
     * 变动前数量
     */
    private Integer beforeQuantity;

    /**
     * 变动后数量
     */
    private Integer afterQuantity;

    /**
     * 关联订单ID
     */
    private String orderId;

    /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 商品名称（非数据库字段）
     */
    @TableField(exist = false)
    private String vegetableName;

    @TableField(exist = false)
    private Integer pageNumber;

    @TableField(exist = false)
    private Integer pageSize;

    /**
     * 库存变动类型枚举
     */
    public static class StockRecordType {
        /** 入库 */
        public static final int STOCK_IN = 1;
        /** 出库 */
        public static final int STOCK_OUT = 2;
        /** 调整 */
        public static final int ADJUST = 3;
        /** 锁定(下单占用) */
        public static final int LOCK = 4;
        /** 解锁(取消订单释放) */
        public static final int UNLOCK = 5;
    }
}
