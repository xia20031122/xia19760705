package com.ape.apesystem.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 系统
 * @version 1.0
 * @description: 新鲜度信息DTO
 * @date 2024/01/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApeFreshnessInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    private String vegetableId;

    /**
     * 商品名称
     */
    private String vegetableName;

    /**
     * 采摘日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date harvestDate;

    /**
     * 保鲜期（天）
     */
    private Integer shelfLife;

    /**
     * 过期日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expiryDate;

    /**
     * 剩余天数
     */
    private Integer remainingDays;

    /**
     * 新鲜度百分比（0-100）
     */
    private Integer freshnessPercent;

    /**
     * 新鲜度状态: 1新鲜 2良好 3临期 4过期
     */
    private Integer freshnessStatus;

    /**
     * 新鲜度状态标签
     */
    private String freshnessLabel;

    /**
     * 新鲜度状态颜色
     */
    private String freshnessColor;

    /**
     * 存储建议
     */
    private String storageAdvice;

    /**
     * 是否预警
     */
    private Boolean isWarning;

    /**
     * 新鲜度状态枚举
     */
    public static class FreshnessStatus {
        /** 新鲜 (>70%) */
        public static final int FRESH = 1;
        /** 良好 (40%-70%) */
        public static final int GOOD = 2;
        /** 临期 (0%-40%) */
        public static final int NEAR_EXPIRY = 3;
        /** 过期 (<0%) */
        public static final int EXPIRED = 4;
    }

    /**
     * 根据新鲜度百分比计算状态
     */
    public static int calculateStatus(int percent) {
        if (percent > 70) {
            return FreshnessStatus.FRESH;
        } else if (percent > 40) {
            return FreshnessStatus.GOOD;
        } else if (percent > 0) {
            return FreshnessStatus.NEAR_EXPIRY;
        } else {
            return FreshnessStatus.EXPIRED;
        }
    }

    /**
     * 获取状态标签
     */
    public static String getStatusLabel(int status) {
        switch (status) {
            case FreshnessStatus.FRESH:
                return "新鲜";
            case FreshnessStatus.GOOD:
                return "良好";
            case FreshnessStatus.NEAR_EXPIRY:
                return "临期";
            case FreshnessStatus.EXPIRED:
                return "已过期";
            default:
                return "未知";
        }
    }

    /**
     * 获取状态颜色
     */
    public static String getStatusColor(int status) {
        switch (status) {
            case FreshnessStatus.FRESH:
                return "#52c41a"; // 绿色
            case FreshnessStatus.GOOD:
                return "#1890ff"; // 蓝色
            case FreshnessStatus.NEAR_EXPIRY:
                return "#faad14"; // 橙色
            case FreshnessStatus.EXPIRED:
                return "#f5222d"; // 红色
            default:
                return "#999999"; // 灰色
        }
    }
}
