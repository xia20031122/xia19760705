<template>
  <div class="profile-page">
    <h2 class="page-title">个人信息</h2>
    
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      class="profile-form"
      v-loading="loading"
    >
      <el-form-item label="头像">
        <div class="avatar-uploader">
          <el-avatar :size="80" :src="getImageUrl(form.avatar)">
            <el-icon size="32"><User /></el-icon>
          </el-avatar>
          <el-button type="primary" link class="change-btn">更换头像</el-button>
        </div>
      </el-form-item>
      
      <el-form-item label="用户名" prop="userName">
        <el-input v-model="form.userName" placeholder="请输入用户名" />
      </el-form-item>
      
      <el-form-item label="登录账号">
        <el-input v-model="form.loginAccount" disabled />
      </el-form-item>
      
      <el-form-item label="性别" prop="sex">
        <el-radio-group v-model="form.sex">
          <el-radio :value="0">男</el-radio>
          <el-radio :value="1">女</el-radio>
        </el-radio-group>
      </el-form-item>
      
      <el-form-item label="手机号" prop="tel">
        <el-input v-model="form.tel" placeholder="请输入手机号" />
      </el-form-item>
      
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入邮箱" />
      </el-form-item>
      
      <el-form-item label="个人简介" prop="remark">
        <el-input
          v-model="form.remark"
          type="textarea"
          :rows="3"
          placeholder="介绍一下自己"
          maxlength="200"
          show-word-limit
        />
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" :loading="saving" @click="handleSave">
          保存修改
        </el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { updateUserInfo } from '@/api/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)
const saving = ref(false)

const form = reactive({
  userName: '',
  loginAccount: '',
  sex: 0,
  tel: '',
  email: '',
  avatar: '',
  remark: ''
})

const rules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度为2-20个字符', trigger: 'blur' }
  ],
  tel: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 获取图片URL
const getImageUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return `/api${path}`
}

// 初始化表单
const initForm = () => {
  const userInfo = userStore.userInfo
  if (userInfo) {
    form.userName = userInfo.userName || ''
    form.loginAccount = userInfo.loginAccount || ''
    form.sex = userInfo.sex || 0
    form.tel = userInfo.tel || ''
    form.email = userInfo.email || ''
    form.avatar = userInfo.avatar || ''
    form.remark = userInfo.remark || ''
  }
}

// 保存
const handleSave = async () => {
  try {
    await formRef.value.validate()
    saving.value = true
    
    await updateUserInfo({
      id: userStore.userInfo?.id,
      userName: form.userName,
      sex: form.sex,
      tel: form.tel,
      email: form.email,
      remark: form.remark
    })
    
    // 更新store中的用户信息
    userStore.updateUserInfo({
      userName: form.userName,
      sex: form.sex,
      tel: form.tel,
      email: form.email,
      remark: form.remark
    })
    
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

// 重置
const handleReset = () => {
  initForm()
}

onMounted(() => {
  initForm()
})
</script>

<style lang="scss" scoped>
.profile-page {
  .page-title {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid #f0f0f0;
  }
}

.profile-form {
  max-width: 500px;
}

.avatar-uploader {
  display: flex;
  align-items: center;
  gap: 16px;
  
  .change-btn {
    font-size: 13px;
  }
}
</style>
