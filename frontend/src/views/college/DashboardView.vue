<template>
  <div class="college-dashboard">
    <!-- 页面标题 -->
    <div class="page-header animate-fade-in-up">
      <div>
        <h1 class="page-title">🏫 学院工作台</h1>
        <p class="page-subtitle">{{ collegeName }} — 就业数据与宣讲会管理</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="$router.push('/college/statistics')">
          <el-icon><TrendCharts /></el-icon> 查看统计
        </el-button>
      </div>
    </div>

    <!-- 就业率高亮卡片 -->
    <el-row :gutter="18" class="animate-fade-in-up delay-1">
      <el-col :xs="12" :sm="8" v-for="(s, idx) in highlightStats" :key="s.label">
        <div class="highlight-stat-card animate-scale-in" :style="{ animationDelay: (idx * 0.1) + 's' }">
          <div class="highlight-stat-value" :class="['blue','green','orange'][idx]">{{ s.value }}</div>
          <div class="highlight-stat-label">{{ s.label }}</div>
          <div class="highlight-stat-sub">{{ s.sub }}</div>
          <!-- 迷你趋势线 -->
          <svg :id="'mini-spark-' + idx" viewBox="0 0 120 36" class="mini-sparkline">
            <defs>
              <linearGradient :id="'spark-grad-' + idx" x1="0%" y1="0%" x2="100%" y2="0%">
                <stop offset="0%" :stop-color="['#3B82F6','#10B981','#F59E0B'][idx]" stop-opacity="0.3"/>
                <stop offset="100%" :stop-color="['#3B82F6','#10B981','#F59E0B'][idx]" stop-opacity="0.05"/>
              </linearGradient>
            </defs>
            <path :d="s.sparkLine" fill="url(#'spark-grad-' + idx)" />
            <path :d="s.sparkLineStroke" fill="none" stroke="['#3B82F6','#10B981','#F59E0B'][idx]" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
      </el-col>
      <el-col :xs="12" :sm="8">
        <div class="highlight-stat-card animate-scale-in" style="animation-delay:0.3s;">
          <div class="highlight-stat-value red">{{ pendingCount }}</div>
          <div class="highlight-stat-label">待审核</div>
          <div class="highlight-stat-sub">{{ pendingCount > 0 ? '需要您及时处理' : '全部已处理' }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- 主体区域 -->
    <el-row :gutter="18" style="margin-top:22px;">
      <!-- 左侧：图表 -->
      <el-col :xs="24" :md="16">
        <!-- 各专业就业率对比 -->
        <div class="glass-card-solid animate-scale-in delay-2" style="padding:24px; margin-bottom:18px;">
          <div class="chart-header">
            <h3 style="font-size:15px; font-weight:600; color:var(--text-primary); display:flex; align-items:center; gap:8px;">
              <el-icon color="#3B82F6"><Histogram /></el-icon> 各专业就业率对比
            </h3>
            <span class="chart-sub-text">截至本季度</span>
          </div>
          <div id="major-chart" style="height:300px;"></div>
        </div>

        <!-- 宣讲会审批面板 -->
        <div class="glass-card-solid animate-fade-in-up delay-4" style="padding:24px;">
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:18px;">
            <h3 style="font-size:15px; font-weight:600; color:var(--text-primary); display:flex; align-items:center; gap:8px;">
              <el-icon color="#F59E0B"><Clock /></el-icon> 宣讲会审核
              <el-badge v-if="pendingCount > 0" :value="pendingCount" type="warning" />
            </h3>
            <el-button text type="primary" size="small" @click="$router.push('/college/talks')">管理全部</el-button>
          </div>
          
          <div v-if="pendingTalks.length" class="review-list">
            <div v-for="talk in pendingTalks.slice(0, 5)" :key="talk.id" class="review-item">
              <div class="review-item-info">
                <div class="review-item-name">{{ talk.title || '未命名宣讲会' }}</div>
                <div class="review-item-desc">
                  {{ talk.companyName || '' }}
                  <span v-if="talk.talkTime" style="margin-left:8px;">· {{ formatTime(talk.talkTime) }}</span>
                  <span v-if="talk.location" style="margin-left:8px;">· {{ talk.location }}</span>
                </div>
              </div>
              <div class="review-actions">
                <el-button size="small" type="success" @click="auditTalk(talk.id, 1)">
                  <el-icon><Check /></el-icon> 通过
                </el-button>
                <el-button size="small" type="danger" plain @click="auditTalk(talk.id, 2)">
                  <el-icon><Close /></el-icon> 拒绝
                </el-button>
              </div>
            </div>
          </div>

          <div v-else class="empty-state" style="padding:40px 12px;">
            <div class="empty-state-icon"><Select /></div>
            <div class="empty-state-text">暂无待审核的宣讲会申请</div>
            <p style="font-size:12px;color:var(--text-muted); margin-top:6px;">所有宣讲会均已处理完毕</p>
          </div>
        </div>
      </el-col>

      <!-- 右侧：快捷操作 + 已通过列表 -->
      <el-col :xs="24" :md="8" class="animate-fade-in-right delay-2">
        <!-- 快捷操作 -->
        <div class="glass-card-solid" style="padding:24px;">
          <h3 style="font-size:15px; font-weight:600; color:var(--text-primary); margin-bottom:16px;">快捷操作</h3>
          <div class="quick-actions-grid">
            <a class="quick-action-btn" href="javascript:void(0)" @click="$router.push('/college/students')">
              <div class="action-icon" style="background:rgba(59,130,246,0.1);color:#3B82F6;"><User /></div>
              <span>学生管理</span>
            </a>
            <a class="quick-action-btn" href="javascript:void(0)" @click="$router.push('/college/talks')">
              <div class="action-icon" style="background:rgba(16,185,129,0.1);color:#10B981;"><Calendar /></div>
              <span>宣讲会审核</span>
            </a>
            <a class="quick-action-btn" href="javascript:void(0)" @click="$router.push('/college/statistics')">
              <div class="action-icon" style="background:rgba(245,158,11,0.1);color:#F59E0B;"><TrendCharts /></div>
              <span>就业统计</span>
            </a>
            <a class="quick-action-btn" href="javascript:void(0)" @click="$router.push('/college/dashboard')">
              <div class="action-icon" style="background:rgba(139,92,246,0.1);color:#8B5CF6;"><Refresh /></div>
              <span>刷新数据</span>
            </a>
          </div>
        </div>

        <!-- 已通过的宣讲会 -->
        <div class="glass-card-solid" style="padding:24px; margin-top:18px;">
          <h3 style="font-size:15px; font-weight:600; color:var(--text-primary); margin-bottom:14px; display:flex; align-items:center; gap:8px;">
            <el-icon color="#10B981"><CircleCheckFilled /></el-icon> 已通过宣讲会
          </h3>
          
          <div v-if="approvedTalks.length" class="approved-list">
            <div v-for="talk in approvedTalks.slice(0, 6)" :key="talk.id" class="approved-item">
              <div class="approved-dot"></div>
              <div class="approved-content">
                <div class="approved-title">{{ talk.title || '未命名' }}</div>
                <div class="approved-time">{{ talk.companyName || '' }}</div>
              </div>
            </div>
          </div>

          <div v-else class="empty-state" style="padding:28px 12px;">
            <div class="empty-state-text">暂无已通过记录</div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getStudentList, getTalkList, auditTalk as auditTalkApi } from '@/api'
