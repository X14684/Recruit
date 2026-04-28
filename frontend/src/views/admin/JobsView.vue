<template>
  <div class="admin-jobs-page">
    <div class="page-header-ribbon">
      <div class="ribbon-icon"><el-icon :size="22"><Briefcase /></el-icon></div>
      <div class="ribbon-text">
        <h2>职位审核</h2>
        <p>审核和管理所有企业发布的职位</p>
      </div>
    </div>

    <div class="search-bar">
      <div class="search-input-wrap">
        <el-icon><Search /></el-icon>
        <input v-model="keyword" placeholder="搜索职位名称..." @keyup.enter="fetchList" />
      </div>
      <el-select v-model="auditStatus" placeholder="审核状态" clearable style="width:130px" class="glass-select">
        <el-option label="待审核" :value="0" /><el-option label="已通过" :value="1" /><el-option label="已拒绝" :value="2" />
      </el-select>
      <button class="glass-btn primary" @click="fetchList">搜索</button>
    </div>

    <div class="jobs-grid" v-loading="loading">
      <template v-if="list.length > 0">
        <div v-for="row in list" :key="row.id" class="job-card" :class="'audit-' + row.auditStatus">
          <div class="jc-header">
            <span class="jc-name">{{ row.jobName }}</span>
            <el-tag :type="['warning','success','danger'][row.auditStatus]" size="small" round effect="light">
              {{ ['待审核','通过','拒绝'][row.auditStatus] }}
            </el-tag>
          </div>
          <div class="jc-body">
            <div class="jc-row"><el-icon><OfficeBuilding /></el-icon> 企业ID: {{ row.companyId }}</div>
            <div class="jc-row"><el-icon><Location /></el-icon> {{ row.workLocation }}</div>
            <div class="jc-row"><el-icon><Coin /></el-icon> {{ row.salaryRange || '薪资面议' }}</div>
            <div class="jc-time">{{ row.createTime?.substring(0,10) }} 发布</div>
          </div>
          <div v-if="row.auditStatus === 0" class="jc-actions">
            <button class="action-toggle success small" @click="audit(row.id,1,'')">通过</button>
            <button class="action-toggle danger small" @click="openReject(row.id)">拒绝</button>
          </div>
        </div>
      </template>
      <template v-else-if="!loading">
        <div class="empty-state"><el-empty description="暂无待审核职位" /></div>
      </template>
    </div>

    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="10"
        v-model:current-page="page" @current-change="fetchList" />
    </div>

    <el-dialog v-model="rejectVisible" title="拒绝原因" width="400px">
      <el-input v-model="rejectRemark" type="textarea" :rows="3" placeholder="请填写拒绝原因..." />
      <template #footer>
        <button class="glass-btn ghost" @click="rejectVisible=false">取消</button>
        <button class="glass-btn danger" @click="confirmReject">确认拒绝</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminJobList, auditJob } from '@/api'

const list = ref([]), total = ref(0), page = ref(1), loading = ref(false)
const keyword = ref(''), auditStatus = ref(null)
const rejectVisible = ref(false), rejectRemark = ref(''), rejectId = ref(null)

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getAdminJobList({ page: page.value, size: 10, keyword: keyword.value, auditStatus: auditStatus.value })
    list.value = res.data?.records || []; total.value = Number(res.data?.total || 0)
  } finally { loading.value = false }
}
const audit = async (id,status,remark) => { await auditJob(id,{status,remark}); ElMessage.success('操作成功'); fetchList() }
const openReject = (id) => { rejectId.value=id; rejectRemark=''; rejectVisible=true }
const confirmReject = async () => { await audit(rejectId.value,2,rejectRemark); rejectVisible.value=false }
onMounted(fetchList)
</script>

