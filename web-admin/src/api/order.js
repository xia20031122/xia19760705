import request from '@/utils/request'

// 分页获取订单
export function getOrderPage(data) {
  return request({
    url: '/order/getApeVegetableOrderPage',
    method: 'post',
    data
  })
}

// 根据ID获取订单详情
export function getOrderById(id) {
  return request({
    url: '/order/getApeVegetableOrderById',
    method: 'get',
    params: { id }
  })
}

// 创建订单（单品下单）
export function saveOrder(data) {
  return request({
    url: '/order/saveApeVegetableOrder',
    method: 'post',
    data
  })
}

// 从购物车创建订单
export function saveCarOrder(data) {
  return request({
    url: '/order/saveApeVegetableCarOrder',
    method: 'post',
    data
  })
}

// 支付订单
export function payOrder(data) {
  return request({
    url: '/order/payOrder',
    method: 'post',
    data
  })
}

// 发货
export function shipOrder(data) {
  return request({
    url: '/order/shipOrder',
    method: 'post',
    data
  })
}

// 确认收货
export function confirmReceive(data) {
  return request({
    url: '/order/confirmReceive',
    method: 'post',
    data
  })
}

// 取消订单
export function cancelOrder(data) {
  return request({
    url: '/order/cancelOrder',
    method: 'post',
    data
  })
}

// 获取订单统计
export function getOrderStats() {
  return request({
    url: '/order/getOrderStats',
    method: 'get'
  })
}

// 编辑订单
export function editOrder(data) {
  return request({
    url: '/order/editApeVegetableOrder',
    method: 'post',
    data
  })
}

// 删除订单
export function removeOrder(ids) {
  return request({
    url: '/order/removeApeVegetableOrder',
    method: 'get',
    params: { ids }
  })
}