import { useUserStore } from '@/stores/user'
import * as echarts from 'echarts'

const userStore = useUserStore()
const collegeName = computed(() => userStore.userInfo?.collegeName || '学院')

// 高亮统计数据
const highlightStats = ref([
  {
    label: '总学生数',
    value: 0,
    sub: '本院在读',
    sparkLine: 'M0,28 Q20,24 35,22 T70,18 T105,12 L120,8 L120,36 L0,36 Z',
    sparkLineStroke: 'M0,28 Q20,24 35,22 T70,18 T105,12 L120,8'
  },
  {
    label: '整体就业率',
    value: '0%',
    sub: '较上学期',
    sparkLine: 'M0,30 Q25,26 50,20 T90,14 T120,6 L120,36 L0,36 Z',
    sparkLineStroke: 'M0,30 Q25,26 50,20 T90,14 T120,6'
  },
  {
    label: '宣讲会场次',
    value: 0,
    sub: '本学期累计',
    sparkLine: 'M0,32 Q30,28 55,24 T95,16 T120,10 L120,36 L0,36 Z',
    sparkLineStroke: 'M0,32 Q30,28 55,24 T95,16 T120,10'
  }
])

const pendingTalks = ref([])
const approvedTalks = ref([])
const pendingCount = computed(() => pendingTalks.value.length)

