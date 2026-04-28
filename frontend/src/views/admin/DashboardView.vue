<template>
  <div class="admin-dashboard">
    <!-- 页面标题 -->
    <div class="page-header animate-fade-in-up">
      <div>
        <h1 class="page-title">⚙️ 系统管理台</h1>
        <p class="page-subtitle">校园招聘系统全局数据概览与运营管理</p>
      </div>
      <div class="header-actions">
        <el-button @click="refreshData" :loading="loading" plain>
          <el-icon><Refresh /></el-icon> 刷新数据
        </el-button>
      </div>
    </div>

    <!-- 平台总览卡片 -->
    <el-row :gutter="18" class="animate-fade-in-up delay-1">
      <el-col :xs="12" :sm="6" v-for="(s, idx) in statCards" :key="s.label">
        <div class="stat-card-premium" :class="['blue','green','orange','red'][idx]">
          <div class="stat-icon-wrapper" :class="['blue','green','orange','red'][idx]">
            <el-icon :size="26"><component :is="s.icon" /></el-icon>
          </div>
          <div class="stat-body">
            <div class="stat-value">{{ s.value }}</div>
            <div class="stat-label">{{ s.label }}</div>
            <span class="stat-trend up">
              ↑ {{ s.growth }}
            </span>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 主体区域 -->
    <el-row :gutter="18" style="margin-top:22px;">
      <!-- 左侧主区域 -->
      <el-col :xs="24" :md="16">
        <!-- 快速审核面板 -->
        <div class="glass-card-solid animate-scale-in delay-2" style="padding:24px; margin-bottom:18px;">
          <h3 style="font-size:15px; font-weight:600; color:var(--text-primary); display:flex; align-items:center; gap:8px;">
            <el-icon color="#F59E0B"><BellFilled /></el-icon> 快速审批
            <el-badge v-if="totalPending > 0" :value="totalPending" type="warning" />
          </h3>

          <el-tabs v-model="activeReviewTab" style="margin-top:8px;">
            <el-tab-pane label="职位审核" name="jobs">
              <div v-if="pendingJobs.length" class="review-list">
                <div v-for="job in pendingJobs.slice(0, 4)" :key="job.id" class="review-item">
                  <div class="review-item-info">
                    <div class="review-item-name">{{ job.jobName || '未命名职位' }}</div>
                    <div class="review-item-desc">
                      {{ job.companyName || '未知企业' }}
                      <span style="margin-left:6px;">·</span>
                      <span style="margin-left:6px;">{{ job.salary || '薪资面议' }}</span>
                    </div>
                  </div>
                  <div class="review-actions">
                    <el-button size="small" type="success" @click="quickAudit('job', job.id, 1)">
                      通过
                    </el-button>
                    <el-button size="small" type="danger" plain @click="quickAudit('job', job.id, 2)">
                      拒绝
                    </el-button>
                  </div>
                </div>
              </div>
              <div v-else class="empty-state" style="padding:24px;">
                <div class="empty-state-text">暂无待审核的职位</div>
              </div>
              <div style="text-align:right; margin-top:10px;" v-if="pendingJobs.length > 4">
                <el-button text type="primary" size="small" @click="$router.push('/admin/jobs')">
                  管理全部 →
                </el-button>
              </div>
            </el-tab-pane>

            <el-tab-pane label="企业入驻" name="companies">
              <div v-if="pendingCompanies.length" class="review-list">
                <div v-for="c in pendingCompanies.slice(0, 4)" :key="c.id" class="review-item">
                  <div class="review-item-info">
                    <div class="review-item-name">{{ c.companyName || c.username }}</div>
                    <div class="review-item-desc">
                      联系人：{{ c.contactPerson || '-' }} · {{ c.contactPhone || '-' }}
                    </div>
                  </div>
                  <div class="review-actions">
                    <el-button size="small" type="success">通过</el-button>
                    <el-button size="small" type="danger" plain>拒绝</el-button>
                  </div>
                </div>
              </div>
              <div v-else class="empty-state" style="padding:24px;">
                <div class="empty-state-text">暂无待审核企业</div>
              </div>
              <div style="text-align:right; margin-top:10px;" v-if="pendingCompanies.length > 4">
                <el-button text type="primary" size="small" @click="$router.push('/admin/companies')">
                  管理全部 →
                </el-button>
              </div>
            </el-tab-pane>

            <el-tab-pane label="宣讲会" name="talks">
              <div class="empty-state" style="padding:24px;">
                <div class="empty-state-text">宣讲会由各学院独立管理</div>
                <p style="font-size:12px;color:var(--text-muted); margin-top:4px;">管理员可在学院端查看详情</p>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>

        <!-- 用户增长趋势图 -->
        <div class="glass-card-solid animate-fade-in-up delay-3" style="padding:24px; margin-bottom:18px;">
          <div class="chart-header">
            <h3 style="font-size:15px; font-weight:600; color:var(--text-primary); display:flex; align-items:center; gap:8px;">
              <el-icon color="#3B82F6"><TrendCharts /></el-icon> 用户增长趋势
            </h3>
            <div class="trend-legend">
              <span class="legend-inline"><i style="width:10px;height:4px;border-radius:2px;background:#3B82F6;display:inline-block;"></i> 学生</span>
              <span class="legend-inline"><i style="width:10px;height:4px;border-radius:2px;background:#10B981;display:inline-block;"></i> 企业</span>
            </div>
          </div>
          <div id="growth-chart" style="height:260px;"></div>
        </div>

        <!-- 招聘流程可视化 -->
        <div class="glass-card-solid animate-fade-in-up delay-5" style="padding:28px 24px;">
          <h3 style="font-size:15px; font-weight:600; color:var(--text-primary); margin-bottom:8px; display:flex; align-items:center; gap:8px;">
            <el-icon color="#8B5CF6"><Connection /></el-icon> 招聘流程总览
          </h3>
          <p style="font-size:12px;color:var(--text-muted); margin-bottom:20px;">标准招聘流程 · 从发布到录用共6步</p>
          
          <div class="flow-steps">
            <div class="flow-line">
              <div class="flow-line-progress" :style="{ width: flowProgress + '%' }"></div>
            </div>
            
            <div v-for="(step, i) in flowSteps" :key="step.label" class="flow-step">
              <div 
                class="flow-step-number"
                :class="{ active: i === currentFlowStep, completed: i < currentFlowStep }"
              >
                <template v-if="i < currentFlowStep">✓</template>
                <template v-else>{{ i + 1 }}</template>
              </div>
              <div 
                class="flow-step-label"
                :class="{ active: i === currentFlowStep, completed: i < currentFlowStep }"
              >{{ step.label }}</div>
            </div>
          </div>
        </div>
      </el-col>

      <!-- 右侧辅助区 -->
      <el-col :xs="24" :md="8" class="animate-fade-in-right delay-2">
        <!-- 系统动态 -->
        <div class="glass-card-solid" style="padding:24px;">
          <h3 style="font-size:15px; font-weight:600; color:var(--text-primary); margin-bottom:16px; display:flex; align-items:center; gap:8px;">
            <el-icon color="#06B6D4"><Promotion /></el-icon> 系统动态
            <span class="live-indicator"></span>
          </h3>
          
          <div class="activity-feed">
            <div v-for="(act, i) in systemActivities" :key="i" class="activity-item">
              <div class="activity-avatar" :style="{ background: act.color }">{{ act.icon }}</div>
              <div class="activity-content">
                <div class="activity-text" v-html="act.text"></div>
                <div class="activity-time">{{ act.time }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 权限矩阵 -->
        <div class="glass-card-solid" style="padding:24px; margin-top:18px;">
          <h3 style="font-size:15px; font-weight:600; color:var(--text-primary); margin-bottom:16px; display:flex; align-items:center; gap:8px;">
            <el-icon color="#F59E0B"><Lock /></el-icon> 角色权限矩阵
          </h3>
          
          <div style="overflow-x:auto;">
            <table class="permission-matrix">
              <thead>
                <tr>
                  <th>功能模块</th>
                  <th>学生</th>
                  <th>企业</th>
                  <th>学院</th>
                  <th>管理员</th>
                </tr>
              </thead>
              <tbody>
                <tr><td>浏览职位</td><td class="perm-yes">✓</td><td class="perm-yes">✓</td><td class="perm-partial">只读</td><td class="perm-yes">✓</td></tr>
                <tr><td>投递简历</td><td class="perm-yes">✓</td><td class="perm-no">—</td><td class="perm-no">—</td><td class="perm-no">—</td></tr>
                <tr><td>发布职位</td><td class="perm-no">—</td><td class="perm-yes">✓</td><td class="perm-no">—</td><td class="perm-yes">✓</td></tr>
                <tr><td>审核管理</td><td class="perm-no">—</td><td class="perm-no">—</td><td class="perm-partial">部分</td><td class="perm-yes">✓</td></tr>
                <tr><td>用户管理</td><td class="perm-no">—</td><td class="perm-no">—</td><td class="perm-partial">部分</td><td class="perm-yes">✓</td></tr>
                <tr><td>数据统计</td><td class="perm-no">—</td><td class="perm-partial">自有</td><td class="perm-yes">✓</td><td class="perm-yes">✓</td></tr>
                <tr><td>系统配置</td><td class="perm-no">—</td><td class="perm-no">—</td><td class="perm-no">—</td><td class="perm-yes">✓</td></tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 快捷操作 -->
        <div class="glass-card-solid" style="padding:24px; margin-top:18px;">
          <h3 style="font-size:15px; font-weight:600; color:var(--text-primary); margin-bottom:16px;">快捷操作</h3>
          <div class="quick-actions-grid">
            <a class="quick-action-btn" href="javascript:void(0)" @click="$router.push('/admin/students')">
              <div class="action-icon" style="background:rgba(59,130,246,0.1);color:#3B82F6;"><User /></div>
              <span>学生管理</span>
            </a>
            <a class="quick-action-btn" href="javascript:void(0)" @click="$router.push('/admin/companies')">
              <div class="action-icon" style="background:rgba(16,185,129,0.1);color:#10B981;"><OfficeBuilding /></div>
              <span>企业管理</span>
            </a>
            <a class="quick-action-btn" href="javascript:void(0)" @click="$router.push('/admin/colleges')">
              <div class="action-icon" style="background:rgba(245,158,11,0.1);color:#F59E0B;"><School /></div>
              <span>学院管理</span>
            </a>
            <a class="quick-action-btn" href="javascript:void(0)" @click="$router.push('/admin/jobs')">
              <div class="action-icon" style="background:rgba(239,68,68,0.1);color:#EF4444;"><Briefcase /></div>
              <span>职位审核</span>
            </a>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getStatistics, getJobList } from '@/api'
