<template>
  <div class="company-app-page">
    <div class="page-header-ribbon">
      <div class="ribbon-icon"><el-icon :size="22"><Message /></el-icon></div>
      <div class="ribbon-text">
        <h2>收到的投递</h2>
        <p>查看和管理学生投递的简历</p>
      </div>
    </div>

    <!-- 投递列表 -->
    <div class="app-list" v-loading="loading">
      <template v-if="list.length > 0">
        <div v-for="row in list" :key="row.id" class="app-card">
          <div class="ac-left">
            <div class="ac-avatar">{{ (row.studentName || '?').charAt(0) }}</div>
            <div class="ac-info">
              <div class="ac-name-row">
                <span class="ac-name">{{ row.studentName }}</span>
                <span class="ac-student-no">{{ row.studentNo }}</span>
              </div>
              <div class="ac-job-name">应聘：{{ row.jobName }}</div>
              <div class="ac-meta">
                <span><el-icon><Calendar /></el-icon> {{ row.createTime }}</span>
                <span v-if="row.interviewTime"><el-icon><AlarmClock /></el-icon> {{ row.interviewTime }}</span>
              </div>
              <div v-if="row.companyRemark" class="ac-remark">
                备注：{{ row.companyRemark }}
              </div>
            </div>
          </div>
          <div class="ac-right">
            <el-tag :type="statusType(row.status)" size="small" round effect="light">
              {{ statusText(row.status) }}
            </el-tag>
            <button class="glass-btn primary small" @click="openUpdate(row)">
              更新状态
            </button>
            <a v-if="row.resumeUrl" :href="getResumeUrl(row.resumeUrl)" target="_blank" class="view-resume">
              查看简历
            </a>
          </div>
        </div>
      </template>
      <template v-else-if="!loading">
        <div class="empty-state"><el-empty description="暂无投递记录" /></div>
      </template>
    </div>

    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="10"
        v-model:current-page="page" @current-change="fetchList" />
    </div>

    <!-- 更新状态弹窗 -->
    <el-dialog v-model="dialogVisible" title="更新投递状态" width="500px">
      <div class="status-options">
        <div v-for="(s,i) in statusOptions" :key="i" 
          :class="['so-item', {active: updateForm.status === s.value}]"
          @click="updateForm.status = s.value">
          <span class="so-dot" :style="{background:s.color}"></span>
          <b>{{ s.label }}</b>
        </div>
      </div>
      <el-form label-width="90px" style="margin-top:18px">
        <el-form-item label="企业备注">
          <el-input v-model="updateForm.remark" type="textarea" :rows="2" placeholder="可选备注信息..." />
        </el-form-item>
        <template v-if="updateForm.status === 3">
          <el-form-item label="面试时间">
            <el-date-picker v-model="updateForm.interviewTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width:100%" />
          </el-form-item>
          <el-form-item label="面试地点">
            <el-input v-model="updateForm.interviewLocation" placeholder="面试地点" />
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <button class="glass-btn ghost" @click="dialogVisible=false">取消</button>
        <button class="glass-btn primary" @click="saveUpdate">确认</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCompanyApplications, updateApplicationStatus } from '@/api'

const list = ref([]), total = ref(0), page = ref(1), loading = ref(false)
const dialogVisible = ref(false), currentId = ref(null)
const updateForm = reactive({ status: 1, remark: '', interviewTime: '', interviewLocation: '' })

const statusText = (s) => ['已投递','已查看','初审通过','面试邀请','已录用','不合适'][s] || '-'
const statusType = (s) => ['info','','success','warning','success','danger'][s] || 'info'

// 简历预览 URL 转换
const getResumeUrl = (url) => {
  if (!url) return '#'
  // url 格式：/uploads/resumes/202604/xxx.pdf
  // path 参数需要去掉 /uploads/ 前缀
  const filePath = url.startsWith('/uploads/') ? url.slice('/uploads/'.length) : url
  return `/api/common/file?path=${filePath}`
}

const statusOptions = [
  {label:'已查看',value:1,color:'#3B82F6'},
  {label:'初审通过',value:2,color:'#10B981'},
  {label:'面试邀请',value:3,color:'#F59E0B'},
  {label:'已录用',value:4,color:'#22C55E'},
  {label:'不合适',value:5,color:'#EF4444'}
]

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getCompanyApplications({ page: page.value, size: 10 })
    list.value = res.data?.records || []
    total.value = Number(res.data?.total || 0)
  } finally { loading.value = false }
}

const openUpdate = (row) => {
  currentId.value = row.id
  Object.assign(updateForm, { status: row.status||1, remark: row.companyRemark||'', interviewTime: row.interviewTime||'', interviewLocation: row.interviewLocation||'' })
  dialogVisible.value = true
}

