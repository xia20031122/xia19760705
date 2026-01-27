import request from '@/utils/request'

// 获取收货地址列表
export function getAddressList(data = {}) {
  return request({
    url: '/address/getAddressList',
    method: 'get'
  })
}

// 根据ID获取地址
export function getAddressById(id) {
  return request({
    url: '/address/getApeOrderAddressById',
    method: 'get',
    params: { id }
  })
}

// 保存地址
export function saveAddress(data) {
  return request({
    url: '/address/saveApeOrderAddress',
    method: 'post',
    data: {
      name: data.name,
      tel: data.tel,
      address: data.address,
      first: data.first
    }
  })
}

// 编辑地址
export function editAddress(data) {
  return request({
    url: '/address/editApeOrderAddress',
    method: 'post',
    data: {
      id: data.id,
      name: data.name,
      tel: data.tel,
      address: data.address,
      first: data.first
    }
  })
}

// 删除地址
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