import * as echarts from 'echarts'

const loading = ref(false)

// 统计卡片
const statCards = ref([
  { label: '注册学生', value: 0, icon: 'User', growth: '+12.5%' },
  { label: '入驻企业', value: 0, icon: 'OfficeBuilding', growth: '+8.3%' },
  { label: '活跃职位', value: 0, icon: 'Briefcase', growth: '+23.1%' },
  { label: '学院数量', value: 0, icon: 'School', growth: '+2' }
])

// 审核
const activeReviewTab = ref('jobs')
const pendingJobs = ref([])
const pendingCompanies = ref([])
const totalPending = computed(() => {
  return pendingJobs.value.length + pendingCompanies.value.length
})

// 流程步骤
const flowSteps = [
  { label: '企业注册' },
  { label: '发布职位' },
  { label: '职位审核' },
  { label: '学生投递' },
  { label: '面试安排' },
  { label: '录用签约' }
]
const currentFlowStep = ref(3)
const flowProgress = computed(() => ((currentFlowStep.value) / (flowSteps.length - 1)) * 100)

// 系统动态（模拟）
const systemActivities = ref([
  { icon: '企', color: '#10B981', text: '<strong>腾讯科技</strong> 发布了新职位「前端开发工程师」', time: '5分钟前' },
  { icon: '学', color: '#3B82F6', text: '<strong>张同学</strong> 投递了阿里巴巴的后端开发岗位', time: '12分钟前' },
  { icon: '管', color: '#8B5CF6', text: '系统自动通过了 <strong>字节跳动</strong> 的3个职位申请', time: '28分钟前' },
  { icon: '学', color: '#3B82F6', text: '<strong>李明</strong> 更新了简历，完成度提升至85%', time: '35分钟前' },
  { icon: '企', color: '#10B981', text: '<strong>美团</strong> 申请举办线下宣讲会', time: '1小时前' },
  { icon: '管', color: '#EF4444', text: '检测到异常登录行为，已自动拦截', time: '2小时前' }
])

