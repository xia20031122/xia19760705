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
 * @description: 库存实体
 * @date 2024/01/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("ape_stock")
public class ApeStock implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 商品ID
     */
    private String vegetableId;

    /**
     * 当前库存数量
     */
    private Integer quantity;

    /**
     * 预警库存数量
     */
    private Integer warningQuantity;

    /**
     * 锁定库存(待发货订单占用)
     */
    private Integer lockedQuantity;

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

    /**
     * 商品名称（非数据库字段）
     */
    @TableField(exist = false)
    private String vegetableName;

    /**
     * 商品图片（非数据库字段）
     */
    @TableField(exist = false)
    private String vegetableImages;

    /**
     * 可用库存 = 当前库存 - 锁定库存（非数据库字段）
     */
    @TableField(exist = false)
    private Integer availableQuantity;

    /**
     * 是否预警（非数据库字段）
     */
    @TableField(exist = false)
    private Boolean isWarning;

    @TableField(exist = false)
    private Integer pageNumber;

    @TableField(exist = false)
    private Integer pageSize;

    /**
     * 获取可用库存
     */
    public Integer getAvailableQuantity() {
        if (quantity == null) return 0;
        if (lockedQuantity == null) return quantity;
        return quantity - lockedQuantity;
    }

    /**
     * 是否处于预警状态
     */
    public Boolean getIsWarning() {
        if (quantity == null || warningQuantity == null) return false;
        return quantity <= warningQuantity;
    }
}
