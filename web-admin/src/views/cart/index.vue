<template>
  <div class="cart-page">
    <div class="page-container">
      <div class="page-header">
        <h1>购物车</h1>
      </div>
      
      <div class="cart-content" v-loading="cartStore.loading">
        <!-- 购物车列表 -->
        <div class="cart-list" v-if="cartStore.cartList.length">
          <div class="cart-header">
            <el-checkbox 
              v-model="allSelected"
              @change="cartStore.toggleSelectAll"
            >
              全选
            </el-checkbox>
            <span class="header-item product">商品信息</span>
            <span class="header-item price">单价</span>
            <span class="header-item quantity">数量</span>
            <span class="header-item subtotal">小计</span>
            <span class="header-item action">操作</span>
          </div>
          
          <div class="cart-item" v-for="item in cartStore.cartList" :key="item.id">
            <el-checkbox 
              :model-value="cartStore.selectedIds.includes(item.id)"
              @change="cartStore.toggleSelect(item.id)"
            />
            
            <div class="item-product">
              <el-image 
                :src="getImageUrl(item.images)" 
                fit="cover"
                class="product-image"
                @click="goDetail(item.vegetableId)"
              >
                <template #error>
                  <div class="image-error">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <div class="product-info">
                <h3 class="product-name" @click="goDetail(item.vegetableId)">
                  {{ item.vegetableName || item.name }}
                </h3>
                <p class="product-spec">{{ item.unit || '斤' }}</p>
              </div>
            </div>
            
            <div class="item-price">
              <span class="price">¥{{ item.price }}</span>
            </div>
            
            <div class="item-quantity">
              <el-input-number 
                :model-value="item.num"
                :min="1"
                :max="99"
                size="small"
                @change="(val) => handleQuantityChange(item, val)"
              />
            </div>
            
            <div class="item-subtotal">
              <span class="subtotal">¥{{ (item.price * item.num).toFixed(2) }}</span>
            </div>
            
            <div class="item-action">
              <el-button 
                type="danger" 
                link 
                @click="handleRemove(item.id)"
              >
                删除
              </el-button>
            </div>
          </div>
        </div>
        
        <!-- 空购物车 -->
        <div class="cart-empty" v-else>
          <el-empty description="购物车是空的">
            <el-button type="primary" @click="router.push('/products')">
              去逛逛
            </el-button>
          </el-empty>
        </div>
        
        <!-- 结算栏 -->
        <div class="cart-footer" v-if="cartStore.cartList.length">
          <div class="footer-left">
            <el-checkbox 
              v-model="allSelected"
              @change="cartStore.toggleSelectAll"
            >
              全选
            </el-checkbox>
            <el-button type="danger" link @click="handleRemoveSelected" :disabled="!cartStore.selectedIds.length">
              删除选中
            </el-button>
          </div>
          
          <div class="footer-right">
            <div class="selected-info">
              已选择 <span class="count">{{ cartStore.selectedIds.length }}</span> 件商品
            </div>
            <div class="total-price">
              合计：<span class="price">¥{{ cartStore.selectedTotal.toFixed(2) }}</span>
            </div>
            <el-button 
              type="danger" 
              size="large"
              :disabled="!cartStore.selectedIds.length"
              @click="handleCheckout"
            >
              去结算
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const cartStore = useCartStore()

// 是否全选
const allSelected = computed({
  get: () => cartStore.isAllSelected,
  set: () => {}
})

// 获取图片URL
const getImageUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return `/api${path}`
}

// 跳转详情
const goDetail = (id) => {
  router.push(`/product/${id}`)
}

// 修改数量
const handleQuantityChange = async (item, val) => {
  if (val === item.num) return
  try {
    await cartStore.updateCartItem({ ...item, num: val })
  } catch (error) {
    // 失败时不处理
  }
}

// 删除单个
const handleRemove = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
      type: 'warning'
    })
    await cartStore.removeCartItem(id)
  } catch (error) {
    // 取消删除
  }
}

// 删除选中
const handleRemoveSelected = async () => {
  if (!cartStore.selectedIds.length) return
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${cartStore.selectedIds.length} 件商品吗？`, '提示', {
      type: 'warning'
    })
    await cartStore.removeCartItem(cartStore.selectedIds.join(','))
  } catch (error) {
    // 取消删除
  }
}

// 去结算
const handleCheckout = () => {
  if (!cartStore.selectedIds.length) return
  router.push({
    path: '/checkout',
    query: {
      type: 'cart',
      ids: cartStore.selectedIds.join(',')
    }
  })
}

onMounted(() => {
  cartStore.fetchCartList()
})
</script>

<style lang="scss" scoped>
.cart-page {
  background: #f5f5f5;
  min-height: calc(100vh - 120px);
  padding: 20px 0 100px;
}

.page-container {
  max-width: 1200px;
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

.cart-content {
  background: #fff;
  border-radius: 8px;
  min-height: 400px;
}

.cart-header {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  background: #fafafa;
  border-radius: 8px 8px 0 0;
  font-size: 14px;
  color: #999;
  
  .header-item {
    text-align: center;
    
    &.product {
      flex: 1;
      text-align: left;
      margin-left: 20px;
    }
    
    &.price,
    &.quantity,
    &.subtotal {
      width: 120px;
    }
    
    &.action {
      width: 80px;
    }
  }
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  
  &:last-child {
    border-bottom: none;
  }
}

.item-product {
  flex: 1;
  display: flex;
  align-items: center;
  margin-left: 20px;
  
  .product-image {
    width: 80px;
    height: 80px;
    border-radius: 4px;
    cursor: pointer;
  }
  
  .image-error {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f5f5;
    color: #ccc;
  }
  
  .product-info {
    margin-left: 16px;
    
    .product-name {
      font-size: 14px;
      color: #333;
      cursor: pointer;
      
      &:hover {
        color: #67c23a;
      }
    }
    
    .product-spec {
      font-size: 12px;
      color: #999;
      margin-top: 4px;
    }
  }
}

.item-price,
.item-quantity,
.item-subtotal,
.item-action {
  width: 120px;
  text-align: center;
}

.item-action {
  width: 80px;
}

.item-price .price {
  font-size: 14px;
  color: #333;
}

.item-subtotal .subtotal {
  font-size: 16px;
  font-weight: 600;
  color: #f56c6c;
}

.cart-empty {
  padding: 80px 20px;
}

.cart-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: #fff;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  z-index: 100;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 24px;
  
  .selected-info {
    font-size: 14px;
    color: #666;
    
    .count {
      color: #f56c6c;
      font-weight: 600;
    }
  }
  
  .total-price {
    font-size: 14px;
    color: #666;
    
    .price {
      font-size: 24px;
      font-weight: 600;
      color: #f56c6c;
    }
  }
}
</style>
