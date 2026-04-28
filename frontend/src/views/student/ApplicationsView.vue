<template>
  <div class="app-page">
    <div class="page-header-ribbon">
      <div class="ribbon-icon"><el-icon :size="22"><List /></el-icon></div>
      <div class="ribbon-text">
        <h2>投递记录</h2>
        <p>查看你的职位投递状态和面试安排</p>
      </div>
      <div class="ribbon-stat">
        <span class="stat-num">{{ total }}</span>
        <span class="stat-label">总投递</span>
      </div>
    </div>

    <!-- 状态统计条 -->
    <div class="status-bar">
      <div v-for="(s, i) in statusStats" :key="i" class="status-item">
        <div class="si-dot" :style="{ background: s.color }"></div>
        <span>{{ s.label }}</span>
        <b>{{ s.count }}</b>
      </div>
    </div>

    <!-- 投递列表 -->
    <div class="app-list" v-loading="loading">
      <template v-if="list.length > 0">
        <div v-for="row in list" :key="row.id" class="app-card">
          <div class="ac-left">
            <div class="ac-job">
              <span class="job-name">{{ row.jobName }}</span>
              <el-tag :type="statusType(row.status)" size="small" round effect="light">
                {{ statusText(row.status) }}
              </el-tag>
            </div>
            <div class="ac-company">{{ row.companyName }}</div>
            <div class="ac-meta">
              <span><el-icon><Calendar /></el-icon> {{ row.createTime }}</span>
              <span v-if="row.interviewTime"><el-icon><AlarmClock /></el-icon> {{ row.interviewTime }}</span>
              <span v-if="row.interviewLocation"><el-icon><Location /></el-icon> {{ row.interviewLocation }}</span>
            </div>
            <div v-if="row.companyRemark" class="ac-remark">
              企业备注：{{ row.companyRemark }}
            </div>
          </div>
          <div class="ac-right">
            <div class="progress-step" :class="'step-' + row.status"></div>
          </div>
        </div>
      </template>
      <template v-else-if="!loading">
        <div class="empty-state">
          <el-empty description="暂无投递记录">
            <router-link to="/jobs">
              <button class="glass-btn primary">
                <el-icon :size="14"><Search /></el-icon> 去找找职位
              </button>
            </router-link>
          </el-empty>
        </div>
      </template>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination
        background layout="prev, pager, next"
        :total="total" :page-size="10"
        v-model:current-page="page"
        @current-change="fetchList"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getStudentApplications } from '@/api'

const list = ref([])
const total = ref(0)
const page = ref(1)
const loading = ref(false)

const statusText = (s) => ['已投递','已查看','初审通过','面试邀请','已录用','不合适'][s] || '-'
const statusType = (s) => ['info','','success','warning','success','danger'][s] || 'info'

// 状态颜色映射
const statusColors = { 0: '#94A3B8', 1: '#3B82F6', 2: '#10B981', 3: '#F59E0B', 4: '#22C55E', 5: '#EF4444' }
const statusLabels = { 0:'已投递', 1:'已查看', 2:'初审通过', 3:'面试邀请', 4:'已录用', 5:'不合适' }

const statusStats = computed(() => {
  const counts = [0,0,0,0,0,0]
  list.value.forEach(r => { if(r.status !== undefined && r.status < 6) counts[r.status]++ })
  return counts.map((c, i) => ({ label: statusLabels[i], count: c, color: statusColors[i] }))
})

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getStudentApplications({ page: page.value, size: 10 })
    list.value = res.data?.records || []
    total.value = Number(res.data?.total || 0)
  } finally { loading.value = false }
}

onMounted(fetchList)
</script>

<style scoped>
.app-page { padding-bottom: 40px; }

