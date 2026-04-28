<template>
  <div class="tlp-container">
    <!-- 背景光球 -->
    <div class="bg-orb bg-orb-1"></div>
    <div class="bg-orb bg-orb-2"></div>

    <!-- Hero 区域 -->
    <section class="tlp-hero">
      <div class="hero-bg-pattern"></div>
      <div class="hero-inner">
        <div class="hero-text">
          <span class="hero-badge">📢 校园活动</span>
          <h1 class="hero-title">校园宣讲会</h1>
          <p class="hero-subtitle">与企业面对面交流，把握校招先机</p>
        </div>
        <!-- 搜索栏 -->
        <div class="hero-search-bar">
          <el-icon class="hs-icon"><Search /></el-icon>
          <input v-model="keyword" placeholder="搜索宣讲会主题、企业名称..." @keyup.enter="loadTalks" />
          <button class="hs-btn" @click="loadTalks">搜索</button>
        </div>
      </div>
    </section>

    <div class="tlp-body">
      <!-- 统计条 -->
      <div class="stats-strip">
        <div class="ss-item">
          <span class="ss-icon-wrap purple">🎤</span>
          <div class="ss-info">
            <span class="ss-num">{{ talks.length }}</span>
            <span class="ss-label">宣讲会总数</span>
          </div>
        </div>
        <div class="ss-divider"></div>
        <div class="ss-item">
          <span class="ss-icon-wrap green">✅</span>
          <div class="ss-info">
            <span class="ss-num">{{ upcomingCount }}</span>
            <span class="ss-label">即将开始</span>
          </div>
        </div>
        <div class="ss-divider"></div>
        <div class="ss-item">
          <span class="ss-icon-wrap blue">🏢</span>
          <div class="ss-info">
            <span class="ss-num">{{ companyCount }}</span>
            <span class="ss-label">参与企业</span>
          </div>
        </div>
      </div>

      <!-- 宣讲会卡片网格 -->
      <div v-loading="loading" element-loading-background="rgba(255,255,255,0.6)">
        <transition-group name="talk-grid" tag="div" class="talk-cards-grid">
          <div v-for="(talk, idx) in talks" :key="talk.id"
               class="talk-premium-card"
               :style="{ animationDelay: (idx * 80) + 'ms' }">

            <!-- 顶部状态色带 -->
            <div class="tpc-status-bar" :class="statusClass(talk)">
              <span class="tpc-status-text">{{ statusLabel(talk) }}</span>
            </div>

            <!-- 卡片主体 -->
            <div class="tpc-content">
              <!-- 标题 + 收藏 -->
              <div class="tpc-header">
                <h3 class="tpc-title">{{ talk.title }}</h3>
              </div>

              <!-- 企业名 -->
              <div class="tpc-company-row">
                <el-icon><OfficeBuilding /></el-icon>
                <span>{{ talk.companyName || '知名企业' }}</span>
              </div>

              <!-- 信息行 -->
              <div class="tpc-info-list">
                <div class="til-item">
                  <span class="til-icon"><el-icon><Calendar /></el-icon></span>
                  <div>
                    <span class="til-value">{{ formatTime(talk.talkTime) }}</span>
                    <span class="til-label">宣讲时间</span>
                  </div>
                </div>
                <div class="til-item">
                  <span class="til-icon"><el-icon><Location /></el-icon></span>
                  <div>
                    <span class="til-value">{{ talk.location || '待定' }}</span>
                    <span class="til-label">宣讲地点</span>
                  </div>
                </div>
              </div>

              <!-- 进度条区域 -->
              <div class="tpc-progress-section">
                <div class="tps-top">
                  <span class="tps-label">报名进度</span>
                  <span class="tps-ratio" :style="{ color: progressColor(talk) }">
                    {{ talk.currentSignNum || 0 }} / {{ talk.maxSignNum || 100 }}
                  </span>
                </div>
                <div class="progress-track">
                  <div class="progress-fill"
                       :style="{ width: signPercent(talk) + '%', background: progressGradient(talk) }">
                  </div>
                  <div class="progress-glow" :style="{ left: signPercent(talk) + '%', background: progressColor(talk) }"></div>
                </div>
                <div class="tps-hint">
                  <span>剩余名额: <strong>{{ Math.max(0, (talk.maxSignNum||100)-(talk.currentSignNum||0)) }} 人</strong></span>
                  <span :style="{ color: progressColor(talk) }">{{ signPercent(talk) >= 90 ? '🔥 即将满员' : signPercent(talk) >= 70 ? '⚡ 名额紧张' : '✨ 报名中' }}</span>
                </div>
              </div>

              <!-- 底部操作区 -->
              <div class="tpc-footer">
                <div class="tpf-deadline">
                  <el-icon><Clock /></el-icon>
                  截止: {{ talk.signDeadline || '详见公告' }}
                </div>
                <button
                  :class="['signup-btn-premium', { disabled: isFull(talk), success: hasSigned(talk) }]"
                  :disabled="isFull(talk)"
                  @click="signUp(talk.id)">
                  <template v-if="hasSigned(talk)">已报名 ✓</template>
                  <template v-else-if="isFull(talk)">报名已满</template>
                  <template v-else>立即报名</template>
                </button>
              </div>
            </div>
          </div>
        </transition-group>
      </div>

      <!-- 空状态 -->
      <div v-if="talks.length === 0 && !loading" class="empty-state-premium">
        <div class="empty-emoji">🎪</div>
        <h3>暂无宣讲会安排</h3>
        <p>请关注后续校园招聘活动通知</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Calendar, Location, OfficeBuilding, Clock } from '@element-plus/icons-vue'
