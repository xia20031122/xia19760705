<template>
  <div class="order-detail-page">
    <div class="page-container" v-loading="loading">
      <!-- 面包屑 -->
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/user/orders' }">我的订单</el-breadcrumb-item>
        <el-breadcrumb-item>订单详情</el-breadcrumb-item>
      </el-breadcrumb>
      
      <div class="order-detail" v-if="order.id">
        <!-- 订单状态 -->
        <div class="status-section">
          <div class="status-icon" :class="getStatusClass(order.status)">
            <el-icon size="48"><component :is="getStatusIcon(order.status)" /></el-icon>
          </div>
          <div class="status-info">
            <h2 class="status-text">{{ getStatusText(order.status) }}</h2>
            <p class="status-tip">{{ getStatusTip(order.status) }}</p>
          </div>
          <div class="status-actions">
            <template v-if="order.status === 0">
              <el-button type="danger" size="large" @click="handlePay">立即支付</el-button>
              <el-button size="large" @click="handleCancel">取消订单</el-button>
            </template>
            <template v-else-if="order.status === 2">
              <el-button type="primary" size="large" @click="handleConfirm">确认收货</el-button>
            </template>
            <template v-else-if="order.status === 3">
              <el-button size="large" @click="handleAfterSale">申请售后</el-button>
            </template>
          </div>
        </div>
        
        <!-- 订单进度 -->
        <div class="progress-section">
          <el-steps :active="getProgressStep(order.status)" align-center>
            <el-step title="提交订单" :description="order.createTime" />
            <el-step title="付款成功" :description="order.payTime" />
            <el-step title="商家发货" :description="order.shipTime" />
            <el-step title="确认收货" :description="order.receiveTime" />
          </el-steps>
        </div>
        
        <!-- 收货信息 -->
        <div class="section">
          <h3 class="section-title">收货信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">收货人：</span>
              <span class="value">{{ order.name }}</span>
            </div>
            <div class="info-item">
              <span class="label">联系电话：</span>
              <span class="value">{{ order.tel }}</span>
            </div>
            <div class="info-item full">
              <span class="label">收货地址：</span>
              <span class="value">{{ order.address }}</span>
            </div>
          </div>
        </div>
        
        <!-- 配送信息 -->
        <div class="section" v-if="order.deliveryName">
          <h3 class="section-title">配送信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">配送员：</span>
              <span class="value">{{ order.deliveryName }}</span>
            </div>
            <div class="info-item">
              <span class="label">配送电话：</span>
              <span class="value">{{ order.deliveryTel }}</span>
            </div>
          </div>
        </div>
        
        <!-- 商品信息 -->
        <div class="section">
          <h3 class="section-title">商品信息</h3>
          <div class="goods-card">
            <el-image 
              :src="getImageUrl(order.images)" 
              fit="cover"
              class="goods-image"
              @click="goProduct(order.vegetableId)"
            />
            <div class="goods-info">
              <h4 class="goods-name" @click="goProduct(order.vegetableId)">
                {{ order.vegetableName }}
              </h4>
              <p class="goods-spec">单价：¥{{ order.price }} / {{ order.unit || '斤' }}</p>
              <p class="goods-quantity">数量：{{ order.num }} {{ order.unit || '斤' }}</p>
            </div>
            <div class="goods-subtotal">
              <span class="price">¥{{ (order.price * order.num).toFixed(2) }}</span>
            </div>
          </div>
        </div>
        
        <!-- 订单信息 -->
        <div class="section">
          <h3 class="section-title">订单信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">订单编号：</span>
              <span class="value">{{ order.orderNo }}</span>
              <el-button type="primary" link size="small" @click="copyOrderNo">复制</el-button>
            </div>
            <div class="info-item">
              <span class="label">下单时间：</span>
              <span class="value">{{ order.createTime }}</span>
            </div>
            <div class="info-item" v-if="order.payTime">
              <span class="label">支付时间：</span>
              <span class="value">{{ order.payTime }}</span>
            </div>
            <div class="info-item" v-if="order.remark">
              <span class="label">订单备注：</span>
              <span class="value">{{ order.remark }}</span>
            </div>
          </div>
        </div>
        
        <!-- 费用信息 -->
        <div class="section price-section">
          <div class="price-row">
            <span>商品金额：</span>
            <span>¥{{ order.totalPrice?.toFixed(2) || (order.price * order.num).toFixed(2) }}</span>
          </div>
          <div class="price-row">
            <span>运费：</span>
            <span>¥0.00</span>
          </div>
          <div class="price-row total">
            <span>实付款：</span>
            <span class="total-price">¥{{ order.totalPrice?.toFixed(2) || (order.price * order.num).toFixed(2) }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 支付弹窗 -->
    <el-dialog v-model="showPayDialog" title="订单支付" width="400px">
      <div class="pay-content">
        <p class="pay-amount">支付金额：<span>¥{{ order.totalPrice?.toFixed(2) || (order.price * order.num).toFixed(2) }}</span></p>
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
        <el-button type="primary" :loading="paying" @click="confirmPay">确认支付</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderById, payOrder, confirmReceive, cancelOrder } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Clock, CreditCard, Van, CircleCheck, 
  CircleClose, Warning 
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const order = ref({})
const showPayDialog = ref(false)
const paying = ref(false)

