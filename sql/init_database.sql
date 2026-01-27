-- =====================================================
-- 蔬菜销售系统 - 完整数据库初始化脚本
-- 版本: 1.0
-- 日期: 2024/01/27
-- 说明: 本文件包含系统所有数据库表结构和初始数据
-- 使用方法: mysql -u root -p < init_database.sql
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `ape_vegetable` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `ape_vegetable`;

-- =====================================================
-- 第一部分：基础表结构
-- =====================================================

-- ----------------------------
-- 1. 用户表
-- ----------------------------
DROP TABLE IF EXISTS `ape_user`;
CREATE TABLE `ape_user` (
    `id` varchar(64) NOT NULL COMMENT '主键ID',
    `post_id` varchar(64) DEFAULT NULL COMMENT '岗位ID',
    `id_arrary` varchar(500) DEFAULT NULL COMMENT '部门数组',
    `dept_id` varchar(64) DEFAULT NULL COMMENT '部门ID',
    `user_name` varchar(100) DEFAULT NULL COMMENT '用户名',
    `login_account` varchar(100) NOT NULL COMMENT '登录账号',
    `user_type` int(2) NOT NULL DEFAULT 1 COMMENT '用户类型: 0管理员 1普通用户 2供应商',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `tel` varchar(20) DEFAULT NULL COMMENT '电话',
    `sex` int(1) DEFAULT 0 COMMENT '性别: 0未知 1男 2女',
    `avatar` varchar(500) DEFAULT NULL COMMENT '头像',
    `password` varchar(200) NOT NULL COMMENT '密码',
    `salt` varchar(50) DEFAULT NULL COMMENT '盐值',
    `status` int(1) NOT NULL DEFAULT 0 COMMENT '状态: 0正常 1禁用',
    `login_ip` varchar(50) DEFAULT NULL COMMENT '登录IP',
    `login_date` datetime DEFAULT NULL COMMENT '登录日期',
    `pwd_update_date` datetime DEFAULT NULL COMMENT '修改密码日期',
    `school` varchar(200) DEFAULT NULL COMMENT '学校',
    `country` varchar(100) DEFAULT NULL COMMENT '国家',
    `major` varchar(200) DEFAULT NULL COMMENT '专业',
    `agree` varchar(200) DEFAULT NULL COMMENT '职称',
    `age` int(3) DEFAULT NULL COMMENT '年龄',
    `birth` date DEFAULT NULL COMMENT '生日',
    `flair` varchar(500) DEFAULT NULL COMMENT '资质',
    `address` varchar(500) DEFAULT NULL COMMENT '地址',
    `work_date` int(4) DEFAULT NULL COMMENT '参加工作时间',
    `del_flag` int(1) NOT NULL DEFAULT 0 COMMENT '删除标志: 0未删除 1已删除',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_login_account` (`login_account`, `user_type`, `del_flag`),
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_user_type` (`user_type`),
    KEY `idx_status` (`status`),
    KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 2. 轮播图表
-- ----------------------------
DROP TABLE IF EXISTS `ape_rotation`;
CREATE TABLE `ape_rotation` (
    `id` varchar(64) NOT NULL COMMENT '主键ID',
    `content` varchar(500) DEFAULT NULL COMMENT '图片路径或内容',
    `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- ----------------------------
-- 3. 蔬菜分类表
-- ----------------------------
DROP TABLE IF EXISTS `ape_vegetable_type`;
CREATE TABLE `ape_vegetable_type` (
    `id` varchar(64) NOT NULL COMMENT '主键ID',
    `name` varchar(100) DEFAULT NULL COMMENT '分类名称',
    `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='蔬菜分类表';

-- ----------------------------
-- 4. 蔬菜表
-- ----------------------------
DROP TABLE IF EXISTS `ape_vegetable`;
CREATE TABLE `ape_vegetable` (
    `id` varchar(64) NOT NULL COMMENT '主键ID',
    `name` varchar(200) DEFAULT NULL COMMENT '名称',
    `type` varchar(64) DEFAULT NULL COMMENT '分类ID',
    `images` varchar(500) DEFAULT NULL COMMENT '图片',
    `address` varchar(200) DEFAULT NULL COMMENT '产地',
    `price` decimal(10,2) DEFAULT NULL COMMENT '现价',
    `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
    `star` decimal(3,1) DEFAULT NULL COMMENT '评分',
    `introduce` text COMMENT '介绍',
    `unit` varchar(20) DEFAULT NULL COMMENT '单位',
    `num` int(11) DEFAULT NULL COMMENT '数量',
    `state` int(2) DEFAULT 1 COMMENT '状态: 0下架 1上架',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `harvest_date` date DEFAULT NULL COMMENT '采摘日期',
    `supplier_id` varchar(64) DEFAULT NULL COMMENT '供应商ID',
    `audit_status` int(2) DEFAULT 1 COMMENT '审核状态: 0待审核 1已通过 2已拒绝',
    `audit_remark` varchar(500) DEFAULT NULL COMMENT '审核备注',
    `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_state` (`state`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='蔬菜表';

-- ----------------------------
-- 5. 购物车表
-- ----------------------------
DROP TABLE IF EXISTS `ape_car`;
CREATE TABLE `ape_car` (
    `id` varchar(64) NOT NULL COMMENT '主键ID',
    `user_id` varchar(64) DEFAULT NULL COMMENT '用户ID',
    `vegetable_id` varchar(64) DEFAULT NULL COMMENT '蔬菜ID',
    `name` varchar(200) DEFAULT NULL COMMENT '商品名称（快照）',
    `images` varchar(500) DEFAULT NULL COMMENT '商品图片（快照）',
    `price` decimal(10,2) DEFAULT NULL COMMENT '商品价格（快照）',
    `unit` varchar(20) DEFAULT NULL COMMENT '单位（快照）',
    `num` int(11) DEFAULT NULL COMMENT '数量',
    `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_vegetable_id` (`vegetable_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ----------------------------
-- 6. 收藏表
-- ----------------------------
DROP TABLE IF EXISTS `ape_vegetable_favor`;
CREATE TABLE `ape_vegetable_favor` (
    `id` varchar(64) NOT NULL COMMENT '主键ID',
    `name` varchar(200) DEFAULT NULL COMMENT '名称(快照)',
    `unit` varchar(20) DEFAULT NULL COMMENT '单位(快照)',
    `price` decimal(10,2) DEFAULT NULL COMMENT '价格(快照)',
    `vegetable_id` varchar(64) DEFAULT NULL COMMENT '蔬菜ID',
    `images` varchar(500) DEFAULT NULL COMMENT '图片(快照)',
    `user_id` varchar(64) DEFAULT NULL COMMENT '用户ID',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_vegetable_id` (`vegetable_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- ----------------------------
-- 7. 收货地址表
-- ----------------------------
DROP TABLE IF EXISTS `ape_order_address`;
CREATE TABLE `ape_order_address` (
    `id` varchar(64) NOT NULL COMMENT '主键ID',
    `name` varchar(50) DEFAULT NULL COMMENT '收货人姓名',
    `tel` varchar(20) DEFAULT NULL COMMENT '联系电话',
    `address` varchar(500) DEFAULT NULL COMMENT '详细地址',
    `user_id` varchar(64) DEFAULT NULL COMMENT '用户ID',
    `first` int(1) DEFAULT 1 COMMENT '是否默认: 0默认 1非默认',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_first` (`first`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- ----------------------------
-- 8. 订单表
-- ----------------------------
DROP TABLE IF EXISTS `ape_vegetable_order`;
CREATE TABLE `ape_vegetable_order` (
    `id` varchar(64) NOT NULL COMMENT '主键ID',
    `vegetable_id` varchar(64) DEFAULT NULL COMMENT '蔬菜ID',
    `order_number` varchar(64) DEFAULT NULL COMMENT '订单号',
    `name` varchar(200) DEFAULT NULL COMMENT '名称',
    `num` int(11) DEFAULT NULL COMMENT '数量',
    `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
    `unit` varchar(20) DEFAULT NULL COMMENT '单位',
    `images` varchar(500) DEFAULT NULL COMMENT '图片',
    `user_id` varchar(64) DEFAULT NULL COMMENT '用户ID',
    `state` int(2) DEFAULT 0 COMMENT '状态: 0待支付 1待发货 2待收货 3已完成 4已取消',
    `delivery_id` varchar(64) DEFAULT NULL COMMENT '配送员ID',
    `delivery_name` varchar(50) DEFAULT NULL COMMENT '配送员姓名',
    `delivery_tel` varchar(20) DEFAULT NULL COMMENT '配送员电话',
    `tel` varchar(20) DEFAULT NULL COMMENT '联系电话',
    `real_name` varchar(50) DEFAULT NULL COMMENT '收货人姓名',
    `address` varchar(500) DEFAULT NULL COMMENT '收货地址',
    `arrival_time` datetime DEFAULT NULL COMMENT '到达时间',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_vegetable_id` (`vegetable_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_state` (`state`),
    KEY `idx_order_number` (`order_number`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- =====================================================
-- 第二部分：扩展功能表
-- =====================================================

-- ----------------------------
-- 7. 库存表
-- ----------------------------
DROP TABLE IF EXISTS `ape_stock`;
CREATE TABLE `ape_stock` (
    `id` varchar(64) NOT NULL COMMENT '主键ID',
    `vegetable_id` varchar(64) NOT NULL COMMENT '商品ID',
    `quantity` int(11) NOT NULL DEFAULT 0 COMMENT '当前库存数量',
    `warning_quantity` int(11) NOT NULL DEFAULT 10 COMMENT '预警库存数量',
    `locked_quantity` int(11) NOT NULL DEFAULT 0 COMMENT '锁定库存(待发货订单占用)',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_vegetable_id` (`vegetable_id`),
    KEY `idx_quantity` (`quantity`),
    KEY `idx_warning` (`quantity`, `warning_quantity`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- ----------------------------
-- 8. 库存变动记录表
-- ----------------------------
DROP TABLE IF EXISTS `ape_stock_record`;
CREATE TABLE `ape_stock_record` (
    `id` varchar(64) NOT NULL COMMENT '主键ID',
    `stock_id` varchar(64) NOT NULL COMMENT '库存ID',
    `vegetable_id` varchar(64) NOT NULL COMMENT '商品ID',
    `record_type` int(2) NOT NULL COMMENT '变动类型: 1入库 2出库 3锁定 4解锁 5调整',
    `quantity` int(11) NOT NULL COMMENT '变动数量',
    `before_quantity` int(11) DEFAULT NULL COMMENT '变动前数量',
    `after_quantity` int(11) DEFAULT NULL COMMENT '变动后数量',
    `order_id` varchar(64) DEFAULT NULL COMMENT '关联订单ID',
    `operator_id` varchar(64) DEFAULT NULL COMMENT '操作人ID',
    `operator_name` varchar(50) DEFAULT NULL COMMENT '操作人姓名',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_vegetable_id` (`vegetable_id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存变动记录表';

-- ----------------------------
-- 9. 结算单表
-- ----------------------------
DROP TABLE IF EXISTS `ape_settlement`;
CREATE TABLE `ape_settlement` (
    `id` varchar(64) NOT NULL COMMENT '主键ID',
    `settlement_no` varchar(64) NOT NULL COMMENT '结算单号',
    `supplier_id` varchar(64) NOT NULL COMMENT '供应商ID',
    `supplier_name` varchar(100) DEFAULT NULL COMMENT '供应商名称',
    `total_amount` decimal(12,2) NOT NULL DEFAULT 0.00 COMMENT '订单总金额',
    `order_count` int(11) NOT NULL DEFAULT 0 COMMENT '订单数量',
    `commission_rate` decimal(5,2) NOT NULL DEFAULT 0.00 COMMENT '佣金比例(%)',
    `commission_amount` decimal(12,2) NOT NULL DEFAULT 0.00 COMMENT '佣金金额',
    `settlement_amount` decimal(12,2) NOT NULL DEFAULT 0.00 COMMENT '结算金额',
    `status` int(2) NOT NULL DEFAULT 0 COMMENT '状态: 0待结算 1已结算 2已取消',
    `start_date` date DEFAULT NULL COMMENT '结算开始日期',
    `end_date` date DEFAULT NULL COMMENT '结算结束日期',
    `settlement_time` datetime DEFAULT NULL COMMENT '结算时间',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_settlement_no` (`settlement_no`),
    KEY `idx_supplier_id` (`supplier_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='结算单表';

-- ----------------------------
-- 10. 佣金配置表
-- ----------------------------
DROP TABLE IF EXISTS `ape_commission_config`;
CREATE TABLE `ape_commission_config` (
    `id` varchar(64) NOT NULL COMMENT '主键ID',
    `config_name` varchar(100) DEFAULT NULL COMMENT '配置名称',
    `supplier_id` varchar(64) DEFAULT NULL COMMENT '供应商ID(为空表示通用配置)',
    `type_id` varchar(64) DEFAULT NULL COMMENT '商品分类ID(为空表示所有分类)',
    `commission_rate` decimal(5,2) NOT NULL COMMENT '佣金比例(%)',
    `is_default` int(1) DEFAULT 0 COMMENT '是否默认配置: 0否 1是',
    `status` int(1) DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_supplier_id` (`supplier_id`),
    KEY `idx_type_id` (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='佣金配置表';

-- ----------------------------
-- 11. 保鲜期配置表
-- ----------------------------
DROP TABLE IF EXISTS `ape_freshness_config`;
CREATE TABLE `ape_freshness_config` (
    `id` varchar(64) NOT NULL COMMENT '主键ID',
    `type_id` varchar(64) NOT NULL COMMENT '商品分类ID',
    `type_name` varchar(100) DEFAULT NULL COMMENT '分类名称',
    `shelf_life` int(11) NOT NULL COMMENT '保鲜期(天)',
    `warning_days` int(11) NOT NULL DEFAULT 2 COMMENT '预警天数',
    `storage_temp` varchar(50) DEFAULT NULL COMMENT '建议存储温度',
    `freshness_advice` varchar(500) DEFAULT NULL COMMENT '保鲜建议',
    `status` int(1) DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_type_id` (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保鲜期配置表';

-- ----------------------------
-- 12. 用户行为记录表
-- ----------------------------
DROP TABLE IF EXISTS `ape_user_behavior`;
CREATE TABLE `ape_user_behavior` (
    `id` varchar(64) NOT NULL COMMENT '主键ID',
    `user_id` varchar(64) NOT NULL COMMENT '用户ID',
    `vegetable_id` varchar(64) DEFAULT NULL COMMENT '商品ID',
    `type_id` varchar(64) DEFAULT NULL COMMENT '商品分类ID',
    `behavior_type` int(2) NOT NULL COMMENT '行为类型: 1浏览 2收藏 3加购 4购买 5搜索',
    `keyword` varchar(200) DEFAULT NULL COMMENT '搜索关键词',
    `weight` int(11) DEFAULT 1 COMMENT '权重值',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_vegetable_id` (`vegetable_id`),
    KEY `idx_type_id` (`type_id`),
    KEY `idx_behavior_type` (`behavior_type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为记录表';

-- ----------------------------
-- 13. 售后申请表
-- ----------------------------
DROP TABLE IF EXISTS `ape_after_sale`;
CREATE TABLE `ape_after_sale` (
    `id` varchar(64) NOT NULL COMMENT '主键ID',
    `after_sale_no` varchar(64) NOT NULL COMMENT '售后单号',
    `order_id` varchar(64) NOT NULL COMMENT '订单ID',
    `order_number` varchar(64) DEFAULT NULL COMMENT '订单号',
    `user_id` varchar(64) NOT NULL COMMENT '用户ID',
    `user_name` varchar(100) DEFAULT NULL COMMENT '用户名',
    `vegetable_id` varchar(64) DEFAULT NULL COMMENT '商品ID',
    `vegetable_name` varchar(200) DEFAULT NULL COMMENT '商品名称',
    `vegetable_images` varchar(1000) DEFAULT NULL COMMENT '商品图片',
    `type` int(2) NOT NULL COMMENT '售后类型: 1退货退款 2仅退款 3换货',
    `reason` varchar(500) DEFAULT NULL COMMENT '申请原因',
    `description` varchar(1000) DEFAULT NULL COMMENT '详细描述',
    `images` varchar(2000) DEFAULT NULL COMMENT '图片凭证',
    `quantity` int(11) DEFAULT NULL COMMENT '申请数量',
    `refund_amount` decimal(12,2) DEFAULT NULL COMMENT '退款金额',
    `status` int(2) NOT NULL DEFAULT 0 COMMENT '状态: 0待审核 1已同意 2已拒绝 3处理中 4已完成 5已取消',
    `audit_remark` varchar(500) DEFAULT NULL COMMENT '审核备注',
    `auditor_id` varchar(64) DEFAULT NULL COMMENT '审核人ID',
    `auditor_name` varchar(100) DEFAULT NULL COMMENT '审核人姓名',
    `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
    `return_logistics_no` varchar(100) DEFAULT NULL COMMENT '退货物流单号',
    `exchange_logistics_no` varchar(100) DEFAULT NULL COMMENT '换货物流单号',
    `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_after_sale_no` (`after_sale_no`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='售后申请表';

-- =====================================================
-- 第三部分：初始数据
-- =====================================================

-- ----------------------------
-- 插入默认分类
-- ----------------------------
INSERT INTO `ape_vegetable_type` (`id`, `name`, `create_time`) VALUES
('1', '叶菜类', NOW()),
('2', '根茎类', NOW()),
('3', '瓜果类', NOW()),
('4', '豆类', NOW()),
('5', '菌菇类', NOW()),
('6', '水果类', NOW())
ON DUPLICATE KEY UPDATE `name`=`name`;

-- ----------------------------
-- 插入默认轮播图
-- ----------------------------
INSERT INTO `ape_rotation` (`id`, `content`, `create_time`) VALUES
('1', '/img/image1.png', NOW()),
('2', '/img/image2.png', NOW()),
('3', '/img/image3.png', NOW())
ON DUPLICATE KEY UPDATE `content`=`content`;

-- ----------------------------
-- 插入默认佣金配置
-- ----------------------------
INSERT INTO `ape_commission_config` (`id`, `config_name`, `commission_rate`, `is_default`, `status`, `create_time`, `update_time`)
VALUES ('1', '默认佣金配置', 5.00, 1, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE `config_name`=`config_name`;

-- =====================================================
-- 初始化完成
-- =====================================================
-- 注意：
-- 1. 用户表创建后，需要通过注册接口或管理员后台创建用户
-- 2. 密码使用 PasswordUtils.encrypt() 方法加密后存储
-- 3. 格式：password$salt (密码和盐值用$分隔)
-- 4. 确保 img 目录下有轮播图图片：image1.png, image2.png, image3.png
-- =====================================================
