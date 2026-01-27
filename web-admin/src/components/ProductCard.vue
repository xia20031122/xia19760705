<template>
  <div class="product-card" @click="goDetail">
    <div class="product-image">
      <el-image 
        :src="getImageUrl(product.images)" 
        fit="cover"
        lazy
      >
        <template #error>
          <div class="image-error">
            <el-icon><Picture /></el-icon>
          </div>
        </template>
      </el-image>
      <div class="product-tags">
        <span class="tag new" v-if="isNew">新品</span>
        <span class="tag hot" v-if="product.hot">热销</span>
      </div>
    </div>
    
    <div class="product-info">
      <h3 class="product-name">{{ product.name }}</h3>
      <p class="product-desc" v-if="product.remark">{{ product.remark }}</p>
      
      <div class="product-bottom">
        <div class="price-area">
          <span class="price">{{ product.price }}</span>
          <span class="unit">/{{ product.unit || '斤' }}</span>
        </div>
        
        <el-button 
          type="primary" 
          size="small" 
          circle
          @click.stop="handleAddCart"
          :loading="addingCart"
        >
          <el-icon><ShoppingCart /></el-icon>
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

const addingCart = ref(false)

// 是否为新品（7天内上架）
const isNew = computed(() => {
  if (!props.product.createTime) return false
  return dayjs().diff(dayjs(props.product.createTime), 'day') <= 7
})

// 获取图片URL
const getImageUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return `/api${path}`
}

// 跳转详情
const goDetail = () => {
  router.push(`/product/${props.product.id}`)
}

// 添加购物车
const handleAddCart = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  addingCart.value = true
  try {
    await cartStore.addToCart(props.product.id, 1)
  } finally {
    addingCart.value = false
  }
}
</script>

<style lang="scss" scoped>
.product-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }
}

.product-image {
  position: relative;
  height: 200px;
  
  .el-image {
    width: 100%;
    height: 100%;
  }
  
  .image-error {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f5f5;
    color: #999;
    font-size: 48px;
  }
}

.product-tags {
  position: absolute;
  top: 8px;
  left: 8px;
  display: flex;
  gap: 4px;
  
  .tag {
    padding: 2px 8px;
    border-radius: 4px;
    font-size: 12px;
    color: #fff;
    
    &.new {
      background: #67c23a;
    }
    
    &.hot {
      background: #f56c6c;
    }
  }
}

.product-info {
  padding: 12px;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.price-area {
  .price {
    font-size: 18px;
    font-weight: 600;
    color: #f56c6c;
    
    &::before {
      content: '¥';
      font-size: 12px;
    }
  }
  
  .unit {
    font-size: 12px;
    color: #999;
  }
}
</style>
