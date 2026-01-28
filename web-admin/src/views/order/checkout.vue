<template>
  <div class="checkout-page">
    <div class="page-container">
      <div class="page-header">
        <h1>确认订单</h1>
      </div>
      
      <div class="checkout-content" v-loading="loading">
        <!-- 收货地址 -->
        <div class="section address-section">
          <div class="section-header">
            <h3>收货地址</h3>
            <el-button type="primary" link @click="showAddressDialog = true">
              <el-icon><Plus /></el-icon>新增地址
            </el-button>
          </div>
          
          <div class="address-list" v-if="addressList.length">
            <div 
              class="address-item" 
              v-for="item in addressList" 
              :key="item.id"
              :class="{ active: selectedAddressId === item.id }"
              @click="selectedAddressId = item.id"
            >
              <div class="address-info">
                <div class="address-top">
                  <span class="name">{{ item.name }}</span>
                  <span class="phone">{{ item.tel }}</span>
                  <el-tag size="small" type="danger" v-if="item.isDefault === 1">默认</el-tag>
                </div>
                <div class="address-detail">
                  {{ item.province }}{{ item.city }}{{ item.area }}{{ item.address }}
                </div>
              </div>
              <el-icon class="check-icon" v-if="selectedAddressId === item.id">
                <CircleCheckFilled />
              </el-icon>
            </div>
          </div>
          
          <div class="no-address" v-else>
            <p>暂无收货地址，请先添加</p>
          </div>
        </div>
        
        <!-- 商品清单 -->
        <div class="section goods-section">
          <div class="section-header">
            <h3>商品清单</h3>
          </div>
          
          <div class="goods-list">
            <div class="goods-item" v-for="item in orderItems" :key="item.id || item.vegetableId">
              <el-image 
                :src="getImageUrl(item.images)" 
                fit="cover"
                class="goods-image"
              />
              <div class="goods-info">
                <h4 class="goods-name">{{ item.vegetableName || item.name }}</h4>
                <p class="goods-spec">{{ item.unit || '斤' }}</p>
              </div>
              <div class="goods-price">¥{{ item.price }}</div>
              <div class="goods-quantity">x{{ item.num || item.quantity }}</div>
              <div class="goods-subtotal">¥{{ ((item.price) * (item.num || item.quantity)).toFixed(2) }}</div>
            </div>
          </div>
        </div>
        
        <!-- 订单备注 -->
        <div class="section remark-section">
          <div class="section-header">
            <h3>订单备注</h3>
          </div>
          <el-input
            v-model="remark"
            type="textarea"
            :rows="3"
            placeholder="选填，可以告诉卖家您的特殊需求"
            maxlength="200"
            show-word-limit
          />
        </div>
        
        <!-- 结算栏 -->
        <div class="checkout-footer">
          <div class="footer-info">
            <div class="info-row">
              <span>商品总额：</span>
              <span>¥{{ totalAmount.toFixed(2) }}</span>
            </div>
            <div class="info-row">
              <span>运费：</span>
              <span>¥{{ freight.toFixed(2) }}</span>
            </div>
          </div>
          
          <div class="footer-total">
            <span class="label">实付款：</span>
            <span class="price">¥{{ (totalAmount + freight).toFixed(2) }}</span>
          </div>
          
          <el-button 
            type="danger" 
            size="large"
            :loading="submitting"
            :disabled="!selectedAddressId || orderItems.length === 0"
            @click="handleSubmit"
          >
            提交订单
          </el-button>
        </div>
      </div>
    </div>
    
    <!-- 新增地址弹窗 -->
    <el-dialog
      v-model="showAddressDialog"
      title="新增收货地址"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="addressFormRef"
        :model="addressForm"
        :rules="addressRules"
        label-width="80px"
      >
        <el-form-item label="收货人" prop="name">
          <el-input v-model="addressForm.name" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="tel">
          <el-input v-model="addressForm.tel" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="所在地区" prop="area">
          <div class="region-inputs">
            <el-input v-model="addressForm.province" placeholder="省" style="width: 30%" />
            <el-input v-model="addressForm.city" placeholder="市" style="width: 30%" />
            <el-input v-model="addressForm.area" placeholder="区/县" style="width: 30%" />
          </div>
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="addressForm.address" type="textarea" :rows="2" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="addressForm.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showAddressDialog = false">取消</el-button>
        <el-button type="primary" :loading="savingAddress" @click="handleSaveAddress">
          保存
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 支付弹窗 -->
    <el-dialog
      v-model="showPayDialog"
      title="订单支付"
      width="400px"
      :close-on-click-modal="false"
    >
      <div class="pay-content">
        <p class="pay-amount">支付金额：<span>¥{{ (totalAmount + freight).toFixed(2) }}</span></p>
        <p class="pay-tip">请选择支付方式：</p>
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
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { getAddressList, saveAddress } from '@/api/address'
import { getVegetableById } from '@/api/vegetable'
import { saveOrder, saveCarOrder, payOrder } from '@/api/order'
import { ElMessage } from 'element-plus'
import { Wallet } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const loading = ref(false)
const submitting = ref(false)
const addressList = ref([])
const selectedAddressId = ref('')
const orderItems = ref([])
const remark = ref('')
const freight = ref(0) // 运费

