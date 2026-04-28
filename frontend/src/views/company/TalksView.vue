<template>
  <div class="talks-page">
    <div class="page-header-ribbon">
      <div class="ribbon-icon"><el-icon :size="22"><Calendar /></el-icon></div>
      <div class="ribbon-text">
        <h2>宣讲会管理</h2>
        <p>申请和管理校园宣讲活动</p>
      </div>
      <div class="ribbon-action">
        <button class="glass-btn primary" @click="openDialog()">
          <el-icon :size="14"><Plus /></el-icon> 申请宣讲会
        </button>
      </div>
    </div>

    <div class="talks-grid" v-loading="loading">
      <template v-if="list.length > 0">
        <div v-for="row in list" :key="row.id" class="talk-card" :class="'audit-' + row.auditStatus">
          <div class="tc-header">
            <span class="tc-title">{{ row.title }}</span>
            <el-tag :type="['warning','success','danger'][row.auditStatus]" size="small" round effect="light">
              {{ ['待审核','已通过','已拒绝'][row.auditStatus] }}
            </el-tag>
          </div>
          <div class="tc-body">
            <div class="tc-row"><el-icon><Timer /></el-icon> {{ formatTime(row.talkTime) }}</div>
            <div class="tc-row"><el-icon><Location /></el-icon> {{ row.location }}</div>
          </div>
          <div class="tc-progress">
            <div class="tcp-label">报名进度</div>
            <el-progress :percentage="Math.round((row.currentSignNum||0)/(row.maxSignNum||1)*100)" 
              :color="progressColor(row)" :show-text="false" />
            <div class="tcp-num">{{ row.currentSignNum || 0 }} / {{ row.maxSignNum || 0 }} 人</div>
          </div>
        </div>
      </template>
      <template v-else-if="!loading">
        <div class="empty-state"><el-empty description="暂无宣讲会记录" /></div>
      </template>
    </div>

    <el-dialog v-model="dialogVisible" title="申请宣讲会" width="580px" :close-on-click-modal="false">
      <el-form :model="form" ref="formRef" label-width="90px">
        <el-form-item label="标题" prop="title" :rules="[{required:true,message:'请输入标题'}]">
          <el-input v-model="form.title" placeholder="宣讲会主题" />
        </el-form-item>
        <el-form-item label="目标学院">
          <el-select v-model="form.collegeId" style="width:100%">
            <el-option v-for="c in colleges" :key="c.id" :label="c.collegeName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="宣讲时间">
          <el-date-picker v-model="form.talkTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width:100%" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="举办地点"><el-input v-model="form.location" placeholder="如：A栋报告厅" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大人数">
              <el-input-number v-model="form.maxSignNum" :min="10" :max="1000" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="报名截止">
          <el-date-picker v-model="form.signDeadline" type="date" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="宣讲描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="简要介绍宣讲内容..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <button class="glass-btn ghost" @click="dialogVisible=false">取消</button>
        <button class="glass-btn primary" :loading="saving" @click="saveTalk">提交申请</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyTalkList, publishTalk, getCollegeList } from '@/api'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

const userStore = useUserStore()
const list = ref([]), loading = ref(false), dialogVisible = ref(false), saving = ref(false)
const formRef = ref(), colleges = ref([])
const form = reactive({ title:'', collegeId:'', talkTime:'', location:'', signDeadline:'', maxSignNum:200, description:'' })

const formatTime = (t) => t ? dayjs(t).format('YYYY/MM/DD HH:mm') : '-'
const progressColor = (talk) => {
  const pct = (talk.currentSignNum||0)/(talk.maxSignNum||1)
  if(pct >= .9) return '#EF4444'; if(pct >= .7) return '#F59E0B'; return '#10B981'
}

const fetchList = async () => {
  loading.value = true
  try { const res = await getMyTalkList({ page:1, size:50 }); list.value = res.data?.records || [] }
  finally { loading.value = false }
}
const openDialog = () => { Object.assign(form,{title:'',collegeId:'',talkTime:'',location:'',signDeadline:'',maxSignNum:200,description:''}); dialogVisible.value = true }