/* ---- 页头丝带 ---- */
.page-header-ribbon {
  display: flex; align-items: center; gap: 16px;
  padding: 24px 28px; border-radius: 16px; margin-bottom: 20px;
  background: linear-gradient(135deg, rgba(245,158,11,0.08), rgba(217,119,6,0.06));
  border: 1px solid rgba(245,158,11,0.12);
}
.ribbon-icon {
  width: 48px; height: 48px; border-radius: 13px;
  background: linear-gradient(135deg, #F59E0B, #D97706);
  display: flex; align-items: center; justify-content: center;
  color: white; flex-shrink: 0;
  box-shadow: 0 4px 14px rgba(245,158,11,0.35);
}
.ribbon-text h2 { font-size: 18px; font-weight: 700; color: #1E293B; margin: 0 0 4px; }
.ribbon-text p { font-size: 13px; color: #94A3B8; margin: 0; }
.ribbon-stat { margin-left: auto; text-align: center; }
.stat-num { display: block; font-size: 28px; font-weight: 800; color: #D97706; line-height: 1; }
.stat-label { font-size: 12px; color: #94A3B8; }

/* ---- 状态统计条 ---- */
.status-bar {
  display: flex; gap: 16px; overflow-x: auto; padding-bottom: 4px;
  margin-bottom: 20px;
}
.status-item {
  display: flex; align-items: center; gap: 7px;
  white-space: nowrap; font-size: 13px; color: #64748B;
  background: #fff; border: 1px solid #F1F5F9; border-radius: 999px;
  padding: 8px 16px; transition: all 0.25s ease;
}
.status-item:hover { border-color: #E2E8F0; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.si-dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; }
.status-item b { color: #1E293B; font-weight: 700; }

/* ---- 投递卡片 ---- */
.app-card {
  display: flex; justify-content: space-between; align-items: center;
  background: #fff; border: 1px solid rgba(226,232,240,0.85);
  border-radius: 14px; padding: 22px 24px; margin-bottom: 12px;
  transition: all 0.25s cubic-bezier(0.4,0,0.2,1);
}
.app-card:hover {
  transform: translateY(-2px); box-shadow: 0 6px 25px rgba(0,0,0,0.06);
  border-color: rgba(203,213,225,0.95);
}

.ac-left { flex: 1; min-width: 0; }
.ac-job { display: flex; align-items: center; gap: 10px; margin-bottom: 5px; }
.job-name { font-size: 15.5px; font-weight: 650; color: #1E293B; }
.ac-company { font-size: 13.5px; color: #475569; margin-bottom: 10px; }
.ac-meta { display: flex; gap: 18px; flex-wrap: wrap; }
.ac-meta span { font-size: 12.5px; color: #94A3B8; display: flex; align-items: center; gap: 4px; }
.ac-remark {
  margin-top: 10px; font-size: 12.5px; color: #64748B;
  background: #F8FAFC; padding: 8px 14px; border-radius: 8px;
  border-left: 3px solid #F59E0B;
}

.ac-right { flex-shrink: 0; margin-left: 20px; }

/* 进度步骤指示器 */
.progress-step {
  width: 44px; height: 44px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  position: relative;
}
.progress-step::before {
  content: ''; position: absolute; inset: -2px;
  border-radius: 14px; opacity: 0.15;
}
.step-0 { background: rgba(148,163,184,0.1); }
.step-0::before { background: #94A3B8; }
.step-1 { background: rgba(59,130,246,0.1); }
.step-1::before { background: #3B82F6; }
.step-2 { background: rgba(16,185,129,0.1); }
.step-2::before { background: #10B981; }
.step-3 { background: rgba(245,158,11,0.15); }
.step-3::before { background: #F59E0B; }
.step-4 { background: rgba(34,197,94,0.1); }
.step-4::before { background: #22C55E; }
.step-5 { background: rgba(239,68,68,0.1); }
.step-5::before { background: #EF4444; }

.glass-btn {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 9px 20px; border-radius: 10px; border: none;
  font-size: 13px; font-weight: 600; cursor: pointer;
  transition: all 0.28s ease;
  background: linear-gradient(135deg, #3B82F6, #2563EB); color: white;
  box-shadow: 0 3px 12px rgba(59,130,246,0.3);
}
.glass-btn:hover { transform: translateY(-1px); box-shadow: 0 5px 16px rgba(59,130,246,0.4); }

.pagination-wrap { display: flex; justify-content: center; margin-top: 24px; }
.empty-state { text-align: center; padding: 60px 20px; }

@media (max-width: 768px) {
  .app-card { flex-direction: column; align-items: flex-start; gap: 14px; }
  .ac-right { margin-left: 0; }
  .status-bar { padding-bottom: 12px; }
}
</style>