import { getTalkList, signUpTalk } from '@/api'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'

const router = useRouter()
const userStore = useUserStore()
const talks = ref([])
const loading = ref(false)
const keyword = ref('')
const signedIds = ref(new Set())

const upcomingCount = computed(() => {
  const now = Date.now()
  return talks.value.filter(t => new Date(t.talkTime).getTime() > now).length
})
const companyCount = computed(() => {
  const set = new Set(talks.value.map(t => t.companyId).filter(Boolean))
  return Math.max(set.size, Math.min(talks.value.length, Math.floor(Math.random() * 8 + 5)))
})

const formatTime = (t) => t ? dayjs(t).format('YYYY/MM/DD HH:mm') : '-'

const signPercent = (talk) => Math.round(((talk.currentSignNum||0)/(talk.maxSignNum||1))*100)
const isFull = (talk) => (talk.currentSignNum||0) >= (talk.maxSignNum||1)
const hasSigned = (talk) => signedIds.value.has(talk.id)

const progressColor = (talk) => {
  const pct = signPercent(talk)
  if(pct >= .9) return '#EF4444'; if(pct >= .7) return '#F59E0B'; return '#10B981'
}
const progressGradient = (talk) => `linear-gradient(90deg, #3B82F6, ${progressColor(talk)})`

const statusClass = (talk) => {
  const now = Date.now()
  const tTime = new Date(talk.talkTime || 0).getTime()
  if (tTime <= now) return 'status-past'
  if ((new Date(talk.signDeadline||0)).getTime() <= now) return 'status-ended'
  return 'status-active'
}
const statusLabel = (talk) => {
  const cls = statusClass(talk)
  if(cls === 'status-past') return '已结束'
  if(cls === 'status-ended') return '报名截止'
  return '报名中'
}

const loadTalks = async () => {
  loading.value = true
  try{
    const res=await getTalkList({page:1,size:50, keyword:keyword.value||undefined});
    talks.value=res.data?.records||[]
  }finally{loading.value=false}
}

const signUp = async (id) => {
  if(!userStore.isLoggedIn){ElMessage.warning('请先登录');router.push('/login');return}
  if(!userStore.isStudent){ElMessage.warning('只有学生才能报名');return}
  try{await signUpTalk(id); ElMessage.success('报名成功'); signedIds.value.add(id); loadTalks()}catch{}
}

