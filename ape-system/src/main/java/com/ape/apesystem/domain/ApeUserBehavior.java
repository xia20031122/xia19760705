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
 * @description: 用户行为记录实体
 * @date 2024/01/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("ape_user_behavior")
public class ApeUserBehavior implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 商品ID
     */
    private String vegetableId;

    /**
     * 商品分类ID
     */
    private String typeId;

    /**
     * 行为类型: 1浏览 2收藏 3加购 4购买 5搜索
     */
    private Integer behaviorType;

    /**
     * 搜索关键词（仅搜索行为）
     */
    private String keyword;

    /**
     * 行为权重（用于推荐算法）
     */
    private Integer weight;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 行为类型枚举
     */
    public static class BehaviorType {
        /** 浏览 */
        public static final int VIEW = 1;
        /** 收藏 */
        public static final int FAVORITE = 2;
        /** 加购物车 */
        public static final int ADD_CART = 3;
        /** 购买 */
        public static final int PURCHASE = 4;
        /** 搜索 */
        public static final int SEARCH = 5;
    }

    /**
     * 行为权重（用于推荐算法计算）
     */
    public static class BehaviorWeight {
        public static final int VIEW_WEIGHT = 1;
        public static final int FAVORITE_WEIGHT = 3;
        public static final int ADD_CART_WEIGHT = 5;
        public static final int PURCHASE_WEIGHT = 10;
        public static final int SEARCH_WEIGHT = 2;
    }
}