let growthChartInstance = null

// 初始化增长趋势图
const initGrowthChart = () => {
  const el = document.getElementById('growth-chart')
  if (!el) return
  
  growthChartInstance = echarts.init(el)
  
  const months = ['9月', '10月', '11月', '12月', '1月', '2月']
  
  // 根据实际数据调整
  const students = statCards.value[0].value ? 
    months.map((_, i) => Math.floor(statCards.value[0].value * (0.3 + i * 0.14) * (0.7 + Math.random() * 0.6))) :
    [120, 200, 340, 450, 520, 680]
  const companies = statCards.value[1].value ?
    months.map((_, i) => Math.floor(statCards.value[1].value * (0.25 + i * 0.13) * (0.65 + Math.random() * 0.7))) :
    [20, 35, 50, 68, 80, 95]

  growthChartInstance.setOption({
    grid: { top: 30, right: 20, bottom: 26, left: 50 },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255,255,255,0.96)',
      borderColor: '#e2e8f0',
      borderRadius: 8,
      textStyle: { fontSize: 13, color: '#334155' }
    },
    legend: {
      data: ['学生', '企业'],
      bottom: 0,
      itemWidth: 16,
      itemHeight: 4,
      itemGap: 24,
      textStyle: { fontSize: 12, color: '#64748B' }
    },
    xAxis: {
      type: 'category',
      data: months,
      axisLine: { lineStyle: { color: '#E2E8F0' } },
      axisLabel: { color: '#94A3B8', fontSize: 12 },
      axisTick: { show: false },
      boundaryGap: false
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisLabel: { color: '#94A3B8', fontSize: 12 },
      splitLine: { lineStyle: { color: '#F1F5F9', type: 'dashed' } }
    },
    series: [
      {
        name: '学生',
        type: 'line',
        smooth: true,
        data: students,
        symbolSize: 6,
        symbol: 'circle',
        lineStyle: { width: 3, color: '#3B82F6' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(59,130,246,0.2)' },
            { offset: 1, color: 'rgba(59,130,246,0)' }
          ])
        },
        itemStyle: { color: '#3B82F6', borderWidth: 2, borderColor: '#fff' }
      },
      {
        name: '企业',
        type: 'line',
        smooth: true,
        data: companies,
        symbolSize: 6,
        symbol: 'circle',
        lineStyle: { width: 3, color: '#10B981' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(16,185,129,0.2)' },
            { offset: 1, color: 'rgba(16,185,129,0)' }
          ])
        },
        itemStyle: { color: '#10B981', borderWidth: 2, borderColor: '#fff' }
      }
    ]
  })
}

