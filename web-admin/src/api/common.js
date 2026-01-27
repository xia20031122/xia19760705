import request from '@/utils/request'

// 获取轮播图列表
export function getRotationList() {
  return request({
    url: '/rotation/getApeRotationList',
    method: 'get'
  })
}

// 分页获取轮播图
export function getRotationPage(data) {
  return request({
    url: '/rotation/getApeRotationPage',
    method: 'post',
    data
  })
}

// 保存轮播图
export function saveRotation(data) {
  return request({
    url: '/rotation/saveApeRotation',
    method: 'post',
    data
  })
}

// 编辑轮播图
export function editRotation(data) {
  return request({
    url: '/rotation/editApeRotation',
    method: 'post',
    data
  })
}

// 删除轮播图
export function removeRotation(ids) {
  return request({
    url: '/rotation/removeApeRotation',
    method: 'get',
    params: { ids }
  })
}

// 获取配送员列表
export function getDeliveryList() {
  return request({
    url: '/delivery/getApeDeliveryList',
    method: 'get'
  })
}

// 分页获取配送员
export function getDeliveryPage(data) {
  return request({
    url: '/delivery/getApeDeliveryPage',
    method: 'post',
    data
  })
}

// 保存配送员
export function saveDelivery(data) {
  return request({
    url: '/delivery/saveApeDelivery',
    method: 'post',
    data
  })
}

// 编辑配送员
export function editDelivery(data) {
  return request({
    url: '/delivery/editApeDelivery',
    method: 'post',
    data
  })
}

// 删除配送员
export function removeDelivery(ids) {
  return request({
    url: '/delivery/removeApeDelivery',
    method: 'get',
    params: { ids }
  })
}

// 文件上传
export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/common/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取收藏列表
export function getFavorList(data) {
  return request({
    url: '/favor/getApeVegetableFavorList',
    method: 'post',
    data
  })
}

// 添加收藏
export function addFavor(data) {
  return request({
    url: '/favor/saveApeVegetableFavor',
    method: 'post',
    data
  })
}

// 取消收藏
export function removeFavor(ids) {
  return request({
    url: '/favor/removeApeVegetableFavor',
    method: 'get',
    params: { ids }
  })
}

// 检查是否已收藏
export function checkFavor(vegetableId) {
  return request({
    url: '/favor/checkFavor',
    method: 'get',
    params: { vegetableId }
  })
}

// 获取收货地址列表
export function getAddressList(data = {}) {
  return request({
    // 统一走新接口（GET /address/getAddressList），后端仍兼容旧接口但这里不再依赖
    url: '/address/getAddressList',
    method: 'get'
  })
}

// 保存收货地址
export function saveAddress(data) {
  return request({
    url: '/address/saveApeOrderAddress',
    method: 'post',
    data
  })
}

// 编辑收货地址
export function editAddress(data) {
  return request({
    url: '/address/editApeOrderAddress',
    method: 'post',
    data
  })
}

// 删除收货地址
export function removeAddress(ids) {
  return request({
    url: '/address/removeApeOrderAddress',
    method: 'get',
    params: { ids }
  })
}

// 设为默认地址
export function setDefaultAddress(id) {
  return request({
    url: '/address/setDefault',
    method: 'get',
    params: { id }
  })
}
