<template>
  <div class="products-page">
    <div class="page-container">
      <!-- 筛选区域 -->
      <div class="filter-section">
        <div class="filter-row">
          <span class="filter-label">分类：</span>
          <div class="filter-options">
            <span 
              class="filter-item" 
              :class="{ active: !currentType }"
              @click="filterByType(null)"
            >
              全部
            </span>
            <span 
              class="filter-item" 
              v-for="item in categories" 
              :key="item.id"
              :class="{ active: currentType === item.id }"
              @click="filterByType(item.id)"
            >
              {{ item.name }}
            </span>
          </div>
        </div>
        
        <div class="filter-row">
          <span class="filter-label">排序：</span>
          <div class="filter-options">
            <span 
              class="filter-item" 
              :class="{ active: sortType === 'default' }"
              @click="sortBy('default')"
            >
              默认
            </span>
            <span 
              class="filter-item" 
              :class="{ active: sortType === 'price_asc' }"
              @click="sortBy('price_asc')"
            >
              价格↑
            </span>
            <span 
              class="filter-item" 
              :class="{ active: sortType === 'price_desc' }"
              @click="sortBy('price_desc')"
            >
              价格↓
            </span>
            <span 
              class="filter-item" 
              :class="{ active: sortType === 'sales' }"
              @click="sortBy('sales')"
            >
              销量
            </span>
            <span 
              class="filter-item" 
              :class="{ active: sortType === 'newest' }"
              @click="sortBy('newest')"
            >
              最新
            </span>
          </div>
        </div>
      </div>
      
      <!-- 搜索结果提示 -->
      <div class="search-tip" v-if="keyword">
        搜索 "<span>{{ keyword }}</span>" 的结果，共 {{ total }} 件商品
        <el-button type="primary" link @click="clearKeyword">清除搜索</el-button>
      </div>
      
      <!-- 商品列表 -->
      <div class="products-grid" v-loading="loading">
        <ProductCard 
          v-for="item in products" 
          :key="item.id" 
          :product="item" 
        />
      </div>
      
      <EmptyState v-if="!loading && products.length === 0" description="暂无商品" />
      
      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getVegetablePage, getVegetableTypeList } from '@/api/vegetable'
import ProductCard from '@/components/ProductCard.vue'
import EmptyState from '@/components/EmptyState.vue'

const route = useRoute()
const router = useRouter()

const categories = ref([])
const products = ref([])
const loading = ref(false)

const currentType = ref(null)
const sortType = ref('default')
const keyword = ref('')
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)

// 获取分类列表
const fetchCategories = async () => {
  try {
    const res = await getVegetableTypeList()
    categories.value = res.data || []
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 更新路由
const updateRoute = () => {
  const query = {}
  if (currentType.value) query.type = currentType.value
  if (keyword.value) query.keyword = keyword.value
  router.push({ path: '/products', query })
}

// 获取商品列表
const fetchProducts = async () => {
  loading.value = true
  try {
    const params = {
      pageNumber: pageNum.value,
      pageSize: pageSize.value
    }
    
    if (currentType.value) {
      params.type = currentType.value
    }
    if (keyword.value) {
      params.name = keyword.value
    }
    
    // 排序参数
    if (sortType.value === 'price_asc') {
      params.orderBy = 'price'
      params.orderType = 'asc'
    } else if (sortType.value === 'price_desc') {
      params.orderBy = 'price'
      params.orderType = 'desc'
    } else if (sortType.value === 'sales') {
      params.orderBy = 'sales'
      params.orderType = 'desc'
    } else if (sortType.value === 'newest') {
      params.orderBy = 'createTime'
      params.orderType = 'desc'
    }
    
    const res = await getVegetablePage(params)
    products.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取商品列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 按分类筛选
const filterByType = (typeId) => {
  currentType.value = typeId
  pageNum.value = 1
  updateRoute()
  fetchProducts()
}

// 排序
const sortBy = (type) => {
  sortType.value = type
  pageNum.value = 1
  fetchProducts()
}

// 清除搜索关键词
const clearKeyword = () => {
  keyword.value = ''
  updateRoute()
  fetchProducts()
}

// 分页
const handlePageChange = (page) => {
  pageNum.value = page
  fetchProducts()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 监听路由变化（延迟到 onMounted 之后）
watch(() => route.query, (query) => {
  if (query.type) {
    currentType.value = query.type
  }
  if (query.keyword) {
    keyword.value = query.keyword
  }
  // 确保在组件挂载后才执行
  if (categories.value.length > 0 || products.value.length > 0) {
    fetchProducts()
  }
})

onMounted(() => {
  fetchCategories()
  // 初始化时获取商品列表
  fetchProducts()
})
</script>

<style lang="scss" scoped>
.products-page {
  background: #f5f5f5;
  min-height: calc(100vh - 120px);
  padding: 20px 0 40px;
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.filter-section {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.filter-row {
  display: flex;
  align-items: flex-start;
  
  & + .filter-row {
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid #f0f0f0;
  }
}

.filter-label {
  flex-shrink: 0;
  width: 60px;
  font-size: 14px;
  color: #666;
  line-height: 28px;
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-item {
  padding: 4px 16px;
  border-radius: 4px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    color: #67c23a;
  }
  
  &.active {
    background: #67c23a;
    color: #fff;
  }
}

.search-tip {
  padding: 12px 16px;
  background: #fff8e6;
  border-radius: 4px;
  margin-bottom: 20px;
  font-size: 14px;
  color: #666;
  
  span {
    color: #f56c6c;
    font-weight: 500;
  }
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  min-height: 200px;
  
  @media (max-width: 992px) {
    grid-template-columns: repeat(3, 1fr);
  }
  
  @media (max-width: 768px) {
    grid-template-columns: repeat(2, 1fr);
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}
</style>
