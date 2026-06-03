import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/',
    component: () => import('../layouts/PublicLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('../views/HomeView.vue') },
      { path: 'login', name: 'Login', component: () => import('../views/LoginView.vue') },
      { path: 'register', name: 'Register', component: () => import('../views/RegisterView.vue') },
      { path: 'spots', name: 'SpotList', component: () => import('../views/SpotListView.vue') },
      { path: 'spots/:id', name: 'SpotDetail', component: () => import('../views/SpotDetailView.vue') },
      { path: 'routes', name: 'RouteList', component: () => import('../views/RouteListView.vue') },
      { path: 'routes/:id', name: 'RouteDetail', component: () => import('../views/RouteDetailView.vue') },
      { path: 'announcements', name: 'AnnouncementList', component: () => import('../views/AnnouncementListView.vue') },
      { path: 'announcements/:id', name: 'AnnouncementDetail', component: () => import('../views/AnnouncementDetailView.vue') },
      { path: 'my/orders', name: 'MyOrders', component: () => import('../views/MyOrdersView.vue'), meta: { requiresAuth: true } },
      { path: 'my/favorites', name: 'MyFavorites', component: () => import('../views/MyFavoritesView.vue'), meta: { requiresAuth: true } },
      { path: 'profile', name: 'Profile', component: () => import('../views/ProfileView.vue'), meta: { requiresAuth: true } }
    ]
  },
  {
    path: '/admin',
    component: () => import('../layouts/AdminLayout.vue'),
    meta: { requiresAdmin: true },
    children: [
      { path: 'dashboard', name: 'AdminDashboard', component: () => import('../views/admin/DashboardView.vue') },
      { path: 'spots', name: 'AdminSpots', component: () => import('../views/admin/SpotManageView.vue') },
      { path: 'routes', name: 'AdminRoutes', component: () => import('../views/admin/RouteManageView.vue') },
      { path: 'orders', name: 'AdminOrders', component: () => import('../views/admin/OrderManageView.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const auth = useAuthStore()

  if (to.matched.some(record => record.meta.requiresAdmin)) {
    if (!auth.isLoggedIn()) {
      next('/login')
    } else if (!auth.isAdmin()) {
      next('/')
    } else {
      next()
    }
  } else if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!auth.isLoggedIn()) {
      next('/login')
    } else {
      next()
    }
  } else if (to.path === '/login' && auth.isLoggedIn()) {
    next(auth.isAdmin() ? '/admin/dashboard' : '/')
  } else {
    next()
  }
})

export default router
