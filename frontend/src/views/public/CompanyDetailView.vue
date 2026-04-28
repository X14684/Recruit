<template>
  <div class="company-detail-page">
    <div class="page-inner" v-if="company">
      <!-- 企业头部 -->
      <div class="company-hero glass-panel">
        <div class="ch-avatar-section">
          <el-avatar :size="80" :src="company.logo" shape="square" class="ch-logo-avatar">
            {{ company.companyName?.charAt(0) }}
          </el-avatar>
          <div class="avatar-ring"></div>
        </div>
        <div class="ch-info">
          <h1 class="ch-name">{{ company.companyName }}</h1>
          <div class="ch-meta-tags">
            <span v-if="company.industry" class="cm-pill blue">{{ company.industry }}</span>
            <span v-if="company.scale" class="cm-pill gray">{{ company.scale }}</span>
            <span v-if="company.nature" class="cm-pill green">{{ company.nature }}</span>
          </div>
          <div class="ch-address" v-if="company.address">
            <el-icon><Location /></el-icon> {{ company.address }}
          </div>
        </div>
      </div>

      <!-- 企业简介 -->
      <div class="glass-panel content-card" style="margin-top:20px">
        <div class="cc-header-bar purple"><h3>企业简介</h3></div>
        <p class="desc-text">{{ company.introduction || '暂无简介' }}</p>
      </div>

      <!-- 在招职位 -->
      <div class="glass-panel" style="margin-top:16px">
        <div class="cc-header-bar blue"><h3>在招职位</h3></div>
        <div class="jobs-grid-inner" v-if="jobs.length > 0">
          <div v-for="job in jobs" :key="job.id" class="mini-job-card"
            @click="$router.push(`/jobs/${job.id}`)">
            <div class="mjc-name">{{ job.jobName }}</div>
            <div class="mjc-bottom">
              <span class="mjc-salary">{{ job.salaryRange }}</span>
              <el-tag size="small" round effect="plain">{{ job.workLocation }}</el-tag>
            </div>
          </div>
        </div>
        <el-empty v-else description="该企业暂无在招职位" />
      </div>
    </div>
    <el-skeleton v-else :rows="8" animated style="max-width:1200px;margin:40px auto;padding:0 24px" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getCompanyDetail, getJobList } from '@/api'

const route = useRoute(), company = ref(null), jobs = ref([])

onMounted(async () => {
  const id = route.params.id
  try {
    const [cRes,jRes] = await Promise.all([getCompanyDetail(id), getJobList({companyId:id, page:1, size:9})])
    company.value = cRes.data; jobs.value = jRes.data?.records||[]
  } catch {}
})
</script>

<style scoped>
.company-detail-page{padding:24px 0;background:#F8FAFC;min-height:calc(100vh - 60px)}
.page-inner{max-width:1200px;margin:0 auto;padding:0 24px}
.glass-panel{background:#fff;border:1px solid rgba(226,232,240,.85);border-radius:15px;padding:26px;box-shadow:0 1px 3px rgba(0,0,0,.04)}

/* Hero */
.company-hero{display:flex;gap:24px;align-items:flex-start}
.ch-avatar-section{position:relative;flex-shrink:0}
.ch-logo-avatar{border-radius:13px!important;font-size:28px!important;font-weight:700!important;color:white!important}
.avatar-ring{
  position:absolute;inset:-4px;border-radius:17px;
  border:2px solid transparent;
  background:conic-gradient(from 180deg,#3B82F6,#60A5FA,#93C5FD,#3B82F6);
  opacity:.25;z-index:-1;animation:ringSpin 6s linear infinite
}
@keyframes ringSpin{to{transform:rotate(360deg)}}

.ch-info{flex:1}.ch-name{font-size:24px;font-weight:800;color:#1E293B;margin-bottom:10px;line-height:1.2}
.ch-meta-tags{display:flex;gap:8px;margin-bottom:12px;flex-wrap:wrap}
.cm-pill{font-size:12px;padding:4px 12px;border-radius:999px;font-weight:550}
.cm-pill.blue{background:rgba(59,130,246,.08);color:#2563EB}.cm-pill.gray{background:rgba(100,116,139,.08);color:#475569}.cm-pill.green{background:rgba(16,185,129,.08);color:#059669}
.ch-address{display:flex;align-items:center;gap:6px;color:#94A3B8;font-size:14px}

/* 内容卡片 */
.content-card{min-height:80px}
.cc-header-bar{display:flex;align-items:center;gap:8px;margin-bottom:18px;padding-bottom:14px;border-bottom:1px solid #F1F5F9}
.cc-header-bar::before{content:'';width:4px;height:18px;border-radius:4px;flex-shrink:0}
.cc-header-bar.purple::before{background:linear-gradient(180deg,#8B5CF6,#A78BFA)}
.cc-header-bar.blue::before{background:linear-gradient(180deg,#3B82F6,#60A5FA)}
.cc-header-bar h3{font-size:16px;font-weight:650;color:#1E293B;margin:0}
.desc-text{color:#475569;line-height:1.9;font-size:14.5px}

/* 职位网格 */
.jobs-grid-inner{display:grid;grid-template-columns:repeat(auto-fill,minmax(220px,1fr));gap:14px}
.mini-job-card{
  padding:18px;border-radius:13px;cursor:pointer;
  transition:all .25s ease;border:1px solid #F1F5F9;background:#FAFBFC
}
.mini-job-card:hover{transform:translateY(-2px);background:#fff;border-color:#BFDBFE;box-shadow:0 4px 15px rgba(59,130,246,.06)}
.mjc-name{font-size:14.5px;font-weight:650;color:#1E293B;margin-bottom:10px}
.mjc-bottom{display:flex;justify-content:space-between;align-items:center}
.mjc-salary{font-size:14px;font-weight:700;color:#EF4444}
@media(max-width:768px){.company-hero{flex-direction:column;align-items:center;text-align:center}.ch-address{justify-content:center}}
</style>