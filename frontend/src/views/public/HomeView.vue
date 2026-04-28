<template>
  <div class="home-view">
    <!-- ===== Hero 区域（渐变背景 + 玻璃搜索） ===== -->
    <section class="hero-section">
      <!-- 背景装饰 -->
      <div class="hero-bg-decor">
        <div class="orb orb-1"></div>
        <div class="orb orb-2"></div>
        <div class="orb orb-3"></div>
        <div class="grid-pattern"></div>
      </div>

      <!-- 导航条 -->
      <nav class="home-nav animate-fade-in-down">
        <div class="nav-inner">
          <div class="nav-logo" @click="$router.push('/home')">
            <div class="logo-icon-sm">
              <el-icon :size="20"><School /></el-icon>
            </div>
            <span class="logo-text">校园招聘</span>
          </div>
          <div class="nav-links">
            <a href="#jobs" class="nav-link">职位</a>
            <a href="#talks" class="nav-link">宣讲会</a>
            <!-- 未登录显示登录按钮 -->
            <router-link v-if="!userStore.isLoggedIn" to="/login" class="nav-link nav-login-btn">
              <el-icon><User /></el-icon> 登录
            </router-link>
            <!-- 已登录显示控制台入口 -->
            <router-link v-else :to="getDashboardPath()" class="nav-link nav-login-btn">
              <el-icon><User /></el-icon> 进入控制台
            </router-link>
          </div>
        </div>
      </nav>

      <!-- 主内容 -->
      <div class="hero-content">
        <h1 class="hero-title animate-fade-in-up delay-1">
          找到你的<span class="text-gradient">理想工作</span>
        </h1>
        <p class="hero-subtitle animate-fade-in-up delay-2">
          连接优质企业，开启职业生涯的第一步
        </p>

        <!-- 玻璃搜索栏 -->
        <div class="glass-search-bar animate-fade-in-up delay-3">
          <el-icon class="search-glass-icon"><Search /></el-icon>
          <input
            v-model="keyword"
            type="text"
            placeholder="搜索职位、企业名称..."
            class="search-input"
            @keyup.enter="goSearch"
          />
          <button class="search-btn" @click="goSearch">
            搜索职位
          </button>
        </div>

        <!-- 热门标签 -->
        <div class="hot-tags animate-fade-in-up delay-4">
          <span class="tags-label">热门：</span>
          <span v-for="tag in hotTags" :key="tag" class="hot-tag" @click="keyword = tag; goSearch()">
            {{ tag }}
          </span>
        </div>

        <!-- 信任指标 -->
        <div class="trust-indicators animate-fade-in-up delay-5">
          <div class="trust-item">
            <el-icon color="#60A5FA"><UserFilled /></el-icon>
            <span>5000+ 学生</span>
          </div>
          <div class="trust-divider"></div>
          <div class="trust-item">
            <el-icon color="#34D399"><OfficeBuilding /></el-icon>
            <span>200+ 企业</span>
          </div>
          <div class="trust-divider"></div>
          <div class="trust-item">
            <el-icon color="#FBBF24"><TrendCharts /></el-icon>
            <span>95% 就业率</span>
          </div>
        </div>
      </div>

      <!-- 向下滚动提示 -->
      <div class="scroll-hint animate-fade-in-up delay-6">
        <div class="scroll-mouse">
          <div class="scroll-dot"></div>
        </div>
        <span>向下滚动探索</span>
      </div>
    </section>

    <!-- ===== 数据统计区域 ===== -->
    <section class="stats-section">
      <div class="stats-container">
        <div class="stat-card-home" v-for="(s, idx) in stats" :key="s.label" :class="`color-${idx}`" :style="{animationDelay: idx * 0.12 + 's'}">
          <div class="sc-icon">
            <el-icon :size="28"><component :is="s.icon" /></el-icon>
          </div>
          <div class="sc-body">
            <div class="sc-value">{{ s.num }}</div>
            <div class="sc-label">{{ s.label }}</div>
          </div>
          <div class="sc-sparkline">
            <svg viewBox="0 0 80 24" preserveAspectRatio="none">
              <path :d="s.sparkLine" fill="none" stroke-width="2" :stroke="s.strokeColor" opacity="0.4"/>
              <path :d="s.sparkFill" fill="url(#sparkGrad)" opacity="0.15"/>
              <defs><linearGradient id="sparkGrad" x1="0%" y1="0%" x2="0%" y2="100%"><stop offset="0%" stop-color="#3B82F6"/><stop offset="100%" stop-color="#3B82F6" stop-opacity="0"/></linearGradient></defs>
            </svg>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== 最新职位 ===== -->
    <section id="jobs" class="content-section jobs-section">
      <div class="content-inner">
        <div class="sec-header">
          <div class="sec-title-group">
            <div class="sec-badge">
              <el-icon><Briefcase /></el-icon>
            </div>
            <div>
              <h2 class="sec-title">最新职位</h2>
              <p class="sec-desc">优质企业最新发布的岗位，快来看看吧</p>
            </div>
          </div>
          <router-link to="/jobs" class="view-all-link">
            查看全部 <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>

        <el-row :gutter="18">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="(job, idx) in latestJobs" :key="job.id" style="margin-bottom: 18px;">
            <div class="job-card-premium" :style="{animationDelay: idx * 0.08 + 's'}" @click="$router.push(`/jobs/${job.id}`)">
              <div class="jcp-header">
                <div class="jcp-company">{{ job.companyName || '知名企业' }}</div>
                <el-tag size="small" :type="job.status === 1 ? 'success' : 'info'" effect="light">
                  {{ job.status === 1 ? '热招中' : '已结束' }}
                </el-tag>
              </div>
              <h3 class="jcp-title">{{ job.jobName }}</h3>
              <div class="jcp-salary">{{ job.salaryRange || '薪资面议' }}</div>
              <div class="jcp-tags">
                <span class="jcp-tag">
                  <el-icon :size="12"><Location /></el-icon> {{ job.workLocation }}
                </span>
                <span class="jcp-tag">
                  <el-icon :size="12"><Reading /></el-icon> {{ job.educationReq || '不限' }}
                </span>
              </div>
              <div class="jcp-footer">
                <span class="jcp-time">{{ timeAgo(job.createTime) }}</span>
                <span class="jcp-action">查看详情 →</span>
              </div>
            </div>
          </el-col>
        </el-row>
        
        <div v-if="latestJobs.length === 0" class="empty-state-card">
          <el-icon :size="48" color="#CBD5E1"><Briefcase /></el-icon>
          <p>暂无职位数据</p>
          <el-button type="primary" plain size="small" @click="$router.push('/login')">登录查看更多</el-button>
        </div>
      </div>
    </section>

    <!-- ===== 宣讲会 ===== -->
    <section id="talks" class="content-section talks-section">
      <div class="content-inner">
        <div class="sec-header">
          <div class="sec-title-group">
            <div class="sec-badge orange">
              <el-icon><Calendar /></el-icon>
            </div>
            <div>
              <h2 class="sec-title">近期宣讲会</h2>
              <p class="sec-desc">企业走进校园，面对面交流机会不容错过</p>
            </div>
          </div>
          <router-link to="/talks" class="view-all-link">
            查看全部 <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>

        <el-row :gutter="18">
          <el-col :xs="24" :sm="12" :md="8" v-for="(talk, idx) in talks" :key="talk.id" style="margin-bottom: 18px;">
            <div class="talk-card-premium" :style="{animationDelay: idx * 0.08 + 's'}">
              <div class="tcp-date-box">
                <div class="tcp-month">{{ talkMonth(talk.talkTime) }}</div>
                <div class="tcp-day">{{ talkDay(talk.talkTime) }}</div>
              </div>
              <div class="tcp-content">
                <h3 class="tcp-title">{{ talk.title }}</h3>
                <div class="tcp-info-row">
                  <el-icon :size="14"><OfficeBuilding /></el-icon>
                  <span>{{ talk.companyName || '宣讲企业' }}</span>
                </div>
                <div class="tcp-info-row">
                  <el-icon :size="14"><Location /></el-icon>
                  <span>{{ talk.location }}</span>
                </div>
                <div class="tcp-progress">
                  <div class="progress-track">
                    <div class="progress-fill" :style="{ width: talkSignPercent(talk) + '%' }"></div>
                  </div>
                  <span class="progress-text">{{ talk.currentSignNum }}/{{ talk.maxSignNum }} 人</span>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>

        <div v-if="talks.length === 0" class="empty-state-card">
          <el-icon :size="48" color="#CBD5E1"><Calendar /></el-icon>
          <p>暂无宣讲会安排</p>
        </div>
      </div>
    </section>

    <!-- ===== 底部 CTA ===== -->
    <section class="cta-section">
      <div class="cta-bg-decor">
        <div class="cta-orb cta-orb-1"></div>
        <div class="cta-orb cta-orb-2"></div>
      </div>
      <div class="cta-content">
        <h2 class="cta-title">准备好开始了吗？</h2>
        <p class="cta-desc">立即加入校园招聘平台，找到属于你的机会</p>
        <div class="cta-buttons">
          <!-- 未登录 -->
          <template v-if="!userStore.isLoggedIn">
            <router-link to="/login" class="cta-btn primary">
              <el-icon><Right /></el-icon> 立即登录
            </router-link>
            <router-link to="/register?type=student" class="cta-btn secondary">
              注册账号
            </router-link>
          </template>
          <!-- 已登录 -->
          <template v-else>
            <router-link :to="getDashboardPath()" class="cta-btn primary">
              <el-icon><Right /></el-icon> 进入控制台
            </router-link>
            <router-link to="/jobs" class="cta-btn secondary">
              浏览职位
            </router-link>
          </template>
        </div>
      </div>
    </section>

    <!-- ===== 页脚 ===== -->
    <footer class="site-footer">
      <div class="footer-inner">
        <div class="footer-brand">
          <div class="footer-logo">
            <el-icon :size="18"><School /></el-icon>
            校园招聘系统
          </div>
          <p class="footer-desc">连接学生与企业的智慧桥梁</p>
        </div>
        <div class="footer-links">
          <div class="f-col">
            <h4>快速入口</h4>
            <a href="#jobs">浏览职位</a>
            <a href="#talks">宣讲会</a>
            <router-link to="/register?type=student">学生注册</router-link>
            <router-link to="/register?type=company">企业入驻</router-link>
          </div>
          <div class="f-col">
            <h4>帮助支持</h4>
            <a href="javascript:void(0)">使用指南</a>
            <a href="javascript:void(0)">常见问题</a>
            <a href="javascript:void(0)">联系我们</a>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        &copy; {{ new Date().getFullYear() }} Campus Recruitment System. All rights reserved.
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getJobList, getTalkList, getStatistics } from '@/api'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'

