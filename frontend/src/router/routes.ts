import type { RouteRecordRaw } from 'vue-router'

/**
 * 路由表，与《06-前端设计》第 3 章页面清单一一对应。
 * 布局作为父路由，页面组件全部懒加载。
 */
export const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('@/layouts/DefaultLayout.vue'),
    children: [
      {
        path: '',
        name: 'home',
        component: () => import('@/views/home/Home.vue'),
        meta: { title: '首页' },
      },
      {
        path: 'products',
        name: 'products',
        component: () => import('@/views/product/ProductList.vue'),
        meta: { title: '商品列表' },
      },
      {
        path: 'products/:id',
        name: 'product-detail',
        component: () => import('@/views/product/ProductDetail.vue'),
        meta: { title: '商品详情' },
      },
      {
        path: 'cart',
        name: 'cart',
        component: () => import('@/views/cart/Cart.vue'),
        meta: { requiresAuth: true, title: '购物车' },
      },
      {
        path: 'order/confirm',
        name: 'order-confirm',
        component: () => import('@/views/order/OrderConfirm.vue'),
        meta: { requiresAuth: true, title: '确认订单' },
      },
      {
        path: 'orders',
        name: 'orders',
        component: () => import('@/views/order/OrderList.vue'),
        meta: { requiresAuth: true, title: '我的订单' },
      },
      {
        path: 'orders/:orderNo',
        name: 'order-detail',
        component: () => import('@/views/order/OrderDetail.vue'),
        meta: { requiresAuth: true, title: '订单详情' },
      },
      {
        path: 'subscriptions/plans',
        name: 'subscription-plans',
        component: () => import('@/views/subscription/SubscriptionPlans.vue'),
        meta: { title: '订阅计划' },
      },
      {
        path: 'notifications',
        name: 'notifications',
        component: () => import('@/views/notification/NotificationList.vue'),
        meta: { requiresAuth: true, title: '消息通知' },
      },
      {
        path: 'subscriptions/my',
        name: 'my-subscriptions',
        component: () => import('@/views/subscription/MySubscriptions.vue'),
        meta: { requiresAuth: true, title: '我的订阅' },
      },
      {
        path: 'user/profile',
        name: 'user-profile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { requiresAuth: true, title: '个人中心' },
      },
      {
        path: 'user/address',
        name: 'user-address',
        component: () => import('@/views/user/Address.vue'),
        meta: { requiresAuth: true, title: '收货地址' },
      },
      {
        path: 'user/preference',
        name: 'user-preference',
        component: () => import('@/views/user/Preference.vue'),
        meta: { requiresAuth: true, title: '咖啡偏好' },
      },
    ],
  },
  {
    path: '/login',
    component: () => import('@/layouts/BlankLayout.vue'),
    children: [
      {
        path: '',
        name: 'login',
        component: () => import('@/views/auth/Login.vue'),
        meta: { title: '登录' },
      },
    ],
  },
  {
    path: '/register',
    component: () => import('@/layouts/BlankLayout.vue'),
    children: [
      {
        path: '',
        name: 'register',
        component: () => import('@/views/auth/Register.vue'),
        meta: { title: '注册' },
      },
    ],
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true, title: '后台管理' },
    children: [
      {
        path: '',
        name: 'admin-dashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { requiresAuth: true, requiresAdmin: true, title: '数据看板' },
      },
      {
        path: 'products',
        name: 'admin-products',
        component: () => import('@/views/admin/ProductManage.vue'),
        meta: { requiresAuth: true, requiresAdmin: true, title: '商品管理' },
      },
      {
        path: 'inventory',
        name: 'admin-inventory',
        component: () => import('@/views/admin/InventoryManage.vue'),
        meta: { requiresAuth: true, requiresAdmin: true, title: '库存管理' },
      },
      {
        path: 'orders',
        name: 'admin-orders',
        component: () => import('@/views/admin/OrderManage.vue'),
        meta: { requiresAuth: true, requiresAdmin: true, title: '订单管理' },
      },
      {
        path: 'subscriptions',
        name: 'admin-subscriptions',
        component: () => import('@/views/admin/SubscriptionManage.vue'),
        meta: { requiresAuth: true, requiresAdmin: true, title: '订阅管理' },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('@/views/error/NotFound.vue'),
    meta: { title: '页面不存在' },
  },
]
