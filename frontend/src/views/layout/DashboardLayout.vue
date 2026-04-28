<template>
  <div class="dashboard-layout">
    <!-- 背景装饰光球 -->
    <div class="bg-orb bg-orb-1"></div>
    <div class="bg-orb bg-orb-2"></div>

    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed }">
      <!-- 顶部发光条 -->
      <div class="sidebar-glow-line"></div>

      <!-- Logo区域 -->
      <div class="sidebar-logo">
        <div class="logo-icon-wrapper">
          <div class="logo-icon">
            <el-icon :size="22" color="#fff"><School /></el-icon>
          </div>
          <!-- Logo光环动画 -->
          <div class="logo-ring"></div>
        </div>
        <Transition name="fade-slide">
          <div v-if="!collapsed" class="logo-text-wrap">
            <span class="logo-title">校园招聘</span>
            <span class="logo-subtitle">Campus Recruit</span>
          </div>
        </Transition>
      </div>

      <!-- 分隔线 -->
      <div v-if="!collapsed" class="sidebar-divider">
        <div class="divider-line"></div>
      </div>

      <!-- 导航菜单 -->
      <el-scrollbar class="sidebar-scroll">
        <nav class="nav-menu">
          <div
            v-for="(item, index) in menuItems"
            :key="item.path"
            class="nav-item"
            :class="{ active: isActive(item.path) }"
            :style="{ '--delay': index * 0.04 + 's' }"
            @click="$router.push(item.path)"
          >
            <!-- 激活背景光效 -->
            <div v-if="isActive(item.path)" class="nav-active-bg"></div>
            
            <div class="nav-icon">
              <el-icon :size="19"><component :is="item.icon" /></el-icon>
            </div>
            <Transition name="fade-slide">
              <span v-if="!collapsed" class="nav-text">{{ item.title }}</span>
            </Transition>
            
            <!-- 激活指示器 -->
            <div v-if="isActive(item.path)" class="nav-active-indicator">
              <span class="indicator-dot"></span>
            </div>

            <!-- 快捷键提示 -->
            <div v-if="!collapsed && index < 4" class="nav-shortcut">{{ index + 1 }}</div>
          </div>
        </nav>
      </el-scrollbar>

      <!-- 底部用户区 -->
      <div class="sidebar-footer" v-if="!collapsed">
        <div class="user-card-glass">
          <div class="user-avatar-section">
            <div class="avatar-glow"></div>
            <el-avatar :size="40" :src="userStore.userInfo?.avatar" class="user-avatar">
              {{ userName.charAt(0).toUpperCase() }}
            </el-avatar>
            <div class="status-online"></div>
          </div>
          <div class="user-detail">
            <div class="user-name">{{ userName }}</div>
            <div class="user-role-row">
              <span class="role-badge-pill" :style="{ background: roleColor + '20', color: roleColor }">
                {{ roleLabel }}
              </span>
            </div>
          </div>
          <div class="user-actions">
            <button class="action-mini-btn" title="设置" @click.stop="goToSettings">
              <el-icon :size="14"><Setting /></el-icon>
            </button>
          </div>
        </div>
        
        <!-- 系统状态指示 -->
        <div class="sys-status-bar">
          <span class="sys-status-item">
            <span class="sys-dot green"></span>系统正常
          </span>
          <span class="sys-status-item">
            v2.0
          </span>
        </div>
      </div>
      
      <div v-else class="sidebar-footer-collapsed">
        <el-tooltip :content="userName + ' - ' + roleLabel" placement="right">
          <div class="collapsed-avatar-wrap">
            <el-avatar :size="34">{{ userName.charAt(0).toUpperCase() }}</el-avatar>
            <span class="online-pulse"></span>
          </div>
        </el-tooltip>
      </div>
    </aside>

    <!-- 右侧主体 -->
    <div class="main-wrapper">
      <!-- 顶栏 -->
      <header class="top-bar">
        <!-- 顶栏玻璃底层 -->
        <div class="top-bar-glass"></div>
        
        <!-- 顶部渐变线 -->
        <div class="top-accent-line"></div>

        <div class="top-bar-inner">
          <div class="top-left">
            <button class="collapse-btn" @click="collapsed = !collapsed" title="切换侧栏">
              <div class="btn-inner">
                <el-icon :size="16"><Fold v-if="!collapsed" /><Expand v-else /></el-icon>
              </div>
            </button>
            
            <!-- 面包屑 -->
            <div class="breadcrumb-area">
              <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                  <span class="breadcrumb-home">
                    <el-icon style="vertical-align: middle;"><HomeFilled /></el-icon>
                    首页
                  </span>
                </el-breadcrumb-item>
                <el-breadcrumb-item v-if="route.meta.title !== '首页'">
                  {{ route.meta.title }}
                </el-breadcrumb-item>
              </el-breadcrumb>
            </div>

            <!-- 页面标题（大屏显示） -->
            <div v-if="route.meta.title && route.meta.title !== '首页'" class="page-title-inline">
              <h2>{{ route.meta.title }}</h2>
            </div>
          </div>
          
          <div class="top-right">
            <!-- 搜索框（学生端） -->
            <div class="header-search" v-if="showSearch">
              <div class="search-icon-wrap">
                <el-icon :size="15" color="#94A3B8"><Search /></el-icon>
              </div>
              <input type="text" placeholder="搜索职位、企业..." class="search-input" />
              <kbd class="search-kbd">⌘K</kbd>
            </div>
            
            <!-- 全屏切换 -->
            <button class="icon-btn" title="全屏" @click="toggleFullscreen">
              <el-icon :size="17"><FullScreen /></el-icon>
            </button>
            
            <!-- 通知铃铛 -->
            <div class="notification-wrapper">
              <el-badge :value="3" :max="99" class="notification-btn">
                <button class="icon-btn notification-icon-btn">
                  <el-icon :size="17"><Bell /></el-icon>
                  <span class="notification-pulse"></span>
                </button>
              </el-badge>
              <!-- 通知下拉面板 -->
              <div class="notification-panel" v-show="showNotifications">
                <div class="panel-header">
                  <span>消息通知</span>
                  <a href="#">全部已读</a>
                </div>
                <div class="panel-list">
                  <div class="panel-item unread">
                    <div class="panel-icon blue"><el-icon><Message /></el-icon></div>
                    <div class="panel-content">
                      <p>新的投递申请待审核</p>
                      <span>2分钟前</span>
                    </div>
                  </div>
                  <div class="panel-item unread">
                    <div class="panel-icon green"><el-icon><CircleCheck /></el-icon></div>
                    <div class="panel-content">
                      <p>面试邀请已发送给学生</p>
                      <span>15分钟前</span>
                    </div>
                  </div>
                  <div class="panel-item">
                    <div class="panel-icon amber"><el-icon><Warning /></el-icon></div>
                    <div class="panel-content">
                      <p>职位审核通过通知</p>
                      <span>1小时前</span>
                    </div>
                  </div>
                </div>
                <div class="panel-footer">
                  <a href="#">查看全部消息 →</a>
                </div>
              </div>
            </div>

            <!-- 分隔线 -->
            <div class="top-divider-v"></div>

            <!-- 用户下拉 -->
            <el-dropdown trigger="click" @command="handleCommand">
              <div class="user-trigger">
                <div class="trigger-avatar-wrap">
                  <el-avatar :size="32" class="trigger-avatar">{{ userName.charAt(0).toUpperCase() }}</el-avatar>
                </div>
                <div class="trigger-info">
                  <span class="trigger-name">{{ userName }}</span>
                  <span class="trigger-role">{{ roleLabel }}</span>
                </div>
                <el-icon :size="12" color="#94A3B8" class="trigger-arrow"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu class="user-dropdown-menu">
                  <div class="dropdown-user-info">
                    <div class="dropdown-avatar-large">
                      <el-avatar :size="48">{{ userName.charAt(0).toUpperCase() }}</el-avatar>
                      <div class="avatar-status online"></div>
                    </div>
                    <div>
                      <div class="dropdown-name">{{ userName }}</div>
                      <div class="dropdown-role">{{ roleLabel }}</div>
                      <div class="dropdown-email">{{ userStore.userInfo?.email || '—' }}</div>
                    </div>
                  </div>
                  
                  <div class="dropdown-section-label">快捷操作</div>
                  
                  <el-dropdown-item command="home">
                    <div class="menu-item-icon" style="background:#EFF6FF;color:#3B82F6;">
                      <el-icon><HomeFilled /></el-icon>
                    </div>
                    返回首页
                    <span class="menu-arrow">→</span>
                  </el-dropdown-item>
                  
                  <el-dropdown-item command="profile">
                    <div class="menu-item-icon" style="background:#F0FDF4;color:#10B981;">
                      <el-icon><User /></el-icon>
                    </div>
                    个人设置
                    <span class="menu-arrow">→</span>
                  </el-dropdown-item>
                  
                  <el-dropdown-item divided command="logout">
                    <div class="menu-item-icon" style="background:#FEF2F2;color:#EF4444;">
                      <el-icon><SwitchButton /></el-icon>
                    </div>
                    退出登录
                    <span class="menu-arrow">→</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </header>

      <!-- 页面内容 -->
      <main class="page-main">
        <router-view v-slot="{ Component, route: currentRoute }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" :key="currentRoute.path" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const collapsed = ref(false)
