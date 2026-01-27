<template>
  <div class="product-detail-page">
    <div class="page-container" v-loading="loading">
      <!-- 面包屑 -->
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/products' }">全部商品</el-breadcrumb-item>
        <el-breadcrumb-item>{{ product.name }}</el-breadcrumb-item>
      </el-breadcrumb>
      
      <!-- 商品信息 -->
      <div class="product-main" v-if="product.id">
        <div class="product-gallery">
          <el-image 
            :src="getImageUrl(product.images)" 
            fit="contain"
            class="main-image"
          >
            <template #error>
              <div class="image-error">
                <el-icon size="64"><Picture /></el-icon>
              </div>
            </template>
          </el-image>
          
          <!-- 新鲜度标签 -->
          <div class="freshness-badge" v-if="freshnessInfo" :style="{ background: freshnessInfo.color }">
            {{ freshnessInfo.statusText }} {{ freshnessInfo.percentage }}%
          </div>
        </div>
        
        <div class="product-info">
          <h1 class="product-name">{{ product.name }}</h1>
          
          <div class="product-tags">
            <el-tag type="success" v-if="isNew">新品</el-tag>
            <el-tag type="warning" v-if="product.hot">热销</el-tag>
            <el-tag v-if="product.typeName">{{ product.typeName }}</el-tag>
          </div>
          
          <div class="price-section">
            <span class="price-label">价格</span>
            <span class="price-value">¥{{ product.price }}</span>
            <span class="price-unit">/{{ product.unit || '斤' }}</span>
          </div>
          
          <div class="info-row">
            <span class="info-label">产地：</span>
            <span class="info-value">{{ product.origin || '本地农场' }}</span>
          </div>
          
          <div class="info-row">
            <span class="info-label">库存：</span>
            <span class="info-value" :class="{ 'low-stock': stockInfo?.quantity < 10 }">
              {{ stockInfo?.quantity || 0 }} {{ product.unit || '斤' }}
              <span v-if="stockInfo?.quantity < 10" class="stock-warning">（库存紧张）</span>
            </span>
          </div>
          
          <div class="info-row" v-if="product.harvestDate">
            <span class="info-label">采摘日期：</span>
            <span class="info-value">{{ product.harvestDate }}</span>
          </div>
          
          <div class="quantity-section">
            <span class="quantity-label">数量</span>
            <el-input-number 
              v-model="quantity" 
              :min="1" 
              :max="stockInfo?.quantity || 99"
              size="large"
            />
            <span class="quantity-unit">{{ product.unit || '斤' }}</span>
          </div>
          
          <div class="total-section">
            <span class="total-label">小计：</span>
            <span class="total-value">¥{{ (product.price * quantity).toFixed(2) }}</span>
          </div>
          
          <div class="action-buttons">
            <el-button 
              type="primary" 
              size="large"
              :loading="addingCart"
              @click="handleAddCart"
            >
              <el-icon><ShoppingCart /></el-icon>
              加入购物车
            </el-button>
            <el-button 
              type="danger" 
              size="large"
              @click="handleBuyNow"
            >
              立即购买
            </el-button>
            <el-button 
              size="large"
              :icon="isFavorite ? StarFilled : Star"
              :type="isFavorite ? 'warning' : 'default'"
              @click="handleFavorite"
            >
              {{ isFavorite ? '已收藏' : '收藏' }}
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 商品详情 -->
      <div class="product-detail" v-if="product.id">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="商品介绍" name="intro">
            <div class="detail-content">
              <p v-if="product.remark">{{ product.remark }}</p>
              <p v-else>暂无商品介绍</p>
            </div>
          </el-tab-pane>
          <el-tab-pane label="规格参数" name="spec">
            <div class="spec-table">
              <div class="spec-row">
                <span class="spec-label">商品名称</span>
                <span class="spec-value">{{ product.name }}</span>
              </div>
              <div class="spec-row">
                <span class="spec-label">商品分类</span>
                <span class="spec-value">{{ product.typeName || '-' }}</span>
              </div>
              <div class="spec-row">
                <span class="spec-label">计量单位</span>
                <span class="spec-value">{{ product.unit || '斤' }}</span>
              </div>
              <div class="spec-row">
                <span class="spec-label">产地</span>
                <span class="spec-value">{{ product.origin || '本地农场' }}</span>
              </div>
              <div class="spec-row" v-if="product.harvestDate">
                <span class="spec-label">采摘日期</span>
                <span class="spec-value">{{ product.harvestDate }}</span>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
      
      <!-- 相似商品推荐 -->
      <div class="similar-products" v-if="similarList.length">
        <h3 class="section-title">相似商品</h3>
        <div class="products-grid">
          <ProductCard 
            v-for="item in similarList" 
            :key="item.id" 
            :product="item" 
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { getVegetableById } from '@/api/vegetable'
import { getSimilarRecommend } from '@/api/recommend'
import { recordBehavior } from '@/api/recommend'
import ProductCard from '@/components/ProductCard.vue'
import { ElMessage } from 'element-plus'
import { Star, StarFilled } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const product = ref({})
const loading = ref(false)
const quantity = ref(1)
const activeTab = ref('intro')
const addingCart = ref(false)
const isFavorite = ref(false)
const stockInfo = ref(null)
const freshnessInfo = ref(null)
const similarList = ref([])

