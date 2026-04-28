import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null'),
    role: localStorage.getItem('role') || ''
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    isStudent: (state) => state.role === 'STUDENT',
    isCompany: (state) => state.role === 'COMPANY',
    isCollege: (state) => state.role === 'COLLEGE',
    isAdmin: (state) => state.role === 'ADMIN'
  },
  actions: {
    setLoginInfo(token, userInfo, role) {
      this.token = token
      this.userInfo = userInfo
      this.role = role
      localStorage.setItem('token', token)
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
      localStorage.setItem('role', role)
    },
    logout() {
      this.token = ''
      this.userInfo = null
      this.role = ''
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      localStorage.removeItem('role')
    }
  }
})