const showNotifications = ref(false)

const showSearch = computed(() => userStore.role === 'STUDENT')

const userName = computed(() => {
  const u = userStore.userInfo
  return u?.realName || u?.companyName || u?.collegeName || u?.username || '用户'
})

const roleLabel = computed(() => {
  const map = { STUDENT: '在校学生', COMPANY: '企业HR', COLLEGE: '学院负责人', ADMIN: '系统管理员' }
  return map[userStore.role] || ''
})

// 角色颜色
const roleColor = computed(() => {
  const map = { STUDENT: '#3B82F6', COMPANY: '#10B981', COLLEGE: '#F59E0B', ADMIN: '#8B5CF6' }
  return map[userStore.role] || '#94A3B8'
})

const isActive = (path) => {
  if (path === route.path) return true
  return route.path.startsWith(path) && path !== '/' && path.length > 1
}

// 跳转到设置页面
const goToSettings = () => {
  const settingsMap = {
    STUDENT: '/student/profile',
    COMPANY: '/company/profile',
    COLLEGE: '/college/profile',
    ADMIN: '/admin/profile'
  }
  const target = settingsMap[userStore.role]
  if (target) {
    router.push(target)
  }
}

const studentMenus = [
  { path: '/student/dashboard', title: '我的主页', icon: 'HomeFilled' },
  { path: '/student/profile', title: '个人信息', icon: 'User' },
  { path: '/student/resume', title: '我的简历', icon: 'Document' },
  { path: '/student/applications', title: '投递记录', icon: 'List' },
  { path: '/jobs', title: '找工作', icon: 'Search' },
  { path: '/talks', title: '宣讲会', icon: 'Calendar' }
]
const companyMenus = [
  { path: '/company/dashboard', title: '企业主页', icon: 'HomeFilled' },
  { path: '/company/profile', title: '企业信息', icon: 'OfficeBuilding' },
  { path: '/company/jobs', title: '职位管理', icon: 'Briefcase' },
  { path: '/company/applications', title: '收到的投递', icon: 'Message' },
  { path: '/company/talks', title: '宣讲会', icon: 'Calendar' }
]
const collegeMenus = [
  { path: '/college/dashboard', title: '学院主页', icon: 'HomeFilled' },
  { path: '/college/students', title: '学生管理', icon: 'User' },
  { path: '/college/talks', title: '宣讲会审核', icon: 'Calendar' },
  { path: '/college/statistics', title: '就业统计', icon: 'TrendCharts' }
]
const adminMenus = [
  { path: '/admin/dashboard', title: '仪表盘', icon: 'DataAnalysis' },
  { path: '/admin/students', title: '学生管理', icon: 'User' },
  { path: '/admin/companies', title: '企业管理', icon: 'OfficeBuilding' },
  { path: '/admin/colleges', title: '学院管理', icon: 'School' },
  { path: '/admin/jobs', title: '职位审核', icon: 'Briefcase' },
  { path: '/admin/employment-statistics', title: '就业统计', icon: 'TrendCharts' }
]

