<template>
  <div class="orders-page">
    <div class="page-header">
      <h2 class="page-title">订单管理</h2>
    </div>
    
    <!-- 搜索筛选 -->
    <div class="filter-bar">
      <el-input
        v-model="searchForm.orderNo"
        placeholder="订单号"
        clearable
        style="width: 200px"
        @keyup.enter="handleSearch"
      />
      <el-select v-model="searchForm.status" placeholder="订单状态" clearable style="width: 150px">
        <el-option label="待付款" :value="0" />
        <el-option label="待发货" :value="1" />
        <el-option label="待收货" :value="2" />
        <el-option label="已完成" :value="3" />
        <el-option label="已取消" :value="4" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>
    
    <!-- 订单列表 -->
    <el-table :data="orders" stripe v-loading="loading">
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="vegetableName" label="商品名称" min-width="150" />
      <el-table-column prop="num" label="数量" width="80" />
      <el-table-column prop="totalPrice" label="金额" width="100">
        <template #default="{ row }">
          ¥{{ row.totalPrice?.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="name" label="收货人" width="100" />
      <el-table-column prop="tel" label="联系电话" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)" size="small">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="下单时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleView(row)">查看</el-button>
          <el-button type="primary" link v-if="row.status === 1" @click="handleShip(row)">发货</el-button>
          <el-button type="danger" link v-if="row.status <= 1" @click="handleCancel(row)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="fetchOrders"
        @size-change="fetchOrders"
      />
    </div>
    
    <!-- 订单详情弹窗 -->
    <el-dialog v-model="showDetailDialog" title="订单详情" width="600px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentOrder.status)" size="small">
            {{ getStatusText(currentOrder.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="商品名称">{{ currentOrder.vegetableName }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ currentOrder.num }} {{ currentOrder.unit || '斤' }}</el-descriptions-item>
        <el-descriptions-item label="单价">¥{{ currentOrder.price }}</el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ currentOrder.totalPrice?.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="收货人">{{ currentOrder.name }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.tel }}</el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">{{ currentOrder.address }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ currentOrder.createTime }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ currentOrder.payTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentOrder.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
    
    <!-- 发货弹窗 -->
    <el-dialog v-model="showShipDialog" title="订单发货" width="500px">
      <el-form :model="shipForm" label-width="100px">
        <el-form-item label="配送员">
          <el-select v-model="shipForm.deliveryId" placeholder="请选择配送员" style="width: 100%">
            <el-option
              v-for="item in deliveryList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showShipDialog = false">取消</el-button>
        <el-button type="primary" :loading="shipping" @click="confirmShip">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getOrderPage, shipOrder, cancelOrder } from '@/api/order'
import { getDeliveryList } from '@/api/common'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()

const loading = ref(false)
const shipping = ref(false)
const orders = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  orderNo: '',
  status: ''
})

const showDetailDialog = ref(false)
const currentOrder = ref(null)

const showShipDialog = ref(false)
const shipForm = reactive({ deliveryId: '' })
const deliveryList = ref([])

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

// 获取订单列表
const fetchOrders = async () => {
  loading.value = true
  try {
    const params = {
      pageNumber: pageNum.value,
      pageSize: pageSize.value
    }
    if (searchForm.orderNo) params.orderNumber = searchForm.orderNo
    if (searchForm.status !== '') params.state = searchForm.status
    
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

// 获取配送员列表
const fetchDeliveryList = async () => {
  try {
    const res = await getDeliveryList()
    deliveryList.value = res.data || []
  } catch (error) {
    console.error('获取配送员失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pageNum.value = 1
  fetchOrders()
}

// 重置
const handleReset = () => {
  searchForm.orderNo = ''
  searchForm.status = ''
  handleSearch()
}

// 查看详情
const handleView = (row) => {
  currentOrder.value = row
  showDetailDialog.value = true
}

// 发货
const handleShip = (row) => {
  currentOrder.value = row
  shipForm.deliveryId = ''
  showShipDialog.value = true
}

// 确认发货
const confirmShip = async () => {
  if (!shipForm.deliveryId) {
    ElMessage.warning('请选择配送员')
    return
  }
  
  shipping.value = true
  try {
    await shipOrder({
      id: currentOrder.value.id,
      deliveryId: shipForm.deliveryId
    })
    ElMessage.success('发货成功')
    showShipDialog.value = false
    fetchOrders()
  } catch (error) {
    console.error('发货失败:', error)
  } finally {
    shipping.value = false
  }
}

// 取消订单
const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
    await cancelOrder({ id: row.id })
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (error) {
    // 取消操作
  }
}

onMounted(() => {
  // 从路由获取初始状态
  if (route.query.status) {
    searchForm.status = parseInt(route.query.status)
  }
  fetchOrders()
  fetchDeliveryList()
})
</script>

<style lang="scss" scoped>
.orders-page {
  .page-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
  }
  
  .page-title {
    font-size: 20px;
    font-weight: 600;
    color: #333;
  }
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