const saveUpdate = async () => {
  await updateApplicationStatus(currentId.value, updateForm)
  ElMessage.success('更新成功')
  dialogVisible.value = false; fetchList()
}
onMounted(fetchList)
</script>

<style scoped>
.company-app-page { padding-bottom: 40px; }

.page-header-ribbon {
  display:flex;align-items:center;gap:16px;padding:24px 28px;border-radius:16px;margin-bottom:20px;
  background:linear-gradient(135deg,rgba(139,92,246,.08),rgba(109,40,217,.06));border:1px solid rgba(139,92,246,.12);
}
.ribbon-icon{width:48px;height:48px;border-radius:13px;background:linear-gradient(135deg,#8B5CF6,#6D28D9);display:flex;align-items:center;justify-content:center;color:white;flex-shrink:0;box-shadow:0 4px 14px rgba(139,92,246,.35)}
.ribbon-text h2{font-size:18px;font-weight:700;color:#1E293B;margin:0 0 4px}.ribbon-text p{font-size:13px;color:#94A3B8;margin:0}

.app-card{
  display:flex;justify-content:space-between;align-items:center;
  background:#fff;border:1px solid rgba(226,232,240,.85);border-radius:14px;
  padding:20px 24px;margin-bottom:12px;transition:all .25s cubic-bezier(.4,0,.2,1);
}
.app-card:hover{transform:translateY(-2px);box-shadow:0 6px 25px rgba(0,0,0,.06);border-color:rgba(203,213,225,.95)}

.ac-left{display:flex;gap:14px;flex:1;min-width:0}
.ac-avatar{
  width:44px;height:44px;border-radius:12px;background:linear-gradient(135deg,#8B5CF6,#6D28D9);
  color:white;font-size:16px;font-weight:700;display:flex;align-items:center;justify-content:center;flex-shrink:0
}
.ac-info{flex:1;min-width:0}
.ac-name-row{display:flex;align-items:center;gap:10px;margin-bottom:4px}
.ac-name{font-size:15.5px;font-weight:650;color:#1E293B}
.ac-student-no{font-size:12px;color:#94A3B8;background:#F1F5F9;padding:2px 8px;border-radius:999px}
.ac-job-name{font-size:13.5px;color:#475569;margin-bottom:8px}
.ac-meta{display:flex;gap:16px;flex-wrap:wrap}
.ac-meta span{font-size:12.5px;color:#94A3B8;display:flex;align-items:center;gap:4px}
.ac-remark{margin-top:8px;font-size:12px;color:#64748B;background:#FFFBEB;padding:7px 12px;border-radius:8px;border-left:3px solid #F59E0B}

.ac-right{display:flex;flex-direction:column;align-items:flex-end;gap:10px;flex-shrink:0;margin-left:16px}
.glass-btn{display:inline-flex;align-items:center;justify-content:center;gap:6px;padding:8px 18px;border-radius:10px;border:none;font-size:12.5px;font-weight:600;cursor:pointer;transition:all .28s ease;background:linear-gradient(135deg,#3B82F6,#2563EB);color:white;box-shadow:0 3px 12px rgba(59,130,246,.3)}
.glass-btn.primary:hover{transform:translateY(-1px);box-shadow:0 5px 16px rgba(59,130,246,.4)}
.glass-btn.small{padding:6px 14px;font-size:12px}
.glass-btn.ghost{background:#F1F5F9;color:#64748B;border:1px solid #E2E8F0;box-shadow:none}.glass-btn.ghost:hover{background:#EEF2FF;color:#3B82F6;border-color:#BFDBFE}
.view-resume{font-size:11.5px;color:#3B82F6;font-weight:500;text-decoration:none;transition:color .2s}.view-resume:hover{color:#2563EB;text-decoration:underline}

/* 状态选择 */
.status-options{display:grid;grid-template-columns:repeat(auto-fill,minmax(120px,1fr));gap:10px}
.so-item{display:flex;align-items:center;gap:8px;padding:10px 14px;border-radius:10px;border:1px solid #E2E8F0;cursor:pointer;transition:all .2s ease;background:#F8FAFC}
.so-item.active{border-color:#3B82F6;background:rgba(59,130,246,.05);box-shadow:0 0 0 3px rgba(59,130,246,.08)}
.so-dot{width:8px;height:8px;border-radius:50%;flex-shrink:0}
.so-item b{font-size:13px;font-weight:550;color:#334155}

.pagination-wrap{display:flex;justify-content:center;margin-top:24px}.empty-state{text-align:center;padding:60px 20px}
@media(max-width:768px){.app-card{flex-direction:column;align-items:flex-start;gap:14px}.ac-right{margin-left:0;width:100%;flex-direction:row}}
</style>