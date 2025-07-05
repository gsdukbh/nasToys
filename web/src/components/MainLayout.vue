<template>
  <div class="main-layout">
    <aside class="sidebar">
      <div class="logo">
        <h2>我的应用</h2>
      </div>
      <nav>
        <ul>
          <li><router-link to="/dashboard">仪表盘</router-link></li>
          <li><router-link to="/devices">设备列表</router-link></li>
          <!-- 在这里添加更多菜单项 -->
        </ul>
      </nav>
      <div class="logout-section">
        <button @click="handleLogout">退出登录</button>
      </div>
    </aside>
    <main class="content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

const handleLogout = () => {
  localStorage.removeItem('token');
  router.push('/');
};
</script>

<style scoped>
:global(*) {
  box-sizing: border-box;
}

.main-layout {
  display: flex;
  height: 100vh; /* 使用 height 代替 min-height */
  background-color: #f3f4f6;
}

.sidebar {
  width: 220px;
  background-color: #111827; /* 深灰色背景 */
  color: white;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  overflow: hidden; /* 防止整个侧边栏滚动 */
}

.logo {
  text-align: center;
  margin-bottom: 2rem;
  font-size: 1.25rem;
  font-weight: bold;
}

.sidebar nav {
  flex-grow: 1; /* 让导航区域占据可用空间 */
  overflow-y: auto; /* 当内容溢出时显示滚动条 */
}

.sidebar nav ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sidebar nav li a {
  color: #d1d5db; /* 浅灰色文字 */
  text-decoration: none;
  display: block;
  padding: 0.8rem 1rem;
  border-radius: 0.5rem;
  transition: background-color 0.2s, color 0.2s;
  margin-bottom: 0.5rem;
}

.sidebar nav li a:hover,
.sidebar nav li a.router-link-exact-active {
  background-color: #3b82f6; /* 蓝色高亮 */
  color: white;
}

.logout-section {
  margin-top: auto; /* 将退出按钮推到底部 */
}

.logout-section button {
  width: 100%;
  padding: 0.75rem 1rem;
  font-size: 1rem;
  font-weight: 600;
  color: white;
  background-color: #ef4444; /* 红色背景 */
  border: none;
  border-radius: 0.5rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.logout-section button:hover {
  background-color: #dc2626; /* 悬停时颜色变深 */
}

.content {
  flex-grow: 1;
  padding: 2rem;
}
</style>
