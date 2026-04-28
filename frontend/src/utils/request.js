import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截器 - 自动附加token
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器 - 统一处理错误
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res
    }
    if (res.code === 401) {
      const token = localStorage.getItem('token')
      if (token) {
        // 已登录状态收到401：token过期或无效，清除并跳登录
        ElMessage.error(res.msg || '登录已过期，请重新登录')
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        localStorage.removeItem('role')
        router.push('/login')
      }
      // 未登录状态收到401：静默处理，不弹错不跳转
      return Promise.reject(new Error(res.msg || '未登录'))
    }
    ElMessage.error(res.msg || '操作失败')
    return Promise.reject(new Error(res.msg))
  },
  error => {
    if (error.response?.status === 401) {
      const token = localStorage.getItem('token')
      if (token) {
        ElMessage.error('登录已过期，请重新登录')
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        localStorage.removeItem('role')
        router.push('/login')
      }
      return Promise.reject(error)
    }
    ElMessage.error(error.message || '网络错误，请稍后重试')
    return Promise.reject(error)
  }
)

export default request

