# 蔬菜销售系统 - 开发任务进度

## 功能模块总览

### 后端模块（Spring Boot）

| 模块 | 状态 | 完成度 | 说明 |
|------|------|--------|------|
| 用户管理 | ✅ 已完成 | 100% | 注册登录、JWT认证、角色权限 |
| 商品管理 | ✅ 已完成 | 100% | CRUD、审核流程、供应商管理 |
| 订单管理 | ✅ 已完成 | 100% | 购物车、状态跟踪、配送管理 |
| 库存管理 | ✅ 已完成 | 100% | 实时跟踪、预警、防超卖 |
| 售后服务 | ✅ 已完成 | 100% | 退换货申请、处理流程 |
| 财务结算 | ✅ 已完成 | 100% | 结算单、佣金、报表 |
| 智能推荐 | ✅ 已完成 | 100% | 个性化、热门、新品、相似推荐 |
| 保鲜期管理 | ✅ 已完成 | 100% | 新鲜度计算、临期预警 |

### 前端模块（Vue 3）

| 模块 | 状态 | 完成度 | 说明 |
|------|------|--------|------|
| 项目基础架构 | ✅ 已完成 | 100% | Vue3 + Vite + Element Plus |
| API请求封装 | ✅ 已完成 | 100% | Axios封装、Token拦截器 |
| 路由与状态管理 | ✅ 已完成 | 100% | Vue Router + Pinia |
| 用户认证页面 | ✅ 已完成 | 100% | 登录、注册页面 |
| 首页与商品展示 | ✅ 已完成 | 100% | 轮播图、推荐、分类浏览 |
| 商品详情与购物车 | ✅ 已完成 | 100% | 商品详情、购物车管理 |
| 订单管理页面 | ✅ 已完成 | 100% | 下单、订单列表、订单详情 |
| 个人中心 | ✅ 已完成 | 100% | 个人信息、地址管理、收藏 |
| 管理后台 | ✅ 已完成 | 100% | 商品、订单、用户、财务管理 |

---

## 详细任务清单

### 一、后端开发（已完成）

#### 1. 用户管理 ✅

- [x] 用户注册登录（采购商、供应商、管理员）
- [x] JWT身份认证
- [x] 角色权限控制（Shiro）
- [x] 密码加密存储
- [x] 登录日志记录

**相关文件：**
- `LoginController.java` - 登录注册接口
- `ApeUserController.java` - 用户管理
- `ShiroConfig.java` - 安全配置
- `JwtFilter.java` - JWT过滤器

---

#### 2. 商品管理 ✅

- [x] 商品CRUD操作
- [x] 商品分类管理
- [x] 商品搜索与筛选
- [x] 商品详情展示
- [x] 商品上架审核流程
- [x] 供应商商品管理权限

**相关文件：**
- `ApeVegetableController.java` - 商品接口
- `ApeVegetableTypeController.java` - 分类管理

---

#### 3. 订单管理 ✅

- [x] 购物车功能
- [x] 订单创建（单品/购物车）
- [x] 订单配送管理
- [x] 订单状态跟踪完善
- [x] 订单支付接口
- [x] 订单取消与退款

**相关文件：**
- `ApeVegetableOrderController.java` - 订单接口
- `ApeCarController.java` - 购物车
- `ApeDeliveryController.java` - 配送管理

---

#### 4. 库存管理 ✅

- [x] 库存实体与数据表设计
- [x] 实时库存跟踪
- [x] 库存预警机制（低库存提醒）
- [x] 库存变动记录（入库、出库、调整）
- [x] 防止超卖保护（下单时库存校验）

**相关文件：**
- `ApeStock.java` - 库存实体
- `ApeStockRecord.java` - 库存变动记录实体
- `ApeStockController.java` - 库存管理接口
- `ApeStockService.java` - 库存业务逻辑

---

#### 5. 售后服务 ✅

- [x] 售后申请实体设计
- [x] 退货申请
- [x] 换货申请
- [x] 售后处理流程
- [x] 退款处理

**相关文件：**
- `ApeAfterSale.java` - 售后实体
- `ApeAfterSaleController.java` - 售后接口
- `ApeAfterSaleService.java` - 售后业务逻辑

---

#### 6. 财务结算 ✅

- [x] 结算单自动生成
- [x] 佣金计算规则
- [x] 供应商结算管理
- [x] 财务报表统计

**相关文件：**
- `ApeSettlement.java` - 结算单实体
- `ApeCommissionConfig.java` - 佣金配置实体
- `ApeFinanceController.java` - 财务报表接口

---

#### 7. 智能推荐 ✅

- [x] 用户行为记录
- [x] 基于历史行为的个性化推荐
- [x] 热门商品推荐
- [x] 新品推荐
- [x] 相似商品推荐

**相关文件：**
- `ApeUserBehavior.java` - 用户行为实体
- `ApeRecommendController.java` - 推荐接口
- `ApeRecommendService.java` - 推荐算法

---

#### 8. 保鲜期管理 ✅

- [x] 保鲜期配置表
- [x] 采摘日期管理
- [x] 农产品新鲜度智能计算
- [x] 新鲜度状态可视化标签
- [x] 临期商品预警

