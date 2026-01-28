<template>
  <div class="orders-page">
    <div class="page-container">
      <div class="page-header">
        <h1>我的订单</h1>
      </div>
      
      <!-- 订单状态筛选 -->
      <div class="order-tabs">
        <el-tabs v-model="activeStatus" @tab-change="handleTabChange">
          <el-tab-pane label="全部订单" name="all" />
          <el-tab-pane label="待付款" name="0" />
          <el-tab-pane label="待发货" name="1" />
          <el-tab-pane label="待收货" name="2" />
          <el-tab-pane label="已完成" name="3" />
        </el-tabs>
      </div>
      
      <!-- 订单列表 -->
      <div class="order-list" v-loading="loading">
        <div class="order-card" v-for="order in orders" :key="order.id">
          <div class="order-header">
            <div class="order-info">
              <span class="order-no">订单号：{{ order.orderNo }}</span>
              <span class="order-time">{{ order.createTime }}</span>
            </div>
            <div class="order-status" :class="getStatusClass(order.status)">
              {{ getStatusText(order.status) }}
            </div>
          </div>
          
          <div class="order-content" @click="goDetail(order.id)">
            <div class="order-goods">
              <el-image 
                :src="getImageUrl(order.images)" 
                fit="cover"
                class="goods-image"
              />
              <div class="goods-info">
                <h4 class="goods-name">{{ order.vegetableName }}</h4>
                <p class="goods-spec">数量：{{ order.num }} {{ order.unit || '斤' }}</p>
              </div>
            </div>
            
            <div class="order-price">
              <span class="label">实付款：</span>
              <span class="price">¥{{ order.totalPrice?.toFixed(2) || order.price?.toFixed(2) }}</span>
            </div>
          </div>
          
          <div class="order-footer">
            <div class="order-address" v-if="order.address">
              <el-icon><Location /></el-icon>
              {{ order.name }} {{ order.tel }} {{ order.address }}
            </div>
            
            <div class="order-actions">
              <!-- 待付款 -->
              <template v-if="order.status === 0">
                <el-button type="danger" @click="handlePay(order)">立即支付</el-button>
                <el-button @click="handleCancel(order)">取消订单</el-button>
              </template>
              
              <!-- 待发货 -->
              <template v-else-if="order.status === 1">
                <el-button @click="goDetail(order.id)">查看详情</el-button>
              </template>
              
              <!-- 待收货 -->
              <template v-else-if="order.status === 2">
                <el-button type="primary" @click="handleConfirm(order)">确认收货</el-button>
                <el-button @click="goDetail(order.id)">查看物流</el-button>
              </template>
              
              <!-- 已完成 -->
              <template v-else-if="order.status === 3">
                <el-button @click="handleAfterSale(order)">申请售后</el-button>
                <el-button @click="handleBuyAgain(order)">再次购买</el-button>
              </template>
              
              <!-- 已取消 -->
              <template v-else-if="order.status === 4">
                <el-button @click="handleBuyAgain(order)">重新购买</el-button>
              </template>
            </div>
          </div>
        </div>
        
        <el-empty v-if="!loading && orders.length === 0" description="暂无订单" />
      </div>
      
      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="fetchOrders"
        />
      </div>
    </div>
    
    <!-- 支付弹窗 -->
    <el-dialog
      v-model="showPayDialog"
      title="订单支付"
      width="400px"
    >
      <div class="pay-content">
        <p class="pay-amount">支付金额：<span>¥{{ currentOrder?.totalPrice?.toFixed(2) }}</span></p>
        <p class="pay-tip">请选择支付方式：</p>
        <div class="pay-methods">
          <div class="pay-method active">
            <el-icon><Wallet /></el-icon>
            <span>模拟支付</span>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showPayDialog = false">取消</el-button>
        <el-button type="primary" :loading="paying" @click="confirmPay">
          确认支付
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderPage, payOrder, confirmReceive, cancelOrder } from '@/api/order'
import { useCartStore } from '@/stores/cart'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const loading = ref(false)
const orders = ref([])
const activeStatus = ref('all')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 支付弹窗
const showPayDialog = ref(false)
const paying = ref(false)
const currentOrder = ref(null)

// 订单状态映射
const statusMap = {
  0: { text: '待付款', class: 'warning' },
  1: { text: '待发货', class: 'info' },
  2: { text: '待收货', class: 'primary' },
  3: { text: '已完成', class: 'success' },
  4: { text: '已取消', class: 'default' },
  5: { text: '退款中', class: 'danger' },
  6: { text: '已退款', class: 'default' }
}

const getStatusText = (status) => statusMap[status]?.text || '未知'
const getStatusClass = (status) => statusMap[status]?.class || 'default'

// 获取图片URL
const getImageUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return `/api${path}`
}

