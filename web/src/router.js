import { createRouter, createWebHistory } from 'vue-router';
import Login from './components/Login.vue';
import MainLayout from './components/MainLayout.vue';
import Dashboard from './components/Dashboard.vue';
import DeviceList from './components/DeviceList.vue';

const routes = [
  {
    path: '/',
    name: 'Login',
    component: Login,
  },
  {
    path: '/',
    component: MainLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
      },
      {
        path: 'devices',
        name: 'DeviceList',
        component: DeviceList,
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 全局前置守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  
  if (to.matched.some(record => record.meta.requiresAuth) && !token) {
    // 如果路由需要认证但用户未登录，则重定向到登录页
    next({ name: 'Login' });
  } else {
    next(); // 否则，正常导航
  }
});

export default router;
