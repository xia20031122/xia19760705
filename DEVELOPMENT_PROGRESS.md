# 蔬菜销售系统 - 开发进度追踪

## 项目概述
基于Spring Boot + MyBatis-Plus + Vue 3的蔬菜销售B2B平台，支持采购商、供应商、管理员三种角色。

---

## 功能模块开发进度

### 一、后端模块（Spring Boot）✅ 100%完成

#### 1. 用户管理模块 ✅
**负责文件：**
- `ApeUser.java` - 用户实体
- `ApeUserService.java` / `ApeUserServiceImpl.java` - 用户服务
- `ApeRole.java` - 角色实体
- `ShiroUtils.java` - Shiro安全工具
- `JwtUtil.java` - JWT工具类
- `LoginController.java` - 登录注册接口
- `ApeUserController.java` - 用户管理接口

**功能清单：**
- [x] 用户注册登录（采购商/供应商/管理员）
- [x] JWT身份认证
- [x] 角色权限控制（Shiro）
- [x] 用户信息管理
- [x] 密码加密存储
- [x] 登录日志记录

---

#### 2. 商品管理模块 ✅
**负责文件：**
- `ApeVegetable.java` - 商品实体
- `ApeVegetableType.java` - 商品分类实体
- `ApeVegetableController.java` - 商品控制器
- `ApeVegetableService.java` - 商品服务
- `ApeVegetableTypeController.java` - 分类控制器

**功能清单：**
- [x] 商品上架与审核（auditStatus字段）
- [x] 商品分类管理
- [x] 商品搜索与筛选
- [x] 商品详情展示
- [x] 商品图片管理
- [x] 供应商商品提交审核

---

#### 3. 订单管理模块 ✅
**负责文件：**
- `ApeVegetableOrder.java` - 订单实体
- `ApeVegetableOrderController.java` - 订单控制器
- `ApeCar.java` - 购物车实体
- `ApeCarService.java` - 购物车服务
- `ApeDelivery.java` - 配送员实体
- `ApeDeliveryController.java` - 配送管理

**功能清单：**
- [x] 购物车功能（添加/删除/数量修改）
- [x] 订单创建（单品/购物车批量下单）
- [x] 订单支付（模拟支付）
- [x] 订单状态跟踪（待付款→待发货→待收货→已完成）
- [x] 订单配送管理（配送员分配）
- [x] 订单取消与退款

**订单状态说明：**
| 状态码 | 状态 | 说明 |
|--------|------|------|
| 0 | 待付款 | 订单已创建，等待支付 |
| 1 | 待发货 | 已支付，等待发货 |
| 2 | 待收货 | 已发货，配送中 |
| 3 | 已完成 | 已确认收货 |
| 4 | 已取消 | 订单取消 |
| 5 | 退款中 | 售后处理中 |
| 6 | 已退款 | 退款完成 |

---

#### 4. 库存管理模块 ✅
**负责文件：**
- `ApeStock.java` - 库存实体
- `ApeStockRecord.java` - 库存变动记录实体
- `ApeStockService.java` / `ApeStockServiceImpl.java` - 库存服务
- `ApeStockRecordService.java` - 库存记录服务
- `ApeStockController.java` - 库存控制器
- `ApeStockMapper.xml` - 库存SQL映射

**功能清单：**
- [x] 实时库存跟踪
- [x] 库存预警机制（warningQuantity字段）
- [x] 库存变动记录（入库/出库/调整/锁定/解锁）
- [x] 防止超卖保护（乐观锁机制）
- [x] 库存锁定/解锁（下单锁定，发货确认扣减）

**核心SQL（防超卖）：**
```sql
-- 扣减库存（乐观锁）
UPDATE ape_stock SET quantity = quantity - #{quantity}
WHERE vegetable_id = #{vegetableId} AND quantity >= #{quantity}
```

---

#### 5. 售后服务模块 ✅
**负责文件：**
- `ApeAfterSale.java` - 售后申请实体
- `ApeAfterSaleService.java` / `ApeAfterSaleServiceImpl.java` - 售后服务
- `ApeAfterSaleController.java` - 售后控制器

**功能清单：**
- [x] 退货退款申请
- [x] 仅退款申请
- [x] 换货申请
- [x] 售后审核（管理员）
- [x] 退货物流填写
- [x] 售后完成（库存归还）

**售后状态流程：**
```
待审核(0) → 已同意(1) → 处理中(3) → 已完成(4)
         ↘ 已拒绝(2)
         ↘ 已取消(5)
```

---

#### 6. 财务结算模块 ✅
**负责文件：**
- `ApeSettlement.java` - 结算单实体
- `ApeCommissionConfig.java` - 佣金配置实体
- `ApeSettlementService.java` / `ApeSettlementServiceImpl.java` - 结算服务
- `ApeCommissionConfigService.java` - 佣金配置服务
- `ApeFinanceController.java` - 财务控制器

