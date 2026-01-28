<template>
  <div class="dashboard-page">
    <h2 class="page-title">控制台</h2>
    
    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
          <el-icon size="28"><ShoppingBag /></el-icon>
        </div>
        <div class="stat-info">
          <p class="stat-label">今日订单</p>
          <h3 class="stat-value">{{ stats.todayOrders || 0 }}</h3>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
          <el-icon size="28"><Money /></el-icon>
        </div>
        <div class="stat-info">
          <p class="stat-label">今日销售额</p>
          <h3 class="stat-value">¥{{ (stats.todaySales || 0).toFixed(2) }}</h3>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
          <el-icon size="28"><User /></el-icon>
        </div>
        <div class="stat-info">
          <p class="stat-label">用户总数</p>
          <h3 class="stat-value">{{ stats.totalUsers || 0 }}</h3>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
          <el-icon size="28"><Goods /></el-icon>
        </div>
        <div class="stat-info">
          <p class="stat-label">商品总数</p>
          <h3 class="stat-value">{{ stats.totalVegetables || 0 }}</h3>
        </div>
      </div>
    </div>
    
    <!-- 快捷操作 -->
    <div class="section">
      <h3 class="section-title">快捷操作</h3>
      <div class="quick-actions">
        <div class="action-item" @click="router.push('/admin/vegetables')">
          <el-icon size="24"><Goods /></el-icon>
          <span>商品管理</span>
        </div>
        <div class="action-item" @click="router.push('/admin/orders')">
          <el-icon size="24"><List /></el-icon>
          <span>订单管理</span>
        </div>
        <div class="action-item" @click="router.push('/admin/users')">
          <el-icon size="24"><User /></el-icon>
          <span>用户管理</span>
        </div>
        <div class="action-item" @click="router.push('/admin/categories')">
          <el-icon size="24"><Menu /></el-icon>
          <span>分类管理</span>
        </div>
        <div class="action-item" @click="router.push('/admin/rotations')">
          <el-icon size="24"><Picture /></el-icon>
          <span>轮播图管理</span>
        </div>
        <div class="action-item" @click="router.push('/admin/delivery')">
          <el-icon size="24"><Van /></el-icon>
          <span>配送员管理</span>
        </div>
      </div>
    </div>
    
    <!-- 待处理事项 -->
    <div class="section">
      <h3 class="section-title">待处理事项</h3>
      <div class="pending-items">
        <div class="pending-item" @click="router.push({ path: '/admin/orders', query: { status: '0' }})">
          <span class="pending-label">待付款订单</span>
          <el-badge :value="stats.pendingPayment || 0" />
        </div>
        <div class="pending-item" @click="router.push({ path: '/admin/orders', query: { status: '1' }})">
          <span class="pending-label">待发货订单</span>
          <el-badge :value="stats.pendingShip || 0" type="warning" />
        </div>
        <div class="pending-item" @click="router.push('/admin/vegetables')">
          <span class="pending-label">库存预警商品</span>
          <el-badge :value="stats.lowStock || 0" type="danger" />
        </div>
      </div>
    </div>
    
    <!-- 最近订单 -->
    <div class="section">
      <div class="section-header">
        <h3 class="section-title">最近订单</h3>
        <el-button type="primary" link @click="router.push('/admin/orders')">
          查看全部
        </el-button>
      </div>
      
      <el-table :data="recentOrders" stripe v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="vegetableName" label="商品名称" />
        <el-table-column prop="totalPrice" label="金额" width="100">
          <template #default="{ row }">
            ¥{{ row.totalPrice?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getOrderPage, getOrderStats } from '@/api/order'

const router = useRouter()

const loading = ref(false)
const stats = ref({})
const recentOrders = ref([])

// 订单状态
const statusMap = {
  0: { text: '待付款', type: 'warning' },
  1: { text: '待发货', type: 'info' },
  2: { text: '待收货', type: 'primary' },
  3: { text: '已完成', type: 'success' },
  4: { text: '已取消', type: 'info' },
  5: { text: '退款中', type: 'danger' },
  6: { text: '已退款', type: 'info' }
}

const getStatusText = (status) => statusMap[status]?.text || '未知'
const getStatusType = (status) => statusMap[status]?.type || 'info'

// 获取统计数据
const fetchStats = async () => {
  try {
    const res = await getOrderStats()
    stats.value = res.data || {}
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取最近订单
const fetchRecentOrders = async () => {
  loading.value = true
  try {
    const res = await getOrderPage({ pageNumber: 1, pageSize: 5 })
    // Map state to status for frontend consistency
    recentOrders.value = (res.data?.records || []).map(order => ({
      ...order,
      status: order.state,
      orderNo: order.orderNumber,
      vegetableName: order.name
    }))
  } catch (error) {
    console.error('获取订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchStats()
  fetchRecentOrders()
})
</script>

<style lang="scss" scoped>
.dashboard-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
    color: #333;
    margin-bottom: 24px;
  }
}

.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
  
  @media (max-width: 992px) {
    grid-template-columns: repeat(2, 1fr);
  }
  
  @media (max-width: 576px) {
    grid-template-columns: 1fr;
  }
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-right: 16px;
}

.stat-info {
  .stat-label {
    font-size: 13px;
    color: #999;
    margin-bottom: 4px;
  }
  
  .stat-value {
    font-size: 24px;
    font-weight: 600;
    color: #333;
  }
}

.section {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  
  .section-header & {
    margin-bottom: 0;
  }
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
  
  @media (max-width: 992px) {
    grid-template-columns: repeat(3, 1fr);
  }
  
  @media (max-width: 576px) {
    grid-template-columns: repeat(2, 1fr);
  }
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background: #f5f5f5;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    background: #e8f5e9;
    color: #67c23a;
  }
  
  .el-icon {
    margin-bottom: 8px;
  }
  
  span {
    font-size: 13px;
  }
}

.pending-items {
  display: flex;
  gap: 40px;
  
  @media (max-width: 576px) {
    flex-direction: column;
    gap: 16px;
  }
}

.pending-item {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  
  &:hover .pending-label {
    color: #67c23a;
  }
  
  .pending-label {
    font-size: 14px;
    color: #666;
    transition: color 0.3s;
  }
}
</style>
