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

// 根据ID获取轮播图
export function getRotationById(id) {
  return request({
    url: '/rotation/getApeRotationById',
    method: 'get',
    params: { id }
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