dayjs.extend(relativeTime)

const router = useRouter()
const userStore = useUserStore()
const keyword = ref('')
const latestJobs = ref([])
const talks = ref([])
const hotTags = ['Java开发', '前端工程师', '产品经理', '数据分析', 'UI设计', '运营']

// 根据角色获取控制台路径
const getDashboardPath = () => {
  const map = {
    STUDENT: '/student/dashboard',
    COMPANY: '/company/dashboard',
    COLLEGE: '/college/dashboard',
    ADMIN: '/admin/dashboard'
  }
  return map[userStore.role] || '/login'
}

const stats = ref([
  { num: '0+', label: '注册学生', icon: 'User', sparkLine: 'M0,22 Q20,18 40,14 T80,4', sparkFill: 'M0,22 Q20,18 40,14 T80,4 L80,24 L0,24 Z', strokeColor: '#3B82F6' },
  { num: '0+', label: '合作企业', icon: 'OfficeBuilding', sparkLine: 'M0,20 Q25,16 50,10 T80,6', sparkFill: 'M0,20 Q25,16 50,10 T80,6 L80,24 L0,24 Z', strokeColor: '#10B981' },
  { num: '0+', label: '发布职位', icon: 'Briefcase', sparkLine: 'M0,18 Q30,14 60,8 T80,2', sparkFill: 'M0,18 Q30,14 60,8 T80,2 L80,24 L0,24 Z', strokeColor: '#F59E0B' },
  { num: '0+', label: '成功就业', icon: 'TrendCharts', sparkLine: 'M0,20 Q20,17 40,13 T80,5', sparkFill: 'M0,20 Q20,17 40,13 T80,5 L80,24 L0,24 Z', strokeColor: '#8B5CF6' },
])

