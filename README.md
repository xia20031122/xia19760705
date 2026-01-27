# 蔬菜销售系统 - 后台管理服务端

## 项目概述

| 项目名称 | BACK_STAGE_SERVER（蔬菜销售后台管理系统） |
|---------|--------------------------------------|
| 技术栈 | Spring Boot 2.4.2 + MyBatis-Plus + Shiro + Redis + MySQL |
| Java版本 | 1.8 |
| 数据库 | MySQL (ape-vegetable) |

---

## 模块结构

```
vegetable-sale-master/
├── ape-admin/          # 🎯 主应用模块 (程序入口)
├── ape-common/         # 🔧 通用工具模块
├── ape-framework/      # ⚙️ 框架核心模块
├── ape-system/         # 📦 业务系统模块
├── ape-generator/      # 🔨 代码生成模块
├── ape-quartz/         # ⏰ 定时任务模块
├── img/                # 🖼️ 图片资源
└── pom.xml             # Maven 父 POM
```

---

## 模块详情

### 1. ape-admin - 主应用模块

**作用**：程序启动入口，包含所有 Controller 层接口

**核心组件**：

| 目录 | 功能 |
|------|-----|
| `controller/login/` | 登录认证接口 |
| `controller/user/` | 用户管理 |
| `controller/vegetable/` | 蔬菜商品管理 |
| `controller/order/` | 订单管理 |
| `controller/car/` | 购物车管理 |
| `controller/shed/` | 大棚管理 |
| `controller/delivery/` | 配送管理 |
| `controller/rotation/` | 轮播图管理 |
| `controller/type/` | 蔬菜分类管理 |
| `controller/favor/` | 收藏功能 |
| `controller/address/` | 收货地址管理 |
| `controller/role/`, `menu/`, `dept/` | 权限管理 (RBAC) |

---

### 2. ape-common - 通用工具模块

**作用**：提供公共工具类、常量、枚举、注解等

**核心组件**：

- `annotation/Log.java` - 操作日志注解
- `constant/Constants.java` - 系统常量
- `domain/Result.java` - 统一响应结果
- `enums/` - 业务状态枚举
- `utils/` - 工具类 (JWT、密码、字符串处理等)

---

### 3. ape-framework - 框架核心模块

**作用**：框架配置、安全认证、数据源等基础设施

**核心组件**：

| 目录 | 功能 |
|------|-----|
| `config/ShiroConfig.java` | Shiro 安全框架配置 |
| `config/RedisConfig.java` | Redis 缓存配置 |
| `config/DruidConfig.java` | 数据源配置 (支持主从) |
| `config/CorsConfig.java` | 跨域配置 |
| `filter/JwtFilter.java` | JWT 认证过滤器 |
| `aspectj/LogAspect.java` | 日志切面 |
| `event/` | 事件驱动 (登录日志、操作日志) |
| `datasource/` | 动态数据源切换 |

---

### 4. ape-system - 业务系统模块

**作用**：核心业务逻辑层，包含 Domain、Mapper、Service

**业务实体**：

| 实体类 | 说明 |
|--------|------|
| `ApeVegetable` | 蔬菜商品 |
| `ApeVegetableType` | 蔬菜分类 |
| `ApeVegetableOrder` | 蔬菜订单 |
| `ApeCar` | 购物车 |
| `ApeVegetableFavor` | 商品收藏 |
| `ApeShed` | 大棚信息 |
| `ApeShedAppointment` | 大棚预约 |
| `ApeDelivery` | 配送信息 |
| `ApeOrderAddress` | 收货地址 |
| `ApeRotation` | 轮播图 |
| `ApeUser` | 系统用户 |
| `ApeRole` / `ApeMenu` | 角色/菜单 (权限) |
| `ApeLoginLog` / `ApeOperateLog` | 日志记录 |

**代码分层**：

```
ape-system/src/main/java/com/ape/apesystem/
├── domain/     # 实体类 (23个)
├── mapper/     # MyBatis Mapper 接口 (23个)
└── service/    # Service 接口与实现 (23个)
```

---

## 技术架构图

```
┌─────────────────────────────────────────────────────┐
│                    ape-admin                        │
│  ┌───────────────────────────────────────────────┐  │
│  │              Controller 层                     │  │
│  │   (REST API: 登录/商品/订单/用户/权限等)        │  │
│  └───────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────┐
│                   ape-framework                     │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐           │
│  │  Shiro   │ │   JWT    │ │  Redis   │           │
│  │  安全    │ │  认证    │ │  缓存    │           │
│  └──────────┘ └──────────┘ └──────────┘           │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐           │
│  │ 日志切面  │ │ 跨域配置 │ │ 动态数据源│           │
│  └──────────┘ └──────────┘ └──────────┘           │
└─────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────┐
│                    ape-system                       │
│  ┌───────────────────────────────────────────────┐  │
│  │     Service 层 (业务逻辑)                      │  │
│  └───────────────────────────────────────────────┘  │
│  ┌───────────────────────────────────────────────┐  │
│  │     Mapper 层 (MyBatis-Plus 数据访问)          │  │
│  └───────────────────────────────────────────────┘  │
│  ┌───────────────────────────────────────────────┐  │
│  │     Domain 层 (实体类)                         │  │
│  └───────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────┐
│                    ape-common                       │
│       工具类 / 常量 / 枚举 / 统一响应结果            │
└─────────────────────────────────────────────────────┘
```

---

## 核心业务功能

1. **商品管理**：蔬菜商品的 CRUD、分类管理、轮播图
2. **订单系统**：购物车、下单、订单管理、收货地址
3. **大棚功能**：大棚信息管理、预约参观
4. **配送管理**：配送员管理、配送状态跟踪
5. **用户系统**：用户管理、角色权限 (RBAC)
6. **系统管理**：部门、岗位、菜单、字典、参数配置
7. **日志系统**：登录日志、操作日志

---

## 运行配置

| 配置项 | 值 |
|-------|-----|
| 端口 | 8080 |
| 数据库 | MySQL `ape-vegetable` |
| 缓存 | Redis (127.0.0.1:6379) |
| 连接池 | Druid (支持主从分离) |

---

## 快速启动

1. 确保 MySQL 和 Redis 服务已启动
2. 创建数据库 `ape-vegetable` 并导入初始数据
3. 修改 `ape-admin/src/main/resources/application-dev.yml` 中的数据库和 Redis 配置
4. 运行 `ApeAdminApplication.java` 启动项目
5. 访问 `http://localhost:8080`

---

## 主要依赖

| 依赖 | 版本 | 说明 |
|-----|------|-----|
| Spring Boot | 2.4.2 | 基础框架 |
| MyBatis-Plus | 3.4.1 | ORM 框架 |
| Shiro | 1.7.1 | 安全框架 |
| Druid | 1.2.16 | 数据库连接池 |
| Hutool | 5.8.15 | 工具库 |
| Fastjson2 | 2.0.25 | JSON 解析 |
| JWT | 0.9.1 | Token 认证 |
| Lombok | 1.18.24 | 简化代码 |