// 支付弹窗相关
const showPayDialog = ref(false)
const paying = ref(false)
const createdOrderId = ref('')

// 地址弹窗
const showAddressDialog = ref(false)
const savingAddress = ref(false)
const addressFormRef = ref()
const addressForm = reactive({
  name: '',
  tel: '',
  province: '',
  city: '',
  area: '',
  address: '',
  isDefault: 0
})

const addressRules = {
  name: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  tel: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  area: [{ required: true, message: '请选择所在地区', trigger: 'blur' }],
  address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

// 商品总额
const totalAmount = computed(() => {
  return orderItems.value.reduce((sum, item) => {
    return sum + item.price * (item.num || item.quantity)
  }, 0)
})

// 获取图片URL
const getImageUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return `/api${path}`
}

// 获取收货地址
const fetchAddressList = async () => {
  try {
    const res = await getAddressList()
    addressList.value = res.data || []
    // 默认选中第一个或默认地址
    const defaultAddr = addressList.value.find(item => item.isDefault === 1)
    if (defaultAddr) {
      selectedAddressId.value = defaultAddr.id
    } else if (addressList.value.length) {
      selectedAddressId.value = addressList.value[0].id
    }
  } catch (error) {
    console.error('获取地址失败:', error)
  }
}

// 获取订单商品
const fetchOrderItems = async () => {
  loading.value = true
  const type = route.query.type
  
  try {
    if (type === 'cart') {
      // 从购物车结算
      const ids = route.query.ids?.split(',') || []
      orderItems.value = cartStore.cartList.filter(item => ids.includes(item.id))
    } else if (type === 'direct') {
      // 直接购买
      const vegetableId = route.query.vegetableId
      const quantity = parseInt(route.query.quantity) || 1
      
      const res = await getVegetableById(vegetableId)
      if (res.data) {
        orderItems.value = [{
          ...res.data,
          vegetableId: res.data.id,
          vegetableName: res.data.name,
          quantity
        }]
      }
    }
  } catch (error) {
    console.error('获取商品信息失败:', error)
  } finally {
    loading.value = false
  }
}

// 保存地址
const handleSaveAddress = async () => {
  try {
    await addressFormRef.value.validate()
    savingAddress.value = true
    
    await saveAddress(addressForm)
    ElMessage.success('地址保存成功')
    showAddressDialog.value = false
    
    // 重置表单
    addressFormRef.value.resetFields()
    
    // 刷新地址列表
    await fetchAddressList()
  } catch (error) {
    console.error('保存地址失败:', error)
  } finally {
    savingAddress.value = false
  }
}

