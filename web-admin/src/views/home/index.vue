<template>
  <div class="home-page">
    <!-- 轮播图 -->
    <section class="banner-section">
      <el-carousel height="400px" :interval="5000">
        <el-carousel-item v-for="item in rotations" :key="item.id">
          <div class="banner-item" :style="{ backgroundImage: `url(${getImageUrl(item.images)})` }">
            <div class="banner-content">
              <h2>{{ item.content }}</h2>
            </div>
          </div>
        </el-carousel-item>
        <el-carousel-item v-if="rotations.length === 0">
          <div class="banner-item default-banner">
            <div class="banner-content">
              <h2>新鲜蔬菜，健康生活</h2>
              <p>产地直供，当日送达</p>
              <el-button type="primary" size="large" @click="router.push('/products')">
                立即选购
              </el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>
    
    <!-- 分类入口 -->
    <section class="category-section">
      <div class="section-container">
        <div class="category-grid">
          <div 
            class="category-item" 
            v-for="item in categories" 
            :key="item.id"
            @click="goCategory(item.id)"
          >
            <div class="category-icon">
              <el-icon size="32"><Apple /></el-icon>
            </div>
            <span class="category-name">{{ item.name }}</span>
          </div>
          <div class="category-item" @click="router.push('/category')">
            <div class="category-icon more">
              <el-icon size="32"><More /></el-icon>
            </div>
            <span class="category-name">全部分类</span>
          </div>
        </div>
      </div>
    </section>
    
    <!-- 推荐商品 -->
    <section class="recommend-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">
            <el-icon color="#67c23a"><Star /></el-icon>
            为您推荐
          </h2>
          <el-link type="primary" underline="never" @click="router.push('/products')">
            查看更多 <el-icon><ArrowRight /></el-icon>
          </el-link>
        </div>
        
        <div class="product-grid" v-loading="loading">
          <ProductCard 
            v-for="item in recommendList" 
            :key="item.id" 
            :product="item" 
          />
        </div>
        
        <EmptyState v-if="!loading && recommendList.length === 0" description="暂无推荐商品" />
      </div>
    </section>
    
    <!-- 热门商品 -->
    <section class="hot-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">
            <el-icon color="#f56c6c"><Histogram /></el-icon>
            热门商品
          </h2>
          <el-link type="primary" underline="never" @click="router.push('/products')">
            查看更多 <el-icon><ArrowRight /></el-icon>
          </el-link>
        </div>
        
        <div class="product-grid" v-loading="hotLoading">
          <ProductCard 
            v-for="item in hotList" 
            :key="item.id" 
            :product="item" 
          />
        </div>
        
        <EmptyState v-if="!hotLoading && hotList.length === 0" description="暂无热门商品" />
      </div>
    </section>
    
    <!-- 新品上市 -->
    <section class="new-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">
            <el-icon color="#67c23a"><Promotion /></el-icon>
            新品上市
          </h2>
          <el-link type="primary" underline="never" @click="router.push('/products')">
            查看更多 <el-icon><ArrowRight /></el-icon>
          </el-link>
        </div>
        
        <div class="product-grid" v-loading="newLoading">
          <ProductCard 
            v-for="item in newList" 
            :key="item.id" 
            :product="item" 
          />
        </div>
        
        <EmptyState v-if="!newLoading && newList.length === 0" description="暂无新品" />
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getRotationList } from '@/api/common'
import { getVegetableIndex } from '@/api/vegetable'
import { getVegetableTypeList } from '@/api/vegetable'
import { getHotRecommend, getNewArrival, getPersonalized } from '@/api/recommend'
import ProductCard from '@/components/ProductCard.vue'
import EmptyState from '@/components/EmptyState.vue'

const router = useRouter()

const rotations = ref([])
const categories = ref([])
const recommendList = ref([])
const hotList = ref([])
const newList = ref([])

const loading = ref(false)
const hotLoading = ref(false)
const newLoading = ref(false)

const getImageUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return `/api${path}`
}

const goCategory = (id) => {
  router.push({ path: '/products', query: { type: id } })
}

// 获取轮播图
const fetchRotations = async () => {
  try {
    const res = await getRotationList()
    rotations.value = res.data || []
  } catch (error) {
    console.error('获取轮播图失败:', error)
  }
}

// 获取分类
const fetchCategories = async () => {
  try {
    const res = await getVegetableTypeList()
    categories.value = (res.data || []).slice(0, 7)
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 获取推荐商品
const fetchRecommend = async () => {
  loading.value = true
  try {
    // 优先获取个性化推荐，失败则获取首页推荐
    try {
      const res = await getPersonalized(8)
      recommendList.value = res.data || []
    } catch {
      const res = await getVegetableIndex()
      recommendList.value = res.data || []
    }
  } catch (error) {
    console.error('获取推荐商品失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取热门商品
const fetchHot = async () => {
  hotLoading.value = true
  try {
    const res = await getHotRecommend(8)
    hotList.value = res.data || []
  } catch (error) {
    // 热门推荐接口可能不存在，使用首页数据
    try {
      const res = await getVegetableIndex()
      hotList.value = res.data || []
    } catch (e) {
      console.error('获取热门商品失败:', e)
    }
  } finally {
    hotLoading.value = false
  }
}

// 获取新品
const fetchNew = async () => {
  newLoading.value = true
  try {
    const res = await getNewArrival(8)
    newList.value = res.data || []
  } catch (error) {
    // 新品推荐接口可能不存在
    console.error('获取新品失败:', error)
  } finally {
    newLoading.value = false
  }
}

onMounted(() => {
  fetchRotations()
  fetchCategories()
  fetchRecommend()
  fetchHot()
  fetchNew()
})
</script>

<style lang="scss" scoped>
.home-page {
  background: #f5f5f5;
}

.banner-section {
  .banner-item {
    width: 100%;
    height: 100%;
    background-size: cover;
    background-position: center;
    display: flex;
    align-items: center;
    justify-content: center;
    
    &.default-banner {
      background: linear-gradient(135deg, #67c23a 0%, #529b2e 100%);
    }
  }
  
  .banner-content {
    text-align: center;
    color: #fff;
    
    h2 {
      font-size: 36px;
      font-weight: 600;
      margin-bottom: 16px;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    }
    
    p {
      font-size: 18px;
      margin-bottom: 24px;
      opacity: 0.9;
    }
  }
}

.section-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.category-section {
  background: #fff;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 16px;
  
  @media (max-width: 768px) {
    grid-template-columns: repeat(4, 1fr);
  }
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 10px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s;
  
  &:hover {
    background: #f5f5f5;
    
    .category-icon {
      transform: scale(1.1);
    }
  }
  
  .category-icon {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    color: #67c23a;
    margin-bottom: 8px;
    transition: transform 0.3s;
    
    &.more {
      background: linear-gradient(135deg, #f5f5f5 0%, #e0e0e0 100%);
      color: #999;
    }
  }
  
  .category-name {
    font-size: 13px;
    color: #666;
  }
}

.product-grid {
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

.recommend-section,
.new-section {
  background: #fff;
}

.hot-section {
  background: #f5f5f5;
}
</style>