const goSearch = () => router.push({ path: '/jobs', query: keyword.value ? { keyword: keyword.value } : {} })

const timeAgo = (t) => t ? dayjs(t).fromNow() : '-'
const talkMonth = (t) => t ? dayjs(t).format('MM月') : '--'
const talkDay = (t) => t ? dayjs(t).format('DD') : '--'
const talkSignPercent = (talk) => {
  if (!talk.maxSignNum) return 0
  return Math.round((talk.currentSignNum / talk.maxSignNum) * 100)
}

onMounted(async () => {
  try {
    const [jobRes, talkRes] = await Promise.all([
      getJobList({ page: 1, size: 8 }),
      getTalkList({ page: 1, size: 3 })
    ])
    latestJobs.value = jobRes.data?.records || []
    talks.value = talkRes.data?.records || []
  } catch {}

  try {
    const res = await getStatistics()
    if (res.data) {
      stats.value[0].num = (res.data.studentCount || 0) + '+'
      stats.value[1].num = (res.data.companyCount || 0) + '+'
      stats.value[2].num = (res.data.jobCount || 0) + '+'
    }
  } catch {}
})
</script>

<style scoped>
/* ========== Hero Section ========== */
.hero-section {
  position: relative;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background: linear-gradient(135deg, #0F172A 0%, #1e3a5f 35%, #2563EB 70%, #7C3AED 100%);
}

/* 背景装饰 */
.hero-bg-decor {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.4;
}
.orb-1 {
  width: 400px; height: 400px;
  background: #3B82F6;
  top: -100px; right: -50px;
  animation: floatOrb 8s ease-in-out infinite;
}
.orb-2 {
  width: 300px; height: 300px;
  background: #8B5CF6;
  bottom: -50px; left: -50px;
  animation: floatOrb 10s ease-in-out infinite reverse;
}
.orb-3 {
  width: 200px; height: 200px;
  background: #06B6D4;
  top: 40%; left: 30%;
  animation: floatOrb 12s ease-in-out infinite;
}

@keyframes floatOrb {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -20px) scale(1.05); }
  66% { transform: translate(-20px, 20px) scale(0.95); }
}

