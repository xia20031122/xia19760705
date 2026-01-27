import request from '@/utils/request'

// 登录
export function login(data) {
  return request({
    url: '/login',
    method: 'post',
    data
  })
}

// 注册
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

// 分页查询用户
export function getUserPage(data) {
  return request({
    url: '/user/getUserPage',
    method: 'post',
    data
  })
}

// 根据ID获取用户
export function getUserById(id) {
  return request({
    url: '/user/getUserById',
    method: 'get',
    params: { id }
  })
}

// 新增用户
export function saveUser(data) {
  return request({
    url: '/user/saveUser',
    method: 'post',
    data
  })
}

// 编辑用户
export function editUser(data) {
  return request({
    url: '/user/editUser',
    method: 'post',
    data
  })
}

// 删除用户
export function removeUser(ids) {
  return request({
    url: '/user/removeUser',
    method: 'get',
    params: { ids }
  })
}

// 重置密码
export function resetPassword(data) {
  return request({
    url: '/user/resetPassword',
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

// 忘记密码-重置
export function forgetPassword(data) {
  return request({
    url: '/user/forgetPassword',
    method: 'post',
    data
  })
}

// 获取收藏列表
export function getFavoriteList(data = {}) {
  return request({
    url: '/favor/getApeVegetableFavorList',
    method: 'post',
    data
  })
}

// 添加收藏
export function addFavorite(data) {
  return request({
    url: '/favor/saveApeVegetableFavor',
    method: 'post',
    data
  })
}

// 删除收藏
export function removeFavorite(ids) {
  return request({
    url: '/favor/removeApeVegetableFavor',
    method: 'get',
    params: { ids }
  })
}

// 检查是否已收藏
export function checkFavorite(vegetableId) {
  return request({
    url: '/favor/checkFavorite',
    method: 'get',
    params: { vegetableId }
  })
}