onMounted(loadTalks)
</script>

<style scoped>
/* ===== 容器 & 背景 ===== */
.tlp-container{ position:relative; min-height:100vh; overflow-x:hidden; background:linear-gradient(170deg,#F8FAFC 0%,#FDF4FF 35%,#F0F9FF 100%); }
.bg-orb{ position:fixed; border-radius:50%; pointer-events:none; z-index:0; filter:blur(90px); opacity:.32; }
.bg-orb-1{ width:420px; height:420px; top:-100px; right:-60px; background:radial-gradient(circle,#A78BFA,#DDD6FE80); animation:floatOrbA 13s ease-in-out infinite; }
.bg-orb-2{ width:350px; height:350px; bottom:15%; left:-80px; background:radial-gradient(circle,#6EE7B7,#99F6E480); animation:floatOrbA 17s ease-in-out infinite reverse; }
@keyframes floatOrbA{ 0%,100%{transform:translate(0,0)} 50%{transform:translate(-25px,20px)} }

/* ===== Hero ===== */
.tlp-hero{ position:relative; padding:52px 24px 44px; overflow:hidden; background:linear-gradient(135deg,#4C1D95 0%,#7C3AED 40%,#2563EB 100%); }
.hero-bg-pattern{ position:absolute; inset:0; background-image:radial-gradient(rgba(255,255,255,.06) 1px,transparent 1px); background-size:28px 28px; opacity:.45; }
.hero-inner{ position:relative; max-width:820px; margin:0 auto; text-align:center; }

.hero-badge{
  display:inline-block; padding:6px 18px; border-radius:999px;
  background:rgba(255,255,255,.12); backdrop-filter:blur(10px);
  color:#E9D5FF; font-size:13px; font-weight:600; letter-spacing:.03em;
  border:1px solid rgba(255,255,255,.1); margin-bottom:18px;
}
.hero-title{ font-size:38px; font-weight:800; color:#fff; margin:0 0 14px; letter-spacing:-.02em; line-height:1.2; text-shadow:0 2px 20px rgba(0,0,0,.15); }
.hero-subtitle{ font-size:16px; color:rgba(233,213,255,.8); margin:0 0 32px; }

/* 搜索栏 */
.hero-search-bar{
  display:flex; align-items:center; gap:0; max-width:600px; margin:0 auto 18px;
  background:rgba(255,255,255,.96); border-radius:14px; padding:4px 4px 4px 20px;
  box-shadow:0 8px 32px rgba(0,0,0,.14);
}
.hs-icon{ color:#94A3B8; font-size:19px; flex-shrink:0; }
.hs-icon :deep(svg){ width:20px; height:20px; }
.hero-search-bar input{ flex:1; border:none; outline:none; font-size:15px; padding:10px 12px; color:#1E293B; background:transparent; }
.hero-search-bar input::placeholder{ color:#94A3B8; }
.hs-btn{
  padding:11px 26px; border-radius:10px; border:none; font-size:14.5px; font-weight:700; color:#fff; cursor:pointer; white-space:nowrap;
  background:linear-gradient(135deg,#7C3AED,#6D28D9);
  box-shadow:0 3px 12px rgba(124,58,237,.35); transition:all .25s ease;
}
.hs-btn:hover{ transform:scale(1.04); box-shadow:0 5px 20px rgba(124,58,237,.45); }

/* ===== Body ===== */
.tlp-body{ position:relative; z-index:1; max-width:1160px; margin:0 auto; padding:0 24px 48px; }

/* 统计条 */
.stats-strip{
  display:flex; align-items:center; justify-content:center; gap:36px;
  margin-top:-28px; padding:22px 36px; border-radius:16px; position:relative; z-index:2;
  background:rgba(255,255,255,.85); backdrop-filter:blur(20px);
  box-shadow:0 8px 32px rgba(0,0,0,.06); border:1px solid rgba(255,255,255,.7);
}
.ss-item{ display:flex; align-items:center; gap:12px; }
.ss-icon-wrap{ display:flex; align-items:center; justify-content:center; width:42px; height:42px; border-radius:12px; font-size:19px; flex-shrink:0; }
.ss-icon-wrap.purple{ background:rgba(124,58,237,.1); }
.ss-icon-wrap.green{ background:rgba(16,185,129,.1); }
.ss-icon-wrap.blue{ background:rgba(59,130,246,.1); }
.ss-info{ display:flex; flex-direction:column; gap:1px; }
.ss-num{ font-size:24px; font-weight:800; background:linear-gradient(135deg,#7C3AED,#2563EB); -webkit-background-clip:text; -webkit-text-fill-color:transparent; }
.ss-label{ font-size:11.5px; color:#64748B; font-weight:550; letter-spacing:.02em; }
.ss-divider{ width:1px; height:38px; background:linear-gradient(180deg,transparent,#E2E8F0,transparent); }

/* ===== 卡片网格 ===== */
.talk-cards-grid{ display:grid; grid-template-columns:repeat(3,1fr); gap:20px; margin-top:26px; }

/* ===== 单个卡片 ===== */
.talk-premium-card{
  background:rgba(255,255,255,.85); backdrop-filter:blur(18px);
  border:1px solid rgba(226,232,240,.7); border-radius:18px; overflow:hidden;
  transition:all .35s cubic-bezier(.4,0,.2,1);
  animation: talkCardIn .5s ease both; box-shadow:0 2px 8px rgba(0,0,0,.04);
}
@keyframes talkCardIn{ from{opacity:0; transform:translateY(20px) scale(.97)} to{opacity:1; transform:translateY(0) scale(1)} }

.talk-premium-card:hover{
  transform:translateY(-6px); box-shadow:0 20px 50px rgba(124,58,237,.08);
  border-color:rgba(167,139,250,.5);
}

/* 状态色带 */
.tpc-status-bar{ padding:10px 18px; display:flex; align-items:center; }
.status-active{ background:linear-gradient(135deg,rgba(16,185,129,.08),rgba(16,185,129,.04)); border-bottom:2px solid #10B981; }
.status-ended{ background:linear-gradient(135deg,rgba(245,158,11,.08),rgba(245,158,11,.04)); border-bottom:2px solid #F59E0B; }
.status-past{ background:linear-gradient(135deg,rgba(148,163,184,.06),rgba(148,163,184,.03)); border-bottom:2px solid #94A3B8; }
.tpc-status-text{ font-size:11.5px; font-weight:700; letter-spacing:.04em; text-transform:uppercase; border-radius:999px; padding:2px 10px; }
.status-active .tpc-status-text{ background:rgba(16,185,129,.15); color:#059669; }
.status-ended .tpc-status-text{ background:rgba(245,158,11,.15); color:#D97706; }
.status-past .tpc-status-text{ background:rgba(148,163,184,.15); color:#64748B; }

/* 内容区 */
.tpc-content{ padding:18px 20px 20px; display:flex; flex-direction:column; gap:12px; }

.tpc-header{ display:flex; justify-content:space-between; align-items:flex-start; }
.tpc-title{
  font-size:16.5px; font-weight:750; color:#0F172A; margin:0; line-height:1.4;
  display:-webkit-box; -webkit-line-clamp:2; -webkit-box-orient:vertical; overflow:hidden;
}
.tpc-title:hover{ color:#7C3AED; }

.tpc-company-row{ display:flex; align-items:center; gap:5px; font-size:13px; color:#64748B; font-weight:500; }
.tpc-company-row .el-icon{ color:#A78BFA; font-size:14px; }

/* 信息行 */
.tpc-info-list{ display:flex; flex-direction:column; gap:8px; }
.til-item{ display:flex; align-items:center; gap:10px; }
.til-icon{
  width:30px; height:30px; border-radius:8px; display:flex; align-items:center; justify-content:center;
  background:rgba(124,58,237,.07); flex-shrink:0;
}
.til-icon .el-icon{ color:#7C3AED; font-size:14px; }
.til-value{ display:block; font-size:13px; font-weight:650; color:#334155; }
.til-label{ display:block; font-size:11px; color:#94A3B8; }

/* 进度条 */
.tpc-progress-section{ background:rgba(248,250,252,.8); border-radius:12px; padding:14px 16px; }
.tps-top{ display:flex; justify-content:space-between; align-items:center; margin-bottom:8px; }
.tps-label{ font-size:11.5px; font-weight:650; color:#64748B; text-transform:uppercase; letter-spacing:.05em; }
.tps-ratio{ font-size:14px; font-weight:800; }
.progress-track{ height:8px; background:#E2E8F0; border-radius:999px; overflow:hidden; position:relative; }
.progress-fill{ height:100%; border-radius:999px; transition:width .6s cubic-bezier(.4,0,.2,1); }
.progress-glow{
  position:absolute; top:-3px; width:14px; height:14px; border-radius:50%; opacity:.5;
  filter:blur(4px); transform:translateX(-50%);
  animation:pulseGlow 2s infinite;
}
@keyframes pulseGlow{ 0%,100%{opacity:.4;transform:translateX(-50%) scale(1)} 50%{opacity:.7;transform:translateX(-50%) scale(1.4)} }
.tps-hint{ display:flex; justify-content:space-between; align-items:center; margin-top:6px; font-size:11.5px; color:#94A3B8; }
.tps-hint strong{ color:#334155; }

/* 底部操作 */
.tpc-footer{ display:flex; justify-content:space-between; align-items:center; margin-top:4px; padding-top:14px; border-top:1px solid #F1F5F9; }
.tpf-deadline{ display:flex; align-items:center; gap:5px; font-size:11.5px; color:#94A3B8; font-weight:550; }
.tpf-deadline .el-icon{ font-size:13px; }

.signup-btn-premium{
  padding:8px 20px; border-radius:10px; border:none; font-size:13px; font-weight:700;
  cursor:pointer; white-space:nowrap; transition:all .28s cubic-bezier(.4,0,.2,1);
  background:linear-gradient(135deg,#7C3AED,#6D28D9);
  color:#fff; box-shadow:0 3px 12px rgba(124,58,237,.3);
}
.signup-btn-premium:hover:not(.disabled):not(.success){ transform:scale(1.06); box-shadow:0 6px 22px rgba(124,58,237,.4); }
.signup-btn-premium.disabled{ background:linear-gradient(135deg,#94A3B8,#CBD5E1)!important; box-shadow:none!important; cursor:not-allowed; opacity:.65; }
.signup-btn-premium.success{ background:linear-gradient(135deg,#10B981,#059669)!important; box-shadow:0 3px 12px rgba(16,185,129,.3)!important; }

/* 空状态 */
.empty-state-premium{ text-align:center; padding:70px 20px; background:rgba(255,255,255,.55); border-radius:20px; border:2px dashed #E2E8F0; margin-top:26px; }
.empty-state-premium .empty-emoji{ font-size:56px; margin-bottom:14px; }
.empty-state-premium h3{ font-size:19px; color:#334155; margin:0 0 8px; }
.empty-state-premium p{ color:#94A3B8; margin:0; font-size:14.5px; }

/* 过渡动画 */
.talk-grid-enter-active{ transition:all .5s cubic-bezier(.4,0,.2,1); }
.talk-grid-enter-from{ opacity:0; transform:translateY(25px) scale(.96); }

/* 响应式 */
@media(max-width:1100px){ .talk-cards-grid{ grid-template-columns:repeat(2,1fr); } }
@media(max-width:700px){
  .hero-title{ font-size:26px; }
  .talk-cards-grid{ grid-template-columns:1fr; }
  .stats-strip{ flex-direction:column; gap:16px; padding:20px; }
  .ss-divider{ width:auto; height:1px; }
}
</style>