.grid-pattern {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255,255,255,0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

/* 导航 */
.home-nav {
  position: absolute;
  top: 0; left: 0; right: 0;
  z-index: 100;
  padding: 16px 32px;
}
.nav-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.nav-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}
.logo-icon-sm {
  width: 36px; height: 36px;
  background: rgba(255,255,255,0.15);
  backdrop-filter: blur(10px);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}
.logo-text {
  font-size: 17px;
  font-weight: 700;
  color: white;
  letter-spacing: -0.01em;
}
.nav-links {
  display: flex;
  align-items: center;
  gap: 28px;
}
.nav-link {
  color: rgba(255,255,255,0.75);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.25s ease;
}
.nav-link:hover { color: white; }
.nav-login-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 8px 18px;
  border-radius: var(--radius-full);
  background: rgba(255,255,255,0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255,255,255,0.15);
  color: white !important;
  transition: all 0.25s ease;
}
.nav-login-btn:hover {
  background: rgba(255,255,255,0.2);
  border-color: rgba(255,255,255,0.3);
  transform: translateY(-1px);
}

/* Hero 内容 */
.hero-content {
  position: relative;
  z-index: 10;
  text-align: center;
  padding: 120px 24px 60px;
  max-width: 720px;
}
.hero-title {
  font-size: 52px;
  font-weight: 800;
  line-height: 1.15;
  color: white;
  margin-bottom: 16px;
  letter-spacing: -0.03em;
}
.text-gradient {
  background: linear-gradient(135deg, #60A5FA 0%, #A78BFA 50%, #34D399 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.hero-subtitle {
  font-size: 19px;
  color: rgba(255,255,255,0.7);
  margin-bottom: 36px;
  line-height: 1.6;
}

/* 玻璃搜索栏 */
.glass-search-bar {
  display: flex;
  align-items: center;
  gap: 0;
  max-width: 560px;
  margin: 0 auto 24px;
  background: rgba(255,255,255,0.12);
  backdrop-filter: blur(24px) saturate(200%);
  -webkit-backdrop-filter: blur(24px) saturate(200%);
  border: 1px solid rgba(255,255,255,0.2);
  border-radius: var(--radius-full);
  padding: 6px 6px 6px 20px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.15), inset 0 1px 0 rgba(255,255,255,0.1);
  transition: all 0.3s ease;
}
.glass-search-bar:focus-within {
  background: rgba(255,255,255,0.18);
  border-color: rgba(96,165,250,0.5);
  box-shadow: 0 8px 32px rgba(0,0,0,0.2), 0 0 0 3px rgba(59,130,246,0.15);
}
.search-glass-icon {
  color: rgba(255,255,255,0.55);
  flex-shrink: 0;
  font-size: 18px;
}
.search-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  color: white;
  font-size: 15px;
  padding: 8px 12px;
  min-width: 180px;
}
.search-input::placeholder {
  color: rgba(255,255,255,0.45);
}
.search-btn {
  padding: 11px 26px;
  border: none;
  border-radius: var(--radius-full);
  background: linear-gradient(135deg, #3B82F6 0%, #8B5CF6 100%);
  color: white;
  font-size: 14.5px;
  font-weight: 600;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.25s ease;
  box-shadow: 0 4px 14px rgba(59,130,246,0.35);
}
.search-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(59,130,246,0.45);
}