const menuItems = computed(() => {
  const map = { STUDENT: studentMenus, COMPANY: companyMenus, COLLEGE: collegeMenus, ADMIN: adminMenus }
  return map[userStore.role] || []
})

const handleCommand = (cmd) => {
  if (cmd === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  } else if (cmd === 'home') {
    router.push('/home')
  } else if (cmd === 'profile') {
    router.push(`/${userStore.role.toLowerCase()}/profile`)
  }
}

// 全屏切换
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen().catch(() => {})
  } else {
    document.exitFullscreen()
  }
}

// 点击外部关闭通知面板
const handleClickOutside = (e) => {
  const wrapper = e.target.closest('.notification-wrapper')
  if (!wrapper) {
    showNotifications.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})
onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
/* ===== 布局基础 ===== */
.dashboard-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
  background: #F8FAFC;
  position: relative;
}

/* ===== 背景装饰光球 ===== */
.bg-orb {
  position: fixed;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.35;
  pointer-events: none;
  z-index: 0;
}
.bg-orb-1 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(59,130,246,0.4) 0%, transparent 70%);
  top: -100px;
  right: 20%;
  animation: orbFloat 12s ease-in-out infinite;
}
.bg-orb-2 {
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(139,92,246,0.3) 0%, transparent 70%);
  bottom: 10%;
  left: 30%;
  animation: orbFloat 15s ease-in-out infinite reverse;
}

