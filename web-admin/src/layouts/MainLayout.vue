<template>
  <div class="main-layout">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-content">
        <div class="logo" @click="router.push('/home')">
          <el-icon size="28" color="#67c23a"><Shop /></el-icon>
          <span class="logo-text">蔬菜销售系统</span>
        </div>
        
        <nav class="nav-menu">
          <router-link to="/home" class="nav-item" :class="{ active: route.path === '/home' }">
            首页
          </router-link>
          <router-link to="/category" class="nav-item" :class="{ active: route.path === '/category' }">
            分类
          </router-link>
          <router-link to="/products" class="nav-item" :class="{ active: route.path.startsWith('/products') }">
            全部商品
          </router-link>
        </nav>
        
        <div class="header-right">
          <!-- 搜索框 -->
          <el-input
            v-model="searchKeyword"
            placeholder="搜索商品"
            class="search-input"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          
          <!-- 购物车 -->
          <div class="cart-icon" @click="router.push('/cart')">
            <el-badge :value="cartStore.cartCount" :hidden="cartStore.cartCount === 0">
              <el-icon size="24"><ShoppingCart /></el-icon>
            </el-badge>
          </div>
          
          <!-- 用户信息 -->
          <div class="user-area" v-if="userStore.isLoggedIn">
            <el-dropdown trigger="click">
              <div class="user-info">
                <el-avatar :size="32" :src="getImageUrl(userStore.avatar)">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <span class="username">{{ userStore.username }}</span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="router.push('/user/profile')">
                    <el-icon><User /></el-icon>个人中心
                  </el-dropdown-item>
                  <el-dropdown-item @click="router.push('/user/orders')">
                    <el-icon><List /></el-icon>我的订单
                  </el-dropdown-item>
                  <el-dropdown-item @click="router.push('/user/favorites')">
                    <el-icon><Star /></el-icon>我的收藏
                  </el-dropdown-item>
                  <el-dropdown-item v-if="userStore.isAdmin" divided @click="router.push('/admin')">
                    <el-icon><Setting /></el-icon>管理后台
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">
                    <el-icon><SwitchButton /></el-icon>退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          <div class="auth-buttons" v-else>
            <el-button type="primary" link @click="router.push('/login')">登录</el-button>
            <el-divider direction="vertical" />
            <el-button type="primary" link @click="router.push('/register')">注册</el-button>
          </div>
        </div>
      </div>
    </header>
    
    <!-- 主内容区 -->
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
    
    <!-- 底部 -->
    <footer class="footer">
      <div class="footer-content">
        <p>© 2024 蔬菜销售系统 - 新鲜蔬菜，健康生活</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const cartStore = useCartStore()

const searchKeyword = ref('')

// 获取图片完整URL
const getImageUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return `/api${path}`
}

// 搜索
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/products', query: { keyword: searchKeyword.value } })
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await userStore.logout()
    router.push('/home')
  } catch (error) {
    // 取消退出
  }
}

// 初始化购物车
onMounted(() => {
  if (userStore.isLoggedIn) {
    cartStore.fetchCartList()
  }
})
</script>

<style lang="scss" scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  z-index: 1000;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  cursor: pointer;
  
  .logo-text {
    margin-left: 8px;
    font-size: 18px;
    font-weight: 600;
    color: #333;
  }
}

.nav-menu {
  display: flex;
  gap: 32px;
  margin-left: 60px;
  
  .nav-item {
    color: #666;
    font-size: 15px;
    transition: color 0.3s;
    
    &:hover,
    &.active {
      color: #67c23a;
    }
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.search-input {
  width: 200px;
}

.cart-icon {
  cursor: pointer;
  padding: 8px;
  
  &:hover {
    color: #67c23a;
  }
}

.user-area {
  .user-info {
    display: flex;
    align-items: center;
    cursor: pointer;
    
    .username {
      margin-left: 8px;
      font-size: 14px;
      color: #333;
    }
  }
}

.auth-buttons {
  display: flex;
  align-items: center;
}

.main-content {
  flex: 1;
  margin-top: 60px;
  min-height: calc(100vh - 120px);
}

.footer {
  background: #333;
  color: #999;
  padding: 20px;
  text-align: center;
  font-size: 14px;
}

// 过渡动画
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