/* 热门标签 */
.hot-tags {
  display: flex;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}
.tags-label {
  color: rgba(255,255,255,0.5);
  font-size: 13px;
}
.hot-tag {
  padding: 4px 14px;
  border-radius: var(--radius-full);
  background: rgba(255,255,255,0.08);
  color: rgba(255,255,255,0.75);
  font-size: 13px;
  cursor: pointer;
  border: 1px solid rgba(255,255,255,0.1);
  transition: all 0.2s ease;
}
.hot-tag:hover {
  background: rgba(255,255,255,0.16);
  color: white;
  border-color: rgba(255,255,255,0.25);
  transform: translateY(-1px);
}

/* 信任指标 */
.trust-indicators {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 24px;
  margin-top: 32px;
}
.trust-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: rgba(255,255,255,0.65);
  font-size: 13px;
  font-weight: 500;
}
.trust-divider {
  width: 1px;
  height: 16px;
  background: rgba(255,255,255,0.2);
}

/* 滚动提示 */
.scroll-hint {
  position: absolute;
  bottom: 32px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: rgba(255,255,255,0.4);
  font-size: 12px;
  animation: bounceHint 2.5s ease infinite;
}
.scroll-mouse {
  width: 22px; height: 34px;
  border: 2px solid rgba(255,255,255,0.3);
  border-radius: 12px;
  position: relative;
}
.scroll-dot {
  width: 3px; height: 7px;
  background: rgba(255,255,255,0.6);
  border-radius: 2px;
  position: absolute;
  top: 6px; left: 50%; transform: translateX(-50%);
  animation: scrollDotAnim 2s ease infinite;
}
@keyframes scrollDotAnim {
  0% { opacity: 1; top: 6px; }
  100% { opacity: 0; top: 20px; }
}
@keyframes bounceHint {
  0%, 100% { transform: translateX(-50%) translateY(0); }
  50% { transform: translateX(-50%) translateY(6px); }
}