**功能清单：**
- [x] 结算单自动生成（按周期）
- [x] 佣金计算（可配置比例）
- [x] 供应商结算管理
- [x] 财务报表统计（日/月/年销售统计）
- [x] 财务概览（今日/本月销售额）

**结算计算公式：**
```
结算金额 = 订单总金额 - 佣金金额
佣金金额 = 订单总金额 × 佣金比例(%)
```

---

#### 7. 智能推荐模块 ✅
**负责文件：**
- `ApeUserBehavior.java` - 用户行为记录实体
- `ApeRecommendService.java` / `ApeRecommendServiceImpl.java` - 推荐服务
- `ApeUserBehaviorService.java` - 用户行为服务
- `ApeRecommendController.java` - 推荐控制器

**功能清单：**
- [x] 基于历史行为的个性化推荐
- [x] 热门商品推荐（30天销量统计）
- [x] 新品推荐（7天内上架）
- [x] 相似商品推荐（同分类）
- [x] 猜你喜欢（综合推荐）
- [x] 用户行为记录（浏览/收藏/加购/购买/搜索）

**推荐算法逻辑：**
1. 获取用户偏好分类（基于行为权重）
2. 基于偏好分类推荐商品
3. 收藏但未购买的商品
4. 热门商品补充

---

#### 8. 保鲜期管理模块 ✅
**负责文件：**
- `ApeFreshnessConfig.java` - 保鲜期配置实体
- `ApeFreshnessInfo.java` - 新鲜度信息DTO
- `ApeFreshnessService.java` / `ApeFreshnessServiceImpl.java` - 保鲜期服务
- `ApeFreshnessController.java` - 保鲜期控制器

**功能清单：**
- [x] 农产品新鲜度智能计算
- [x] 分类别保鲜期配置
- [x] 新鲜度状态可视化标签
- [x] 采摘日期管理
- [x] 临期商品预警
- [x] 过期商品列表

**新鲜度状态：**
| 状态 | 百分比 | 颜色 |
|------|--------|------|
| 新鲜 | >70% | 绿色 #52c41a |
| 良好 | 40%-70% | 蓝色 #1890ff |
| 临期 | 0%-40% | 橙色 #faad14 |
| 过期 | <0% | 红色 #f5222d |

---

---

### 二、前端模块（Vue 3）✅ 100%完成

#### 1. 项目基础架构 ✅
**负责文件：**
- `package.json` - 项目依赖
- `vite.config.js` - Vite配置
- `main.js` - 入口文件
- `App.vue` - 根组件

**功能清单：**
- [x] Vue 3 + Vite 项目搭建
- [x] Element Plus UI框架集成
- [x] SCSS样式预处理
- [x] 自动导入配置（组件、图标）
- [x] 中文语言包配置

---

#### 2. API请求封装 ✅
**负责文件：**
- `src/utils/request.js` - Axios封装
- `src/api/*.js` - API接口定义

**功能清单：**
- [x] Axios实例创建（baseURL、timeout）
- [x] 请求拦截器（Token自动注入）
- [x] 响应拦截器（统一错误处理）
- [x] Token过期自动跳转登录
- [x] 统一错误提示

**API文件列表：**
- `user.js` - 用户相关接口
- `vegetable.js` - 商品相关接口
- `cart.js` - 购物车接口
- `order.js` - 订单接口
- `address.js` - 地址接口
- `recommend.js` - 推荐接口
- `rotation.js` - 轮播图接口
- `common.js` - 公共接口（配送员、收藏等）

---

#### 3. 路由与状态管理 ✅
**负责文件：**
- `src/router/index.js` - 路由配置
- `src/stores/user.js` - 用户状态管理
- `src/stores/cart.js` - 购物车状态管理

**功能清单：**
- [x] Vue Router路由配置
- [x] 路由守卫（登录验证、权限控制）
- [x] Pinia状态管理
- [x] 用户信息持久化（localStorage）
- [x] 购物车状态管理（数量、选中状态）

**路由结构：**
- 前台路由：首页、分类、商品、购物车、订单、个人中心
- 后台路由：控制台、商品管理、分类管理、订单管理、用户管理、轮播图、配送员

---

#### 4. 用户认证页面 ✅
**负责文件：**
- `src/views/login/index.vue` - 登录页
- `src/views/login/register.vue` - 注册页

**功能清单：**
- [x] 登录页面（用户名/密码、记住我）
- [x] 注册页面（用户类型选择、表单验证）
- [x] 表单验证（必填、格式校验）
- [x] 登录状态保持

---

