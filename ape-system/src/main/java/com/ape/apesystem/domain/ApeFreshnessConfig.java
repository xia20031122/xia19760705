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
 * @description: 保鲜期配置实体
 * @date 2024/01/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("ape_freshness_config")
public class ApeFreshnessConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 商品分类ID
     */
    private String typeId;

    /**
     * 分类名称
     */
    private String typeName;

    /**
     * 保鲜期（天）
     */
    private Integer shelfLife;

    /**
     * 预警天数（剩余多少天预警）
     */
    private Integer warningDays;

    /**
     * 存储条件
     */
    private String storageCondition;

    /**
     * 存储温度（摄氏度）
     */
    private String storageTemperature;

    /**
     * 存储湿度
     */
    private String storageHumidity;

    /**
     * 保鲜建议
     */
    private String freshnessAdvice;

    /**
     * 状态: 0禁用 1启用
     */
    private Integer status;

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
