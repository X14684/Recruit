<template>
  <div class="login-page">
    <!-- 背景装饰 -->
    <div class="login-bg-decor">
      <div class="bg-orb bg-orb-1"></div>
      <div class="bg-orb bg-orb-2"></div>
      <div class="bg-orb bg-orb-3"></div>
      <div class="bg-grid"></div>
    </div>

    <div class="login-container animate-fade-in-up">
      <!-- 左侧品牌区 -->
      <div class="login-brand">
        <div class="brand-glow"></div>
        <div class="brand-content">
          <div class="brand-logo">
            <el-icon :size="44"><School /></el-icon>
          </div>
          <h1>校园招聘系统</h1>
          <p>连接学生与企业<br/>共创美好未来</p>

          <!-- 特性列表 -->
          <ul class="brand-features">
            <li class="animate-fade-in-left delay-2" style="opacity:0; animation-fill-mode:forwards;">
              <span class="feature-icon blue"><el-icon><Briefcase /></el-icon></span>
              <span><strong>海量职位</strong> 精准匹配推荐</span>
            </li>
            <li class="animate-fade-in-left delay-3" style="opacity:0; animation-fill-mode:forwards;">
              <span class="feature-icon green"><el-icon><OfficeBuilding /></el-icon></span>
              <span><strong>优质企业</strong> 校企直通对接</span>
            </li>
            <li class="animate-fade-in-left delay-4" style="opacity:0; animation-fill-mode:forwards;">
              <span class="feature-icon orange"><el-icon><Calendar /></el-icon></span>
              <span><strong>宣讲会</strong> 面对面交流机会</span>
            </li>
            <li class="animate-fade-in-left delay-5" style="opacity:0; animation-fill-mode:forwards;">
              <span class="feature-icon purple"><el-icon><DataAnalysis /></el-icon></span>
              <span><strong>数据洞察</strong> 就业趋势一目了然</span>
            </li>
          </ul>
        </div>

        <!-- 底部装饰 -->
        <div class="brand-footer">
          <div class="brand-stats-mini">
            <div class="bs-item">
              <span class="bs-num">5000+</span>
              <span class="bs-label">学生</span>
            </div>
            <div class="bs-divider"></div>
            <div class="bs-item">
              <span class="bs-num">200+</span>
              <span class="bs-label">企业</span>
            </div>
            <div class="bs-divider"></div>
            <div class="bs-item">
              <span class="bs-num">95%</span>
              <span class="bs-label">就业率</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单（玻璃效果） -->
      <div class="login-form-area">
        <div class="glass-login-form">
          <!-- 表单头部 -->
          <div class="form-header">
            <h2>欢迎回来</h2>
            <p class="form-subtitle">请选择您的身份登录系统</p>
          </div>

          <!-- 角色选择 -->
          <div class="role-selector">
            <div
              v-for="r in roles"
              :key="r.value"
              :class="['role-option', { active: form.role === r.value }]"
              @click="form.role = r.value"
            >
              <div class="ro-icon" :class="{ [`bg-${r.color}`]: true }">
                <el-icon :size="20"><component :is="r.icon" /></el-icon>
              </div>
              <span class="ro-label">{{ r.label }}</span>
            </div>
          </div>

          <!-- 登录表单 -->
          <el-form :model="form" :rules="rules" ref="formRef" @keyup.enter="handleLogin">
            <el-form-item prop="username">
              <div class="field-wrapper">
                <div class="field-icon"><el-icon :size="17"><User /></el-icon></div>
                <el-input
                  v-model="form.username"
                  placeholder="请输入用户名"
                  size="large"
                  autocomplete="username"
                />
              </div>
            </el-form-item>
            <el-form-item prop="password">
              <div class="field-wrapper">
                <div class="field-icon"><el-icon :size="17"><Lock /></el-icon></div>
                <el-input
                  v-model="form.password"
                  type="password"
                  placeholder="请输入密码"
                  size="large"
                  show-password
                  autocomplete="current-password"
                />
              </div>
            </el-form-item>

            <button
              type="button"
              class="login-submit-btn"
              :class="{ loading }"
              @click="handleLogin"
              :disabled="loading"
            >
              <span v-if="!loading">登 录</span>
              <span v-else class="btn-loading">
                <svg class="spinner" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10" stroke-width="3" fill="none"/></svg>
                登录中...
              </span>
            </button>
          </el-form>

          <!-- 底部链接 -->
          <div class="form-bottom">
            <template v-if="form.role === 'STUDENT'">
              还没有账号？
              <router-link to="/register?type=student">注册学生账号</router-link>
            </template>
            <template v-else-if="form.role === 'COMPANY'">
              企业还未入驻？
              <router-link to="/register?type=company">申请企业入驻</router-link>
            </template>
            <template v-else>
              <router-link to="/home" class="back-home">
                <el-icon><HomeFilled /></el-icon> 返回首页
              </router-link>
            </template>
          </div>

          <!-- 安全提示 -->
          <div class="security-hint">
            <el-icon color="#94A3B8" :size="13"><Lock /></el-icon>
            <span>您的数据经过加密保护传输</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const roles = [
  { value: 'STUDENT', label: '学生', icon: 'User', color: 'blue' },
  { value: 'COMPANY', label: '企业', icon: 'OfficeBuilding', color: 'green' },
  { value: 'COLLEGE', label: '学院', icon: 'School', color: 'orange' },
  { value: 'ADMIN', label: '管理员', icon: 'Setting', color: 'purple' }
]

