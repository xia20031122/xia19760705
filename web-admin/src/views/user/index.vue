<template>
  <div class="user-center-page">
    <div class="page-container">
      <div class="user-sidebar">
        <div class="user-card">
          <el-avatar :size="64" :src="getImageUrl(userStore.avatar)">
            <el-icon size="32"><User /></el-icon>
          </el-avatar>
          <h3 class="user-name">{{ userStore.username }}</h3>
          <p class="user-type">{{ getUserTypeText(userStore.userInfo?.userType) }}</p>
        </div>
        
        <el-menu
          :default-active="activeMenu"
          router
          class="user-menu"
        >
          <el-menu-item index="/user/profile">
            <el-icon><User /></el-icon>
            <span>个人信息</span>
          </el-menu-item>
          <el-menu-item index="/user/address">
            <el-icon><Location /></el-icon>
            <span>收货地址</span>
          </el-menu-item>
          <el-menu-item index="/user/favorites">
            <el-icon><Star /></el-icon>
            <span>我的收藏</span>
          </el-menu-item>
          <el-menu-item index="/user/orders">
            <el-icon><List /></el-icon>
            <span>我的订单</span>
          </el-menu-item>
          <el-menu-item index="/user/password">
            <el-icon><Lock /></el-icon>
            <span>修改密码</span>
          </el-menu-item>
        </el-menu>
      </div>
      
      <div class="user-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()

// 当前激活菜单
const activeMenu = computed(() => route.path)

// 获取用户类型文本
const getUserTypeText = (type) => {
  const typeMap = {
    0: '管理员',
    1: '采购商',
    2: '供应商'
  }
  return typeMap[type] || '普通用户'
}

// 获取图片URL
const getImageUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return `/api${path}`
}
</script>

<style lang="scss" scoped>
.user-center-page {
  background: #f5f5f5;
  min-height: calc(100vh - 120px);
  padding: 20px 0 40px;
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  gap: 20px;
  
  @media (max-width: 768px) {
    flex-direction: column;
  }
}

.user-sidebar {
  width: 240px;
  flex-shrink: 0;
  
  @media (max-width: 768px) {
    width: 100%;
  }
}

.user-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  text-align: center;
  margin-bottom: 16px;
  
  .user-name {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin: 12px 0 4px;
  }
  
  .user-type {
    font-size: 13px;
    color: #999;
  }
}

.user-menu {
  background: #fff;
  border-radius: 8px;
  border: none;
  
  .el-menu-item {
    height: 50px;
    line-height: 50px;
    
    &.is-active {
      background: #f0f9eb;
      color: #67c23a;
    }
  }
}

.user-content {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  min-height: 500px;
}
</style>
