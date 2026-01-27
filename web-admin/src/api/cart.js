import request from '@/utils/request'

// 获取购物车列表
export function getCartList(data = {}) {
  return request({
    url: '/car/getApeCarList',
    method: 'post',
    data
  })
}

// 根据ID获取购物车项
export function getCartById(id) {
  return request({
    url: '/car/getApeCarById',
    method: 'get',
    params: { id }
  })
}

// 添加到购物车
export function addToCart(data) {
  return request({
    url: '/car/saveApeCar',
    method: 'post',
    data
  })
}

// 编辑购物车（修改数量）
export function editCart(data) {
  return request({
    url: '/car/editApeCar',
    method: 'post',
    data
  })
}

// 删除购物车项
export function removeCart(ids) {
  return request({
    url: '/car/removeApeCar',
    method: 'get',
    params: { ids }
  })
}