// 获取订单列表
const fetchOrders = async () => {
  loading.value = true
  try {
    const params = {
      pageNumber: pageNum.value,
      pageSize: pageSize.value
    }
    
    if (activeStatus.value !== 'all') {
      params.state = parseInt(activeStatus.value)
    }
    
    const res = await getOrderPage(params)
    // Map state to status for frontend consistency
    orders.value = (res.data?.records || []).map(order => ({
      ...order,
      status: order.state,
      orderNo: order.orderNumber,
      vegetableName: order.name
    }))
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 切换标签
const handleTabChange = () => {
  pageNum.value = 1
  fetchOrders()
}

// 跳转详情
const goDetail = (id) => {
  router.push(`/user/orders/${id}`)
}

// 支付订单
const handlePay = (order) => {
  currentOrder.value = order
  showPayDialog.value = true
}

// 确认支付
const confirmPay = async () => {
  paying.value = true
  try {
    await payOrder({ orderId: currentOrder.value.id })
    ElMessage.success('支付成功')
    showPayDialog.value = false
    fetchOrders()
  } catch (error) {
    console.error('支付失败:', error)
  } finally {
    paying.value = false
  }
}

// 取消订单
const handleCancel = async (order) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
    await cancelOrder({ orderId: order.id })
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (error) {
    // 取消操作
  }
}

// 确认收货
const handleConfirm = async (order) => {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '提示', { type: 'info' })
    await confirmReceive({ orderId: order.id })
    ElMessage.success('已确认收货')
    fetchOrders()
  } catch (error) {
    // 取消操作
  }
}

// 申请售后
const handleAfterSale = (order) => {
  router.push({ path: '/user/afterSale', query: { orderId: order.id } })
}

// 再次购买
const handleBuyAgain = async (order) => {
  try {
    await cartStore.addToCart(order.vegetableId, order.num)
    router.push('/cart')
  } catch (error) {
    console.error('添加购物车失败:', error)
  }
}

onMounted(() => {
  // 从路由获取初始状态
  if (route.query.status) {
    activeStatus.value = route.query.status
  }
  fetchOrders()
})
</script>

<style lang="scss" scoped>
.orders-page {
  background: #f5f5f5;
  min-height: calc(100vh - 120px);
  padding: 20px 0 40px;
}

.page-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  margin-bottom: 20px;
  
  h1 {
    font-size: 24px;
    font-weight: 600;
    color: #333;
  }
}

.order-tabs {
  background: #fff;
  border-radius: 8px 8px 0 0;
  padding: 0 20px;
  margin-bottom: 2px;
  
  :deep(.el-tabs__header) {
    margin: 0;
  }
}

.order-list {
  min-height: 300px;
}

.order-card {
  background: #fff;
  border-radius: 0 0 8px 8px;
  margin-bottom: 16px;
  overflow: hidden;
  
  &:first-child {
    border-radius: 0;
  }
}

.order-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

.order-info {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
  color: #999;
}

.order-status {
  font-size: 14px;
  font-weight: 500;
  
  &.warning { color: #e6a23c; }
  &.info { color: #909399; }
  &.primary { color: #409eff; }
  &.success { color: #67c23a; }
  &.danger { color: #f56c6c; }
  &.default { color: #999; }
}

.order-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  cursor: pointer;
  
  &:hover {
    background: #fafafa;
  }
}

.order-goods {
  display: flex;
  align-items: center;
  
  .goods-image {
    width: 80px;
    height: 80px;
    border-radius: 4px;
    flex-shrink: 0;
  }
  
  .goods-info {
    margin-left: 16px;
    
    .goods-name {
      font-size: 14px;
      color: #333;
      margin-bottom: 8px;
    }
    
    .goods-spec {
      font-size: 13px;
      color: #999;
    }
  }
}

.order-price {
  text-align: right;
  
  .label {
    font-size: 13px;
    color: #999;
  }
  
  .price {
    font-size: 18px;
    font-weight: 600;
    color: #f56c6c;
  }
}

.order-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  border-top: 1px solid #f0f0f0;
}

.order-address {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #999;
  
  .el-icon {
    color: #67c23a;
  }
}

.order-actions {
  display: flex;
  gap: 8px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

// 支付弹窗
.pay-content {
  .pay-amount {
    font-size: 16px;
    color: #333;
    margin-bottom: 16px;
    
    span {
      font-size: 24px;
      font-weight: 600;
      color: #f56c6c;
    }
  }
  
  .pay-tip {
    font-size: 14px;
    color: #666;
    margin-bottom: 12px;
  }
  
  .pay-methods {
    display: flex;
    gap: 12px;
  }
  
  .pay-method {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 20px;
    border: 2px solid #e0e0e0;
    border-radius: 8px;
    cursor: pointer;
    
    &.active {
      border-color: #67c23a;
      background: #f0f9eb;
      color: #67c23a;
    }
  }
}
</style>