const saveTalk = async () => {
  await formRef.value.validate(); saving.value = true
  try { await publishTrade({...form, companyId:userStore.userInfo?.id}); ElMessage.success('申请成功，等待审核'); dialogVisible.value=false; fetchList() }
  finally { saving.value = false }
}
onMounted(async () => { fetchList(); const res = await getCollegeList({ page:1, size:100 }); colleges.value = res.data?.records || [] })
</script>
<style scoped>
.talks-page{padding-bottom:40px}
.page-header-ribbon{display:flex;align-items:center;gap:16px;padding:24px 28px;border-radius:16px;margin-bottom:24px;background:linear-gradient(135deg,rgba(245,158,11,.08),rgba(217,119,6,.06));border:1px solid rgba(245,158,11,.12)}
.ribbon-icon{width:48px;height:48px;border-radius:13px;background:linear-gradient(135deg,#F59E0B,#D97706);display:flex;align-items:center;justify-content:center;color:white;flex-shrink:0;box-shadow:0 4px 14px rgba(245,158,11,.35)}
.ribbon-text h2{font-size:18px;font-weight:700;color:#1E293B;margin:0 0 4px}.ribbon-text p{font-size:13px;color:#94A3B8;margin:0}
.ribbon-action{margin-left:auto}
.glass-btn{display:inline-flex;align-items:center;gap:7px;padding:9px 20px;border-radius:11px;border:none;font-size:13.5px;font-weight:600;cursor:pointer;transition:all .28s ease;background:linear-gradient(135deg,#3B82F6,#2563EB);color:white;box-shadow:0 3px 12px rgba(59,130,246,.3)}
.glass-btn.primary:hover{transform:translateY(-1px);box-shadow:0 5px 16px rgba(59,130,246,.4)}
.glass-btn.ghost{background:#F1F5F9;color:#64748B;border:1px solid #E2E8F0;box-shadow:none}.glass-btn.ghost:hover{background:#EEF2FF;color:#3B82F6;border-color:#BFDBFE}

.talks-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(340px,1fr));gap:16px}
.talk-card{background:#fff;border:1px solid rgba(226,232,240,.85);border-radius:15px;padding:22px;transition:all .25s cubic-bezier(.4,0,.2,1);position:relative;overflow:hidden}
.talk-card::before{content:'';position:absolute;top:0;left:0;right:0;height:3px;background:linear-gradient(90deg,#F59E0B,#D97706);opacity:.7}
.talk-card:hover{transform:translateY(-2px);box-shadow:0 6px 25px rgba(0,0,0,.06)}
.talk-card.audit-0::before{background:linear-gradient(90deg,#F59E0B,#D97706)}
.talk-card.audit-1::before{background:linear-gradient(90deg,#10B981,#059669)}
.talk-card.audit-2::before{background:linear-gradient(90deg,#EF4444,#DC2626)}

.tc-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:14px}
.tc-title{font-size:16px;font-weight:650;color:#1E293B}
.tc-body{margin-bottom:16px}
.tc-row{display:flex;align-items:center;gap:7px;font-size:13.5px;color:#64748B;margin-bottom:7px}
.tc-row .el-icon{color:#94A3B8;font-size:15px;flex-shrink:0}

.tc-progress{padding-top:14px;border-top:1px dashed #F1F5F9}
.tcp-label{font-size:12px;color:#94A3B8;margin-bottom:8px;display:block}
.tcp-num{text-align:right;font-size:12px;font-weight:600;color:#475569;margin-top:5px}

.pagination-wrap{display:flex;justify-content:center;margin-top:24px}.empty-state{text-align:center;padding:40px;grid-column:1/-1}
@media(max-width:768px){.page-header-ribbon{flex-direction:column;align-items:flex-start};.ribbon-action{margin-left:0;margin-top:12px}.talks-grid{grid-template-columns:1fr}}
</style>