@keyframes orbFloat {
  0%, 100% { transform: translate(0, 0); }
  33% { transform: translate(30px, -20px); }
  66% { transform: translate(-20px, 15px); }
}

/* ================================================================
   侧边栏 - 全面升级
   ================================================================ */
.sidebar {
  width: 260px;
  background: linear-gradient(180deg, #0C1222 0%, #111D35 40%, #162033 70%, #0F172A 100%);
  display: flex;
  flex-direction: column;
  transition: all 0.38s cubic-bezier(0.4, 0, 0.2, 1);
  flex-shrink: 0;
  position: relative;
  z-index: 100;
  overflow: hidden;
}

/* 侧边栏背景纹理叠加 */
.sidebar::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse at 30% 0%, rgba(59,130,246,0.08) 0%, transparent 60%),
    url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)' opacity='0.03'/%3E%3C/svg%3E");
  pointer-events: none;
  z-index: 0;
}

/* 右侧发光边缘 */
.sidebar::after {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 1px;
  height: 100%;
  background: linear-gradient(
    180deg,
    rgba(59,130,246,0.3) 0%,
    rgba(59,130,246,0.08) 30%,
    transparent 50%,
    rgba(139,92,246,0.05) 80%,
    rgba(139,92,246,0.2) 100%
  );
  z-index: 1;
}

.sidebar.collapsed { width: 72px; }

/* 顶部发光条 */
.sidebar-glow-line {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, 
    transparent 0%, 
    rgba(59,130,246,0.5) 30%, 
    rgba(96,165,250,0.8) 50%,
    rgba(139,92,246,0.5) 70%,
    transparent 100%
  );
  z-index: 2;
  box-shadow: 0 0 12px rgba(59,130,246,0.4), 0 0 24px rgba(59,130,246,0.2);
}

/* ---- Logo 区域 ---- */
.sidebar-logo {
  height: 72px;
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 0 20px;
  flex-shrink: 0;
  position: relative;
  z-index: 2;
}

.logo-icon-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon {
  width: 40px;
  height: 40px;
  border-radius: 11px;
  background: linear-gradient(135deg, #3B82F6 0%, #2563EB 50%, #1D4ED8 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 
    0 4px 16px rgba(59,130,246,0.4),
    0 0 0 1px rgba(255,255,255,0.1) inset;
  position: relative;
  z-index: 1;
}

/* Logo旋转光环 */
.logo-ring {
  position: absolute;
  inset: -3px;
  border-radius: 13px;
  border: 2px solid transparent;
  border-top-color: #60A5FA;
  border-right-color: rgba(139,92,246,0.6);
  animation: logoRingSpin 4s linear infinite;
}

@keyframes logoRingSpin {
  to { transform: rotate(360deg); }
}

.logo-text-wrap {
  display: flex;
  flex-direction: column;
  white-space: nowrap;
}

.logo-title {
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 0.03em;
  text-shadow: 0 1px 2px rgba(0,0,0,0.2);
}

.logo-subtitle {
  color: rgba(148,163,184,0.7);
  font-size: 10.5px;
  font-weight: 500;
  letter-spacing: 0.08em;
  margin-top: 2px;
  text-transform: uppercase;
}

/* 分隔线 */
.sidebar-divider {
  padding: 0 18px;
  position: relative;
  z-index: 2;
}

.divider-line {
  height: 1px;
  background: linear-gradient(90deg, 
    transparent 0%,
    rgba(255,255,255,0.06) 20%,
    rgba(255,255,255,0.1) 50%,
    rgba(255,255,255,0.06) 80%,
    transparent 100%
  );
}

/* ---- 导航菜单 ---- */
.sidebar-scroll {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  position: relative;
  z-index: 2;
}

.nav-menu {
  padding: 14px 10px;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 11px 14px;
  border-radius: 11px;
  cursor: pointer;
  position: relative;
  transition: all 0.28s cubic-bezier(0.4, 0, 0.2, 1);
  color: rgba(203,213,225,0.65);
  user-select: none;
  white-space: nowrap;
  animation: navItemIn 0.35s ease both;
  animation-delay: var(--delay, 0s);
}

@keyframes navItemIn {
  from { opacity: 0; transform: translateX(-12px); }
  to { opacity: 1; transform: translateX(0); }
}

/* 悬停效果 */
.nav-item:hover {
  background: rgba(255,255,255,0.05);
  color: rgba(226,232,240,0.9);
  transform: translateX(3px);
}

/* 激活态背景光效 */
.nav-active-bg {
  position: absolute;
  inset: 0;
  border-radius: 11px;
  background: linear-gradient(135deg, rgba(59,130,246,0.18) 0%, rgba(99,102,241,0.1) 100%);
  border: 1px solid rgba(59,130,246,0.15);
  box-shadow: 
    inset 0 1px 0 rgba(255,255,255,0.05),
    0 0 16px rgba(59,130,246,0.08);
}

.nav-item.active {
  color: #fff;
  font-weight: 500;
}

.nav-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 34px;
  height: 34px;
  border-radius: 9px;
  transition: all 0.28s ease;
  position: relative;
  z-index: 1;
}

