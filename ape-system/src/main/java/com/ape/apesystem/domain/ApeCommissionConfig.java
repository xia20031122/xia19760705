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
 * @description: 佣金配置实体
 * @date 2024/01/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("ape_commission_config")
public class ApeCommissionConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 配置名称
     */
    private String name;

    /**
     * 商品分类ID（为空表示全局配置）
     */
    private String typeId;

    /**
     * 商品分类名称
     */
    private String typeName;

    /**
     * 供应商ID（为空表示所有供应商）
     */
    private String supplierId;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 佣金比例（百分比，如：5.00表示5%）
     */
    private BigDecimal commissionRate;

    /**
     * 最低佣金金额
     */
    private BigDecimal minCommission;

    /**
     * 最高佣金金额（0表示不限）
     */
    private BigDecimal maxCommission;

    /**
     * 生效开始日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date effectiveStartDate;

    /**
     * 生效结束日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date effectiveEndDate;

    /**
     * 状态: 0禁用 1启用
     */
    private Integer status;

    /**
     * 优先级（数字越大优先级越高）
     */
    private Integer priority;

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
}
