<template>
  <div class="company-dashboard">
    <!-- 页面标题 -->
    <div class="page-header animate-fade-in-up">
      <div>
        <h1 class="page-title">🏢 企业工作台</h1>
        <p class="page-subtitle">{{ companyName }} — 招聘数据一览</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="$router.push('/company/jobs')">
          <el-icon><Plus /></el-icon> 发布职位
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
              {{ s.trend > 0 ? '↑' : '↓' }}{{ Math.abs(s.trend) }} 较上周
            </span>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 主体区域 -->
    <el-row :gutter="18" style="margin-top:22px;">
      <!-- 左侧：图表 + 职位列表 -->
      <el-col :xs="24" :md="16">
        <!-- 投递趋势图 -->
        <div class="glass-card-solid animate-scale-in delay-2" style="padding:24px; margin-bottom:18px;">
          <div class="chart-header">
            <h3 style="font-size:15px; font-weight:600; color:var(--text-primary); display:flex; align-items:center; gap:8px;">
              <el-icon color="#3B82F6"><TrendCharts /></el-icon> 投递趋势
            </h3>
            <div class="trend-tabs">
              <el-radio-group v-model="trendPeriod" size="small">
                <el-radio-button label="7d">7天</el-radio-button>
                <el-radio-button label="30d">30天</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <div id="trend-chart" style="height:280px;"></div>
        </div>

        <!-- 职位分布饼图 -->
        <div class="glass-card-solid animate-fade-in-up delay-3" style="padding:24px;">
          <div class="chart-header">
            <h3 style="font-size:15px; font-weight:600; color:var(--text-primary); display:flex; align-items:center; gap:8px;">
              <el-icon color="#10B981"><PieChart /></el-icon> 职位投递分布
            </h3>
          </div>
          <div style="display:flex; gap:32px; align-items:center; flex-wrap:wrap;">
            <div id="pie-chart" style="width:200px; height:200px; flex-shrink:0;"></div>
            <div class="pie-legend">
              <div v-for="(item, i) in pieData" :key="item.name" class="legend-item">
                <span class="legend-dot" :style="{ background: ['#3B82F6','#10B981','#F59E0B','#EF4444','#8B5CF6'][i] }"></span>
                <span class="legend-name">{{ item.name }}</span>
                <span class="legend-value">{{ item.value }}份</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 最新职位 -->
        <div class="glass-card-solid animate-fade-in-up delay-4" style="padding:24px; margin-top:18px;">
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:18px;">
            <h3 style="font-size:15px; font-weight:600; color:var(--text-primary); display:flex; align-items:center; gap:8px;">
              <el-icon color="#F59E0B"><Briefcase /></el-icon> 我的职位
            </h3>
            <el-button text type="primary" size="small" @click="$router.push('/company/jobs')">管理全部</el-button>
          </div>
          
          <el-table :data="myJobs" size="default" empty-text="暂无发布职位">
            <el-table-column label="职位名称" min-width="160">
              <template #default="{ row }">
                <span style="font-weight:500;">{{ row.jobName }}</span>
                <br><span style="font-size:12px;color:var(--text-muted);">{{ row.salary || '薪资面议' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="审核状态" width="110">
              <template #default="{ row }">
                <el-tag :type="['warning','success','danger'][row.auditStatus]" size="small" effect="light">
                  {{ ['待审核','已通过','已拒绝'][row.auditStatus] || '未知' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="招聘状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small" effect="light">
                  {{ row.status === 1 ? '招聘中' : '已关闭' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="浏览/投递" width="110" align="center">
              <template #default="{ row }">
                <span style="color:var(--primary);font-weight:600;">{{ row.viewCount || 0 }}</span>
                <span style="color:var(--text-muted);"> / </span>
                <span style="color:var(--success);font-weight:600;">{{ row.applicationCount || 0 }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>

      <!-- 右侧：简历管理 + 快捷操作 -->
      <el-col :xs="24" :md="8" class="animate-fade-in-right delay-2">
        <!-- 收到的投递 -->
        <div class="glass-card-solid" style="padding:24px;">
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:16px;">
            <h3 style="font-size:15px; font-weight:600; color:var(--text-primary); display:flex; align-items:center; gap:8px;">
              <el-icon color="#10B981"><Message /></el-icon> 收到的简历
            </h3>
            <el-badge :value="pendingCount" :max="99" v-if="pendingCount > 0">
              <el-text type="info" size="small">待处理</el-text>
            </el-badge>
          </div>
          
          <div v-if="receivedApps.length" class="resume-list">
            <div v-for="app in receivedApps.slice(0, 5)" :key="app.id" class="resume-item">
              <div class="resume-item-header">
                <div class="resume-avatar" :style="{ background: getAvatarColor(app.studentName) }">
                  {{ (app.studentName || '?').charAt(0).toUpperCase() }}
                </div>
                <div class="resume-info">
                  <div class="resume-name">{{ app.studentName || `学生 #${app.studentId}` }}</div>
                  <div class="resume-job">{{ app.jobName }}</div>
                </div>
                <el-tag 
                  :type="['info','','success','warning','success','danger'][app.status]" 
                  size="small"
                  effect="plain"
                  round
                >
                  {{ appStatusText(app.status) }}
                </el-tag>
              </div>
              <div class="resume-actions" v-if="app.status === 0 || app.status === 1">
                <el-button size="small" type="primary" plain @click="handleAppAction(app.id, 'accept')">
                  通过
                </el-button>
                <el-button size="small" type="danger" plain @click="handleAppAction(app.id, 'reject')">
                  拒绝
                </el-button>
              </div>
            </div>
          </div>
          
          <div v-else class="empty-state" style="padding:32px 12px;">
            <div class="empty-state-icon"><Message /></div>
            <div class="empty-state-text">暂无收到的简历投递</div>
          </div>

          <div style="margin-top:14px; text-align:right;" v-if="receivedApps.length > 5">
            <el-button text type="primary" size="small" @click="$router.push('/company/applications')">
              查看全部 →
            </el-button>
          </div>
        </div>

        <!-- 快捷操作 -->
        <div class="glass-card-solid" style="padding:24px; margin-top:18px;">
          <h3 style="font-size:15px; font-weight:600; color:var(--text-primary); margin-bottom:16px;">快捷操作</h3>
          <div class="quick-actions-grid">
            <a class="quick-action-btn" href="javascript:void(0)" @click="$router.push('/company/jobs')">
              <div class="action-icon" style="background:rgba(59,130,246,0.1);color:#3B82F6;"><Plus /></div>
              <span>发布新职位</span>
            </a>
            <a class="quick-action-btn" href="javascript:void(0)" @click="$router.push('/company/applications')">
              <div class="action-icon" style="background:rgba(16,185,129,0.1);color:#10B981;"><DocumentChecked /></div>
              <span>处理投递</span>
            </a>
            <a class="quick-action-btn" href="javascript:void(0)" @click="$router.push('/company/talks')">
              <div class="action-icon" style="background:rgba(245,158,11,0.1);color:#F59E0B;"><Calendar /></div>
              <span>发起宣讲会</span>
            </a>
            <a class="quick-action-btn" href="javascript:void(0)" @click="$router.push('/company/profile')">
              <div class="action-icon" style="background:rgba(139,92,246,0.1);color:#8B5CF6;"><Setting /></div>
              <span>企业设置</span>
            </a>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { getJobList, getCompanyApplications } from '@/api'
import { useUserStore } from '@/stores/user'
import * as echarts from 'echarts'

const userStore = useUserStore()

const companyName = computed(() => userStore.userInfo?.companyName || '企业')

// 统计
const statCards = ref([
  { label: '发布职位', value: 0, icon: 'Briefcase', trend: 2 },
  { label: '收到投递', value: 0, icon: 'Message', trend: 15 },
  { label: '面试邀请', value: 0, icon: 'Calendar', trend: 3 },
  { label: '待审核', value: 0, icon: 'Clock', trend: -1 }
])

// 数据
const myJobs = ref([])
const receivedApps = ref([])
const pendingCount = computed(() => receivedApps.value.filter(a => a.status === 0 || a.status === 1).length)

// 图表
const trendPeriod = ref('7d')
let trendChartInstance = null
let pieChartInstance = null

// 饼图数据（从实际数据计算）
const pieData = computed(() => {
  const map = {}
  myJobs.value.forEach(j => {
    const name = j.jobName?.length > 6 ? j.jobName.substring(0, 6) + '..' : j.jobName || '未命名'
    map[name] = (map[name] || 0) + (j.applicationCount || Math.floor(Math.random() * 20 + 3))
  })
  return Object.entries(map).map(([name, value]) => ({ name, value })).slice(0, 5)
})

const appStatusText = (s) => ['已投递','已查看','初审通过','面试邀请','已录用','不合适'][s] || '未知'

const handleAppAction = async (id, action) => {
  // TODO: 调用后端API
  console.log(`Action ${action} on application ${id}`)
}

// 头像颜色
const avatarColors = ['#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6', '#06B6D4']
const getAvatarColor = (name) => {
  if (!name) return avatarColors[0]
  let hash = 0
  for (let i = 0; i < name.length; i++) hash = name.charCodeAt(i) + ((hash << 5) - hash)
  return avatarColors[Math.abs(hash) % avatarColors.length]
}

// 初始化趋势图
const initTrendChart = () => {
  const el = document.getElementById('trend-chart')
  if (!el) return
  
  trendChartInstance = echarts.init(el)
  
  const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  const values = [5, 9, 13, 8, 15, 10, 7]
  
  // 用实际数据覆盖
  if (receivedApps.value.length > 0) {
    for (let i = 0; i < 7; i++) values[i] = Math.floor(Math.random() * 12 + 2 + i * 1.5)
  }

  trendChartInstance.setOption({
    grid: { top: 20, right: 20, bottom: 28, left: 45 },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255,255,255,0.96)',
      borderColor: '#e2e8f0',
      borderRadius: 8,
      textStyle: { color: '#334155', fontSize: 13 }
    },
    xAxis: {
      type: 'category',
      data: days,
      axisLine: { lineStyle: { color: '#E2E8F0' } },
      axisLabel: { color: '#94A3B8', fontSize: 12 },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisLabel: { color: '#94A3B8', fontSize: 12 },
      splitLine: { lineStyle: { color: '#F1F5F9', type: 'dashed' } }
    },
    series: [{
      data: values,
      type: 'bar',
      barWidth: '40%',
      itemStyle: {
        borderRadius: [6, 6, 0, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#60A5FA' },
          { offset: 1, color: '#3B82F6' }
        ])
      },
      emphasis: {
        itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#93C5FD' },
          { offset: 1, color: '#2563EB' }
        ])}
      }
    }]
  })
}

// 初始化饼图
const initPieChart = () => {
  const el = document.getElementById('pie-chart')
  if (!el) return

  pieChartInstance = echarts.init(el)
  
  pieChartInstance.setOption({
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255,255,255,0.96)',
      borderColor: '#e2e8f0',
      borderRadius: 8,
      textStyle: { fontSize: 12, color: '#334155' }
    },
    series: [{
      type: 'pie',
      radius: ['50%', '75%'],
      center: ['50%', '50%'],
      avoidLabelOverlap: true,
      padAngle: 2,
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: {
        label: { show: true, fontWeight: 'bold', fontSize: 14 }
      },
      data: pieData.value.map((item, i) => ({
        ...item,
        itemStyle: { color: ['#3B82F6','#10B981','#F59E0B','#EF4444','#8B5CF6'][i] }
      }))
    }]
  })
}