.nav-item.active .nav-icon {
  background: linear-gradient(135deg, rgba(59,130,246,0.25), rgba(99,102,241,0.15));
  box-shadow: 0 0 12px rgba(59,130,246,0.15);
}

.nav-text {
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.01em;
  position: relative;
  z-index: 1;
}

/* 激活指示器 - 发光圆点 */
.nav-active-indicator {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 1;
}

.indicator-dot {
  display: block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #60A5FA;
  box-shadow: 
    0 0 6px rgba(96,165,250,0.8),
    0 0 16px rgba(96,165,250,0.4);
  animation: indicatorPulse 2.5s ease-in-out infinite;
}

@keyframes indicatorPulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.6; transform: scale(0.85); }
}

/* 快捷键数字 */
.nav-shortcut {
  position: absolute;
  right: 26px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 9.5px;
  font-weight: 600;
  color: rgba(148,163,184,0.35);
  background: rgba(255,255,255,0.05);
  padding: 1px 5px;
  border-radius: 4px;
  border: 1px solid rgba(255,255,255,0.06);
  z-index: 1;
}

.nav-item.active .nav-shortcut {
  display: none;
}

/* ---- 侧边栏底部 ---- */
.sidebar-footer {
  padding: 14px 14px 16px;
  border-top: 1px solid rgba(255,255,255,0.06);
  flex-shrink: 0;
  position: relative;
  z-index: 2;
}

.user-card-glass {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(255,255,255,0.07) 0%, rgba(255,255,255,0.03) 100%);
  border: 1px solid rgba(255,255,255,0.09);
  backdrop-filter: blur(10px);
  position: relative;
  overflow: hidden;
}

/* 用户卡片底部微光 */
.user-card-glass::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 20%;
  right: 20%;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(59,130,246,0.3), transparent);
}

.user-avatar-section {
  position: relative;
  flex-shrink: 0;
}

.avatar-glow {
  position: absolute;
  inset: -2px;
  border-radius: 50%;
  background: conic-gradient(from 0deg, #3B82F6, #8B5CF6, #3B82F6);
  animation: avatarGlowSpin 4s linear infinite;
  opacity: 0.6;
  z-index: 0;
}

@keyframes avatarGlowSpin {
  to { transform: rotate(360deg); }
}

.user-avatar {
  position: relative;
  z-index: 1;
  background: linear-gradient(135deg, #3B82F6, #6366F1) !important;
  font-weight: 600 !important;
  font-size: 15px !important;
  color: white !important;
  border: 2px solid rgba(15,23,42,0.8);
}

.status-online {
  position: absolute;
  bottom: 1px;
  right: 1px;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #22C55E;
  border: 2px solid #0F172A;
  z-index: 2;
  box-shadow: 0 0 6px rgba(34,197,94,0.5);
}

.user-detail {
  flex: 1;
  min-width: 0;
}

.user-name {
  color: #F1F5F9;
  font-size: 13px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-role-row {
  margin-top: 3px;
}

.role-badge-pill {
  display: inline-flex;
  align-items: center;
  font-size: 10.5px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 6px;
  letter-spacing: 0.02em;
}

.user-actions {
  flex-shrink: 0;
}

.action-mini-btn {
  width: 28px;
  height: 28px;
  border: none;
  border-radius: 8px;
  background: rgba(255,255,255,0.06);
  color: rgba(203,213,225,0.6);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.action-mini-btn:hover {
  background: rgba(255,255,255,0.12);
  color: #fff;
}

/* 系统状态栏 */
.sys-status-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 10px;
  padding: 0 4px;
}

.sys-status-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 10.5px;
  color: rgba(148,163,184,0.5);
  font-weight: 500;
}

.sys-dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
}
.sys-dot.green {
  background: #22C55E;
  box-shadow: 0 0 4px rgba(34,197,94,0.5);
}

/* 折叠态底部 */
.sidebar-footer-collapsed {
  display: flex;
  justify-content: center;
  padding: 16px 0;
  border-top: 1px solid rgba(255,255,255,0.06);
  flex-shrink: 0;
  position: relative;
  z-index: 2;
}

.collapsed-avatar-wrap {
  position: relative;
  cursor: pointer;
}

.online-pulse {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #22C55E;
  border: 2px solid #111D35;
  animation: onlinePulse 2.5s ease-in-out infinite;
}

@keyframes onlinePulse {
  0%, 100% { box-shadow: 0 0 0 0 rgba(34,197,94,0.4); }
  50% { box-shadow: 0 0 0 4px rgba(34,197,94,0); }
}

/* ================================================================
   主区域
   ================================================================ */
.main-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
  position: relative;
  z-index: 1;
}

