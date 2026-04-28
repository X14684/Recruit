<template>
  <div class="student-dashboard">
    <!-- 页面标题 -->
    <div class="page-header animate-fade-in-up">
      <div>
        <h1 class="page-title">👋 欢迎回来，{{ userName }}</h1>
        <p class="page-subtitle">这是你的求职工作台，掌握最新动态</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="$router.push('/jobs')">
          <el-icon><Search /></el-icon> 浏览职位
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="18" class="animate-fade-in-up delay-1">
      <el-col :xs="12" :sm="6" v-for="(s, idx) in statCards" :key="s.label">
        <div class="stat-card-premium" :class="['blue','green','orange','red'][idx]">
          <div class="stat-icon-wrapper" :class="['blue','green','orange','red'][idx]">
            <el-icon :size="26"><component :is="s.icon" /></el-icon>
          </div>
          <div class="stat-body">
            <div class="stat-value">{{ s.value }}</div>
            <div class="stat-label">{{ s.label }}</div>
            <span class="stat-trend" :class="s.trend > 0 ? 'up' : 'down'">
              {{ s.trend > 0 ? '↑' : '↓' }}{{ Math.abs(s.trend) }}
            </span>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 主体区域 -->
    <el-row :gutter="18" style="margin-top: 22px;">
      <!-- 左侧：简历完成度 + 职位推荐 -->
      <el-col :xs="24" :md="16">
        <el-row :gutter="18">
          <!-- 简历完成度 -->
          <el-col :xs="24" :sm="10" class="animate-scale-in delay-2">
            <div class="glass-card-solid" style="padding: 24px; height: 100%;">
              <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:20px;">
                <h3 style="font-size:15px; font-weight:600; color:var(--text-primary);">简历完成度</h3>
                <el-button text type="primary" size="small" @click="$router.push('/student/resume')">完善</el-button>
              </div>
              
              <!-- 进度环 -->
              <div style="display:flex; flex-direction:column; align-items:center; margin: 16px 0 24px;">
                <div class="progress-ring-container">
                  <svg class="progress-ring-svg" width="120" height="120" viewBox="0 0 120 120">
                    <circle class="progress-ring-bg" cx="60" cy="60" r="52" stroke-width="8"/>
                    <circle 
                      class="progress-ring-fill"
                      cx="60" cy="60" r="52" stroke-width="8"
                      :stroke="resumeColor"
                      :stroke-dasharray="circumference"
                      :stroke-dashoffset="circumference - (resumeProgress / 100) * circumference"
                      stroke-linecap="round"
                    />
                  </svg>
                  <div class="progress-ring-text">{{ resumeProgress }}<small style="font-size:14px;color:var(--text-muted);">%</small></div>
                  <div class="progress-ring-sub">完整度</div>
                </div>
              </div>

              <!-- 分项进度 -->
              <div class="resume-sections">
                <div v-for="section in resumeSections" :key="section.name" class="resume-section-item">
                  <div class="section-header">
                    <span class="section-name">{{ section.name }}</span>
                    <span class="section-percent">{{ section.percent }}%</span>
                  </div>
                  <div class="progress-bar-custom">
                    <div class="progress-bar-fill blue" :style="{ width: section.percent + '%' }"></div>
                  </div>
                </div>
              </div>
            </div>
          </el-col>

          <!-- 职位推荐 -->
          <el-col :xs="24" :sm="14" class="animate-fade-in-right delay-2">
            <div class="glass-card-solid" style="padding: 24px; height: 100%;">
              <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:18px;">
                <h3 style="font-size:15px; font-weight:600; color:var(--text-primary);">为你推荐</h3>
                <el-button text type="primary" size="small" @click="$router.push('/jobs')">查看全部</el-button>
              </div>
              
              <div class="job-recommend-list">
                <div v-for="job in recommendedJobs" :key="job.id" class="job-rec-card" @click="$router.push('/jobs')">
                  <div class="job-rec-top">
                    <div class="job-rec-title">{{ job.title }}</div>
                    <span :class="['match-badge', job.match >= 80 ? 'high' : job.match >= 50 ? 'medium' : 'low']">
                      匹配 {{ job.match }}%
                    </span>
                  </div>
                  <div class="job-rec-company">
                    <el-icon :size="14" color="#94A3B8"><OfficeBuilding /></el-icon> {{ job.company }}
                  </div>
                  <div class="job-rec-tags">
                    <span class="job-tag"><el-icon><Location /></el-icon> {{ job.location }}</span>
                    <span class="job-salary">{{ job.salary }}</span>
                  </div>
                </div>
                
                <!-- 空状态兜底 -->
                <div v-if="recommendedJobs.length === 0" class="empty-state">
                  <div class="empty-state-icon"><Briefcase /></div>
                  <div class="empty-state-text">暂无推荐职位，快去完善简历吧</div>
                  <el-button type="primary" plain size="small" style="margin-top:12px;" @click="$router.push('/jobs')">浏览职位</el-button>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>

        <!-- 最新投递记录 -->
        <div class="glass-card-solid animate-fade-in-up delay-4" style="margin-top:22px; padding:24px;">
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:18px;">
            <h3 style="font-size:15px; font-weight:600; color:var(--text-primary); display:flex; align-items:center; gap:8px;">
              <el-icon color="#3B82F6"><List /></el-icon> 最近投递
            </h3>
            <el-button text type="primary" size="small" @click="$router.push('/student/applications')">全部记录</el-button>
          </div>
          
          <el-table :data="applications" size="default" :show-header="true" empty-text="暂无投递记录">
            <el-table-column label="职位名称" min-width="160">
              <template #default="{ row }">
                <span style="font-weight:500;">{{ row.jobName || `职位 #${row.jobId}` }}</span>
              </template>
            </el-table-column>
            <el-table-column label="企业" min-width="140">
              <template #default="{ row }">
                <span style="color:var(--text-secondary);">{{ row.companyName || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="110">
              <template #default="{ row }">
                <el-tag :type="appStatusType(row.status)" size="small" effect="light">{{ appStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="投递时间" width="170">
              <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="90" fixed="right">
              <template #default="{ row }">
                <el-button text type="primary" size="small">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>

      <!-- 右侧：快捷操作 + 面试安排 -->
      <el-col :xs="24" :md="8" class="animate-fade-in-right delay-3">
        <!-- 快捷操作 -->
        <div class="glass-card-solid" style="padding:24px;">
          <h3 style="font-size:15px; font-weight:600; color:var(--text-primary); margin-bottom:16px;">快捷操作</h3>
          <div class="quick-actions-grid">
            <a class="quick-action-btn" href="javascript:void(0)" @click="$router.push('/jobs')">
              <div class="action-icon" style="background:rgba(59,130,246,0.1);color:#3B82F6;"><Search /></div>
              <span>浏览职位</span>
            </a>
            <a class="quick-action-btn" href="javascript:void(0)" @click="$router.push('/student/resume')">
              <div class="action-icon" style="background:rgba(16,185,129,0.1);color:#10B981;"><Document /></div>
              <span>编辑简历</span>
            </a>
            <a class="quick-action-btn" href="javascript:void(0)" @click="$router.push('/student/applications')">
              <div class="action-icon" style="background:rgba(245,158,11,0.1);color:#F59E0B;"><List /></div>
              <span>投递记录</span>
            </a>
            <a class="quick-action-btn" href="javascript:void(0)" @click="$router.push('/talks')">
              <div class="action-icon" style="background:rgba(139,92,246,0.1);color:#8B5CF6;"><Calendar /></div>
              <span>宣讲会</span>
            </a>
          </div>
        </div>

        <!-- 面试安排时间线 -->
        <div class="glass-card-solid" style="padding:24px; margin-top:18px;">
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:18px;">
            <h3 style="font-size:15px; font-weight:600; color:var(--text-primary); display:flex; align-items:center; gap:8px;">
              <el-icon color="#F59E0B"><Clock /></el-icon> 面试安排
            </h3>
            <el-tag type="warning" size="small" effect="plain" v-if="interviewCount > 0">{{ interviewCount }} 个待参加</el-tag>
          </div>
          
          <div v-if="interviews.length" class="timeline-modern">
            <div v-for="(item, i) in interviews" :key="i" class="timeline-item">
              <div class="timeline-dot active" :class="{ success: item.status === 'done', warning: item.status === 'upcoming' }"></div>
              <div class="timeline-content">
                <h4>{{ item.company }} · {{ item.position }}</h4>
                <p>{{ item.type }}面试</p>
                <span class="timeline-time">{{ item.time }}</span>
              </div>
            </div>
          </div>
          
          <div v-else class="empty-state" style="padding:32px 12px;">
            <div class="empty-state-icon"><Calendar /></div>
            <div class="empty-state-text">暂无面试安排</div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getStudentApplications } from '@/api'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const userName = computed(() => {
  const u = userStore.userInfo
  return u?.realName || u?.username || '同学'
})

// 统计数据
const statCards = ref([
  { label: '已投递', value: 0, icon: 'Document', trend: 12 },
  { label: '面试邀请', value: 0, icon: 'Calendar', trend: 5 },
  { label: '收藏职位', value: 0, icon: 'Star', trend: 3 },
  { label: '浏览量', value: 0, icon: 'View', trend: 28 }
])

// 投递数据
const applications = ref([])
const interviewCount = computed(() => applications.value.filter(a => a.status === 3).length)

// 简历进度（模拟，实际应从后端获取）
const resumeProgress = ref(68)
const circumference = 2 * Math.PI * 52
const resumeColor = '#3B82F6'

const resumeSections = [
  { name: '基本信息', percent: 95 },
  { name: '教育经历', percent: 85 },
  { name: '项目经验', percent: 55 },
  { name: '技能特长', percent: 40 }
]

// 推荐职位（模拟数据）
const recommendedJobs = ref([
  {
    id: 1,
    title: '前端开发工程师',
    company: '腾讯科技',
    location: '深圳·南山区',
    salary: '15-25K',
    match: 92
  },
  {
    id: 2,
    title: 'Java后端开发',
    company: '字节跳动',
    location: '北京·海淀',
    salary: '20-35K',
    match: 78
  },
  {
    id: 3,
    title: '产品经理实习生',
    company: '阿里巴巴',
    location: '杭州·余杭',
    salary: '8-12K',
    match: 65
  }
])

// 面试安排（从投递数据中提取）
const interviews = computed(() => {
  return applications.value
    .filter(a => a.status === 3)
    .slice(0, 4)
    .map(a => ({
      company: a.companyName || '',
      position: a.jobName || '',
      type: '技术面',
      time: a.interviewTime ? new Date(a.interviewTime).toLocaleString('zh-CN') : '待定',
      status: 'upcoming'
    }))
})

const appStatusText = (s) => ['已投递','已查看','初审通过','面试邀请','已录用','不合适'][s] || '未知'
const appStatusType = (s) => ['info','','success','warning','success','danger'][s] || 'info'

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

onMounted(async () => {
  try {
    const res = await getStudentApplications({ page: 1, size: 10 })
    const records = res.data?.records || []
    applications.value = records
    
    // 更新统计
    const total = res.data?.total || 0
    statCards.value[0].value = total
    statCards.value[1].value = records.filter(r => r.status === 3).length
    statCards.value[2].value = Math.floor(total * 0.6)
    statCards.value[3].value = Math.floor(total * 3.5 + 42)
    
    // 根据简历数据调整进度（这里用简单逻辑模拟）
    if (userStore.userInfo?.major) resumeSections[2].percent += 30
    if (userStore.userInfo?.skills) resumeSections[3].percent += 40
  } catch (e) {
    console.warn('加载学生仪表盘数据失败:', e)
  }
})
</script>

<style scoped>
.student-dashboard {
  min-height: 100%;
}

.header-actions {
  flex-shrink: 0;
}

/* 简历分项 */
.resume-sections {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.resume-section-item .section-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
}

.section-name {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
}

.section-percent {
  font-size: 13px;
  font-weight: 600;
  color: var(--primary);
}

/* 职位推荐 */
.job-recommend-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.job-rec-card {
  padding: 16px;
  border-radius: var(--radius-md);
  border: 1px solid rgba(226,232,240,0.7);
  cursor: pointer;
  transition: all var(--transition-smooth);
  background: white;
}

.job-rec-card:hover {
  border-color: rgba(59,130,246,0.25);
  box-shadow: var(--shadow-md);
  transform: translateX(4px);
}

.job-rec-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.job-rec-title {
  font-size: 14.5px;
  font-weight: 600;
  color: var(--text-primary);
}

.job-rec-company {
  font-size: 12.5px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 10px;
}

.job-rec-tags {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.job-tag {
  font-size: 12px;
  color: var(--text-muted);
  display: flex;
  align-items: center;
  gap: 3px;
}

.job-salary {
  font-size: 14px;
  font-weight: 700;
  color: #EF4444;
}
</style>
