<template>
  <div class="jobs-page">
    <div class="page-header-ribbon">
      <div class="ribbon-icon"><el-icon :size="22"><Briefcase /></el-icon></div>
      <div class="ribbon-text">
        <h2>职位管理</h2>
        <p>发布和管理你的招聘职位</p>
      </div>
      <div class="ribbon-action">
        <button class="glass-btn primary" @click="openDialog()">
          <el-icon :size="14"><Plus /></el-icon> 发布职位
        </button>
      </div>
    </div>

    <!-- 职位列表 -->
    <div class="jobs-grid" v-loading="loading">
      <template v-if="list.length > 0">
        <div v-for="row in list" :key="row.id" class="job-card" :class="'audit-' + row.auditStatus">
          <div class="jc-header">
            <span class="jc-name">{{ row.jobName }}</span>
            <div class="jc-badges">
              <el-tag size="small" round effect="light">{{ row.jobType || '全职' }}</el-tag>
              <el-tag :type="['warning','success','danger'][row.auditStatus]" size="small" round effect="light">
                {{ ['待审核','通过','拒绝'][row.auditStatus] }}
              </el-tag>
            </div>
          </div>
          <div class="jc-body">
            <div class="jc-info-row"><el-icon><Location /></el-icon> {{ row.workLocation }}</div>
            <div class="jc-info-row"><el-icon><Coin /></el-icon> {{ row.salaryRange || '薪资面议' }}</div>
            <div class="jc-info-row"><el-icon><User /></el-icon> 招 {{ row.recruitNum }} 人</div>
            <div class="jc-stats">
              <div class="jcs-item"><b>{{ row.viewCount }}</b><span>浏览</span></div>
              <div class="jcs-divider"></div>
              <div class="jcs-item"><b>{{ row.createTime?.substring(0,10) }}</b><span>发布</span></div>
            </div>
          </div>
          <div class="jc-footer">
            <label class="switch-wrap">
              <el-switch v-model="row.status" :active-value="1" :inactive-value="0"
                @change="(v) => changeStatus(row.id, v)" :disabled="row.auditStatus !== 1"
                size="small" />
              <span>{{ row.status === 1 ? '招聘中' : '已暂停' }}</span>
            </label>
            <div class="jc-actions">
              <button class="action-link blue" @click="openDialog(row)">
                <el-icon :size="13"><EditPen /></el-icon> 编辑
              </button>
              <el-popconfirm title="确定删除该职位?" @confirm="deleteJob(row.id)" confirm-button-text="删除" cancel-button-text="取消">
                <template #reference>
                  <button class="action-link red"><el-icon :size="13"><Delete /></el-icon> 删除</button>
                </template>
              </el-popconfirm>
            </div>
          </div>
        </div>
      </template>
      <template v-else-if="!loading">
        <div class="empty-state"><el-empty description="暂无职位，点击上方发布吧" /></div>
      </template>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="10" v-model:current-page="page" @current-change="fetchList" />
    </div>

    <!-- 发布/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑职位' : '发布职位'" width="680px" :close-on-click-modal="false">
      <el-form :model="jobForm" :rules="rules" ref="jobFormRef" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="职位名称" prop="jobName">
              <el-input v-model="jobForm.jobName" placeholder="如：前端开发工程师" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位类型">
              <el-select v-model="jobForm.jobType" style="width:100%">
                <el-option v-for="t in ['全职','实习','兼职']" :key="t" :label="t" :value="t" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工作地点" prop="workLocation">
              <el-input v-model="jobForm.workLocation" placeholder="城市或详细地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="薪资范围">
              <el-input v-model="jobForm.salaryRange" placeholder="如：10K-15K" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学历要求">
              <el-select v-model="jobForm.educationReq" style="width:100%">
                <el-option v-for="e in ['大专','本科','硕士','博士','不限']" :key="e" :label="e" :value="e" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="招聘人数" prop="recruitNum">
              <el-input-number v-model="jobForm.recruitNum" :min="1" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="截止日期">
              <el-date-picker v-model="jobForm.deadline" type="date" value-format="YYYY-MM-DD" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="职位描述" prop="description">
              <el-input v-model="jobForm.description" type="textarea" :rows="4" placeholder="详细描述岗位职责..." />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="任职要求">
              <el-input v-model="jobForm.requirement" type="textarea" :rows="3" placeholder="任职资格、技能要求..." />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <button class="glass-btn ghost" @click="dialogVisible = false">取消</button>
        <button class="glass-btn primary" :loading="saving" @click="saveJob">{{ editId ? '更新' : '发布' }}</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getJobList, publishJob, updateJob, deleteJob as deleteJobApi, toggleJobStatus } from '@/api'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const list = ref([]), total = ref(0), page = ref(1), loading = ref(false)
const dialogVisible = ref(false), editId = ref(null), saving = ref(false)
const jobFormRef = ref()

const jobForm = reactive({ jobName:'', jobType:'全职', workLocation:'', salaryRange:'', educationReq:'本科', recruitNum:1, description:'', requirement:'', deadline:'' })
const rules = {
  jobName:[{required:true,message:'请输入职位名称'}],
  workLocation:[{required:true,message:'请输入工作地点'}],
  recruitNum:[{required:true,message:'请填写招聘人数'}],
  description:[{required:true,message:'请填写职位描述'}]
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getJobList({ companyId: userStore.userInfo?.id, page: page.value, size: 10 })
    list.value = res.data?.records || []
    total.value = Number(res.data?.total || 0)
  } finally { loading.value = false }
}