const form = reactive({
  username: '',
  password: '',
  role: 'STUDENT'
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await login(form)
    // 使用表单选择的角色（后端userInfo中没有返回role字段）
    const userRole = form.role
    userStore.setLoginInfo(res.data.token, res.data.userInfo, userRole)
    ElMessage.success('登录成功')
    const roleMap = {
      STUDENT: '/student/dashboard',
      COMPANY: '/company/dashboard',
      COLLEGE: '/college/dashboard',
      ADMIN: '/admin/dashboard'
    }
    const targetPath = roleMap[userRole]
    if (!targetPath) {
      ElMessage.error('用户角色未知，无法跳转')
      return
    }
    await router.push(targetPath)
  } catch (e) {
    // 错误已由 request.js 拦截器处理
    console.error('登录失败:', e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* ===== 页面容器 ===== */
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0F172A 0%, #1e3a5f 40%, #2563EB 100%);
  position: relative;
  overflow: hidden;
  padding: 24px;
}

/* ===== 背景装饰 ===== */
.login-bg-decor {
  position: absolute;
  inset: 0;
  pointer-events: none;
}
.bg-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(90px);
  opacity: 0.35;
}
.bg-orb-1 {
  width: 420px; height: 420px;
  background: #3B82F6;
  top: -120px; right: -80px;
  animation: floatOrb 9s ease-in-out infinite;
}
.bg-orb-2 {
  width: 320px; height: 320px;
  background: #8B5CF6;
  bottom: -80px; left: -60px;
  animation: floatOrb 11s ease-in-out infinite reverse;
}
.bg-orb-3 {
  width: 200px; height: 200px;
  background: #06B6D4;
  top: 50%; left: 45%;
  animation: floatOrb 14s ease-in-out infinite;
}
@keyframes floatOrb {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(25px, -18px) scale(1.06); }
  66% { transform: translate(-18px, 16px) scale(0.95); }
}

.bg-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255,255,255,0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,0.03) 1px, transparent 1px);
  background-size: 56px 56px;
}

/* ===== 主容器 ===== */
.login-container {
  display: flex;
  width: 920px;
  min-height: 600px;
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow:
    0 32px 64px rgba(0,0,0,0.25),
    0 0 0 1px rgba(255,255,255,0.08);
  position: relative;
  z-index: 10;
}

