<template>
  <div class="jlp-container">
    <!-- 背景光球 -->
    <div class="bg-orb bg-orb-1"></div>
    <div class="bg-orb bg-orb-2"></div>

    <!-- Hero 区域 -->
    <section class="jlp-hero">
      <div class="hero-bg-pattern"></div>
      <div class="hero-inner">
        <div class="hero-text">
          <span class="hero-badge">🔥 热门招聘</span>
          <h1 class="hero-title">发现你的理想职位</h1>
          <p class="hero-subtitle">探索 {{ total }}+ 个优质岗位，开启职业生涯新篇章</p>
        </div>
        <!-- 快速搜索 -->
        <div class="hero-search-bar">
          <el-icon class="hs-icon"><Search /></el-icon>
          <input v-model="quickSearch" placeholder="搜索职位名称、公司或关键词..." @keyup.enter="doQuickSearch" />
          <button class="hs-btn" @click="doQuickSearch">搜索</button>
        </div>
        <!-- 热门标签 -->
        <div class="hot-tags">
          <span class="ht-label">热门：</span>
          <button v-for="t in hotKeywords" :key="t" class="ht-tag" @click="query.keyword=t;fetchJobs()">{{ t }}</button>
        </div>
      </div>
    </section>

    <div class="jlp-body">
      <!-- 统计条 -->
      <div class="stats-strip">
        <div class="ss-item"><span class="ss-num">{{ total }}</span><span class="ss-label">职位总数</span></div>
        <div class="ss-divider"></div>
        <div class="ss-item"><span class="ss-num">{{ todayNew }}</span><span class="ss-label">今日新增</span></div>
        <div class="ss-divider"></div>
        <div class="ss-item"><span class="ss-num">{{ companies }}</span><span class="ss-label">参与企业</span></div>
      </div>

      <!-- 高级过滤面板 -->
      <div class="filter-section glass-panel-premium">
        <div class="fs-header">
          <h3 class="fs-title"><el-icon><Filter /></el-icon> 筛选条件</h3>
          <button class="fs-reset" @click="resetQuery">
            <el-icon><RefreshRight /></el-icon> 重置
          </button>
        </div>
        <div class="fs-body">
          <div class="fs-row">
            <div class="fs-field">
              <label>关键词</label>
              <el-input v-model="query.keyword" placeholder="职位/企业" clearable :prefix-icon="Search" />
            </div>
            <div class="fs-field">
              <label>工作类型</label>
              <el-select v-model="query.jobType" placeholder="不限" clearable>
                <el-option v-for="t in jobTypes" :key="t" :label="t" :value="t" />
              </el-select>
            </div>
            <div class="fs-field">
              <label>工作地点</label>
              <el-input v-model="query.workLocation" placeholder="城市" clearable :prefix-icon="Location" />
            </div>
            <div class="fs-field">
              <label>学历要求</label>
              <el-select v-model="query.education" placeholder="不限" clearable>
                <el-option v-for="e in educations" :key="e" :label="e" :value="e" />
              </el-select>
            </div>
          </div>
          <div class="fs-actions">
            <button class="glass-btn-primary" @click="fetchJobs">
              <el-icon><Search /></el-icon> 搜索职位
            </button>
          </div>
        </div>
      </div>

      <!-- 结果区域 -->
      <div class="results-area">
        <div class="results-header">
          <h3 class="rh-title">
            <span class="rh-icon-wrap blue">💼</span>
            职位列表
          </h3>
          <span class="rh-count">共找到 <b>{{ total }}</b> 个匹配职位</span>
        </div>

        <!-- 加载骨架 -->
        <div v-if="loading" class="skeleton-grid">
          <div v-for="i in 5" :key="i" class="sk-card">
            <div class="sk-line sk-title"></div>
            <div class="sk-line sk-sub"></div>
            <div class="sk-tags"><span class="sk-tag" v-for="j in 4" :key="j"></span></div>
          </div>
        </div>

        <!-- 职位卡片 -->
        <template v-else>
          <transition-group name="card-list" tag="div" class="job-cards-list">
            <div v-for="(job, idx) in jobs" :key="job.id"
                 class="job-premium-card"
                 :style="{ animationDelay: (idx * 60) + 'ms' }"
                 @click="goDetail(job.id)">
              <!-- 左侧色带 -->
              <div class="jpc-accent" :class="'accent-' + (idx % 6)"></div>
              <div class="jpc-main">
                <!-- 头部 -->
                <div class="jpc-top-row">
                  <div class="jpc-left-info">
                    <h4 class="jpc-job-name">{{ job.jobName }}</h4>
                    <div class="jpc-company">
                      <el-icon><OfficeBuilding /></el-icon>
                      <span>{{ job.companyName || '知名企业' }}</span>
                    </div>
                  </div>
                  <div class="jpc-right-salary">
                    <span class="jpc-salary-text">{{ job.salaryRange || '薪资面议' }}</span>
                  </div>
                </div>

                <!-- 标签行 -->
                <div class="jpc-tag-row">
                  <span class="tag-pill blue"><el-icon><Location /></el-icon>{{ job.workLocation || '全国' }}</span>
                  <span class="tag-pill green"><el-icon><Reading /></el-icon>{{ job.educationReq || '学历不限' }}</span>
                  <span class="tag-pill purple"><el-icon><SuitcaseLine /></el-icon>{{ job.jobType || '全职' }}</span>
                  <span class="tag-pill amber"><el-icon><UserFilled /></el-icon>招{{ job.recruitNum }}人</span>
                  <span v-if="job.auditStatus === 1 && job.status === 1" class="status-dot-green">
                    <span class="dot-pulse"></span> 招聘中
                  </span>
                </div>

                <!-- 底部描述 -->
                <p class="jpc-desc">{{ job.description ? job.description.slice(0, 80) + '...' : '欢迎投递，期待你的加入！' }}</p>
              </div>
              <!-- 操作区 -->
              <div class="jpc-action">
                <button class="apply-btn-premium" @click.stop="applyNow(job)">立即投递</button>
                <span class="jpc-time">{{ formatTime(job.createTime) }}</span>
              </div>
            </div>
          </transition-group>

          <!-- 空状态 -->
          <div v-if="jobs.length === 0 && !loading" class="empty-state-premium">
            <div class="empty-emoji">🔍</div>
            <h3>暂无符合条件的职位</h3>
            <p>试试调整筛选条件，或者浏览全部职位</p>
            <button class="glass-btn-primary" style="margin-top:16px" @click="resetQuery();fetchJobs()">查看全部</button>
          </div>

          <!-- 分页 -->
          <div v-if="total > query.size" class="pagination-premium">
            <el-pagination
              background layout="prev, pager, next"
              :total="total" :page-size="query.size"
              v-model:current-page="query.page"
              @current-change="fetchJobs" />
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Location, Reading, SuitcaseLine, UserFilled, Filter, RefreshRight, OfficeBuilding } from '@element-plus/icons-vue'
import { getJobList, applyJob } from '@/api'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

