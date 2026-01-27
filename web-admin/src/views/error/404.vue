<template>
  <div class="not-found-page">
    <div class="content">
      <h1>404</h1>
      <p class="message">抱歉，您访问的页面不存在</p>
      <div class="actions">
        <el-button type="primary" @click="router.push('/home')">返回首页</el-button>
        <el-button @click="router.back()">返回上一页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<style lang="scss" scoped>
.not-found-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
}

.content {
  text-align: center;
  
  h1 {
    font-size: 120px;
    font-weight: 700;
    color: #67c23a;
    margin-bottom: 16px;
    line-height: 1;
  }
  
  .message {
    font-size: 18px;
    color: #666;
    margin-bottom: 32px;
  }
  
  .actions {
    display: flex;
    gap: 16px;
    justify-content: center;
  }
}
</style>