/* ========== 统计区域 ========== */
.stats-section {
  padding: 48px 24px;
  background: white;
  position: relative;
  margin-top: -48px;
  z-index: 10;
}
.stats-container {
  max-width: 1100px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 18px;
}
.stat-card-home {
  background: white;
  border-radius: var(--radius-lg);
  padding: 22px 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  border: 1px solid rgba(226,232,240,0.7);
  box-shadow: var(--shadow-md);
  transition: all 0.3s cubic-bezier(0.4,0,0.2,1);
  animation: fadeInUp 0.5s ease forwards;
  opacity: 0;
  position: relative;
  overflow: hidden;
}
.stat-card-home::before {
  content: '';
  position: absolute;
  left: 0; top: 0; bottom: 0;
  width: 3px;
}
.stat-card-home.color-0::before { background: var(--gradient-primary); }
.stat-card-home.color-1::before { background: var(--gradient-success); }
.stat-card-home.color-2::before { background: var(--gradient-warning); }
.stat-card-home.color-3::before { background: linear-gradient(135deg, #8B5CF6, #EC4899); }

.stat-card-home:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-xl);
}

.sc-icon {
  width: 52px; height: 52px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.stat-card-home.color-0 .sc-icon { background: rgba(59,130,246,0.1); color: var(--primary); }
.stat-card-home.color-1 .sc-icon { background: rgba(16,185,129,0.1); color: var(--success); }
.stat-card-home.color-2 .sc-icon { background: rgba(245,158,11,0.1); color: var(--warning); }
.stat-card-home.color-3 .sc-icon { background: rgba(139,92,246,0.1); color: #8B5CF6; }

.sc-value {
  font-size: 28px;
  font-weight: 800;
  color: var(--text-primary);
  letter-spacing: -0.02em;
  line-height: 1.1;
}
.sc-label {
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: 2px;
}
.sc-sparkline {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 72px;
  height: 24px;
  opacity: 0.6;
}
.sc-sparkline svg { width: 100%; height: 100%; }

/* ========== 内容区域通用 ========== */
.content-section {
  padding: 56px 24px;
}
.content-inner {
  max-width: 1160px;
  margin: 0 auto;
}
.jobs-section { background: var(--bg-body); }
.talks-section { background: white; }

.sec-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
}
.sec-title-group {
  display: flex;
  align-items: center;
  gap: 14px;
}
.sec-badge {
  width: 44px; height: 44px;
  border-radius: var(--radius-md);
  background: rgba(59,130,246,0.1);
  color: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}
.sec-badge.orange {
  background: rgba(245,158,11,0.1);
  color: var(--warning);
}
.sec-title {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.02em;
  margin: 0;
}
.sec-desc {
  font-size: 14px;
  color: var(--text-muted);
  margin-top: 3px;
}
.view-all-link {
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--primary);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  padding: 8px 16px;
  border-radius: var(--radius-full);
  background: rgba(59,130,246,0.05);
  transition: all 0.2s ease;
}
.view-all-link:hover {
  background: rgba(59,130,246,0.1);
  transform: translateX(2px);
}