// 快速审核操作
const quickAudit = async (type, id, status) => {
  ElMessage.success(status === 1 ? `✓ 已通过` : `✗ 已拒绝`)
  // TODO: 调用实际API
}

// 刷新数据
const refreshData = async () => {
  loading.value = true
  try {
    await loadData()
    ElMessage.success('数据已刷新')
  } finally {
    loading.value = false
  }
}

const loadData = async () => {
  try {
    const res = await getStatistics()
    if (res.data) {
      statCards.value[0].value = res.data.studentCount || 0
      statCards.value[1].value = res.data.companyCount || 0
      statCards.value[2].value = res.data.jobCount || 0
      statCards.value[3].value = res.data.collegeCount || 3
    }

    // 加载待审核职位
    const jobRes = await getJobList({ page: 1, size: 50 })
    pendingJobs.value = (jobRes.data?.records || []).filter(j => j.auditStatus === 0)

    // TODO: 加载待审核企业
  } catch (e) {
    console.warn('加载管理员仪表盘数据失败:', e)
  }

  await nextTick()
  initGrowthChart()
}

onMounted(loadData)

if (typeof window !== 'undefined') {
  window.addEventListener('resize', () => growthChartInstance?.resize())
}
</script>

<style scoped>
.admin-dashboard {
  min-height: 100%;
}

.header-actions { flex-shrink: 0; }

/* 审核列表 */
.review-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* 图例 */
.trend-legend {
  display: flex;
  gap: 16px;
}

.legend-inline {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--text-secondary);
}

/* 动态指示器 */
.live-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--success);
  animation: pulse-soft 2s ease infinite;
  box-shadow: 0 0 6px rgba(16,185,129,0.4);
}
</style>
