<template>
  <div class="job-detail-page">
    <div class="page-inner" v-if="job">
      <!-- 顶部信息卡 -->
      <div class="detail-hero glass-panel">
        <div class="dh-main">
          <h1 class="job-title">{{ job.jobName }}</h1>
          <div class="job-salary">{{ job.salaryRange || '薪资面议' }}</div>
          <div class="job-meta">
            <span class="meta-item"><el-icon><Location /></el-icon>{{ job.workLocation }}</span>
            <span class="meta-item"><el-icon><Reading /></el-icon>{{ job.educationReq || '学历不限' }}</span>
            <span class="meta-item"><el-icon><Timer /></el-icon>{{ job.experienceReq || '经验不限' }}</span>
            <span class="meta-item"><el-icon><User /></el-icon>招 {{ job.recruitNum }} 人</span>
          </div>
          <div class="job-tags" v-if="job.tags">
            <el-tag v-for="tag in parseTags(job.tags)" :key="tag" size="small" round effect="light">{{ tag }}</el-tag>
          </div>
        </div>
        <div class="dh-action">
          <button :class="['apply-main-btn', {applied}]" :disabled="applied" @click="handleApply">
            <span v-if="!applied">立即投递</span>
            <span v-else>已投递 ✓</span>
          </button>
          <div class="deadline-info" v-if="job.deadline">截止：{{ job.deadline }}</div>
        </div>
      </div>

      <!-- 内容区 -->
      <el-row :gutter="24" style="margin-top:24px">
        <el-col :span="16">
          <div class="glass-panel content-card">
            <div class="cc-header-bar blue"><h3>职位描述</h3></div>
            <div class="desc-content" v-html="formatText(job.description)"></div>
          </div>
          <div class="glass-panel content-card" style="margin-top:16px">
            <div class="cc-header-bar purple"><h3>任职要求</h3></div>
            <div class="desc-content" v-html="formatText(job.requirement)"></div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="glass-panel company-side-card" @click="$router.push(`/companies/${job.companyId}`)">
            <div class="cc-header-bar green"><h3>企业信息</h3></div>
            <div class="company-info-inner" v-if="company">
              <div class="ci-avatar-wrap">
                <el-avatar :size="56" :src="company.logo" shape="square">{{ company.companyName?.charAt(0) }}</el-avatar>
              </div>
              <div class="ci-name">{{ company.companyName }}</div>
              <div class="ci-meta-row">
                <span class="cm-pill">{{ company.industry }}</span>
                <span class="cm-pill gray">{{ company.scale }}</span>
                <span class="cm-pill green">{{ company.nature }}</span>
              </div>
              <p class="ci-intro">{{ company.introduction || '暂无简介' }}</p>
              <div class="ci-more">点击查看更多 →</div>
            </div>
          </div>

          <!-- 在招职位 -->
          <div class="glass-panel" style="margin-top:16px">
            <div class="cc-header-bar amber"><h3>在招职位</h3></div>
            <div class="side-job-list">
              <div v-for="j in jobs" :key="j.id" class="side-job-item"
                @click="$router.push(`/jobs/${j.id}`)">
                <b>{{ j.jobName }}</b>
                <span>{{ j.salaryRange }}</span>
                <el-tag size="small" type="info" round effect="plain">{{ j.workLocation }}</el-tag>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
    <div v-else class="loading-wrap"><el-skeleton :rows="10" animated /></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getJobDetail, getCompanyDetail, applyJob, hasApplied } from '@/api'
import { useUserStore } from '@/stores/user'

const route = useRoute(), router = useRouter(), userStore = useUserStore()
const job = ref(null), company = ref(null), applied = ref(false), jobs = ref([])
const parseTags = (tags) => { try{return JSON.parse(tags)}catch{return tags.split(',')} }
const formatText = (text) => text?.replace(/\n/g,'<br>')||''

const handleApply = async () => {
  if(!userStore.isLoggedIn){ElMessage.warning('请先登录');router.push('/login');return}
  if(!userStore.isStudent){ElMessage.warning('只有学生才能投递');return}
  try{await applyJob(job.value.id); applied.value=true; ElMessage.success('投递成功！')}catch{}
}

onMounted(async () => {
  const id = route.params.id
  try {
    const res = await getJobDetail(id); job.value = res.data
    const compRes = await getCompanyDetail(res.data.companyId); company.value = compRes.data
    const jRes = await getJobList({companyId:id, page:1, size:6}); jobs.value = jRes.data?.records||[]
    if(userStore.isStudent){const aRes=await hasApplied(id); applied.value=aRes.data}
  } catch {}
})
</script>