/* ================================================================
   顶栏 - 全面升级玻璃拟态风格
   ================================================================ */
.top-bar {
  height: 68px;
  flex-shrink: 0;
  position: relative;
  z-index: 50;
}

/* 顶栏玻璃底层 */
.top-bar-glass {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.65);
  backdrop-filter: blur(24px) saturate(200%) brightness(1.02);
  -webkit-backdrop-filter: blur(24px) saturate(200%) brightness(1.02);
  border-bottom: 1px solid rgba(226, 232, 240, 0.45);
}

/* 顶部强调色细线 */
.top-accent-line {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1.5px;
  background: linear-gradient(90deg,
    transparent 0%,
    rgba(59,130,246,0.3) 15%,
    rgba(99,102,241,0.4) 40%,
    rgba(168,85,247,0.3) 60%,
    rgba(59,130,246,0.3) 85%,
    transparent 100%
  );
  opacity: 0.7;
}

.top-bar-inner {
  position: relative;
  z-index: 1;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
}

.top-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 折叠按钮 - 玻璃质感 */
.collapse-btn {
  width: 38px;
  height: 38px;
  border: 1px solid rgba(226,232,240,0.6);
  border-radius: 11px;
  background: rgba(255,255,255,0.55);
  backdrop-filter: blur(8px);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.28s cubic-bezier(0.4,0,0.2,1);
  flex-shrink: 0;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
}

.collapse-btn:hover {
  background: #fff;
  border-color: rgba(59,130,246,0.3);
  box-shadow: 0 2px 8px rgba(59,130,246,0.1), 0 1px 3px rgba(0,0,0,0.05);
  transform: scale(1.03);
  color: var(--primary, #3B82F6);
}

.btn-inner {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #64748B;
  transition: color 0.2s ease;
}

.collapse-btn:hover .btn-inner {
  color: #3B82F6;
}

/* 面包屑 - 优化样式 */
.breadcrumb-area {
  font-size: 14px;
}

.breadcrumb-home {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  color: #64748B;
  font-weight: 450;
  font-size: 13px;
}

::deep(.breadcrumb-area .el-breadcrumb__item) {
  font-size: 13px;
}

::deep(.breadcrumb-area .el-breadcrumb__inner) {
  color: #64748B !important;
  font-weight: 450;
  transition: color 0.2s ease;
}

::deep(.breadcrumb-area .el-breadcrumb__inner.is-link:hover) {
  color: #3B82F6 !important;
}

::deep(.breadcrumb-area .el-breadcrumb__separator) {
  color: #CBD5E1 !important;
  margin: 0 6px !important;
}

/* 内联页面标题 */
.page-title-inline {
  padding-left: 14px;
  border-left: 2.5px solid #E2E8F0;
  margin-left: 2px;
}

.page-title-inline h2 {
  font-size: 16px;
  font-weight: 650;
  color: #1E293B;
  margin: 0;
  letter-spacing: -0.01em;
}

.top-right {
  display: flex;
  align-items: center;
  gap: 6px;
}

/* ---- 搜索框 - 玻璃拟态 ---- */
.header-search {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 12px;
  background: rgba(241,245,249,0.75);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(226,232,240,0.8);
  transition: all 0.32s cubic-bezier(0.4,0,0.2,1);
  width: 240px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.03);
}

.header-search:focus-within {
  border-color: rgba(59,130,246,0.4);
  background: #fff;
  box-shadow: 
    0 0 0 3px rgba(59,130,246,0.08),
    0 4px 12px rgba(0,0,0,0.06);
  width: 300px;
}

.search-icon-wrap {
  display: flex;
  align-items: center;
  color: #94A3B8;
  flex-shrink: 0;
}

.search-input {
  border: none;
  outline: none;
  background: transparent;
  font-size: 13px;
  color: #1E293B;
  width: 100%;
  letter-spacing: 0.005em;
}