<style scoped>
.admin-jobs-page{padding-bottom:40px}
.page-header-ribbon{display:flex;align-items:center;gap:16px;padding:24px 28px;border-radius:16px;margin-bottom:20px;background:linear-gradient(135deg,rgba(245,158,11,.08),rgba(217,119,6,.06));border:1px solid rgba(245,158,11,.12)}
.ribbon-icon{width:48px;height:48px;border-radius:13px;background:linear-gradient(135deg,#F59E0B,#D97706);display:flex;align-items:center;justify-content:center;color:white;flex-shrink:0;box-shadow:0 4px 14px rgba(245,158,11,.35)}
.ribbon-text h2{font-size:18px;font-weight:700;color:#1E293B;margin:0 0 4px}.ribbon-text p{font-size:13px;color:#94A3B8;margin:0}

.search-bar{display:flex;gap:12px;margin-bottom:20px;align-items:center}
.search-input-wrap{display:flex;align-items:center;gap:8px;padding:10px 16px;border-radius:12px;background:#fff;border:1px solid rgba(226,232,240,.85);flex:1;max-width:260px;transition:all .25s ease}.search-input-wrap:focus-within{border-color:rgba(59,130,246,.4);box-shadow:0 0 0 3px rgba(59,130,246,.06)}
.search-input-wrap .el-icon{color:#94A3B8;font-size:16px;flex-shrink:0}.search-input-wrap input{border:none;outline:none;background:transparent;font-size:13.5px;color:#334155;width:100%}.search-input-wrap input::placeholder{color:#94A3B8}.glass-select{flex-shrink:0}
.glass-btn{display:inline-flex;align-items:center;gap:7px;padding:10px 22px;border-radius:11px;border:none;font-size:13.5px;font-weight:600;cursor:pointer;transition:all .28s ease;background:linear-gradient(135deg,#3B82F6,#2563EB);color:white;box-shadow:0 3px 12px rgba(59,130,246,.3)}.glass-btn.primary:hover{transform:translateY(-1px);box-shadow:0 5px 16px rgba(59,130,246,.4)}.glass-btn.danger{background:linear-gradient(135deg,#EF4444,#DC2626);box-shadow:0 2px 10px rgba(239,68,68,.25)}.glass-btn.danger:hover{transform:translateY(-1px)}.glass-btn.ghost{background:#F1F5F9;color:#64748B;border:1px solid #E2E8F0;box-shadow:none}.glass-btn.ghost:hover{background:#EEF2FF;color:#3B82F6;border-color:#BFDBFE}

.jobs-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(340px,1fr));gap:16px}
.job-card{background:#fff;border:1px solid rgba(226,232,240,.85);border-radius:15px;padding:22px;transition:all .25s cubic-bezier(.4,0,.2,1);position:relative;overflow:hidden}
.job-card::before{content:'';position:absolute;top:0;left:0;right:0;height:3px;background:linear-gradient(90deg,#F59E0B,#D97706);opacity:.8}
.job-card:hover{transform:translateY(-2px);box-shadow:0 6px 25px rgba(0,0,0,.06)}
.job-card.audit-0::before{background:linear-gradient(90deg,#F59E0B,#D97706)}.job-card.audit-1::before{background:linear-gradient(90deg,#10B981,#059669)}.job-card.audit-2::before{background:linear-gradient(90deg,#EF4444,#DC2626)}

.jc-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:14px}
.jc-name{font-size:16px;font-weight:650;color:#1E293B}
.jc-body{margin-bottom:16px}
.jc-row{display:flex;align-items:center;gap:7px;font-size:13px;color:#64748B;margin-bottom:7px}.jc-row .el-icon{color:#94A3B8;font-size:14px;flex-shrink:0}
.jc-time{text-align:right;font-size:11.5px;color:#94A3B8;padding-top:10px;border-top:1px dashed #F1F5F9}
.jc-actions{display:flex;gap:10px;justify-content:flex-end;padding-top:12px;border-top:1px solid #F1F5F9}
.action-toggle{padding:6px 16px;border-radius:9px;border:none;font-size:12.5px;font-weight:600;cursor:pointer;transition:all .2s ease;color:white}.action-toggle.success{background:linear-gradient(135deg,#10B981,#059669)}.action-toggle.danger{background:linear-gradient(135deg,#EF4444,#DC2626)}.action-toggle:hover{transform:scale(1.04)}.action-toggle.small{padding:5px 14px;font-size:11.5px}
.pagination-wrap{display:flex;justify-content:center;margin-top:24px}.empty-state{text-align:center;padding:40px;grid-column:1/-1}
@media(max-width:768px){.search-bar{flex-direction:column};.search-input-wrap,.glass-select{max-width:none}.jobs-grid{grid-template-columns:1fr}}
</style>