<template>
  <div class="employment-stats-page">
    <!-- 页面标题 -->
    <div class="page-header-ribbon">
      <div class="ribbon-icon"><el-icon :size="22"><TrendCharts /></el-icon></div>
      <div class="ribbon-text">
        <h2>就业统计</h2>
        <p>全平台就业数据总览与多维度分析</p>
      </div>
    </div>

    <div class="stats-grid">
      <!-- 总体就业率卡片 -->
      <div class="stat-hero-card">
        <div class="shc-left">
          <div class="shc-label">全平台就业率</div>
          <div class="shc-number">{{ employmentRate }}<span class="shc-pct">%</span></div>
          <div class="shc-sub">{{ employedStudents }} / {{ totalStudents }} 人已就业</div>
          <div class="shc-bar-wrap">
            <div class="shc-bar" :style="{ width: employmentRate + '%' }"></div>
          </div>
        </div>
        <div class="shc-right">
          <svg viewBox="0 0 100 100" width="100" height="100">
            <circle cx="50" cy="50" r="40" fill="none" stroke="#F1F5F9" stroke-width="10"/>
            <circle cx="50" cy="50" r="40" fill="none" stroke="#22C55E" stroke-width="10"
              :stroke-dasharray="251.2"
              :stroke-dashoffset="251.2 - 251.2 * (employmentRate / 100)"
              transform="rotate(-90 50 50)" style="transition: all 1s ease"/>
            <text x="50" y="55" text-anchor="middle" font-size="18" font-weight="700" fill="#1E293B">{{ employmentRate }}%</text>
          </svg>
        </div>
      </div>

      <!-- 快速统计 -->
      <div class="quick-stats-row">
        <div class="qs-card">
          <div class="qs-icon blue"><el-icon><User /></el-icon></div>
          <div><b>{{ totalStudents }}</b><span>总学生数</span></div>
        </div>
        <div class="qs-card">
          <div class="qs-icon green"><el-icon><CircleCheck /></el-icon></div>
          <div><b>{{ employedStudents }}</b><span>已就业</span></div>
        </div>
        <div class="qs-card">
          <div class="qs-icon amber"><el-icon><Clock /></el-icon></div>
          <div><b>{{ totalStudents - employedStudents }}</b><span>待业中</span></div>
        </div>
        <div class="qs-card">
          <div class="qs-icon purple"><el-icon><OfficeBuilding /></el-icon></div>
          <div><b>{{ collegeStats.length }}</b><span>学院数</span></div>
        </div>
      </div>

      <!-- 各学院就业统计表格 -->
      <div class="chart-card">
        <div class="cc-header">
          <h3>各学院就业统计</h3>
          <span class="cc-badge">数据表格</span>
        </div>
        <el-table :data="collegeStats" stripe style="width: 100%" v-loading="loading">
          <el-table-column prop="collegeName" label="学院名称" min-width="140" />
          <el-table-column prop="totalStudents" label="总学生数" width="100" align="center" />
          <el-table-column prop="employedStudents" label="已就业" width="100" align="center" />
          <el-table-column label="就业率" width="180">
            <template #default="{ row }">
              <div style="display:flex;align-items:center;gap:8px;">
                <el-progress 
                  :percentage="row.employmentRate" 
                  :stroke-width="8"
                  :color="row.employmentRate >= 80 ? '#22C55E' : row.employmentRate >= 60 ? '#F59E0B' : '#EF4444'"
                  :format="() => row.employmentRate.toFixed(1) + '%'"
                  style="flex:1;"
                />
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="80" align="center">
            <template #default="{ row }">
              <el-tag 
                :type="row.employmentRate >= 80 ? 'success' : row.employmentRate >= 60 ? 'warning' : 'danger'" 
                size="small"
              >
                {{ row.employmentRate >= 80 ? '优秀' : row.employmentRate >= 60 ? '良好' : '预警' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 各专业就业统计 -->
      <div class="chart-card" v-if="majorStats.length > 0">
        <div class="cc-header">
          <h3>各专业就业情况</h3>
          <span class="cc-badge">堆叠柱状图</span>
        </div>
        <div id="major-chart" style="height:320px;"></div>
      </div>

      <!-- 行业就业分布 -->
      <div class="chart-card" v-if="industryStats.length > 0">
        <div class="cc-header">
          <h3>各行业就业分布</h3>
          <span class="cc-badge">饼图</span>
        </div>
        <div id="industry-chart" style="height:320px;"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { getEmploymentStatistics } from '@/api'
import * as echarts from 'echarts'

const loading = ref(false)
const employmentRate = ref(0)
const totalStudents = ref(0)
const employedStudents = ref(0)
const collegeStats = ref([])
const majorStats = ref([])
const industryStats = ref([])

let majorChart = null
let industryChart = null

onMounted(async () => {
  loading.value = true
  try {
    const res = await getEmploymentStatistics()
    const data = res.data || {}

    employmentRate.value = Math.round((data.employmentRate || 0) * 100) / 100
    totalStudents.value = data.totalStudents || 0
    employedStudents.value = data.employedStudents || 0
    collegeStats.value = data.collegeStats || []
    majorStats.value = (data.majorStats || []).slice(0, 15)
    industryStats.value = data.industryStats || []

    await nextTick()

    // 专业就业统计
    if (majorStats.value.length > 0 && document.getElementById('major-chart')) {
      const majors = majorStats.value.map(m => m.major || '未设置')
      const employed = majorStats.value.map(m => m.employedStudents || 0)
      const unemployed = majorStats.value.map(m => (m.totalStudents || 0) - (m.employedStudents || 0))

      majorChart = echarts.init(document.getElementById('major-chart'))
      majorChart.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        legend: { data: ['已就业', '待就业'], top: 0, textStyle: { fontSize: 12, color: '#64748B' } },
        grid: { left: 100, right: 20, top: 40, bottom: 36 },
        xAxis: { type: 'value', splitLine: { lineStyle: { color: '#F1F5F9' } }, axisLabel: { fontSize: 12, color: '#94A3B8' } },
        yAxis: { type: 'category', data: majors, axisLabel: { fontSize: 11, color: '#64748B' }, axisLine: { lineStyle: { color: '#E2E8F0' } }, axisTick: { show: false } },
        series: [
          { name: '已就业', type: 'bar', stack: 'total', data: employed, barWidth: 20,
            itemStyle: { color: '#22C55E', borderRadius: [0, 4, 4, 0] } },
          { name: '待就业', type: 'bar', stack: 'total', data: unemployed,
            itemStyle: { color: '#E2E8F0' } }
        ]
      })
    }

    // 行业就业分布
    if (industryStats.value.length > 0 && document.getElementById('industry-chart')) {
      const industries = industryStats.value.map(i => i.industry || '未分类').slice(0, 8)
      const counts = industryStats.value.map(i => i.employedCount || 0).slice(0, 8)

      industryChart = echarts.init(document.getElementById('industry-chart'))
      industryChart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
        legend: { orient: 'vertical', right: 10, top: 'center', textStyle: { fontSize: 12, color: '#64748B' } },
        series: [{
          type: 'pie',
          radius: ['35%', '65%'],
          center: ['35%', '50%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
          label: { show: false },
          emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
          data: industries.map((name, idx) => ({ value: counts[idx], name }))
        }]
      })
    }
  } catch (err) {
    console.error('获取就业统计失败', err)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.employment-stats-page { padding-bottom: 40px; }

.page-header-ribbon {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px 28px;
  border-radius: 16px;
  margin-bottom: 20px;
  background: linear-gradient(135deg, rgba(34,197,94,0.08), rgba(5,150,105,0.06));
  border: 1px solid rgba(34,197,94,0.12);
}
.ribbon-icon {
  width: 48px; height: 48px; border-radius: 13px;
  background: linear-gradient(135deg, #22C55E, #059669);
  display: flex; align-items: center; justify-content: center;
  color: white; flex-shrink: 0;
  box-shadow: 0 4px 14px rgba(34,197,94,0.35);
}
.ribbon-text h2 { font-size: 18px; font-weight: 700; color: #1E293B; margin: 0 0 4px; }
.ribbon-text p { font-size: 13px; color: #94A3B8; margin: 0; }

.stats-grid { display: flex; flex-direction: column; gap: 18px; }

.stat-hero-card {
  display: flex; align-items: center; justify-content: space-between;
  background: #fff; border: 1px solid rgba(226,232,240,0.85);
  border-radius: 16px; padding: 28px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04), 0 8px 30px rgba(0,0,0,0.03);
  position: relative; overflow: hidden;
}
.stat-hero-card::before {
  content: ''; position: absolute; top: -30px; right: -30px;
  width: 120px; height: 120px; border-radius: 50%;
  background: radial-gradient(circle, rgba(34,197,94,0.08), transparent);
}
.shc-label { font-size: 14px; color: #94A3B8; font-weight: 500; margin-bottom: 6px; }
.shc-number { font-size: 48px; font-weight: 800; color: #22C55E; line-height: 1; letter-spacing: -2px; }
.shc-pct { font-size: 26px; font-weight: 600; margin-left: 2px; }
.shc-sub { font-size: 13px; color: #94A3B8; margin-top: 8px; }
.shc-bar-wrap { width: 200px; height: 6px; background: #ECFDF5; border-radius: 999px; margin-top: 14px; overflow: hidden; }
.shc-bar { height: 100%; background: linear-gradient(90deg, #22C55E, #86EFAC); border-radius: 999px; transition: width 1s ease; }

.chart-card {
  background: #fff; border: 1px solid rgba(226,232,240,0.85);
  border-radius: 16px; padding: 24px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
}
.cc-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.cc-header h3 { font-size: 15.5px; font-weight: 650; color: #1E293B; margin: 0; }
.cc-badge { font-size: 11px; background: #EFF6FF; color: #3B82F6; padding: 3px 10px; border-radius: 999px; font-weight: 550; }

.quick-stats-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 14px; }
.qs-card {
  display: flex; align-items: center; gap: 14px;
  background: #fff; border: 1px solid rgba(226,232,240,0.85);
  border-radius: 14px; padding: 20px;
  transition: all .25s ease;
}
.qs-card:hover { transform: translateY(-2px); box-shadow: 0 4px 15px rgba(0,0,0,0.05); }
.qs-icon {
  width: 42px; height: 42px; border-radius: 11px;
  display: flex; align-items: center; justify-content: center;
  font-size: 19px; flex-shrink: 0; color: white;
}
.qs-icon.blue { background: linear-gradient(135deg, #3B82F6, #2563EB); }
.qs-icon.green { background: linear-gradient(135deg, #10B981, #059669); }
.qs-icon.amber { background: linear-gradient(135deg, #F59E0B, #D97706); }
.qs-icon.purple { background: linear-gradient(135deg, #8B5CF6, #7C3AED); }
.qs-card b { display: block; font-size: 22px; font-weight: 750; color: #1E293B; line-height: 1; }
.qs-card span { font-size: 12px; color: #94A3B8; margin-top: 3px; display: block; }

@media (max-width: 768px) {
  .stat-hero-card { flex-direction: column; text-align: center; gap: 20px; }
  .shc-bar-wrap { margin: 0 auto; }
  .quick-stats-row { grid-template-columns: 1fr 1fr; }
}
</style>