.search-input::placeholder {
  color: #94A3B8;
}

.search-kbd {
  font-family: inherit;
  font-size: 10px;
  font-weight: 600;
  color: #94A3B8;
  background: rgba(226,232,240,0.8);
  padding: 2px 6px;
  border-radius: 4px;
  border: 1px solid rgba(203,213,225,0.5);
  flex-shrink: 0;
  letter-spacing: 0.02em;
}

/* ---- 图标按钮通用样式 ---- */
.icon-btn {
  width: 38px;
  height: 38px;
  border: 1px solid rgba(226,232,240,0.6);
  border-radius: 11px;
  background: rgba(255,255,255,0.5);
  backdrop-filter: blur(8px);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.25s cubic-bezier(0.4,0,0.2,1);
  color: #64748B;
  position: relative;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
}

.icon-btn:hover {
  background: #fff;
  border-color: rgba(59,130,246,0.3);
  color: #3B82F6;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  transform: translateY(-1px);
}

/* ---- 通知组件 ---- */
.notification-wrapper {
  position: relative;
  margin: 0 2px;
}

.notification-icon-btn {
  position: relative;
}

.notification-pulse {
  position: absolute;
  top: 7px;
  right: 7px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #EF4444;
  border: 2px solid #fff;
  animation: notifPulse 2s ease-in-out infinite;
}

@keyframes notifPulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.3); opacity: 0.7; }
}

.notification-btn :deep(.el-badge__content) {
  font-size: 10px;
  font-weight: 600;
  top: 7px;
  right: 5px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.15);
}

/* 通知下拉面板 */
.notification-panel {
  position: absolute;
  top: calc(100% + 8px);
  right: -12px;
  width: 340px;
  background: #fff;
  border-radius: 14px;
  border: 1px solid rgba(226,232,240,0.8);
  box-shadow: 
    0 4px 24px rgba(0,0,0,0.08),
    0 1px 3px rgba(0,0,0,0.04);
  z-index: 1000;
  overflow: hidden;
  animation: panelSlideIn 0.25s cubic-bezier(0.4,0,0.2,1);
}

@keyframes panelSlideIn {
  from { opacity: 0; transform: translateY(-8px) scale(0.97); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 18px 12px;
  font-size: 14px;
  font-weight: 650;
  color: #1E293B;
  border-bottom: 1px solid #F1F5F9;
}

.panel-header a {
  font-size: 12px;
  font-weight: 500;
  color: #3B82F6;
  text-decoration: none;
}

.panel-list {
  max-height: 260px;
  overflow-y: auto;
}

.panel-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px 18px;
  cursor: pointer;
  transition: background 0.15s ease;
  border-bottom: 1px solid #F8FAFC;
}

.panel-item:last-child { border-bottom: none; }

.panel-item:hover {
  background: #F8FAFC;
}

.panel-item.unread {
  background: rgba(59,130,246,0.025);
}