/* ===== 品牌区域 ===== */
.login-brand {
  width: 380px;
  flex-shrink: 0;
  background: linear-gradient(160deg, #1e3a5f 0%, #0F172A 70%, #1e293b 100%);
  padding: 48px 36px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  position: relative;
  overflow: hidden;
}

.brand-glow {
  position: absolute;
  top: -80px; right: -80px;
  width: 220px; height: 220px;
  background: radial-gradient(circle, rgba(59,130,246,0.2), transparent 70%);
  filter: blur(30px);
  pointer-events: none;
}

.brand-content {
  position: relative;
  z-index: 2;
}

.brand-logo {
  width: 68px; height: 68px;
  border-radius: 18px;
  background: linear-gradient(135deg, #3B82F6 0%, #8B5CF6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-bottom: 22px;
  box-shadow: 0 8px 24px rgba(59,130,246,0.4);
}

.brand-content h1 {
  font-size: 26px;
  font-weight: 800;
  color: white;
  letter-spacing: -0.02em;
  margin-bottom: 10px;
}
.brand-content > p {
  font-size: 15px;
  color: rgba(255,255,255,0.55);
  line-height: 1.7;
  margin-bottom: 32px;
}

/* 特性列表 */
.brand-features {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.brand-features li {
  display: flex;
  align-items: center;
  gap: 12px;
  color: rgba(255,255,255,0.75);
  font-size: 13.5px;
  line-height: 1.45;
}
.feature-icon {
  width: 34px; height: 34px;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 16px;
  color: white;
}
.feature-icon.blue { background: rgba(59,130,246,0.2); color: #60A5FA; }
.feature-icon.green { background: rgba(16,185,129,0.2); color: #34D399; }
.feature-icon.orange { background: rgba(245,158,11,0.2); color: #FBBF24; }
.feature-icon.purple { background: rgba(139,92,246,0.2); color: #A78BFA; }

.brand-features li strong {
  color: rgba(255,255,255,0.92);
}

/* 品牌底部统计 */
.brand-footer {
  border-top: 1px solid rgba(255,255,255,0.07);
  padding-top: 20px;
}
.brand-stats-mini {
  display: flex;
  justify-content: space-around;
  align-items: center;
}
.bs-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.bs-num {
  font-size: 20px;
  font-weight: 800;
  color: white;
}
.bs-label {
  font-size: 11px;
  color: rgba(255,255,255,0.4);
  margin-top: 2px;
}
.bs-divider {
  width: 1px;
  height: 28px;
  background: rgba(255,255,255,0.1);
}

/* ===== 登录表单区域（玻璃效果） ===== */
.login-form-area {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 42px;
  background: rgba(255,255,255,0.92);
  backdrop-filter: blur(30px) saturate(180%);
  -webkit-backdrop-filter: blur(30px) saturate(180%);
}

.glass-login-form {
  width: 100%;
  max-width: 380px;
}

/* 表头 */
.form-header {
  margin-bottom: 28px;
}
.form-header h2 {
  font-size: 26px;
  font-weight: 800;
  color: var(--text-primary);
  letter-spacing: -0.02em;
  margin: 0 0 6px;
}
.form-subtitle {
  font-size: 14px;
  color: var(--text-muted);
}

/* 角色选择器 */
.role-selector {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
  margin-bottom: 26px;
}

.role-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 12px 4px;
  border-radius: var(--radius-md);
  border: 1.5px solid rgba(226,232,240,0.85);
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4,0,0.2,1);
  background: white;
}
.role-option:hover {
  border-color: rgba(59,130,246,0.4);
  background: rgba(59,130,246,0.02);
  transform: translateY(-1px);
}
.role-option.active {
  border-color: var(--primary);
  background: rgba(59,130,246,0.05);
  box-shadow: 0 0 0 3px rgba(59,130,246,0.08);
}

.ro-icon {
  width: 38px; height: 38px;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.25s ease;
  color: #64748B;
}
.ro-icon.bg-blue { background: rgba(148,163,184,0.12); }
.ro-icon.bg-green { background: rgba(148,163,184,0.12); }
.ro-icon.bg-orange { background: rgba(148,163,184,0.12); }
.ro-icon.bg-purple { background: rgba(148,163,184,0.12); }

.role-option.active .ro-icon.bg-blue { background: rgba(59,130,246,0.12); color: var(--primary); }
.role-option.active .ro-icon.bg-green { background: rgba(16,185,129,0.12); color: var(--success); }
.role-option.active .ro-icon.bg-orange { background: rgba(245,158,11,0.12); color: var(--warning); }
.role-option.active .ro-icon.bg-purple { background: rgba(139,92,246,0.12); color: #8B5CF6; }

.ro-label {
  font-size: 11.5px;
  font-weight: 550;
  color: var(--text-secondary);
  transition: all 0.2s;
}
.role-option.active .ro-label {
  color: var(--text-primary);
  font-weight: 650;
}

/* 输入框包装 */
.field-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}
.field-icon {
  position: absolute;
  left: 13px;
  z-index: 1;
  color: var(--text-muted);
  transition: color 0.2s;
  display: flex;
  align-items: center;
}
.field-wrapper:focus-within .field-icon {
  color: var(--primary);
}
.field-wrapper :deep(.el-input__wrapper) {
  border-radius: var(--radius-md) !important;
  padding-left: 40px !important;
  box-shadow: 0 0 0 1px rgba(226,232,240,0.9) !important;
  transition: all 0.25s ease !important;
}
.field-wrapper :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px rgba(59,130,246,0.4) !important;
}
.field-wrapper :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(59,130,246,0.15) !important;
  border-color: transparent !important;
}

/* 提交按钮 */
.login-submit-btn {
  width: 100%;
  height: 46px;
  border: none;
  border-radius: var(--radius-md);
  background: linear-gradient(135deg, #3B82F6 0%, #8B5CF6 100%);
  color: white;
  font-size: 15.5px;
  font-weight: 650;
  cursor: pointer;
  margin-top: 6px;
  letter-spacing: 0.04em;
  transition: all 0.3s cubic-bezier(0.4,0,0.2,1);
  box-shadow: 0 4px 16px rgba(59,130,246,0.3);
  position: relative;
  overflow: hidden;
}
.login-submit-btn::before {
  content: '';
  position: absolute;
  top: 0; left: -100%;
  width: 100%; height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.15), transparent);
  transition: left 0.5s ease;
}
.login-submit-btn:hover::before { left: 100%; }
.login-submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 22px rgba(59,130,246,0.4);
}
.login-submit-btn:active:not(:disabled) {
  transform: translateY(0);
}
.login-submit-btn.loading,
.login-submit-btn:disabled {
  opacity: 0.8;
  cursor: not-allowed;
  transform: none;
}

.btn-loading {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
.spinner {
  width: 18px; height: 18px;
  animation: spin 0.8s linear infinite;
  stroke: currentColor;
}
.spinner circle {
  stroke-dasharray: 63;
  stroke-dashoffset: 45;
  stroke-linecap: round;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* 底部链接 */
.form-bottom {
  text-align: center;
  font-size: 14px;
  color: var(--text-muted);
  margin-top: 20px;
}
.form-bottom a {
  color: var(--primary);
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
}
.form-bottom a:hover { color: var(--primary-dark); }

.back-home {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: var(--text-secondary) !important;
}
.back-home:hover { color: var(--primary) !important; }

/* 安全提示 */
.security-hint {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  margin-top: 18px;
  font-size: 12px;
  color: var(--text-muted);
  opacity: 0.65;
}

/* ===== 动画 ===== */
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(24px); }
  to { opacity: 1; transform: translateY(0); }
}
@keyframes fadeInLeft {
  from { opacity: 0; transform: translateX(-16px); }
  to { opacity: 1; transform: translateX(0); }
}
.animate-fade-in-up { animation: fadeInUp 0.6s cubic-bezier(0.4,0,0.2,1) forwards; }
.animate-fade-in-left { animation: fadeInLeft 0.5s ease forwards; }
.delay-2 { animation-delay: 0.15s; }
.delay-3 { animation-delay: 0.25s; }
.delay-4 { animation-delay: 0.35s; }
.delay-5 { animation-delay: 0.45s; }

/* ===== 响应式 ===== */
@media (max-width: 900px) {
  .login-container {
    flex-direction: column;
    max-width: 440px;
    min-height: auto;
  }
  .login-brand {
    width: 100%;
    padding: 36px 28px;
  }
  .brand-features { display: none; }
  .brand-footer { display: none; }
  .login-form-area { padding: 32px 28px; }
  .role-selector { grid-template-columns: repeat(4, 1fr); }
}

@media (max-width: 480px) {
  .login-page { padding: 16px; }
  .login-container { border-radius: var(--radius-lg); }
  .login-brand { padding: 28px 20px; }
  .login-form-area { padding: 24px 20px; }
  .role-selector { gap: 6px; }
  .role-option { padding: 10px 2px; }
  .ro-icon { width: 34px; height: 34px; }
  .form-header h2 { font-size: 22px; }
}
</style>
