<template>
  <div class="password-page">
    <h2 class="page-title">修改密码</h2>
    
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      class="password-form"
    >
      <el-form-item label="当前密码" prop="oldPassword">
        <el-input
          v-model="form.oldPassword"
          type="password"
          placeholder="请输入当前密码"
          show-password
        />
      </el-form-item>
      
      <el-form-item label="新密码" prop="newPassword">
        <el-input
          v-model="form.newPassword"
          type="password"
          placeholder="请输入新密码（6-20位）"
          show-password
        />
      </el-form-item>
      
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input
          v-model="form.confirmPassword"
          type="password"
          placeholder="请再次输入新密码"
          show-password
        />
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handleSubmit">
          确认修改
        </el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
    
    <div class="password-tips">
      <h4>密码设置建议：</h4>
      <ul>
        <li>密码长度为6-20个字符</li>
        <li>建议使用字母、数字和符号的组合</li>
        <li>不要使用与其他网站相同的密码</li>
        <li>定期更换密码可以提高账户安全性</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { changePassword } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)

const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 验证确认密码
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 提交修改
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true
    
    await changePassword({
      oldPassword: form.oldPassword,
      newPassword: form.newPassword
    })
    
    ElMessage.success('密码修改成功，请重新登录')
    
    // 退出登录
    await userStore.logout()
    router.push('/login')
  } catch (error) {
    console.error('修改密码失败:', error)
  } finally {
    loading.value = false
  }
}

// 重置
const handleReset = () => {
  formRef.value.resetFields()
}
</script>

<style lang="scss" scoped>
.password-page {
  .page-title {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid #f0f0f0;
  }
}

.password-form {
  max-width: 400px;
}

.password-tips {
  margin-top: 40px;
  padding: 16px;
  background: #f5f5f5;
  border-radius: 8px;
  
  h4 {
    font-size: 14px;
    color: #666;
    margin-bottom: 12px;
  }
  
  ul {
    margin: 0;
    padding-left: 20px;
    
    li {
      font-size: 13px;
      color: #999;
      line-height: 1.8;
    }
  }
}
</style>