.panel-icon {
  width: 34px;
  height: 34px;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 15px;
}
.panel-icon.blue { background: #EFF6FF; color: #3B82F6; }
.panel-icon.green { background: #F0FDF4; color: #10B981; }
.panel-icon.amber { background: #FFFBEB; color: #F59E0B; }

.panel-content {
  flex: 1;
  min-width: 0;
}

.panel-content p {
  font-size: 13px;
  color: #334155;
  line-height: 1.4;
  margin: 0 0 3px;
  font-weight: 480;
}

.panel-item.unread p {
  font-weight: 600;
  color: #1E293B;
}

.panel-content span {
  font-size: 11.5px;
  color: #94A3B8;
}

.panel-footer {
  padding: 10px 18px;
  text-align: center;
  border-top: 1px solid #F1F5F9;
  background: #FAFBFC;
}

.panel-footer a {
  font-size: 12.5px;
  font-weight: 550;
  color: #3B82F6;
  text-decoration: none;
}

/* ---- 垂直分隔线 ---- */
.top-divider-v {
  width: 1px;
  height: 24px;
  background: linear-gradient(180deg, transparent, rgba(203,213,225,0.5), transparent);
  margin: 0 6px;
  flex-shrink: 0;
}

/* ---- 用户触发器 - 升级版 ---- */
.user-trigger {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 5px 12px 5px 5px;
  border-radius: var(--radius-full, 9999px);
  cursor: pointer;
  transition: all 0.25s ease;
  margin-left: 4px;
  border: 1px solid transparent;
}

.user-trigger:hover {
  background: rgba(241,245,249,0.8);
  border-color: rgba(226,232,240,0.7);
}

.trigger-avatar-wrap {
  position: relative;
}

.trigger-avatar {
  background: linear-gradient(135deg, #3B82F6, #6366F1) !important;
  font-size: 13px !important;
  font-weight: 600 !important;
  color: white !important;
  box-shadow: 0 2px 8px rgba(59,130,246,0.25);
  transition: transform 0.2s ease;
}

.user-trigger:hover .trigger-avatar {
  transform: scale(1.05);
}

.trigger-info {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.trigger-name {
  font-size: 13px;
  font-weight: 600;
  color: #1E293B;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.trigger-role {
  font-size: 11px;
  color: #94A3B8;
  font-weight: 450;
}

.trigger-arrow {
  transition: transform 0.2s ease;
}

.user-trigger:hover .trigger-arrow {
  transform: translateY(1px);
}

/* ---- 下拉菜单 - 大幅升级 ---- */
:deep(.user-dropdown-menu) {
  min-width: 240px;
  padding: 12px;
  border-radius: 16px !important;
  border: 1px solid rgba(226,232,240,0.8) !important;
  box-shadow: 
    0 8px 32px rgba(0,0,0,0.1),
    0 2px 8px rgba(0,0,0,0.04) !important;
  background: #fff !important;
  overflow: visible;
}

.dropdown-user-info {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 8px 16px;
  margin-bottom: 4px;
}

.dropdown-avatar-large {
  position: relative;
}

.dropdown-avatar-large .el-avatar {
  background: linear-gradient(135deg, #3B82F6, #6366F1) !important;
  color: white;
  font-weight: 700;
  font-size: 18px !important;
}

.avatar-status {
  position: absolute;
  bottom: 1px;
  right: 1px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2.5px solid #fff;
}

.avatar-status.online { background: #22C55E; }

.dropdown-name {
  font-size: 15px;
  font-weight: 650;
  color: #1E293B;
  letter-spacing: -0.01em;
}

.dropdown-role {
  font-size: 12px;
  color: #64748B;
  margin-top: 2px;
  font-weight: 500;
}

.dropdown-email {
  font-size: 11.5px;
  color: #94A3B8;
  margin-top: 2px;
}

.dropdown-section-label {
  font-size: 10.5px;
  font-weight: 700;
  color: #94A3B8;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  padding: 6px 8px 4px;
}

:deep(.user-dropdown-menu .el-dropdown-menu__item) {
  border-radius: 10px;
  padding: 10px 12px;
  margin-bottom: 3px;
  font-size: 13px;
  font-weight: 480;
  color: #334155;
  display: flex;
  align-items: center;
  gap: 10px;
  transition: all 0.2s ease;
}

:deep(.user-dropdown-menu .el-dropdown-menu__item:hover) {
  background: #F1F5F9 !important;
  color: #1E293B !important;
  transform: translateX(2px);
}

.menu-item-icon {
  width: 30px;
  height: 30px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 14px;
}

.menu-arrow {
  margin-left: auto;
  color: #CBD5E1;
  font-size: 12px;
  opacity: 0;
  transition: opacity 0.2s ease;
}

:deep(.user-dropdown-menu .el-dropdown-menu__item:hover .menu-arrow) {
  opacity: 1;
  color: #94A3B8;
}

/* ================================================================
   页面内容
   ================================================================ */
.page-main {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 28px;
  background: transparent;
  position: relative;
  z-index: 1;
}

/* ===== 过渡动画 ===== */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.2s ease, transform 0.25s ease;
}
.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-10px);
}

.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.2s ease, transform 0.25s ease;
}
.page-fade-enter-from {
  opacity: 0;
  transform: translateY(8px);
}
.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* ===== Element Plus 覆盖 ===== */
:deep(.el-scrollbar__bar.is-horizontal) { display: none; }
:deep(.el-menu) { border-right: none; }

/* ================================================================
   响应式
   ================================================================ */
@media (max-width: 1200px) {
  .header-search { width: 200px; }
  .header-search:focus-within { width: 240px; }
  .page-title-inline { display: none; }
}

@media (max-width: 768px) {
  .sidebar { 
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 1000;
  }
  .sidebar.collapsed { width: 0; overflow: hidden; }
  
  .page-main { padding: 16px; }
  .top-bar-inner { padding: 0 16px; }
  
  .header-search { display: none; }
  .trigger-info { display: none; }
  .top-divider-v { display: none; }
  
  .collapse-btn { 
    position: fixed; 
    left: 16px; 
    top: 15px; 
    z-index: 999; 
    box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  }
  
  .notification-panel { 
    right: -40px; 
    width: calc(100vw - 32px);
  }
}
</style>
