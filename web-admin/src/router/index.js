import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('@/views/category/index.vue'),
        meta: { title: '商品分类' }
      },
      {
        path: 'products',
        name: 'Products',
        component: () => import('@/views/products/index.vue'),
        meta: { title: '商品列表' }
      },
      {
        path: 'product/:id',
        name: 'ProductDetail',
        component: () => import('@/views/products/detail.vue'),
        meta: { title: '商品详情' }
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('@/views/cart/index.vue'),
        meta: { title: '购物车', requiresAuth: true }
      },
      {
        path: 'checkout',
        name: 'Checkout',
        component: () => import('@/views/order/checkout.vue'),
        meta: { title: '确认订单', requiresAuth: true }
      },
      {
        path: 'user',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '个人中心', requiresAuth: true },
        children: [
          {
            path: '',
            name: 'User',
            redirect: '/user/profile'
          },
          {
            path: 'profile',
            name: 'UserProfile',
            component: () => import('@/views/user/profile.vue'),
            meta: { title: '个人信息' }
          },
          {
            path: 'address',
            name: 'UserAddress',
            component: () => import('@/views/user/address.vue'),
            meta: { title: '收货地址' }
          },
          {
            path: 'favorites',
            name: 'UserFavorites',
            component: () => import('@/views/user/favorites.vue'),
            meta: { title: '我的收藏' }
          },
          {
            path: 'orders',
            name: 'UserOrders',
            component: () => import('@/views/order/index.vue'),
            meta: { title: '我的订单', requiresAuth: true }
          },
          {
            path: 'orders/:id',
            name: 'UserOrderDetail',
            component: () => import('@/views/order/detail.vue'),
            meta: { title: '订单详情', requiresAuth: true }
          },
          {
            path: 'password',
            name: 'UserPassword',
            component: () => import('@/views/user/password.vue'),
            meta: { title: '修改密码' }
          }
        ]
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/login/register.vue'),
    meta: { title: '注册' }
  },
  // 管理后台路由
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        redirect: '/admin/dashboard'
      },
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/dashboard.vue'),
        meta: { title: '控制台' }
      },
      {
        path: 'vegetables',
        name: 'AdminVegetables',
        component: () => import('@/views/admin/vegetables/index.vue'),
        meta: { title: '商品管理' }
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: () => import('@/views/admin/categories/index.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/orders/index.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/users/index.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'rotations',
        name: 'AdminRotations',
        component: () => import('@/views/admin/rotations/index.vue'),
        meta: { title: '轮播图管理' }
      },
      {
        path: 'delivery',
        name: 'AdminDelivery',
        component: () => import('@/views/admin/delivery/index.vue'),
        meta: { title: '配送员管理' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 蔬菜销售系统` : '蔬菜销售系统'
  
  const userStore = useUserStore()
  
  // 需要登录的页面
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }
  
  // 需要管理员权限的页面
  if (to.meta.requiresAdmin && userStore.userInfo?.userType !== 0) {
    next({ path: '/home' })
    return
  }
  
  next()
})

export default router