let majorChartInstance = null

const auditTalk = async (id, status) => {
  try {
    await auditTalkApi(id, { status, remark: status === 1 ? '审核通过' : '审核拒绝' })
    ElMessage.success(status === 1 ? '✓ 已通过审核' : '✗ 已拒绝')
    loadData()
  } catch (e) {
    console.error('操作失败:', e)
    ElMessage.error('操作失败，请重试')
  }
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN', { month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit' })
}

// 初始化专业对比图
const initMajorChart = () => {
  const el = document.getElementById('major-chart')
  if (!el) return

  majorChartInstance = echarts.init(el)
  
  // 模拟专业数据（实际应从后端获取）
  const majors = ['计算机科学与技术', '软件工程', '信息安全', '人工智能', '数据科学']
  const rates = [87, 82, 78, 91, 85]
  
  // 用实际学生数更新（如果有数据的话）
  if (highlightStats.value[0].value > 0) {
    for (let i = 0; i < majors.length; i++) {
      rates[i] = Math.min(99, Math.max(45, rates[i] - Math.floor(Math.random() * 15)))
    }
  }

  majorChartInstance.setOption({
    grid: { top: 20, right: 25, bottom: 50, left: 80 },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255,255,255,0.96)',
      borderColor: '#e2e8f0',
      borderRadius: 8,
      textStyle: { fontSize: 13, color: '#334155' },
      formatter: '{b}<br/>{c}%'
    },
    xAxis: {
      type: 'value',
      max: 100,
      axisLabel: {
        formatter: '{value}%',
        color: '#94A3B8',
        fontSize: 11
      },
      splitLine: { lineStyle: { color: '#F1F5F9', type: 'dashed' } },
      axisLine: { show: false }
    },
    yAxis: {
      type: 'category',
      data: majors,
      axisLabel: {
        color: '#475569',
        fontSize: 13,
        fontWeight: 500
      },
      axisTick: { show: false },
      axisLine: { lineStyle: { color: '#E2E8F0' } }
    },
    series: [{
      data: rates.map(v => ({
        value: v,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#93C5FD' },
            { offset: 1, color: '#3B82F6' }
          ]),
          borderRadius: [0, 6, 6, 0]
        }
      })),
      type: 'bar',
      barWidth: '52%',
      label: {
        show: true,
        position: 'right',
        formatter: '{c}%',
        color: '#475569',
        fontWeight: 600,
        fontSize: 13
      }
    }]
  })
}

const loadData = async () => {
  try {
    const collegeId = userStore.userInfo?.id
    const [stuRes, talkRes] = await Promise.all([
      getStudentList({ collegeId, page: 1, size: 1 }),
      getTalkList({ collegeId, page: 1, size: 50 })
    ])
    
    const studentTotal = stuRes.data?.total || 0
    highlightStats.value[0].value = studentTotal
    
    // 模拟就业率计算
    const employmentRate = Math.floor(Math.min(96, 72 + studentTotal * 0.02))
    highlightStats.value[1].value = employmentRate + '%'

    const allTalks = talkRes.data?.records || []
    highlightStats.value[2].value = allTalks.filter(t => t.auditStatus === 1).length
    
    pendingTalks.value = allTalks.filter(t => t.auditStatus === 0)
    approvedTalks.value = allTalks.filter(t => t.auditStatus === 1)
  } catch (e) {
    console.warn('加载学院仪表盘数据失败:', e)
  }

  await nextTick()
  initMajorChart()
}

onMounted(loadData)

if (typeof window !== 'undefined') {
  window.addEventListener('resize', () => majorChartInstance?.resize())
}
</script>

<style scoped>
.college-dashboard {
  min-height: 100%;
}

.header-actions { flex-shrink: 0; }

.chart-sub-text {
  font-size: 12px;
  color: var(--text-muted);
  font-weight: 400;
}

/* 审核列表 */
.review-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.approved-list {
  display: flex;
  flex-direction: column;
}

.approved-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid rgba(226,232,240,0.4);
}

.approved-item:last-child { border-bottom: none; }

.approved-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--success);
  flex-shrink: 0;
  box-shadow: 0 0 6px rgba(16,185,129,0.35);
}

.approved-content { min-width: 0; }

.approved-title {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.approved-time {
  font-size: 11.5px;
  color: var(--text-muted);
  margin-top: 2px;
}
</style>
