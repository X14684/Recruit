<template>
  <div class="public-layout">
    <!-- 顶部导航 -->
    <header class="top-nav">
      <div class="nav-inner">
        <router-link to="/home" class="nav-logo">
          <el-icon :size="24"><School /></el-icon>
          <span>校园招聘</span>
        </router-link>
        <nav class="nav-links">
          <router-link to="/home">首页</router-link>
          <router-link to="/jobs">找工作</router-link>
          <router-link to="/talks">宣讲会</router-link>
        </nav>
        <div class="nav-actions">
          <template v-if="userStore.isLoggedIn">
            <el-dropdown @command="handleCommand">
              <span class="user-btn">
                <el-avatar :size="28" :src="userStore.userInfo?.avatar">
                  {{ userStore.userInfo?.username?.charAt(0)?.toUpperCase() }}
                </el-avatar>
                <span>{{ userStore.userInfo?.realName || userStore.userInfo?.companyName || userStore.userInfo?.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="dashboard">进入控制台</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/login"><el-button>登录</el-button></router-link>
            <router-link to="/register"><el-button type="primary">注册</el-button></router-link>
          </template>
        </div>
      </div>
    </header>

    <main class="main-content">
      <router-view />
    </main>

    <!-- 底部 -->
    <footer class="footer">
      <p>© 2024 校园求职招聘系统 All Rights Reserved</p>
    </footer>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/home')
  } else if (command === 'dashboard') {
    const roleMap = {
      STUDENT: '/student/dashboard',
      COMPANY: '/company/dashboard',
      COLLEGE: '/college/dashboard',
      ADMIN: '/admin/dashboard'
    }
    router.push(roleMap[userStore.role] || '/home')
  }
}
</script>

<style scoped>
.public-layout { min-height: 100vh; display: flex; flex-direction: column; background: #f5f7fa; }
.top-nav {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}
.nav-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  height: 60px;
  display: flex;
  align-items: center;
  gap: 32px;
}
.nav-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 700;
  color: #409eff;
  text-decoration: none;
}
.nav-links { display: flex; gap: 24px; flex: 1; }
.nav-links a {
  color: #606266;
  text-decoration: none;
  font-size: 15px;
  transition: color 0.2s;
}
.nav-links a:hover, .nav-links a.router-link-active { color: #409eff; font-weight: 600; }
.nav-actions { display: flex; align-items: center; gap: 12px; }
.user-btn { display: flex; align-items: center; gap: 6px; cursor: pointer; color: #303133; }
.main-content { flex: 1; }
.footer { background: #303133; color: #909399; text-align: center; padding: 20px; font-size: 13px; }
</style>