const openDialog = (row = null) => {
  editId.value = row?.id || null
  if (row) Object.assign(jobForm, row)
  else Object.assign(jobForm, { jobName:'', jobType:'全职', workLocation:'', salaryRange:'', educationReq:'本科', recruitNum:1, description:'', requirement:'', deadline:'' })
  dialogVisible.value = true
}

const saveJob = async () => {
  await jobFormRef.value.validate()
  saving.value = true
  try {
    if (editId.value) { await updateJob(editId.value, jobForm); ElMessage.success('更新成功，等待重新审核') }
    else { await publishJob(jobForm); ElMessage.success('发布成功，等待审核') }
    dialogVisible.value = false; fetchList()
  } finally { saving.value = false }
}

const deleteJob = async (id) => { await deleteJobApi(id); ElMessage.success('删除成功'); fetchList() }
const changeStatus = async (id, status) => { try { await toggleJobStatus(id, { status }) } catch { fetchList() } }

onMounted(fetchList)
</script>

<style scoped>
.jobs-page { padding-bottom: 40px; }

.page-header-ribbon {
  display: flex; align-items: center; gap: 16px;
  padding: 24px 28px; border-radius: 16px; margin-bottom: 24px;
  background: linear-gradient(135deg, rgba(59,130,246,0.08), rgba(99,102,241,0.06));
  border: 1px solid rgba(59,130,246,0.12);
}
.ribbon-icon { width:48px;height:48px;border-radius:13px;background:linear-gradient(135deg,#3B82F6,#2563EB);display:flex;align-items:center;justify-content:center;color:white;flex-shrink:0;box-shadow:0 4px 14px rgba(59,130,246,0.35); }
.ribbon-text h2 { font-size:18px;font-weight:700;color:#1E293B;margin:0 0 4px; }
.ribbon-text p { font-size:13px;color:#94A3B8;margin:0; }
.ribbon-action { margin-left:auto; }

.glass-btn {
  display:inline-flex;align-items:center;gap:7px;padding:9px 20px;border-radius:11px;border:none;font-size:13.5px;font-weight:600;cursor:pointer;transition:all .28s ease;background:linear-gradient(135deg,#e2e8f0,#cbd5e1);color:#475569;
}
.glass-btn.primary { background:linear-gradient(135deg,#3B82F6,#2563EB);color:white;box-shadow:0 3px 12px rgba(59,130,246,.3); }
.glass-btn.primary:hover:not(:disabled) { transform:translateY(-1px);box-shadow:0 5px 16px rgba(59,130,246,.4); }
.glass-btn.ghost { background:#F1F5F9;color:#64748B;border:1px solid #E2E8F0; }
.glass-btn.ghost:hover { background:#EEF2FF;color:#3B82F6;border-color:#BFDBFE; }

.jobs-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(380px, 1fr)); gap: 16px; }

.job-card {
  background: #fff; border: 1px solid rgba(226,232,240,0.85); border-radius: 15px;
  padding: 22px; transition: all 0.3s cubic-bezier(0.4,0,0.2,1);
  position: relative; overflow: hidden;
}
.job-card::before {
  content: ''; position: absolute; top: 0; left: 0; right: 0; height: 3px;
  background: linear-gradient(90deg, #3B82F6, #60A5FA); opacity: 0;
  transition: opacity 0.25s ease;
}
.job-card:hover { transform: translateY(-3px); box-shadow: 0 8px 30px rgba(0,0,0,0.07); border-color: rgba(203,213,225,0.95); }
.job-card:hover::before { opacity: 1; }
.job-card.audit-0::before { background: linear-gradient(90deg, #F59E0B, #D97706); opacity: 1; }
.job-card.audit-2::before { background: linear-gradient(90deg, #EF4444, #DC2626); opacity: 1; }

.jc-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:14px; }
.jc-name { font-size:16px;font-weight:700;color:#1E293B; }
.jc-badges { display:flex; gap:6px; }

.jc-body { margin-bottom:16px; }
.jc-info-row { display:flex; align-items:center; gap:6px; font-size:13px; color:#64748B; margin-bottom:7px; }
.jc-info-row .el-icon { color:#94A3B8; font-size:14px; flex-shrink:0; }
.jc-stats { display:flex; align-items:center; gap:12px; margin-top:12px; padding-top:12px; border-top:1px dashed #F1F5F9; }
.jcs-item b { font-size:14px; font-weight:650; color:#334155; display:block; line-height:1; }
.jcs-item span { font-size:11.5px; color:#94A3B8; margin-top:2px; display:block; }
.jcs-divider { width:1px; height:28px; background:#E2E8F0; }

.jc-footer { display:flex; justify-content:space-between; align-items:center; padding-top:14px; border-top:1px solid #F1F5F9; }
.switch-wrap { display:flex; align-items:center; gap:8px; font-size:12.5px; color:#64748B; cursor:pointer; }
.jc-actions { display:flex; gap:12px; }
.action-link { display:inline-flex;align-items:center;gap:4px;padding:5px 10px;border-radius:8px;border:none;background:none;font-size:12.5px;font-weight:550;cursor:pointer;transition:all .2s ease; }
.action-link.blue { color:#3B82F6; }
.action-link.blue:hover { background:rgba(59,130,246,.08); }
.action-link.red { color:#EF4444; }
.action-link.red:hover { background:rgba(239,68,68,.08); }

.pagination-wrap { display:flex;justify-content:center;margin-top:24px; }
.empty-state { text-align:center;padding:40px;grid-column:1/-1; }

@media (max-width: 768px) {
  .page-header-ribbon { flex-direction:column; align-items:flex-start; }
  .ribbon-action { margin-left:0; margin-top:12px; }
  .jobs-grid { grid-template-columns:1fr; }
}
</style>