<style scoped>
.job-detail-page{padding:24px 0;background:#F8FAFC;min-height:calc(100vh - 60px)}
.page-inner{max-width:1200px;margin:0 auto;padding:0 24px}
.glass-panel{background:#fff;border:1px solid rgba(226,232,240,.85);border-radius:15px;padding:24px;box-shadow:0 1px 3px rgba(0,0,0,.04)}

/* Hero */
.detail-hero{display:flex;justify-content:space-between;align-items:flex-start;position:relative;overflow:hidden}
.dh-main{flex:1}.job-title{font-size:26px;font-weight:800;color:#1E293B;margin-bottom:8px;line-height:1.2}
.job-salary{font-size:24px;font-weight:700;color:#EF4444;margin-bottom:14px}
.job-meta{display:flex;gap:20px;flex-wrap:wrap;margin-bottom:12px}
.meta-item{display:flex;align-items:center;gap:5px;color:#64748B;font-size:14px}.meta-item .el-icon{color:#94A3B8}
.job-tags .el-tag{margin-right:6px}

.dh-action{flex-shrink:0;margin-left:40px;text-align:right;display:flex;flex-direction:column;align-items:flex-end;gap:10px}
.apply-main-btn{
  padding:12px 32px;border-radius:13px;border:none;font-size:16px;font-weight:700;
  color:white;cursor:pointer;transition:all .28s cubic-bezier(.4,0,.2,1);
  background:linear-gradient(135deg,#3B82F6,#2563EB);
  box-shadow:0 5px 22px rgba(59,130,246,.35)
}
.apply-main-btn:hover:not(:disabled){transform:translateY(-2px) scale(1.02);box-shadow:0 8px 30px rgba(59,130,246,.45)}
.apply-main-btn.applied{background:linear-gradient(135deg,#22C55E,#059669);box-shadow:0 5px 22px rgba(34,197,94,.35)}.apply-main-btn.applied:hover:not(:disabled){box-shadow:0 8px 30px rgba(34,197,94,.45)}
.deadline-info{font-size:13px;color:#94A3B8}

/* 内容卡片 */
.content-card{min-height:120px}
.cc-header-bar{display:flex;align-items:center;gap:8px;margin-bottom:18px;padding-bottom:14px;border-bottom:1px solid #F1F5F9}
.cc-header-bar::before{content:'';width:4px;height:18px;border-radius:4px;flex-shrink:0}
.cc-header-bar.blue::before{background:linear-gradient(180deg,#3B82F6,#60A5FA)}
.cc-header-bar.purple::before{background:linear-gradient(180deg,#8B5CF6,#A78BFA)}
.cc-header-bar.green::before{background:linear-gradient(180deg,#10B981,#6EE7B7)}
.cc-header-bar.amber::before{background:linear-gradient(180deg,#F59E0B,#FCD34D)}
.cc-header-bar h3{font-size:16px;font-weight:650;color:#1E293B;margin:0}
.desc-content{line-height:1.9;color:#475569;font-size:14.5px}

/* 企业侧栏 */
.company-side-card{cursor:pointer;transition:all .25s ease}.company-side-card:hover{transform:translateY(-2px);box-shadow:0 6px 25px rgba(0,0,0,.06)}
.ci-avatar-wrap{text-align:center;margin-bottom:12px}
.ci-name{text-align:center;font-size:17px;font-weight:700;color:#1E293B;margin-bottom:10px}
.ci-meta-row{display:flex;gap:6px;justify-content:center;flex-wrap:wrap;margin-bottom:12px}
.cm-pill{font-size:11.5px;background:#EFF6FF;color:#2563EB;padding:3px 10px;border-radius:999px;font-weight:500}.cm-pill.gray{background:#F1F5F9;color:#64748B}.cm-pill.green{background:#ECFDF5;color:#059669}
.ci-intro{font-size:13px;color:#64748B;line-height:1.7;text-align:center}
.ci-more{text-align:center;margin-top:14px;font-size:12.5px;color:#3B82F6;font-weight:550;cursor:pointer}

.side-job-list{display:flex;flex-direction:column;gap:8px}
.side-job-item{
  display:flex;align-items:center;gap:8px;padding:10px 14px;
  border-radius:10px;cursor:pointer;transition:all .2s ease;border:1px solid transparent
}
.side-job-item:hover{background:#F8FAFC;border-color:#E2E8F0}
.side-job-item b{font-size:13.5px;font-weight:600;color:#1E293B;flex:1;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.side-job-item span{font-size:12.5px;font-weight:600;color:#EF4444;white-space:nowrap}
.loading-wrap{max-width:1200px;margin:40px auto;padding:0 24px}
@media(max-width:992px){.detail-hero{flex-direction:column}.dh-action{margin-left:0;width:100%;align-items:stretch;margin-top:20px}.dh-action button{width:100%}}
</style>