#### 5. 首页与商品展示 ✅
**负责文件：**
- `src/layouts/MainLayout.vue` - 主布局
- `src/views/home/index.vue` - 首页
- `src/views/category/index.vue` - 分类页
- `src/views/products/index.vue` - 商品列表
- `src/views/products/detail.vue` - 商品详情
- `src/components/ProductCard.vue` - 商品卡片

**功能清单：**
- [x] 顶部导航（搜索、购物车、用户信息）
- [x] 轮播图展示
- [x] 商品分类展示
- [x] 推荐商品（个性化、热门、新品）
- [x] 商品分类页面
- [x] 商品列表（筛选、排序、分页）
- [x] 商品详情（图片、价格、库存、新鲜度）
- [x] 相似商品推荐
- [x] 商品收藏功能

---

#### 6. 商品详情与购物车 ✅
**负责文件：**
- `src/views/cart/index.vue` - 购物车页
- `src/views/order/checkout.vue` - 结算页

**功能清单：**
- [x] 购物车列表展示
- [x] 购物车数量修改
- [x] 购物车删除（单个/批量）
- [x] 全选/取消全选
- [x] 结算页面（地址选择、商品确认）
- [x] 订单备注
- [x] 地址新增/编辑

---

#### 7. 订单管理页面 ✅
**负责文件：**
- `src/views/order/index.vue` - 订单列表
- `src/views/order/detail.vue` - 订单详情

**功能清单：**
- [x] 订单列表（状态筛选、分页）
- [x] 订单详情（进度跟踪、商品信息、收货信息）
- [x] 订单支付（模拟支付）
- [x] 确认收货
- [x] 取消订单
- [x] 申请售后
- [x] 再次购买

---

#### 8. 个人中心 ✅
**负责文件：**
- `src/views/user/index.vue` - 个人中心布局
- `src/views/user/profile.vue` - 个人信息
- `src/views/user/address.vue` - 收货地址
- `src/views/user/favorites.vue` - 我的收藏
- `src/views/user/password.vue` - 修改密码

**功能清单：**
- [x] 个人中心侧边栏导航
- [x] 个人信息编辑（头像、昵称、性别、手机、邮箱）
- [x] 收货地址管理（新增、编辑、删除、设为默认）
- [x] 我的收藏（收藏列表、取消收藏、加入购物车）
- [x] 修改密码（当前密码、新密码、确认密码）

---

#### 9. 管理后台 ✅
**负责文件：**
- `src/layouts/AdminLayout.vue` - 后台布局
- `src/views/admin/dashboard.vue` - 控制台
- `src/views/admin/vegetables/index.vue` - 商品管理
- `src/views/admin/categories/index.vue` - 分类管理
- `src/views/admin/orders/index.vue` - 订单管理
- `src/views/admin/users/index.vue` - 用户管理
- `src/views/admin/rotations/index.vue` - 轮播图管理
- `src/views/admin/delivery/index.vue` - 配送员管理

**功能清单：**
- [x] 后台侧边栏导航（可折叠）
- [x] 控制台（数据统计、快捷操作、待处理事项、最近订单）
- [x] 商品管理（CRUD、上下架、搜索筛选）
- [x] 分类管理（CRUD、排序）
- [x] 订单管理（列表、详情、发货、取消）
- [x] 用户管理（CRUD、重置密码、状态管理）
- [x] 轮播图管理（CRUD、显示/隐藏）
- [x] 配送员管理（CRUD、状态管理）

---

## 数据库表结构

### 核心业务表
| 表名 | 说明 | 状态 |
|------|------|------|
| ape_user | 用户表 | ✅ |
| ape_vegetable | 商品表 | ✅ |
| ape_vegetable_type | 商品分类表 | ✅ |
| ape_vegetable_order | 订单表 | ✅ |
| ape_car | 购物车表 | ✅ |
| ape_order_address | 收货地址表 | ✅ |
| ape_delivery | 配送员表 | ✅ |
| ape_rotation | 轮播图表 | ✅ |
| ape_vegetable_favor | 收藏表 | ✅ |

### 扩展功能表
| 表名 | 说明 | 状态 |
|------|------|------|
| ape_stock | 库存表 | ✅ |
| ape_stock_record | 库存变动记录表 | ✅ |
| ape_after_sale | 售后申请表 | ✅ |
| ape_settlement | 结算单表 | ✅ |
| ape_commission_config | 佣金配置表 | ✅ |
| ape_freshness_config | 保鲜期配置表 | ✅ |
| ape_user_behavior | 用户行为记录表 | ✅ |

---

## 配置说明

### 后端配置（application-dev.yml）
```yaml
# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ape-vegetable
    username: root
    password: your_password

# Redis配置
spring:
  redis:
    host: 127.0.0.1
    port: 6379

```