const router = useRouter(), route = useRoute(), userStore = useUserStore()
const loading = ref(false), jobs = ref([]), total = ref(0)
const companies = ref(0), todayNew = ref(0)
const quickSearch = ref('')
const jobTypes = ['全职','实习','兼职']
const educations = ['大专','本科','硕士','博士','不限']
const hotKeywords = ['Java开发','前端工程师','产品经理','UI设计师','运营专员','数据分析']

const query = reactive({
  keyword: route.query.keyword || '',
  jobType: '', workLocation: '', education: '',
  page: 1, size: 10
})

const fetchJobs = async () => {
  loading.value = true
  try {
    const res = await getJobList(query)
    jobs.value = res.data?.records || []
    total.value = Number(res.data?.total || 0)
    // 模拟统计数据
    companies.value = Math.min(total.value, Math.floor(Math.random() * 30 + 20))
    todayNew.value = Math.floor(Math.random() * 10 + 3)
  } finally { loading.value = false }
}

const doQuickSearch = () => {
  query.keyword = quickSearch.value
  query.page = 1
  fetchJobs()
}
const resetQuery = () => {
  Object.assign(query, { keyword:'', jobType:'', workLocation:'', education:'', page:1 })
  quickSearch.value = ''
  fetchJobs()
}
const goDetail = (id) => router.push(`/jobs/${id}`)
const formatTime = (t) => t ? dayjs(t).fromNow() : '-'