// 提交订单
const handleSubmit = async () => {
  if (!selectedAddressId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  
  if (!orderItems.value.length) {
    ElMessage.warning('订单中没有商品')
    return
  }
  
  submitting.value = true
  
  try {
    const type = route.query.type
    const address = addressList.value.find(item => item.id === selectedAddressId.value)
    
    let res
    if (type === 'cart') {
      // 购物车下单 - 需要传递选中的购物车项目数组
      const selectedCartItems = orderItems.value.map(item => ({
        id: item.id,
        vegetableId: item.vegetableId,
        name: item.vegetableName || item.name,
        num: item.num || item.quantity
      }))
      
      res = await saveCarOrder({
        arr: selectedCartItems,
        realName: address.name,
        tel: address.tel,
        address: `${address.province}${address.city}${address.area}${address.address}`,
        remark: remark.value
      })
    } else {
      // 直接下单
      const item = orderItems.value[0]
      res = await saveOrder({
        vegetableId: item.vegetableId || item.id,
        num: item.quantity || item.num,
        realName: address.name,
        tel: address.tel,
        address: `${address.province}${address.city}${address.area}${address.address}`,
        remark: remark.value
      })
    }
    
    ElMessage.success('订单提交成功')
    
    // 刷新购物车
    if (type === 'cart') {
      cartStore.clearSelection()
      cartStore.fetchCartList()
    }
    
    // 保存订单ID并显示支付窗口
    createdOrderId.value = res.data?.id || res.data
    showPayDialog.value = true
  } catch (error) {
    console.error('提交订单失败:', error)
  } finally {
    submitting.value = false
  }
}

// 确认支付
const confirmPay = async () => {
  paying.value = true
  try {
    await payOrder({ orderId: createdOrderId.value })
    ElMessage.success('支付成功')
    showPayDialog.value = false
    
    // 跳转到订单列表，并显示待发货订单
    router.push('/user/orders?status=1')
  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error('支付失败，请稍后重试')
  } finally {
    paying.value = false
  }
}

// 稍后支付，跳转到订单详情
const payLater = () => {
  showPayDialog.value = false
  router.push(`/user/orders/${createdOrderId.value}`)
}

onMounted(() => {
  fetchAddressList()
  fetchOrderItems()
})
</script>

<style lang="scss" scoped>
.checkout-page {
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

.section {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  
  h3 {
    font-size: 16px;
    font-weight: 600;
    color: #333;
  }
}

.address-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
}

.address-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border: 2px solid #f0f0f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    border-color: #67c23a;
  }
  
  &.active {
    border-color: #67c23a;
    background: #f0f9eb;
  }
}

.address-info {
  flex: 1;
  
  .address-top {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 8px;
    
    .name {
      font-size: 15px;
      font-weight: 600;
      color: #333;
    }
    
    .phone {
      font-size: 14px;
      color: #666;
    }
  }
  
  .address-detail {
    font-size: 13px;
    color: #999;
  }
}

.check-icon {
  font-size: 24px;
  color: #67c23a;
}

.no-address {
  text-align: center;
  padding: 20px;
  color: #999;
}

.goods-list {
  .goods-item {
    display: flex;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;
    
    &:last-child {
      border-bottom: none;
    }
  }
  
  .goods-image {
    width: 60px;
    height: 60px;
    border-radius: 4px;
    flex-shrink: 0;
  }
  
  .goods-info {
    flex: 1;
    margin-left: 12px;
    
    .goods-name {
      font-size: 14px;
      color: #333;
      margin-bottom: 4px;
    }
    
    .goods-spec {
      font-size: 12px;
      color: #999;
    }
  }
  
  .goods-price,
  .goods-quantity {
    width: 80px;
    text-align: center;
    font-size: 14px;
    color: #666;
  }
  
  .goods-subtotal {
    width: 100px;
    text-align: right;
    font-size: 14px;
    font-weight: 600;
    color: #f56c6c;
  }
}

.checkout-footer {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 40px;
}

.footer-info {
  .info-row {
    display: flex;
    justify-content: space-between;
    font-size: 14px;
    color: #666;
    margin-bottom: 4px;
    
    &:last-child {
      margin-bottom: 0;
    }
  }
}

.footer-total {
  .label {
    font-size: 14px;
    color: #666;
  }
  
  .price {
    font-size: 24px;
    font-weight: 600;
    color: #f56c6c;
  }
}

.region-inputs {
  display: flex;
  gap: 8px;
}

// 支付弹窗样式
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
