<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import apiClient from '../api'; // 导入我们创建的 api 客户端

const router = useRouter();

// 创建响应式变量
const username = ref('');
const password = ref('');
const message = ref('');
const messageType = ref(''); // 'success' 或 'error'

// 定义登录处理函数
const handleLogin = async () => {
    message.value = '';
    messageType.value = '';

    if (username.value && password.value) {
        try {
            const response = await apiClient.post('/login', { // 使用 apiClient
                username: username.value,
                password: password.value,
            });
            
            // 假设返回的数据中包含 token
            if (response.data.data.token) {
            
                // 将 token 存储到 localStorage
                localStorage.setItem('token', response.data.data.token);
                // 跳转到 dashboard
                router.push('/dashboard');
            }

        } catch (error) {
            message.value = '登录失败，请检查您的凭据。';
            messageType.value = 'error';
            console.error('登录错误:', error);
        }
    } else {
        message.value = '请输入用户名和密码。';
        messageType.value = 'error';
    }
    
};
</script>

<template>
  <div class="login-container">
    <div class="login-box">
      
      <div class="header">
        <h1>欢迎回来</h1>
        <p>请登录您的账户</p>
      </div>

      <form class="form" @submit.prevent="handleLogin">
        
        <div class="input-group">
          <input 
            type="text" 
            v-model="username"
            required
            placeholder="用户名"
            class="input-field">
        </div>

        <div class="input-group">
          <input 
            type="password" 
            v-model="password"
            required
            placeholder="密码"
            class="input-field">
        </div>

        <button type="submit" class="login-button">
          登 录
        </button>

        <div v-if="message" class="message" :class="messageType">
          {{ message }}
        </div>

      </form>
    </div>
  </div>
</template>

<style scoped>
/* 使用 scoped 确保样式只应用于此组件 */

/* 全局和容器样式 */
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background-color: #f3f4f6;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

.login-box {
  width: 100%;
  max-width: 400px;
  margin: 1rem;
  padding: 2rem;
  background-color: white;
  border-radius: 1rem; /* 圆角 */
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05); /* 阴影 */
}

/* 头部样式 */
.header {
  text-align: center;
  margin-bottom: 2rem;
}

.header h1 {
  font-size: 1.875rem; /* 30px */
  font-weight: bold;
  color: #111827;
}

.header p {
  margin-top: 0.5rem;
  font-size: 0.875rem; /* 14px */
  color: #4b5563;
}

/* 表单和输入框样式 */
.form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem; /* 元素之间的间距 */
}

.input-field {
  width: 100%;
  padding: 0.75rem 1rem;
  font-size: 1rem;
  color: #111827;
  background-color: #f3f4f6;
  border: 2px solid #e5e7eb;
  border-radius: 0.5rem; /* 圆角 */
  transition: border-color 0.2s;
  box-sizing: border-box; /* 确保 padding 不会影响宽度 */
}

.input-field:focus {
  outline: none;
  border-color: #3b82f6; /* 蓝色高亮 */
  background-color: white;
}

/* 按钮样式 */
.login-button {
  width: 100%;
  padding: 0.75rem 1rem;
  font-size: 1rem;
  font-weight: 600;
  color: white;
  background-color: #2563eb; /* 蓝色背景 */
  border: none;
  border-radius: 0.5rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.login-button:hover {
  background-color: #1d4ed8; /* 悬停时颜色变深 */
}

/* 消息提示样式 */
.message {
  text-align: center;
  padding: 0.75rem;
  border-radius: 0.5rem;
  font-size: 0.875rem;
}

.message.success {
  background-color: #d1fae5;
  color: #065f46;
}

.message.error {
  background-color: #fee2e2;
  color: #991b1b;
}
</style>
