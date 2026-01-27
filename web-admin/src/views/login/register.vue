<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-header">
        <router-link to="/home" class="logo">
          <el-icon size="32" color="#67c23a"><Shop /></el-icon>
          <span>蔬菜销售系统</span>
        </router-link>
      </div>
      
      <div class="register-form-wrapper">
        <h2>用户注册</h2>
        <p class="subtitle">创建您的账户，享受新鲜蔬菜</p>
        
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          size="large"
          label-position="top"
          @submit.prevent="handleRegister"
        >
          <el-form-item label="用户名" prop="loginAccount">
            <el-input
              v-model="form.loginAccount"
              placeholder="请输入用户名"
              prefix-icon="User"
            />
          </el-form-item>
          
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          
          <el-form-item label="昵称" prop="userName">
            <el-input
              v-model="form.userName"
              placeholder="请输入昵称"
              prefix-icon="UserFilled"
            />
          </el-form-item>
          
          <el-form-item label="手机号" prop="tel">
            <el-input
              v-model="form.tel"
              placeholder="请输入手机号"
              prefix-icon="Phone"
            />
          </el-form-item>
          
          <el-form-item label="邮箱" prop="email">
            <el-input
              v-model="form.email"
              placeholder="请输入邮箱（选填）"
              prefix-icon="Message"
            />
          </el-form-item>
          
          <el-form-item label="用户类型" prop="userType">
            <el-radio-group v-model="form.userType">
              <el-radio :value="1">采购商</el-radio>
              <el-radio :value="2">供应商</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item>
            <el-checkbox v-model="form.agree">
              我已阅读并同意
              <el-link type="primary" underline="never">服务条款</el-link>
              和
              <el-link type="primary" underline="never">隐私政策</el-link>
            </el-checkbox>
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              class="register-btn"
              :loading="loading"
              :disabled="!form.agree"
              @click="handleRegister"
            >
              注册
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="register-footer">
          <span>已有账号？</span>
          <router-link to="/login">立即登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  loginAccount: '',
  password: '',
  confirmPassword: '',
  userName: '',
  tel: '',
  email: '',
  userType: 1,
  agree: false
})

// 验证确认密码
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 验证手机号
const validatePhone = (rule, value, callback) => {
  const phoneReg = /^1[3-9]\d{9}$/
  if (!phoneReg.test(value)) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}

const rules = {
  loginAccount: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  userName: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  tel: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { validator: validatePhone, trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  userType: [
    { required: true, message: '请选择用户类型', trigger: 'change' }
  ]
}

// 注册
const handleRegister = async () => {
  if (!form.agree) {
    ElMessage.warning('请先同意服务条款和隐私政策')
    return
  }
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    const { confirmPassword, agree, ...data } = form
    await register(data)
    
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.register-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
  padding: 40px 20px;
}

.register-container {
  max-width: 480px;
  margin: 0 auto;
}

.register-header {
  text-align: center;
  margin-bottom: 32px;
  
  .logo {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    font-size: 20px;
    font-weight: 600;
    color: #333;
  }
}

.register-form-wrapper {
  background: #fff;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  
  h2 {
    font-size: 24px;
    font-weight: 600;
    color: #333;
    margin-bottom: 8px;
    text-align: center;
  }
  
  .subtitle {
    text-align: center;
    color: #999;
    margin-bottom: 32px;
    font-size: 14px;
  }
}

.register-btn {
  width: 100%;
}

.register-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: #666;
  
  a {
    color: #67c23a;
    margin-left: 4px;
    
    &:hover {
      text-decoration: underline;
    }
  }
}
</style>