/* ========== 职位卡片 ========== */
.job-card-premium {
  background: white;
  border-radius: var(--radius-lg);
  padding: 22px;
  cursor: pointer;
  border: 1px solid rgba(226,232,240,0.7);
  box-shadow: var(--shadow-sm);
  transition: all 0.3s cubic-bezier(0.4,0,0.2,1);
  height: 100%;
  animation: fadeInUp 0.5s ease forwards;
  opacity: 0;
  display: flex;
  flex-direction: column;
}
.job-card-premium:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-xl);
  border-color: rgba(59,130,246,0.25);
}
.jcp-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.jcp-company {
  font-size: 13px;
  color: var(--text-secondary);
  font-weight: 500;
}
.jcp-title {
  font-size: 16px;
  font-weight: 650;
  color: var(--text-primary);
  margin-bottom: 8px;
  line-height: 1.3;
}
.jcp-salary {
  font-size: 17px;
  font-weight: 750;
  color: #EF4444;
  margin-bottom: 14px;
}
.jcp-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: auto;
}
.jcp-tag {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  font-size: 12px;
  color: var(--text-muted);
  background: rgba(241,245,249,1);
  padding: 3px 9px;
  border-radius: var(--radius-full);
}
.jcp-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 14px;
  padding-top: 12px;
  border-top: 1px solid rgba(226,232,240,0.5);
}
.jcp-time {
  font-size: 12px;
  color: var(--text-muted);
}
.jcp-action {
  font-size: 13px;
  color: var(--primary);
  font-weight: 600;
  transition: all 0.2s;
}
.job-card-premium:hover .jcp-action {
  transform: translateX(3px);
}

/* ========== 宣讲会卡片 ========== */
.talk-card-premium {
  background: white;
  border-radius: var(--radius-lg);
  padding: 22px;
  display: flex;
  gap: 18px;
  border: 1px solid rgba(226,232,240,0.7);
  box-shadow: var(--shadow-sm);
  transition: all 0.3s cubic-bezier(0.4,0,0.2,1);
  animation: fadeInUp 0.5s ease forwards;
  opacity: 0;
}
.talk-card-premium:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-lg);
  border-color: rgba(245,158,11,0.25);
}
.tcp-date-box {
  width: 64px;
  height: 68px;
  border-radius: var(--radius-md);
  background: linear-gradient(135deg, #F59E0B 0%, #D97706 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: white;
}
.tcp-month {
  font-size: 12px;
  font-weight: 600;
  opacity: 0.85;
}
.tcp-day {
  font-size: 24px;
  font-weight: 800;
  line-height: 1;
}
.tcp-content { flex: 1; min-width: 0; }
.tcp-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 10px;
  line-height: 1.35;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.tcp-info-row {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 6px;
}
.tcp-progress {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 12px;
}
.progress-track {
  flex: 1;
  height: 6px;
  background: rgba(226,232,240,0.8);
  border-radius: var(--radius-full);
  overflow: hidden;
}
.progress-fill {
  height: 100%;
  border-radius: var(--radius-full);
  background: linear-gradient(90deg, #F59E0B, #FBBF24);
  transition: width 0.8s ease;
}
.progress-text {
  font-size: 11.5px;
  color: var(--text-muted);
  font-weight: 500;
  white-space: nowrap;
}

/* ========== CTA 区域 ========== */
.cta-section {
  position: relative;
  padding: 88px 24px;
  text-align: center;
  background: linear-gradient(135deg, #1e3a5f 0%, #0F172A 100%);
  overflow: hidden;
}
.cta-bg-decor {
  position: absolute;
  inset: 0;
  pointer-events: none;
}
.cta-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(90px);
  opacity: 0.25;
}
.cta-orb-1 {
  width: 350px; height: 350px;
  background: #3B82F6;
  top: -80px; right: 10%;
}
.cta-orb-2 {
  width: 280px; height: 280px;
  background: #8B5CF6;
  bottom: -60px; left: 15%;
}
.cta-content {
  position: relative;
  z-index: 10;
  max-width: 520px;
  margin: 0 auto;
}
.cta-title {
  font-size: 36px;
  font-weight: 800;
  color: white;
  margin-bottom: 12px;
  letter-spacing: -0.03em;
}
.cta-desc {
  font-size: 16px;
  color: rgba(255,255,255,0.6);
  margin-bottom: 32px;
}
.cta-buttons {
  display: flex;
  justify-content: center;
  gap: 14px;
}
.cta-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 13px 30px;
  border-radius: var(--radius-full);
  font-size: 15px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.25s ease;
}
.cta-btn.primary {
  background: linear-gradient(135deg, #3B82F6 0%, #8B5CF6 100%);
  color: white;
  box-shadow: 0 4px 16px rgba(59,130,246,0.35);
}
.cta-btn.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(59,130,246,0.45);
}
.cta-btn.secondary {
  background: rgba(255,255,255,0.08);
  color: rgba(255,255,255,0.85);
  border: 1px solid rgba(255,255,255,0.15);
  backdrop-filter: blur(10px);
}
.cta-btn.secondary:hover {
  background: rgba(255,255,255,0.14);
  border-color: rgba(255,255,255,0.25);
  transform: translateY(-2px);
}

/* ========== 页脚 ========== */
.site-footer {
  background: #0F172A;
  color: rgba(255,255,255,0.6);
  padding: 48px 24px 0;
}
.footer-inner {
  max-width: 1100px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  gap: 40px;
  padding-bottom: 36px;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}
.footer-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: white;
  margin-bottom: 8px;
}
.footer-desc { font-size: 14px; color: rgba(255,255,255,0.4); }
.footer-links { display: flex; gap: 56px; }
.f-col h4 {
  color: white;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 14px;
}
.f-col a {
  display: block;
  color: rgba(255,255,255,0.45);
  text-decoration: none;
  font-size: 13.5px;
  margin-bottom: 10px;
  transition: color 0.2s;
}
.f-col a:hover { color: rgba(255,255,255,0.8); }
.footer-bottom {
  text-align: center;
  padding: 20px;
  font-size: 13px;
  color: rgba(255,255,255,0.3);
}

