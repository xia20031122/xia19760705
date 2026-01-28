# 修复说明 - 付款窗口和金额显示问题

## 问题描述

用户报告了两个主要问题：
1. 提交订单后没有付款窗口，直接跳转到订单详情页
2. 显示金额和付款金额不一致（价格计算错误）

## 根本原因分析

### 1. 缺少付款窗口
- 在 `checkout.vue` 中，提交订单成功后直接使用 `router.push` 跳转到订单详情页
- 没有显示支付弹窗让用户选择支付方式

### 2. 价格计算不一致
**数据库和后端问题：**
- 数据库表 `ape_vegetable_order` 只有一个 `price` 字段
- 后端在创建订单时，将 `price` 字段设置为 `单价 × 数量`（总价）
- 但前端在多处将此 `price` 当作单价使用，导致重复计算

**示例：**
```java
// 后端代码 (错误的逻辑)
apeVegetableOrder.setPrice(vegetable.getPrice() * num);  // 存储的是总价

// 前端代码 (错误的计算)
¥{{ (order.price * order.num).toFixed(2) }}  // 再次乘以数量，导致金额错误
```

### 3. 字段命名不一致
- 后端使用 `state`、`orderNumber` 字段
- 前端模板使用 `status`、`orderNo` 字段
- 缺少字段映射导致显示异常

## 修复方案

### 1. 数据库层面

**添加 total_price 字段：**
```sql
ALTER TABLE `ape_vegetable_order` ADD COLUMN `total_price` decimal(10,2) COMMENT '总价' AFTER `price`;
ALTER TABLE `ape_vegetable_order` MODIFY COLUMN `price` decimal(10,2) COMMENT '单价';
```

**数据迁移：**
```sql
-- 将现有的总价数据迁移到 total_price
-- 同时重新计算单价
UPDATE `ape_vegetable_order` 
SET 
    `total_price` = `price`,
    `price` = CASE 
        WHEN `num` > 0 THEN `price` / `num`
        ELSE `price`
    END;
```

### 2. 后端层面

**更新 ApeVegetableOrder 实体类：**
```java
// 添加字段
private Float price;        // 单价
private Float totalPrice;   // 总价
```

**更新订单创建逻辑：**
```java
// 单品下单
apeVegetableOrder.setPrice(vegetable.getPrice());              // 单价
apeVegetableOrder.setTotalPrice(vegetable.getPrice() * num);   // 总价

// 购物车下单
apeVegetableOrder.setPrice(vegetable.getPrice());              // 单价
apeVegetableOrder.setTotalPrice(vegetable.getPrice() * apeCar.getNum());  // 总价
```

### 3. 前端层面

**添加支付弹窗（checkout.vue）：**
```vue
<!-- 提交订单后显示支付弹窗 -->
<el-dialog v-model="showPayDialog" title="订单支付">
  <div class="pay-content">
    <p class="pay-amount">支付金额：<span>¥{{ (totalAmount + freight).toFixed(2) }}</span></p>
    <div class="pay-methods">
      <div class="pay-method active">
        <el-icon><Wallet /></el-icon>
        <span>模拟支付</span>
      </div>
    </div>
  </div>
  <template #footer>
    <el-button @click="payLater">稍后支付</el-button>
    <el-button type="primary" :loading="paying" @click="confirmPay">
      确认支付
    </el-button>
  </template>
</el-dialog>
```

**更新订单提交流程：**
```javascript
// 提交订单成功后，保存订单ID并显示支付弹窗
createdOrderId.value = res.data?.id || res.data
showPayDialog.value = true

// 确认支付后跳转到订单列表
const confirmPay = async () => {
  await payOrder({ orderId: createdOrderId.value })
  ElMessage.success('支付成功')
  router.push('/user/orders?status=1')  // 跳转到待发货订单
}
```

**修复价格显示：**
```vue
<!-- 使用 totalPrice 字段，并提供 fallback -->
实付款：¥{{ order.totalPrice?.toFixed(2) || (order.price * order.num).toFixed(2) }}
```

**添加字段映射：**
```javascript
// 映射后端字段到前端期望的字段名
orders.value = (res.data?.records || []).map(order => ({
  ...order,
  status: order.state,           // state -> status
  orderNo: order.orderNumber,    // orderNumber -> orderNo
  vegetableName: order.name      // name -> vegetableName
}))
```

**修复 API 参数：**
```javascript
// 所有订单相关API调用统一使用 orderId
await payOrder({ orderId: order.id })           // 原来是 { id: ... }
await cancelOrder({ orderId: order.id })
await confirmReceive({ orderId: order.id })
```

## 受影响的文件

### 后端文件
1. `ape-system/src/main/java/com/ape/apesystem/domain/ApeVegetableOrder.java`
   - 添加 `totalPrice` 字段

2. `ape-admin/src/main/java/com/ape/apeadmin/controller/order/ApeVegetableOrderController.java`
   - 更新 `saveApeVegetableOrder` 方法
   - 更新 `saveApeVegetableCarOrder` 方法
   - 分别设置单价和总价

### 数据库文件
3. `sql/init_database.sql`
   - 更新表结构，添加 `total_price` 字段

4. `sql/migration_add_total_price.sql`（新建）
   - 数据迁移脚本

