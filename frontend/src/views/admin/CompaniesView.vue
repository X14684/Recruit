<template>
  <div class="admin-companies-page">
    <div class="page-header-ribbon">
      <div class="ribbon-icon"><el-icon :size="22"><OfficeBuilding /></el-icon></div>
      <div class="ribbon-text">
        <h2>企业管理</h2>
        <p>审核和管理企业账号</p>
      </div>
    </div>

    <div class="search-bar">
      <div class="search-input-wrap">
        <el-icon><Search /></el-icon>
        <input v-model="keyword" placeholder="搜索企业名称..." @keyup.enter="fetchList" />
      </div>
      <el-select v-model="auditStatus" placeholder="审核状态" clearable style="width:130px" class="glass-select">
        <el-option label="待审核" :value="0" /><el-option label="已通过" :value="1" /><el-option label="已拒绝" :value="2" />
      </el-select>
      <button class="glass-btn primary" @click="fetchList">搜索</button>
    </div>

    <div class="table-card" v-loading="loading">
      <el-table :data="list" style="width:100%">
        <el-table-column label="企业名称" prop="companyName" min-width="150">
          <template #default="{ row }"><span class="name-bold">{{ row.companyName }}</span></template>
        </el-table-column>
        <el-table-column label="联系人" prop="contactName" width="100" />
        <el-table-column label="联系电话" prop="contactPhone" width="130" />
        <el-table-column label="行业" prop="industry" width="100" />
        <el-table-column label="规模" prop="scale" width="100" />
        <el-table-column label="审核" width="100">
          <template #default="{ row }">
            <el-tag :type="['warning','success','danger'][row.auditStatus]" size="small" round effect="light">
              {{ ['待审核','通过','拒绝'][row.auditStatus] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <span :class="'status-dot ' + (row.status===1?'green':'red')"></span>{{ row.status===1?'正常':'禁用' }}
          </template>
        </el-table-column>
        <el-table-column label="注册时间" prop="createTime" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <template v-if="row.auditStatus === 0">
              <button class="action-toggle success small" @click="audit(row.id,1,'')">通过</button>
              <button class="action-toggle danger small" @click="openReject(row.id)">拒绝</button>
            </template>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap" v-if="total > 0">
        <el-pagination background layout="prev,pager,next" :total="total" :page-size="10"
          v-model:current-page="page" @current-change="fetchList" />
      </div>
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
import { getCompanyList, auditCompany } from '@/api'

const list = ref([]), total = ref(0), page = ref(1), loading = ref(false), keyword = ref(''), auditStatus = ref(null)
const rejectVisible = ref(false), rejectRemark = ref(''), rejectId = ref(null)

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getCompanyList({ page: page.value, size: 10, keyword: keyword.value, auditStatus: auditStatus.value })
    list.value = res.data?.records || []; total.value = Number(res.data?.total || 0)
  } finally { loading.value = false }
}

const audit = async (id, status, remark) => { await auditCompany(id,{status,remark}); ElMessage.success('操作成功'); fetchList() }
const openReject = (id) => { rejectId.value=id; rejectRemark.value=''; rejectVisible.value=true }
const confirmReject = async () => { await audit(rejectId.value,2,rejectRemark.value); rejectVisible.value=false }
onMounted(fetchList)
</script>

<style scoped>
.admin-companies-page{padding-bottom:40px}
.page-header-ribbon{display:flex;align-items:center;gap:16px;padding:24px 28px;border-radius:16px;margin-bottom:20px;background:linear-gradient(135deg,rgba(16,185,129,.08),rgba(5,150,105,.06));border:1px solid rgba(16,185,129,.12)}
.ribbon-icon{width:48px;height:48px;border-radius:13px;background:linear-gradient(135deg,#10B981,#059669);display:flex;align-items:center;justify-content:center;color:white;flex-shrink:0;box-shadow:0 4px 14px rgba(16,185,129,.35)}
.ribbon-text h2{font-size:18px;font-weight:700;color:#1E293B;margin:0 0 4px}.ribbon-text p{font-size:13px;color:#94A3B8;margin:0}
.search-bar{display:flex;gap:12px;margin-bottom:20px;align-items:center}
.search-input-wrap{display:flex;align-items:center;gap:8px;padding:10px 16px;border-radius:12px;background:#fff;border:1px solid rgba(226,232,240,.85);flex:1;max-width:260px;transition:all .25s ease}.search-input-wrap:focus-within{border-color:rgba(59,130,246,.4);box-shadow:0 0 0 3px rgba(59,130,246,.06)}
.search-input-wrap .el-icon{color:#94A3B8;font-size:16px;flex-shrink:0}.search-input-wrap input{border:none;outline:none;background:transparent;font-size:13.5px;color:#334155;width:100%}.search-input-wrap input::placeholder{color:#94A3B8}
.glass-select{flex-shrink:0}
.glass-btn{display:inline-flex;align-items:center;gap:7px;padding:10px 22px;border-radius:11px;border:none;font-size:13.5px;font-weight:600;cursor:pointer;transition:all .28s ease;background:linear-gradient(135deg,#3B82F6,#2563EB);color:white;box-shadow:0 3px 12px rgba(59,130,246,.3)}.glass-btn.primary:hover{transform:translateY(-1px);box-shadow:0 5px 16px rgba(59,130,246,.4)}
.glass-btn.danger{background:linear-gradient(135deg,#EF4444,#DC2626);box-shadow:0 2px 10px rgba(239,68,68,.25)}.glass-btn.danger:hover{transform:translateY(-1px)}.glass-btn.ghost{background:#F1F5F9;color:#64748B;border:1px solid #E2E8F0;box-shadow:none}.glass-btn.ghost:hover{background:#EEF2FF;color:#3B82F6;border-color:#BFDBFE}
.table-card{background:#fff;border:1px solid rgba(226,232,240,.85);border-radius:16px;padding:24px;box-shadow:0 1px 3px rgba(0,0,0,.04)}
.name-bold{font-weight:600;color:#1E293B}.status-dot{display:inline-block;width:8px;height:8px;border-radius:50%;margin-right:4px}.status-dot.green{background:#22C55E;box-shadow:0 0 5px rgba(34,197,94,.4)}.status-dot.red{background:#EF4444;box-shadow:0 0 5px rgba(239,68,68,.4)}
.action-toggle{padding:5px 14px;border-radius:8px;border:none;font-size:12px;font-weight:600;cursor:pointer;transition:all .2s ease;color:white}.action-toggle.success{background:linear-gradient(135deg,#10B981,#059669)}.action-toggle.danger{background:linear-gradient(135deg,#EF4444,#DC2626)}.action-toggle:hover{transform:scale(1.04)}.action-toggle.small{padding:4px 11px;font-size:11.5px}
.pagination-wrap{display:flex;justify-content:center;margin-top:20px}@media(max-width:768px){.search-bar{flex-direction:column}.search-input-wrap,.glass-select{max-width:none}}
</style>