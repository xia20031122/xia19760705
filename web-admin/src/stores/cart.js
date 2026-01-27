import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getCartList, addToCart as addToCartApi, editCart, removeCart } from '@/api/cart'
import { ElMessage } from 'element-plus'

export const useCartStore = defineStore('cart', () => {
  // 状态
  const cartList = ref([])
  const loading = ref(false)
  const selectedIds = ref([])
  
  // 计算属性
  const cartCount = computed(() => cartList.value.reduce((sum, item) => sum + item.num, 0))
  const selectedItems = computed(() => cartList.value.filter(item => selectedIds.value.includes(item.id)))
  const selectedTotal = computed(() => {
    return selectedItems.value.reduce((sum, item) => sum + item.price * item.num, 0)
  })
  const isAllSelected = computed(() => {
    return cartList.value.length > 0 && selectedIds.value.length === cartList.value.length
  })
  
  // 获取购物车列表
  async function fetchCartList() {
    loading.value = true
    try {
      const res = await getCartList()
      cartList.value = res.data || []
    } catch (error) {
      console.error('获取购物车失败:', error)
    } finally {
      loading.value = false
    }
  }
  
  // 添加到购物车
  async function addToCart(vegetableId, num = 1) {
    try {
      await addToCartApi({ vegetableId, num })
      ElMessage.success('已添加到购物车')
      await fetchCartList()
    } catch (error) {
      console.error('添加购物车失败:', error)
      throw error
    }
  }
  
  // 更新购物车商品数量
  async function updateCartItem(item) {
    try {
      await editCart(item)
      await fetchCartList()
    } catch (error) {
      console.error('更新购物车失败:', error)
      throw error
    }
  }
  
  // 删除购物车商品
  async function removeCartItem(ids) {
    try {
      await removeCart(ids)
      ElMessage.success('删除成功')
      // 更新选中状态
      selectedIds.value = selectedIds.value.filter(id => !ids.split(',').includes(id))
      await fetchCartList()
    } catch (error) {
      console.error('删除购物车失败:', error)
      throw error
    }
  }
  
  // 选择/取消选择
  function toggleSelect(id) {
    const index = selectedIds.value.indexOf(id)
    if (index > -1) {
      selectedIds.value.splice(index, 1)
    } else {
      selectedIds.value.push(id)
    }
  }
  
  // 全选/取消全选
  function toggleSelectAll() {
    if (isAllSelected.value) {
      selectedIds.value = []
    } else {
      selectedIds.value = cartList.value.map(item => item.id)
    }
  }
  
  // 清空选择
  function clearSelection() {
    selectedIds.value = []
  }
  
  return {
    cartList,
    loading,
    selectedIds,
    cartCount,
    selectedItems,
    selectedTotal,
    isAllSelected,
    fetchCartList,
    addToCart,
    updateCartItem,
    removeCartItem,
    toggleSelect,
    toggleSelectAll,
    clearSelection
  }
})