**相关文件：**
- `ApeFreshnessConfig.java` - 保鲜期配置实体
- `ApeFreshnessController.java` - 保鲜期接口
- `ApeFreshnessService.java` - 新鲜度计算

---

---

### 二、前端开发（已完成）

#### 1. 项目基础架构 ✅

- [x] Vue 3 + Vite 项目搭建
- [x] Element Plus UI框架集成
- [x] SCSS样式预处理
- [x] 自动导入配置

**相关文件：**
- `package.json` - 依赖配置
- `vite.config.js` - Vite配置
- `main.js` - 入口文件

---

#### 2. API请求封装 ✅

- [x] Axios实例创建
- [x] 请求拦截器（Token注入）
- [x] 响应拦截器（错误处理）
- [x] 统一错误提示

**相关文件：**
- `src/utils/request.js` - 请求封装
- `src/api/*.js` - API接口定义

---

#### 3. 路由与状态管理 ✅

- [x] Vue Router路由配置
- [x] 路由守卫（权限控制）
- [x] Pinia状态管理
- [x] 用户状态持久化

**相关文件：**
- `src/router/index.js` - 路由配置
- `src/stores/user.js` - 用户状态
- `src/stores/cart.js` - 购物车状态

---

#### 4. 用户认证页面 ✅

- [x] 登录页面
- [x] 注册页面
- [x] 表单验证
- [x] 记住登录

**相关文件：**
- `src/views/login/index.vue` - 登录页
- `src/views/login/register.vue` - 注册页

---

#### 5. 首页与商品展示 ✅

- [x] 主布局组件
- [x] 轮播图组件
- [x] 商品分类页面
- [x] 商品列表页面（筛选/排序/分页）
- [x] 商品详情页面
- [x] 商品卡片组件

**相关文件：**
- `src/layouts/MainLayout.vue` - 主布局
- `src/views/home/index.vue` - 首页
- `src/views/category/index.vue` - 分类页
- `src/views/products/index.vue` - 商品列表
- `src/views/products/detail.vue` - 商品详情
- `src/components/ProductCard.vue` - 商品卡片

---

#### 6. 商品详情与购物车 ✅

- [x] 购物车页面
- [x] 购物车数量修改
- [x] 购物车删除
- [x] 全选/取消全选
- [x] 结算页面

**相关文件：**
- `src/views/cart/index.vue` - 购物车页
- `src/views/order/checkout.vue` - 结算页

---

#### 7. 订单管理页面 ✅

- [x] 订单列表页面
- [x] 订单状态筛选
- [x] 订单详情页面
- [x] 订单支付
- [x] 确认收货
- [x] 取消订单

**相关文件：**
- `src/views/order/index.vue` - 订单列表
- `src/views/order/detail.vue` - 订单详情

---

#### 8. 个人中心 ✅

- [x] 个人中心布局
- [x] 个人信息编辑
- [x] 收货地址管理
- [x] 我的收藏
- [x] 修改密码

**相关文件：**
- `src/views/user/index.vue` - 个人中心布局
- `src/views/user/profile.vue` - 个人信息
- `src/views/user/address.vue` - 收货地址
- `src/views/user/favorites.vue` - 我的收藏
- `src/views/user/password.vue` - 修改密码

---

#### 9. 管理后台 ✅

- [x] 管理后台布局
- [x] 控制台（数据统计）
- [x] 商品管理
- [x] 分类管理
- [x] 订单管理
- [x] 用户管理
- [x] 轮播图管理
- [x] 配送员管理

**相关文件：**
- `src/layouts/AdminLayout.vue` - 后台布局
- `src/views/admin/dashboard.vue` - 控制台
- `src/views/admin/vegetables/index.vue` - 商品管理
- `src/views/admin/categories/index.vue` - 分类管理
- `src/views/admin/orders/index.vue` - 订单管理
- `src/views/admin/users/index.vue` - 用户管理
- `src/views/admin/rotations/index.vue` - 轮播图管理
- `src/views/admin/delivery/index.vue` - 配送员管理

---

## 项目文件结构

### 后端结构

```
ape-admin/src/main/java/com/ape/apeadmin/controller/
├── login/          # 登录认证
├── user/           # 用户管理
├── vegetable/      # 商品管理
├── type/           # 分类管理
├── order/          # 订单管理
├── car/            # 购物车
├── address/        # 收货地址
├── delivery/       # 配送管理
├── rotation/       # 轮播图
├── favor/          # 收藏
├── stock/          # 库存管理
├── afterSale/      # 售后服务
├── finance/        # 财务结算
├── recommend/      # 智能推荐
├── freshness/      # 保鲜期管理
```

### 前端结构

