import axios from 'axios';

// 创建一个 axios 实例
const apiClient = axios.create({
  baseURL: '/api', // 你的 API 基础 URL
});

// 添加请求拦截器
apiClient.interceptors.request.use(
  (config) => {
    // 从 localStorage 获取 token
    const token = localStorage.getItem('token');
    
    // 如果 token 存在，则添加到请求头中
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    // 对请求错误做些什么
    return Promise.reject(error);
  }
);

export default apiClient;