const applyNow = async (job) => {
  if (!userStore.isLoggedIn) { ElMessage.warning('请先登录'); router.push('/login'); return }
  if (!userStore.isStudent) { ElMessage.warning('只有学生才能投递'); return }
  try {
    await applyJob(job.id)
    ElMessage.success('投递成功！企业将尽快审核')
  } catch {}
}

onMounted(fetchJobs)
</script>

<style scoped>
/* ===== 容器 & 背景 ===== */
.jlp-container {
  position: relative; min-height: 100vh;
  background: linear-gradient(170deg, #F8FAFC 0%, #EEF2FF 40%, #F0F9FF 100%);
  overflow-x: hidden;
}
.bg-orb {
  position: fixed; border-radius: 50%; pointer-events: none; z-index: 0;
  filter: blur(100px); opacity: .35;
}
.bg-orb-1 { width:450px; height:450px; top:-120px; right:-80px; background: radial-gradient(circle, #93C5FD, #BFDBFE80); animation: floatOrb 12s ease-in-out infinite; }
.bg-orb-2 { width:380px; height:380px; bottom:10%; left:-100px; background: radial-gradient(circle, #C4B5FD, #DDD6FE80); animation: floatOrb 15s ease-in-out infinite reverse; }
@keyframes floatOrb { 0%,100%{transform:translate(0,0)} 50%{transform:translate(30px,-25px)} }

/* ===== Hero ===== */
.jlp-hero {
  position: relative; padding: 52px 24px 44px; overflow: hidden;
  background: linear-gradient(135deg, #1E3A5F 0%, #2563EB 50%, #7C3AED 100%);
}
.hero-bg-pattern {
  position: absolute; inset: 0;
  background-image: radial-gradient(rgba(255,255,255,.06) 1px, transparent 1px);
  background-size: 28px 28px; opacity: .5;
}
.hero-inner {
  position: relative; max-width:900px; margin:0 auto; text-align:center;
}
.hero-badge {
  display:inline-block; padding:6px 18px; border-radius:999px;
  background: rgba(255,255,255,.12); backdrop-filter: blur(10px);
  color:#E0E7FF; font-size:13px; font-weight:600; letter-spacing:.03em;
  border:1px solid rgba(255,255,255,.1); margin-bottom:18px;
}
.hero-title {
  font-size:38px; font-weight:800; color:#fff; margin:0 0 14px;
  letter-spacing: -.02em; line-height:1.2;
  text-shadow: 0 2px 20px rgba(0,0,0,.15);
}
.hero-subtitle { font-size:16px; color:rgba(224,231,255,.8); margin:0 0 32px; }

/* Hero 搜索栏 */
.hero-search-bar {
  display:flex; align-items:center; gap:0; max-width:640px; margin:0 auto 20px;
  background:rgba(255,255,255,.95); border-radius:14px; padding:4px 4px 4px 20px;
  box-shadow:0 8px 32px rgba(0,0,0,.12);
}
.hs-icon { color:#94A3B8; font-size:19px; flex-shrink:0; }
.hs-icon :deep(svg) { width:20px; height:20px; }
.hero-search-bar input {
  flex:1; border:none; outline:none; font-size:15px; padding:10px 12px; color:#1E293B;
  background:transparent;
}
.hero-search-bar input::placeholder { color:#94A3B8; }
.hs-btn {
  padding:11px 26px; border-radius:10px; border:none; font-size:14.5px; font-weight:700;
  color:#fff; cursor:pointer; white-space:nowrap;
  background:linear-gradient(135deg,#3B82F6,#2563EB);
  transition:all .25s ease; box-shadow:0 3px 12px rgba(59,130,246,.35);
}
.hs-btn:hover{ transform:scale(1.04); box-shadow:0 5px 20px rgba(59,130,246,.45); }

/* 热门标签 */
.hot-tags { display:flex; align-items:center; justify-content:center; gap:8px; flex-wrap:wrap; }
.ht-label { color:rgba(224,231,255,.65); font-size:13px; }
.ht-tag {
  padding:5px 14px; border-radius:999px; border:1px solid rgba(255,255,255,.18);
  background:rgba(255,255,255,.08); color:rgba(255,255,255,.85);
  font-size:12.5px; cursor:pointer; transition:all .2s ease; font-weight:500;
}
.ht-tag:hover{ background:rgba(255,255,255,.18); color:#fff; border-color:rgba(255,255,255,.3); }

/* ===== Body ===== */
.jlp-body{ position:relative; z-index:1; max-width:1160px; margin:0 auto; padding:0 24px 48px; }

/* 统计条 */
.stats-strip{
  display:flex; align-items:center; justify-content:center; gap:36px;
  margin-top:-28px; padding:20px 40px; border-radius:16px; position:relative; z-index:2;
  background:rgba(255,255,255,.85); backdrop-filter: blur(20px);
  box-shadow:0 8px 32px rgba(0,0,0,.06); border:1px solid rgba(255,255,255,.7);
}
.ss-item{ display:flex; flex-direction:column; align-items:center; gap:2px; }
.ss-num{ font-size:28px; font-weight:800; background:linear-gradient(135deg,#3B82F6,#7C3AED); -webkit-background-clip:text; -webkit-text-fill-color:transparent; }
.ss-label{ font-size:12.5px; color:#64748B; font-weight:550; letter-spacing:.02em; }
.ss-divider{ width:1px; height:36px; background:linear-gradient(180deg,transparent,#E2E8F0,transparent); }

/* 过滤面板 */
.filter-section{ margin-bottom:24px; border-radius:18px!important; overflow:hidden; border:none!important; background:rgba(255,255,255,.78)!important; backdrop-filter: blur(20px); box-shadow:0 4px 24px rgba(0,0,0,.05); border:1px solid rgba(255,255,255,.9)!important; }
.fs-header{ display:flex; justify-content:space-between; align-items:center; padding:18px 24px 0; }
.fs-title{ display:flex; align-items:center; gap:8px; font-size:16px; font-weight:700; color:#1E293B; margin:0; }
.fs-title .el-icon{ color:#3B82F6; font-size:18px; }
.fs-reset{ display:flex; align-items:center; gap:4px; padding:6px 14px; border-radius:8px; border:1px solid #E2E8F0; background:#fff; font-size:13px; color:#64748B; cursor:pointer; transition:all .2s ease; }
.fs-reset:hover{ color:#EF4444; border-color:#FECACA; background:#FEF2F2; }
.fs-body{ padding:18px 24px 22px; }
.fs-row{ display:grid; grid-template-columns:repeat(4, 1fr); gap:16px; }
.fs-field{ display:flex; flex-direction:column; gap:6px; }
.fs-field label{ font-size:11.5px; font-weight:650; color:#64748B; text-transform:uppercase; letter-spacing:.06em; }
.fs-field :deep(.el-input__wrapper){ border-radius:10px; box-shadow:0 0 0 1px #E2E8F0; transition:all .2s ease; }
.fs-field :deep(.el-input__wrapper:hover){ box-shadow:0 0 0 1px #93C5FD; }
.fs-field :deep(.el-select){ width:100%; }
.fs-actions{ display:flex; justify-content:flex-end; margin-top:16px; }

.glass-btn-primary{
  display:inline-flex; align-items:center; gap:6px; padding:10px 28px; border-radius:11px; border:none;
  font-size:14px; font-weight:700; color:#fff; cursor:pointer;
  background:linear-gradient(135deg,#3B82F6,#2563EB);
  box-shadow:0 4px 16px rgba(59,130,246,.3); transition:all .25s cubic-bezier(.4,0,.2,1);
}
.glass-btn-primary:hover{ transform:translateY(-2px); box-shadow:0 8px 26px rgba(59,130,246,.4); }

/* ===== 结果区域 ===== */
.results-area{ }
.results-header{ display:flex; justify-content:space-between; align-items:center; margin-bottom:18px; }
.rh-title{ display:flex; align-items:center; gap:10px; font-size:18px; font-weight:750; color:#1E293B; margin:0; }
.rh-icon-wrap{ display:inline-flex; align-items:center; justify-content:center; width:34px; height:34px; border-radius:10px; font-size:17px; }
.rh-icon-wrap.blue{ background:rgba(59,130,246,.1); }
.rh-count{ font-size:13.5px; color:#64748B; }
.rh-count b{ color:#3B82F6; font-weight:800; font-size:16px; margin:0 3px; }

/* 骨架屏 */
.skeleton-grid{ display:flex; flex-direction:column; gap:14px; }
.sk-card{ background:#fff; border-radius:14px; padding:22px 26px; border:1px solid #F1F5F9; }
.sk-line{ background:linear-gradient(90deg,#F1F5F9,#E2E8F0,#F1F5F9); background-size:200% 100%; animation:shimmer 1.5s infinite; border-radius:6px; }
.sk-title{ height:20px; width:45%; margin-bottom:12px; }
.sk-sub{ height:14px; width:28%; margin-bottom:16px; }
.sk-tags{ display:flex; gap:8px; }
.sk-tag{ height:24px; width:70px; border-radius:999px; }
@keyframes shimmer{ to{background-position-x:-200%} }

/* ===== 职位卡片 ===== */
.job-cards-list{ display:flex; flex-direction:column; gap:12px; }

.job-premium-card{
  display:flex; align-items:stretch; background:rgba(255,255,255,.88); backdrop-filter: blur(16px);
  border:1px solid rgba(226,232,240,.75); border-radius:16px; overflow:hidden;
  cursor:pointer; transition:all .3s cubic-bezier(.4,0,.2,1);
  animation: cardIn .4s ease both; box-shadow:0 1px 4px rgba(0,0,0,.03);
}
@keyframes cardIn{ from{opacity:0; transform:translateY(12px)} to{opacity:1; transform:translateY(0)} }

.job-premium-card:hover{ transform:translateY(-3px) translateX(2px); box-shadow:0 12px 40px rgba(59,130,246,.08); border-color:rgba(148,163,184,.55); }
.job-premium-card:hover .jpc-action .apply-btn-premium{ transform:scale(1.06); box-shadow:0 6px 24px rgba(59,130,246,.4); }

/* 左侧色带 */
.jpc-accent{ width:5px; flex-shrink:0; transition:width .25s ease; }
.job-premium-card:hover .jpc-accent{ width:6px; }
.accent-0{ background:linear-gradient(180deg,#3B82F6,#60A5FA); }
.accent-1{ background:linear-gradient(180deg,#8B5CF6,#A78BFA); }
.accent-2{ background:linear-gradient(180deg,#10B981,#34D399); }
.accent-3{ background:linear-gradient(180deg,#F59E0B,#FBBF24); }
.accent-4{ background:linear-gradient(180deg,#EF4444,#F87171); }
.accent-5{ background:linear-gradient(180deg,#EC4899,#F472B6); }

.jpc-main{ flex:1; padding:20px 24px; min-width:0; display:flex; flex-direction:column; gap:10px; }
.jpc-top-row{ display:flex; justify-content:space-between; align-items:flex-start; }
.jpc-left-info{ display:flex; flex-direction:column; gap:5px; }
.jpc-job-name{ font-size:18px; font-weight:750; color:#0F172A; margin:0; line-height:1.3; letter-spacing:-.01em; transition:color .2s ease; }
.job-premium-card:hover .jpc-job-name{ color:#2563EB; }
.jpc-company{ display:flex; align-items:center; gap:5px; font-size:13.5px; color:#64748B; font-weight:500; }
.jpc-company .el-icon{ color:#94A3B8; font-size:14px; }
.jpc-right-salary{ flex-shrink:0; }
.jpc-salary-text{ font-size:21px; font-weight:800; color:#EF4444; white-space:nowrap; }

/* 标签行 */
.jpc-tag-row{ display:flex; gap:8px; flex-wrap:wrap; align-items:center; }
.tag-pill{
  display:inline-flex; align-items:center; gap:4px; padding:4px 11px;
  border-radius:999px; font-size:11.5px; font-weight:600; white-space:nowrap;
  transition:all .2s ease;
}
.tag-pill.blue{ background:rgba(59,130,246,.08); color:#2563EB; }
.tag-pill.green{ background:rgba(16,185,129,.08); color:#059669; }
.tag-pill.purple{ background:rgba(139,92,246,.08); color:#7C3AED; }
.tag-pill.amber{ background:rgba(245,158,11,.08); color:#D97706; }
.tag-pill .el-icon{ font-size:12.5px; }

.status-dot-green{
  display:inline-flex; align-items:center; gap:5px; padding:4px 12px;
  border-radius:999px; font-size:11.5px; font-weight:600; color:#059669;
  background:rgba(16,185,129,.08);
}
.dot-pulse{ width:7px; height:7px; border-radius:50%; background:#10B981; display:inline-block; animation:pulseDot 2s infinite; }
@keyframes pulseDot{ 0%,100%{box-shadow:0 0 0 0 rgba(16,185,129,.4)} 50%{box-shadow:0 0 0 5px rgba(16,185,129,0)} }

.jpc-desc{ font-size:13px; color:#94A3B8; line-height:1.5; margin:0; display:-webkit-box; -webkit-line-clamp:1; -webkit-box-orient:vertical; overflow:hidden; }

/* 操作区 */
.jpc-action{
  display:flex; flex-direction:column; align-items:center; justify-content:center; gap:10px;
  padding:0 20px; border-left:1px dashed #F1F5F9; flex-shrink:0;
  min-width:110px;
}
.apply-btn-premium{
  padding:10px 22px; border-radius:11px; border:none; font-size:13px; font-weight:700;
  color:#fff; cursor:pointer; white-space:nowrap;
  background:linear-gradient(135deg,#3B82F6,#2563EB);
  box-shadow:0 3px 14px rgba(59,130,246,.3);
  transition:all .25s cubic-bezier(.4,0,.2,1);
}
.apply-btn-premium:hover{ transform:scale(1.06); box-shadow:0 6px 24px rgba(59,130,246,.4); }
.jpc-time{ font-size:11px; color:#CBD5E1; }

/* 空状态 */
.empty-state-premium{ text-align:center; padding:70px 20px; background:rgba(255,255,255,.6); border-radius:20px; border:2px dashed #E2E8F0; }
.empty-state-premium .empty-emoji{ font-size:56px; margin-bottom:14px; }
.empty-state-premium h3{ font-size:19px; color:#334155; margin:0 0 8px; }
.empty-state-premium p{ color:#94A3B8; margin:0; font-size:14.5px; }

/* 分页 */
.pagination-premium{ display:flex; justify-content:center; margin-top:28px; padding-top:20px; }
.pagination-premium :deep(.el-pagination.is-background .btn-prev),
.pagination-premium :deep(.el-pagination.is-background .btn-next),
.pagination-premium :deep(.el-pagination.is-background .el-pager li){
  border-radius:9px; border:1px solid #E2E8F0; background:#fff; font-weight:600; transition:all .2s ease; }
.pagination-premium :deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active){
  background:linear-gradient(135deg,#3B82F6,#2563EB)!important; color:#fff; border-color:transparent;
  box-shadow:0 3px 12px rgba(59,130,246,.3);
}

.card-list-enter-active{ transition:all .4s ease; }
.card-list-leave-active{ transition:all .2s ease; }
.card-list-enter-from{ opacity:0; transform:translateY(15px); }

/* 响应式 */
@media(max-width:992px){
  .hero-title{ font-size:28px; }
  .fs-row{ grid-template-columns:repeat(2,1fr); }
  .job-premium-card{ flex-direction:column; }
  .jpc-action{ flex-direction:row; border-left:none; border-top:1px dashed #F1F5F9; padding:14px 20px; min-width:auto; justify-content:space-between; }
  .jpc-top-row{ flex-direction:column; gap:8px; }
  .jpc-right-salary{}
  .stats-strip{ gap:20px; padding:16px 24px; }
}
@media(max-width:640px){
  .hero-title{ font-size:24px; }
  .fs-row{ grid-template-columns:1fr; }
  .results-header{ flex-direction:column; gap:8px; align-items:flex-start; }
}
</style>