```
web-admin/src/
├── api/                  # API接口
│   ├── user.js          # 用户相关
│   ├── vegetable.js     # 商品相关
│   ├── cart.js          # 购物车
│   ├── order.js         # 订单
│   ├── address.js       # 地址
│   ├── recommend.js     # 推荐
│   ├── rotation.js      # 轮播图
│   └── common.js        # 公共接口
├── components/           # 公共组件
│   ├── ProductCard.vue  # 商品卡片
│   ├── EmptyState.vue   # 空状态
│   └── ImageUpload.vue  # 图片上传
├── layouts/              # 布局组件
│   ├── MainLayout.vue   # 前台布局
│   └── AdminLayout.vue  # 后台布局
├── router/               # 路由配置
│   └── index.js
├── stores/               # 状态管理
│   ├── user.js          # 用户状态
│   └── cart.js          # 购物车状态
├── utils/                # 工具函数
│   └── request.js       # 请求封装
├── views/                # 页面组件
│   ├── home/            # 首页
│   ├── login/           # 登录注册
│   ├── category/        # 分类
│   ├── products/        # 商品
│   ├── cart/            # 购物车
│   ├── order/           # 订单
│   ├── user/            # 个人中心
│   ├── admin/           # 管理后台
│   └── error/           # 错误页面
├── styles/               # 样式文件
│   └── index.scss
├── App.vue              # 根组件
└── main.js              # 入口文件
```

---

## API接口汇总

### 用户相关
| 接口 | 方法 | 说明 |
|------|------|------|
| /login | POST | 用户登录 |
| /login/register | POST | 用户注册 |
| /login/logout | GET | 退出登录 |
| /user/getUserInfo | GET | 获取用户信息 |
| /user/setUserInfo | POST | 修改用户信息 |
| /user/changePassword | POST | 修改密码 |

### 商品相关
| 接口 | 方法 | 说明 |
|------|------|------|
| /vegetable/getApeVegetablePage | POST | 分页查询商品 |
| /vegetable/getApeVegetableById | GET | 获取商品详情 |
| /type/getApeVegetableTypeList | GET | 获取分类列表 |

### 购物车相关
| 接口 | 方法 | 说明 |
|------|------|------|
| /car/getApeCarList | POST | 获取购物车 |
| /car/saveApeCar | POST | 添加购物车 |
| /car/editApeCar | POST | 修改数量 |
| /car/removeApeCar | GET | 删除购物车 |

### 订单相关
| 接口 | 方法 | 说明 |
|------|------|------|
| /order/getApeVegetableOrderPage | POST | 订单列表 |
| /order/getApeVegetableOrderById | GET | 订单详情 |
| /order/saveApeVegetableOrder | POST | 创建订单 |
| /order/payOrder | POST | 支付订单 |
| /order/confirmReceive | POST | 确认收货 |
| /order/cancelOrder | POST | 取消订单 |

### 推荐相关
| 接口 | 方法 | 说明 |
|------|------|------|
| /recommend/personalized | GET | 个性化推荐 |
| /recommend/hot | GET | 热门推荐 |
| /recommend/newArrival | GET | 新品推荐 |
| /recommend/similar | GET | 相似推荐 |

---

## 更新日志

| 日期 | 内容 |
|------|------|
| 2026-01-26 | 创建开发任务文档，分析现有功能 |
| 2026-01-26 | 完成库存管理模块（实时跟踪、预警、变动记录、防超卖） |
| 2026-01-26 | 完成订单状态跟踪（支付、发货、收货、取消） |
| 2026-01-26 | 完成售后服务模块（退换货申请、审核、处理流程） |
| 2026-01-26 | 完成财务结算模块（结算单、佣金配置、供应商结算、报表） |
| 2026-01-26 | 完成智能推荐模块（个性化、热门、新品、相似推荐） |
| 2026-01-26 | 完成保鲜期管理模块（新鲜度计算、保鲜期配置、临期预警） |
| 2026-01-26 | 完成商品审核流程（供应商提交、管理员审核、重新提交）|
| 2026-01-27 | 完成前端商品分类页面 |
| 2026-01-27 | 完成前端商品列表页面（筛选、排序、分页） |
| 2026-01-27 | 完成前端商品详情页面（相似推荐） |
| 2026-01-27 | 完成前端购物车页面 |
| 2026-01-27 | 完成前端结算页面（地址选择、订单提交） |
| 2026-01-27 | 完成前端订单列表页面（状态筛选） |
| 2026-01-27 | 完成前端订单详情页面（支付、收货） |
| 2026-01-27 | 完成前端个人中心（信息、地址、收藏、密码） |
| 2026-01-27 | 完成前端管理后台（控制台、商品、分类、订单、用户、轮播图、配送员） |
| 2026-01-27 | 全部开发任务完成 |

---

## 待优化事项

1. **性能优化**
   - [ ] 热门推荐结果缓存（Redis）
   - [ ] 库存预警异步通知
   - [ ] 图片懒加载优化

2. **功能增强**
   - [ ] 短信通知集成
   - [ ] 数据导出功能
   - [ ] 商品评价系统

3. **安全加固**
   - [ ] 接口限流
   - [ ] 敏感数据加密
   - [ ] XSS防护

4. **用户体验**
   - [ ] 移动端适配优化
   - [ ] 骨架屏加载
   - [ ] 页面缓存

---

## 项目启动

### 后端启动
```bash
# 1. 确保MySQL和Redis已启动
# 2. 导入SQL脚本 sql/vegetable_sale_tables.sql
# 3. 修改配置文件
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