### 前端配置（vite.config.js）
```javascript
export default {
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
}
```

---

## API接口汇总

### 用户相关 /user
- POST /login - 用户登录
- POST /login/register - 用户注册
- GET /login/logout - 退出登录
- GET /user/getUserInfo - 获取用户信息
- POST /user/setUserInfo - 修改用户信息
- POST /user/changePassword - 修改密码

### 商品相关 /vegetable
- POST /vegetable/getApeVegetablePage - 分页查询商品
- GET /vegetable/getApeVegetableById - 获取商品详情
- POST /vegetable/saveApeVegetable - 保存商品
- POST /vegetable/editApeVegetable - 编辑商品
- GET /vegetable/removeApeVegetable - 删除商品

### 订单相关 /order
- POST /order/getApeVegetableOrderPage - 订单列表
- GET /order/getApeVegetableOrderById - 订单详情
- POST /order/saveApeVegetableOrder - 创建订单
- POST /order/payOrder - 支付订单
- POST /order/confirmReceive - 确认收货
- POST /order/cancelOrder - 取消订单

### 库存管理 /stock
- POST /stock/getStockPage - 分页查询库存
- GET /stock/getWarningList - 库存预警列表
- POST /stock/stockIn - 入库
- POST /stock/stockOut - 出库
- POST /stock/adjustStock - 库存调整

### 财务结算 /finance
- POST /finance/getSettlementPage - 结算单列表
- POST /finance/generateSettlement - 生成结算单
- POST /finance/confirmSettlement - 确认结算
- GET /finance/getSalesStatistics - 销售统计
- GET /finance/getFinanceOverview - 财务概览

### 智能推荐 /recommend
- GET /recommend/personalized - 个性化推荐
- GET /recommend/hot - 热门推荐
- GET /recommend/newArrival - 新品推荐
- GET /recommend/similar - 相似推荐
- GET /recommend/guessYouLike - 猜你喜欢

### 保鲜期管理 /freshness
- GET /freshness/calculateFreshness - 计算新鲜度
- GET /freshness/getNearExpiryList - 临期商品
- GET /freshness/getExpiredList - 过期商品
- POST /freshness/updateHarvestDate - 更新采摘日期

### 售后管理 /afterSale
- POST /afterSale/submitAfterSale - 提交售后
- POST /afterSale/auditAfterSale - 审核售后
- POST /afterSale/fillReturnLogistics - 填写物流
- POST /afterSale/completeAfterSale - 完成售后

---

## 开发时间线

| 日期 | 完成内容 |
|------|----------|
| 2026-01-26 | 后端核心功能开发完成（用户、商品、订单、库存、售后、财务、推荐、保鲜期） |
| 2026-01-27 | 前端项目基础架构搭建 |
| 2026-01-27 | 前端用户认证页面（登录、注册） |
| 2026-01-27 | 前端首页与商品展示（首页、分类、列表、详情） |
| 2026-01-27 | 前端购物车与结算页面 |
| 2026-01-27 | 前端订单管理页面（列表、详情） |
| 2026-01-27 | 前端个人中心（信息、地址、收藏、密码） |
| 2026-01-27 | 前端管理后台（控制台、商品、分类、订单、用户、轮播图、配送员） |
| 2026-01-27 | **全部开发任务完成** ✅ |

---

## 待优化事项

1. **性能优化**
   - [ ] 热门推荐结果缓存（Redis）
   - [ ] 库存预警异步通知
   - [ ] 图片懒加载优化
   - [ ] 路由懒加载优化

2. **功能增强**
   - [ ] 短信通知集成
   - [ ] 数据导出功能
   - [ ] 商品评价系统
   - [ ] 优惠券系统

3. **安全加固**
   - [ ] 接口限流
   - [ ] 敏感数据加密
   - [ ] XSS防护
   - [ ] CSRF防护

4. **用户体验**
   - [ ] 移动端适配优化
   - [ ] 骨架屏加载
   - [ ] 页面缓存
   - [ ] 离线支持

---

## 项目启动

### 后端启动
```bash
# 1. 确保MySQL和Redis已启动
# 2. 导入SQL脚本 sql/vegetable_sale_tables.sql
# 3. 修改配置文件 application-dev.yml
cd ape-admin
mvn spring-boot:run
```

### 前端启动
```bash
cd web-admin
npm install
npm run dev
```

访问地址：
- 前台：http://localhost:5173
- 后台：http://localhost:5173/admin
- API：http://localhost:8080

---

## 总结

✅ **后端开发：100%完成**
- 9个核心业务模块全部完成
- 所有API接口已实现
- 数据库表结构完整

✅ **前端开发：100%完成**
- 9个功能模块全部完成
- 所有页面组件已创建
- 路由和状态管理完善

🎉 **项目开发完成度：100%**