// 是否新品
const isNew = computed(() => {
  if (!product.value.createTime) return false
  return dayjs().diff(dayjs(product.value.createTime), 'day') <= 7
})

// 获取图片URL
const getImageUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return `/api${path}`
}

// 获取商品详情
const fetchProduct = async (id) => {
  loading.value = true
  try {
    const res = await getVegetableById(id)
    product.value = res.data || {}
    
    // 记录浏览行为
    if (userStore.isLoggedIn) {
      recordBehavior({ vegetableId: id, type: 1 }).catch(() => {})
    }
    
    // 获取相似商品
    fetchSimilar(id)
  } catch (error) {
    console.error('获取商品详情失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取相似商品
const fetchSimilar = async (id) => {
  try {
    const res = await getSimilarRecommend(id, 4)
    similarList.value = res.data || []
  } catch (error) {
    console.error('获取相似商品失败:', error)
  }
}

// 加入购物车
const handleAddCart = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  
  addingCart.value = true
  try {
    await cartStore.addToCart(product.value.id, quantity.value)
  } finally {
    addingCart.value = false
  }
}

// 立即购买
const handleBuyNow = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  
  router.push({
    path: '/checkout',
    query: {
      type: 'direct',
      vegetableId: product.value.id,
      quantity: quantity.value
    }
  })
}

// 收藏/取消收藏
const handleFavorite = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  
  isFavorite.value = !isFavorite.value
  ElMessage.success(isFavorite.value ? '收藏成功' : '已取消收藏')
}

// 监听路由变化
watch(() => route.params.id, (id) => {
  if (id) {
    fetchProduct(id)
  }
}, { immediate: true })

onMounted(() => {
  window.scrollTo({ top: 0 })
})
</script>

<style lang="scss" scoped>
.product-detail-page {
  background: #f5f5f5;
  min-height: calc(100vh - 120px);
  padding: 20px 0 40px;
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.el-breadcrumb {
  margin-bottom: 20px;
}

.product-main {
  display: flex;
  gap: 40px;
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 20px;
  
  @media (max-width: 768px) {
    flex-direction: column;
  }
}

.product-gallery {
  width: 400px;
  flex-shrink: 0;
  position: relative;
  
  @media (max-width: 768px) {
    width: 100%;
  }
  
  .main-image {
    width: 100%;
    height: 400px;
    border-radius: 8px;
    overflow: hidden;
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
  
  .freshness-badge {
    position: absolute;
    top: 12px;
    right: 12px;
    padding: 4px 12px;
    border-radius: 4px;
    color: #fff;
    font-size: 12px;
  }
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.product-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}

.price-section {
  background: #fff8e6;
  padding: 16px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  
  .price-label {
    font-size: 14px;
    color: #999;
    margin-right: 16px;
  }
  
  .price-value {
    font-size: 32px;
    font-weight: 600;
    color: #f56c6c;
  }
  
  .price-unit {
    font-size: 14px;
    color: #999;
  }
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
  
  .info-label {
    color: #999;
    width: 80px;
  }
  
  .info-value {
    color: #333;
    
    &.low-stock {
      color: #f56c6c;
    }
    
    .stock-warning {
      font-size: 12px;
    }
  }
}

.quantity-section {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 24px 0;
  
  .quantity-label {
    font-size: 14px;
    color: #999;
    width: 68px;
  }
  
  .quantity-unit {
    font-size: 14px;
    color: #666;
  }
}

.total-section {
  margin-bottom: 24px;
  
  .total-label {
    font-size: 14px;
    color: #999;
  }
  
  .total-value {
    font-size: 24px;
    font-weight: 600;
    color: #f56c6c;
  }
}

.action-buttons {
  display: flex;
  gap: 12px;
  
  .el-button {
    min-width: 140px;
  }
}

.product-detail {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 20px;
}

.detail-content {
  padding: 20px 0;
  color: #666;
  line-height: 1.8;
}

.spec-table {
  .spec-row {
    display: flex;
    border-bottom: 1px solid #f0f0f0;
    
    &:last-child {
      border-bottom: none;
    }
  }
  
  .spec-label {
    width: 120px;
    padding: 12px 16px;
    background: #fafafa;
    color: #999;
    font-size: 14px;
  }
  
  .spec-value {
    flex: 1;
    padding: 12px 16px;
    color: #333;
    font-size: 14px;
  }
}

.similar-products {
  .section-title {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin-bottom: 20px;
  }
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  
  @media (max-width: 992px) {
    grid-template-columns: repeat(3, 1fr);
  }
  
  @media (max-width: 768px) {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