// 状态映射
const statusConfig = {
  0: { text: '待付款', tip: '请在24小时内完成支付，超时订单将自动取消', icon: Clock, class: 'warning' },
  1: { text: '待发货', tip: '商家正在准备商品，请耐心等待', icon: CreditCard, class: 'info' },
  2: { text: '待收货', tip: '商品配送中，请注意查收', icon: Van, class: 'primary' },
  3: { text: '已完成', tip: '订单已完成，感谢您的购买', icon: CircleCheck, class: 'success' },
  4: { text: '已取消', tip: '订单已取消', icon: CircleClose, class: 'default' },
  5: { text: '退款中', tip: '售后处理中，请耐心等待', icon: Warning, class: 'danger' },
  6: { text: '已退款', tip: '退款已完成', icon: CircleCheck, class: 'default' }
}

const getStatusText = (status) => statusConfig[status]?.text || '未知'
const getStatusTip = (status) => statusConfig[status]?.tip || ''
const getStatusIcon = (status) => statusConfig[status]?.icon || Clock
const getStatusClass = (status) => statusConfig[status]?.class || 'default'

// 获取进度步骤
const getProgressStep = (status) => {
  if (status === 0) return 0
  if (status === 1) return 1
  if (status === 2) return 2
  if (status >= 3) return 3
  return 0
}

// 获取图片URL
const getImageUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return `/api${path}`
}

// 获取订单详情
const fetchOrder = async (id) => {
  loading.value = true
  try {
    const res = await getOrderById(id)
    order.value = res.data || {}
  } catch (error) {
    console.error('获取订单详情失败:', error)
  } finally {
    loading.value = false
  }
}

// 跳转商品详情
const goProduct = (id) => {
  router.push(`/product/${id}`)
}

// 复制订单号
const copyOrderNo = () => {
  navigator.clipboard.writeText(order.value.orderNo)
  ElMessage.success('订单号已复制')
}

// 支付订单
const handlePay = () => {
  showPayDialog.value = true
}

// 确认支付
const confirmPay = async () => {
  paying.value = true
  try {
    await payOrder({ orderId: order.value.id })
    ElMessage.success('支付成功')
    showPayDialog.value = false
    fetchOrder(order.value.id)
  } catch (error) {
    console.error('支付失败:', error)
  } finally {
    paying.value = false
  }
}

// 取消订单
const handleCancel = async () => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
    await cancelOrder({ orderId: order.value.id })
    ElMessage.success('订单已取消')
    fetchOrder(order.value.id)
  } catch (error) {
    // 取消操作
  }
}

// 确认收货
const handleConfirm = async () => {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '提示', { type: 'info' })
    await confirmReceive({ orderId: order.value.id })
    ElMessage.success('已确认收货')
    fetchOrder(order.value.id)
  } catch (error) {
    // 取消操作
  }
}

// 申请售后
const handleAfterSale = () => {
  router.push({ path: '/user/afterSale', query: { orderId: order.value.id } })
}

// 监听路由变化
watch(() => route.params.id, (id) => {
  if (id) {
    fetchOrder(id)
  }
}, { immediate: true })

onMounted(() => {
  window.scrollTo({ top: 0 })
})
</script>

<style lang="scss" scoped>
.order-detail-page {
  background: #f5f5f5;
  min-height: calc(100vh - 120px);
  padding: 20px 0 40px;
}

.page-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.el-breadcrumb {
  margin-bottom: 20px;
}

.status-section {
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, #67c23a 0%, #529b2e 100%);
  border-radius: 8px;
  padding: 32px;
  margin-bottom: 16px;
  color: #fff;
}

.status-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24px;
}

.status-info {
  flex: 1;
  
  .status-text {
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 8px;
  }
  
  .status-tip {
    font-size: 14px;
    opacity: 0.9;
  }
}

.status-actions {
  display: flex;
  gap: 12px;
}

.progress-section {
  background: #fff;
  border-radius: 8px;
  padding: 32px 20px;
  margin-bottom: 16px;
}

.section {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
}

.info-item {
  display: flex;
  align-items: center;
  font-size: 14px;
  
  &.full {
    grid-column: span 2;
    
    @media (max-width: 768px) {
      grid-column: span 1;
    }
  }
  
  .label {
    color: #999;
    flex-shrink: 0;
  }
  
  .value {
    color: #333;
    margin-right: 8px;
  }
}

.goods-card {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
  
  .goods-image {
    width: 100px;
    height: 100px;
    border-radius: 4px;
    flex-shrink: 0;
    cursor: pointer;
  }
  
  .goods-info {
    flex: 1;
    margin-left: 16px;
    
    .goods-name {
      font-size: 16px;
      color: #333;
      margin-bottom: 8px;
      cursor: pointer;
      
      &:hover {
        color: #67c23a;
      }
    }
    
    .goods-spec,
    .goods-quantity {
      font-size: 13px;
      color: #999;
      margin-bottom: 4px;
    }
  }
  
  .goods-subtotal {
    .price {
      font-size: 20px;
      font-weight: 600;
      color: #f56c6c;
    }
  }
}

.price-section {
  .price-row {
    display: flex;
    justify-content: flex-end;
    gap: 20px;
    font-size: 14px;
    color: #666;
    margin-bottom: 8px;
    
    &.total {
      margin-top: 12px;
      padding-top: 12px;
      border-top: 1px solid #f0f0f0;
      font-size: 16px;
      
      .total-price {
        font-size: 24px;
        font-weight: 600;
        color: #f56c6c;
      }
    }
  }
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