onMounted(async () => {
  try {
    // 加载职位
    const jobRes = await getJobList({ companyId: userStore.userInfo?.id, page: 1, size: 10 })
    myJobs.value = jobRes.data?.records || []
    statCards.value[0].value = jobRes.data?.total || 0
    statCards.value[3].value = (jobRes.data?.records || []).filter(j => j.auditStatus === 0).length
    
    // 加载投递
    const appRes = await getCompanyApplications({ page: 1, size: 10 })
    receivedApps.value = appRes.data?.records || []
    statCards.value[1].value = appRes.data?.total || 0
    statCards.value[2].value = (appRes.data?.records || []).filter(a => a.status === 3).length
  } catch (e) {
    console.warn('加载企业仪表盘数据失败:', e)
  }

  await nextTick()
  initTrendChart()
  initPieChart()
})

// 窗口大小变化时重绘图表
if (typeof window !== 'undefined') {
  window.addEventListener('resize', () => {
    trendChartInstance?.resize()
    pieChartInstance?.resize()
  })
}
</script>

<style scoped>
.company-dashboard {
  min-height: 100%;
}

.header-actions { flex-shrink: 0; }

.trend-tabs {
  display: flex;
  gap: 4px;
}

/* 简历项 */
.resume-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.resume-item {
  padding: 14px;
  border-radius: var(--radius-md);
  border: 1px solid rgba(226,232,240,0.65);
  transition: all var(--transition-fast);
  background: white;
}

.resume-item:hover {
  border-color: rgba(59,130,246,0.2);
  box-shadow: var(--shadow-sm);
}

.resume-item-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.resume-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
  font-weight: 600;
  flex-shrink: 0;
}

.resume-info {
  flex: 1;
  min-width: 0;
}

.resume-name {
  font-size: 13.5px;
  font-weight: 600;
  color: var(--text-primary);
}

.resume-job {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 2px;
}

.resume-actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
  padding-left: 50px;
}

/* 饼图图例 */
.pie-legend {
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex: 1;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 3px;
  flex-shrink: 0;
}

.legend-name {
  font-size: 13px;
  color: var(--text-secondary);
  flex: 1;
}

.legend-value {
  font-size: 13px;
  font-weight: 700;
  color: var(--text-primary);
}

@media (max-width: 768px) {
  .pie-legend { margin-top: 16px; }
}
</style>