/* ========== 空状态 ========== */
.empty-state-card {
  text-align: center;
  padding: 60px 24px;
  color: var(--text-muted);
  background: white;
  border-radius: var(--radius-lg);
  border: 1px solid rgba(226,232,240,0.6);
}
.empty-state-card p { margin-top: 12px; font-size: 14px; }

/* ========== 动画 ========== */
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
@keyframes fadeInDown {
  from { opacity: 0; transform: translateY(-16px); }
  to { opacity: 1; transform: translateY(0); }
}
.animate-fade-in-up { animation: fadeInUp 0.5s ease forwards; }
.animate-fade-in-down { animation: fadeInDown 0.5s ease forwards; }
.delay-1 { animation-delay: 0.1s; opacity: 0; }
.delay-2 { animation-delay: 0.2s; opacity: 0; }
.delay-3 { animation-delay: 0.3s; opacity: 0; }
.delay-4 { animation-delay: 0.4s; opacity: 0; }
.delay-5 { animation-delay: 0.5s; opacity: 0; }
.delay-6 { animation-delay: 0.6s; opacity: 0; }

/* ========== 响应式 ========== */
@media (max-width: 1024px) {
  .stats-container { grid-template-columns: repeat(2, 1fr); }
  .hero-title { font-size: 40px; }
  .cta-title { font-size: 30px; }
}

@media (max-width: 768px) {
  .hero-title { font-size: 32px; }
  .hero-section { min-height: auto; padding: 100px 16px 60px; }
  .home-nav { padding: 12px 16px; }
  .nav-links .nav-link:not(.nav-login-btn) { display: none; }
  .glass-search-bar { flex-direction: column; padding: 12px; border-radius: var(--radius-lg); }
  .search-input { text-align: center; }
  .search-btn { width: 100%; justify-content: center; }
  .stats-container { grid-template-columns: repeat(2, 1fr); gap: 12px; }
  .stat-card-home { padding: 16px 14px; }
  .sc-value { font-size: 24px; }
  .trust-indicators { display: none; }
  .scroll-hint { display: none; }
  .footer-inner { flex-direction: column; gap: 28px; }
  .footer-links { gap: 32px; }
  .sec-header { flex-direction: column; align-items: flex-start; gap: 12px; }
  .cta-buttons { flex-direction: column; align-items: center; }
  .cta-section { padding: 56px 16px; }
  .talk-card-premium { flex-direction: column; }
  .tcp-date-box { width: auto; height: auto; flex-direction: row; gap: 8px; padding: 8px 14px; border-radius: var(--radius-full); }
}
</style>
