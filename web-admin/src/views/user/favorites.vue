<template>
  <div class="favorites-page">
    <div class="page-header">
      <h2 class="page-title">我的收藏</h2>
      <span class="total-count">共 {{ favoriteList.length }} 件商品</span>
    </div>
    
    <div class="favorites-list" v-loading="loading">
      <div class="product-grid">
        <div 
          class="product-card" 
          v-for="item in favoriteList" 
          :key="item.id"
        >
          <div class="product-image" @click="goDetail(item.vegetableId)">
            <el-image 
              :src="getImageUrl(item.images)" 
              fit="cover"
              lazy
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </div>
          
          <div class="product-info">
            <h3 class="product-name" @click="goDetail(item.vegetableId)">
              {{ item.vegetableName || item.name }}
            </h3>
            
            <div class="product-bottom">
              <div class="price-area">
                <span class="price">{{ item.price }}</span>
                <span class="unit">/{{ item.unit || '斤' }}</span>
              </div>
              
              <div class="action-buttons">
                <el-button 
                  type="primary" 
                  size="small"
                  @click="handleAddCart(item)"
                >
                  加入购物车
                </el-button>
                <el-button 
                  type="danger" 
                  size="small"
                  link
                  @click="handleRemove(item)"
                >
                  取消收藏
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <el-empty v-if="!loading && favoriteList.length === 0" description="暂无收藏商品">
        <el-button type="primary" @click="router.push('/products')">去逛逛</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { getFavoriteList, removeFavorite } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const cartStore = useCartStore()

const loading = ref(false)
const favoriteList = ref([])

// 获取图片URL
const getImageUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return `/api${path}`
}

// 获取收藏列表
const fetchFavoriteList = async () => {
  loading.value = true
  try {
    const res = await getFavoriteList()
    favoriteList.value = res.data || []
  } catch (error) {
    console.error('获取收藏列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 跳转详情
const goDetail = (id) => {
  router.push(`/product/${id}`)
}

// 加入购物车
const handleAddCart = async (item) => {
  try {
    await cartStore.addToCart(item.vegetableId, 1)
  } catch (error) {
    console.error('添加购物车失败:', error)
  }
}

// 取消收藏
const handleRemove = async (item) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏吗？', '提示', { type: 'warning' })
    await removeFavorite(item.id)
    ElMessage.success('已取消收藏')
    fetchFavoriteList()
  } catch (error) {
    // 取消操作
  }
}

onMounted(() => {
  fetchFavoriteList()
})
</script>

<style lang="scss" scoped>
.favorites-page {
  .page-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid #f0f0f0;
  }
  
  .page-title {
    font-size: 18px;
    font-weight: 600;
    color: #333;
  }
  
  .total-count {
    font-size: 14px;
    color: #999;
  }
}

.favorites-list {
  min-height: 200px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  
  @media (max-width: 992px) {
    grid-template-columns: repeat(2, 1fr);
  }
  
  @media (max-width: 576px) {
    grid-template-columns: 1fr;
  }
}

.product-card {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
  
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

.product-image {
  height: 160px;
  cursor: pointer;
  
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
    color: #ccc;
    font-size: 48px;
  }
}

.product-info {
  padding: 12px;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 12px;
  cursor: pointer;
  
  &:hover {
    color: #67c23a;
  }
}

.product-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 8px;
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

.action-buttons {
  display: flex;
  gap: 8px;
}
</style>
