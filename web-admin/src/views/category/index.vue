<template>
  <div class="category-page">
    <div class="page-container">
      <div class="page-header">
        <h1>商品分类</h1>
        <p>选择您感兴趣的蔬菜分类</p>
      </div>
      
      <div class="category-grid" v-loading="loading">
        <div 
          class="category-card" 
          v-for="item in categories" 
          :key="item.id"
          @click="goProducts(item.id)"
        >
          <div class="category-icon">
            <el-icon size="48"><component :is="getCategoryIcon(item.name)" /></el-icon>
          </div>
          <div class="category-info">
            <h3>{{ item.name }}</h3>
            <p>{{ item.remark || '新鲜直供，品质保证' }}</p>
          </div>
          <div class="category-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>
      </div>
      
      <EmptyState v-if="!loading && categories.length === 0" description="暂无分类" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getVegetableTypeList } from '@/api/vegetable'
import EmptyState from '@/components/EmptyState.vue'
import { Apple, Orange, Grape, Cherry, Pear, Watermelon, Coffee, Food } from '@element-plus/icons-vue'

const router = useRouter()

const categories = ref([])
const loading = ref(false)

// 根据分类名称获取图标
const getCategoryIcon = (name) => {
  const iconMap = {
    '叶菜类': Apple,
    '根茎类': Orange,
    '瓜果类': Grape,
    '菌菇类': Cherry,
    '豆类': Pear,
    '水果': Watermelon,
    '调味品': Coffee,
  }
  return iconMap[name] || Food
}

// 跳转到商品列表
const goProducts = (typeId) => {
  router.push({ path: '/products', query: { type: typeId } })
}

// 获取分类列表
const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await getVegetableTypeList()
    categories.value = res.data || []
  } catch (error) {
    console.error('获取分类失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchCategories()
})
</script>

<style lang="scss" scoped>
.category-page {
  background: #f5f5f5;
  min-height: calc(100vh - 120px);
  padding: 40px 0;
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
  
  h1 {
    font-size: 28px;
    font-weight: 600;
    color: #333;
    margin-bottom: 8px;
  }
  
  p {
    font-size: 14px;
    color: #999;
  }
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  
  @media (max-width: 992px) {
    grid-template-columns: repeat(2, 1fr);
  }
  
  @media (max-width: 576px) {
    grid-template-columns: 1fr;
  }
}

.category-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
    
    .category-icon {
      background: #67c23a;
      color: #fff;
    }
    
    .category-arrow {
      color: #67c23a;
      transform: translateX(4px);
    }
  }
}

.category-icon {
  width: 80px;
  height: 80px;
  border-radius: 16px;
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #67c23a;
  flex-shrink: 0;
  transition: all 0.3s;
}

.category-info {
  flex: 1;
  margin-left: 20px;
  
  h3 {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin-bottom: 4px;
  }
  
  p {
    font-size: 13px;
    color: #999;
  }
}

.category-arrow {
  color: #ccc;
  font-size: 20px;
  transition: all 0.3s;
}
</style>
