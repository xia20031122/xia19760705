import request from '@/utils/request'

// 获取个性化推荐
export function getPersonalized(limit = 8) {
  return request({
    url: '/recommend/personalized',
    method: 'get',
    params: { limit }
  })
}

// 获取热门推荐
export function getHotRecommend(limit = 8) {
  return request({
    url: '/recommend/hot',
    method: 'get',
    params: { limit }
  })
}

// 获取新品推荐
export function getNewArrival(limit = 8) {
  return request({
    url: '/recommend/newArrival',
    method: 'get',
    params: { limit }
  })
}

// 获取相似商品推荐
export function getSimilar(vegetableId, limit = 8) {
  return request({
    url: '/recommend/similar',
    method: 'get',
    params: { vegetableId, limit }
  })
}

// 获取相似商品推荐 (别名)
export function getSimilarRecommend(vegetableId, limit = 8) {
  return getSimilar(vegetableId, limit)
}

// 获取猜你喜欢
export function getGuessYouLike(limit = 8) {
  return request({
    url: '/recommend/guessYouLike',
    method: 'get',
    params: { limit }
  })
}

// 记录用户行为
export function recordBehavior(data) {
  return request({
    url: '/recommend/recordBehavior',
    method: 'post',
    data
  })
}