### 前端文件
5. `web-admin/src/views/order/checkout.vue`
   - 添加支付弹窗组件
   - 更新订单提交流程
   - 修复购物车订单提交的数据结构

6. `web-admin/src/views/order/detail.vue`
   - 修复价格显示
   - 添加字段映射
   - 修复 API 参数

7. `web-admin/src/views/order/index.vue`
   - 添加字段映射
   - 修复 API 参数

8. `web-admin/src/views/admin/orders/index.vue`
   - 添加字段映射

9. `web-admin/src/views/admin/dashboard.vue`
   - 添加字段映射

## 测试步骤

### 前置准备

1. **运行数据库迁移：**
```bash
# 连接到MySQL数据库
mysql -u root -p ape-vegetable

# 执行迁移脚本
source /path/to/sql/migration_add_total_price.sql

# 验证字段已添加
DESCRIBE ape_vegetable_order;
```

2. **重启后端服务：**
```bash
cd ape-admin
mvn spring-boot:run
```

3. **重启前端服务：**
```bash
cd web-admin
npm run dev
```

### 测试场景 1：直接购买商品

1. 登录系统
2. 浏览商品列表，选择一个商品
3. 点击"立即购买"
4. 在结算页面：
   - ✅ 检查商品单价显示正确
   - ✅ 检查小计 = 单价 × 数量
   - ✅ 检查总金额显示正确
5. 选择收货地址，点击"提交订单"
6. **验证：应该弹出支付窗口**
   - ✅ 窗口标题："订单支付"
   - ✅ 显示支付金额正确
   - ✅ 有"稍后支付"和"确认支付"两个按钮
7. 点击"确认支付"
8. **验证：跳转到订单列表页，显示"待发货"订单**

### 测试场景 2：购物车下单

1. 添加多个商品到购物车
2. 进入购物车页面
3. 选择部分商品，点击"去结算"
4. 在结算页面：
   - ✅ 检查每个商品的单价正确
   - ✅ 检查每个商品的小计 = 单价 × 数量
   - ✅ 检查商品总额正确
   - ✅ 检查实付款 = 商品总额 + 运费
5. 提交订单
6. **验证：弹出支付窗口**
7. 点击"确认支付"
8. **验证：跳转到订单列表，购物车中对应商品已移除**

### 测试场景 3：订单详情页支付

1. 提交订单后选择"稍后支付"
2. 进入订单详情页
3. **验证价格显示：**
   - ✅ 商品单价显示正确
   - ✅ 数量显示正确
   - ✅ 商品金额 = 单价 × 数量
   - ✅ 实付款金额正确
4. 点击"立即支付"按钮
5. **验证：弹出支付窗口，金额正确**
6. 确认支付
7. **验证：订单状态更新为"待发货"**

### 测试场景 4：订单列表

1. 进入"我的订单"页面
2. **验证每个订单的价格显示正确**
3. 切换不同状态标签（待付款、待发货、待收货等）
4. **验证订单状态显示正确**

### 测试场景 5：管理后台

1. 使用管理员账号登录
2. 进入"订单管理"
3. **验证订单列表价格显示正确**
4. 查看订单详情
5. **验证金额字段显示正确**

## 验证检查点

### 价格计算验证

| 场景 | 单价 | 数量 | 应显示的小计 | 应显示的总价 |
|------|------|------|--------------|--------------|
| 蔬菜A | ¥5.00 | 3 | ¥15.00 | ¥15.00 |
| 蔬菜B | ¥8.50 | 2 | ¥17.00 | ¥17.00 |
| 购物车 | - | - | ¥32.00 | ¥32.00 + 运费 |

### 支付流程验证

- [ ] 提交订单后立即显示支付弹窗
- [ ] 支付弹窗显示正确的支付金额
- [ ] 可以选择"稍后支付"跳转到订单详情
- [ ] 可以选择"确认支付"完成支付
- [ ] 支付成功后跳转到订单列表（待发货）
- [ ] 订单状态正确更新（待付款 → 待发货）

### 显示一致性验证

- [ ] 结算页的总金额 = 订单列表的实付款 = 订单详情的实付款
- [ ] 所有页面的价格计算逻辑一致
- [ ] 订单状态在所有页面显示一致

## 回滚方案

如果更新后出现问题，可以执行以下回滚操作：

```sql
-- 1. 回滚数据库（如果需要）
ALTER TABLE `ape_vegetable_order` DROP COLUMN `total_price`;
ALTER TABLE `ape_vegetable_order` MODIFY COLUMN `price` decimal(10,2) COMMENT '价格';

-- 2. 恢复旧版本代码
git revert <commit-hash>
```

## 注意事项

1. **数据库迁移必须在部署代码前执行**
2. **现有订单数据会自动迁移，单价 = 原价格 / 数量**
3. **如果有大量订单数据，建议在低峰期执行迁移**
4. **建议先在测试环境验证后再部署到生产环境**

## 技术债务清理

本次修复同时清理了以下技术债务：

1. ✅ 统一了价格字段的语义（单价 vs 总价）
2. ✅ 统一了 API 参数命名（orderId）
3. ✅ 统一了前后端字段映射（state/status, orderNumber/orderNo）
4. ✅ 改善了用户体验（支付流程更清晰）

## 相关文档

- [数据库迁移脚本](sql/migration_add_total_price.sql)
- [运行指南](RUN_GUIDE.md)
- [项目README](README.md)
