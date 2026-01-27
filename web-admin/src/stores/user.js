import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, logout as logoutApi, getUserInfo } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
  
  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.userType === 0)
  const username = computed(() => userInfo.value?.userName || userInfo.value?.loginAccount || '')
  const avatar = computed(() => userInfo.value?.avatar || '/img/avatar.jpg')
  
  // 登录
  async function login(loginData) {
    const res = await loginApi(loginData)
    if (res.data?.token) {
      token.value = res.data.token
      localStorage.setItem('token', res.data.token)
      // 获取用户信息
      await fetchUserInfo()
    }
    return res
  }
  
  // 获取用户信息
  async function fetchUserInfo() {
    try {
      const res = await getUserInfo()
      if (res.data) {
        userInfo.value = res.data
        localStorage.setItem('userInfo', JSON.stringify(res.data))
      }
      return res
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    }
  }
  
  // 退出登录
  async function logout() {
    try {
      await logoutApi()
    } catch (error) {
      console.error('退出登录失败:', error)
    } finally {
      token.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }
  
  // 更新用户信息
  function updateUserInfo(info) {
    userInfo.value = { ...userInfo.value, ...info }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }
  
  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    username,
    avatar,
    login,
    logout,
    fetchUserInfo,
    updateUserInfo
  }
})
