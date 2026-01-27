import request from '@/utils/request'

// 用户登录
export function login(data) {
  return request({
    url: '/login',
    method: 'post',
    data
  })
}

// 用户注册
export function register(data) {
  return request({
    url: '/login/register',
    method: 'post',
    data
  })
}

// 退出登录
export function logout() {
  return request({
    url: '/login/logout',
    method: 'get'
  })
}

// 获取当前用户信息
export function getUserInfo() {
  return request({
    url: '/user/getUserInfo',
    method: 'get'
  })
}

// 修改个人信息
export function updateUserInfo(data) {
  return request({
    url: '/user/setUserInfo',
    method: 'post',
    data
  })
}

// 修改密码
export function changePassword(data) {
  return request({
    url: '/user/changePassword',
    method: 'post',
    data
  })
}

// 忘记密码-获取验证码
export function getEmailCode(email) {
  return request({
    url: '/user/getEmailReg',
    method: 'get',
    params: { email }
  })
}

// 忘记密码-重置密码
export function forgetPassword(data) {
  return request({
    url: '/user/forgetPassword',
    method: 'post',
    data
  })
}
