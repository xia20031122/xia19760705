import request from '@/utils/request'

// 分页获取蔬菜列表
export function getVegetablePage(data) {
  return request({
    url: '/vegetable/getApeVegetablePage',
    method: 'post',
    data
  })
}

// 获取首页推荐蔬菜
export function getVegetableIndex() {
  return request({
    url: '/vegetable/getApeVegetableIndex',
    method: 'get'
  })
}

// 获取所有蔬菜列表
export function getVegetableList() {
  return request({
    url: '/vegetable/getApeVegetableList',
    method: 'get'
  })
}

// 根据ID获取蔬菜详情
export function getVegetableById(id) {
  return request({
    url: '/vegetable/getApeVegetableById',
    method: 'get',
    params: { id }
  })
}

// 保存蔬菜
export function saveVegetable(data) {
  return request({
    url: '/vegetable/saveApeVegetable',
    method: 'post',
    data
  })
}

// 编辑蔬菜
export function editVegetable(data) {
  return request({
    url: '/vegetable/editApeVegetable',
    method: 'post',
    data
  })
}

// 删除蔬菜
export function removeVegetable(ids) {
  return request({
    url: '/vegetable/removeApeVegetable',
    method: 'get',
    params: { ids }
  })
}

// 供应商提交商品
export function submitVegetable(data) {
  return request({
    url: '/vegetable/submitVegetable',
    method: 'post',
    data
  })
}

// 获取待审核商品
export function getPendingAuditPage(data) {
  return request({
    url: '/vegetable/getPendingAuditPage',
    method: 'post',
    data
  })
}

// 审核商品
export function auditVegetable(data) {
  return request({
    url: '/vegetable/auditVegetable',
    method: 'post',
    data
  })
}

// 获取审核统计
export function getAuditStats() {
  return request({
    url: '/vegetable/getAuditStats',
    method: 'get'
  })
}

// 获取蔬菜分类列表
export function getVegetableTypeList() {
  return request({
    url: '/type/getApeVegetableTypeList',
    method: 'get'
  })
}

// 分页获取蔬菜分类
export function getVegetableTypePage(data) {
  return request({
    url: '/type/getApeVegetableTypePage',
    method: 'post',
    data
  })
}

// 保存蔬菜分类
export function saveVegetableType(data) {
  return request({
    url: '/type/saveApeVegetableType',
    method: 'post',
    data
  })
}

// 编辑蔬菜分类
export function editVegetableType(data) {
  return request({
    url: '/type/editApeVegetableType',
    method: 'post',
    data
  })
}

// 删除蔬菜分类
export function removeVegetableType(ids) {
  return request({
    url: '/type/removeApeVegetableType',
    method: 'get',
    params: { ids }
  })
}
