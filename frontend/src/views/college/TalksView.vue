<template>
  <div class="college-talks-page">
    <div class="page-header-ribbon">
      <div class="ribbon-icon"><el-icon :size="22"><Calendar /></el-icon></div>
      <div class="ribbon-text">
        <h2>宣讲会审核</h2>
        <p>审核和管理校园宣讲会申请</p>
      </div>
    </div>

    <!-- 审核列表 -->
    <div class="talks-list" v-loading="loading">
      <template v-if="list.length > 0">
        <div v-for="row in list" :key="row.id" class="talk-card" :class="'audit-' + row.auditStatus">
          <div class="tc-left">
            <div class="tc-header">
              <span class="tc-title">{{ row.title }}</span>
              <el-tag :type="['warning','success','danger'][row.auditStatus]" size="small" round effect="light">
                {{ ['待审核','已通过','已拒绝'][row.auditStatus] }}
              </el-tag>
            </div>
            <div class="tc-info">
              <span><el-icon><Timer /></el-icon> {{ formatTime(row.talkTime) }}</span>
              <span><el-icon><Location /></el-icon> {{ row.location }}</span>
              <span>上限：{{ row.maxSignNum }} 人</span>
            </div>
          </div>
          <div class="tc-actions">
            <template v-if="row.auditStatus === 0">
              <button class="glass-btn primary small" @click="audit(row.id, 1)">通过</button>
              <button class="glass-btn danger small" @click="openReject(row.id)">拒绝</button>
            </template>
            <span v-else class="handled-badge">已处理</span>
          </div>
        </div>
      </template>
      <template v-else-if="!loading">
        <div class="empty-state"><el-empty description="暂无宣讲会记录" /></div>
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
import { getTalkList, auditTalk } from '@/api'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

const userStore = useUserStore()
const list = ref([]), total = ref(0), page = ref(1), loading = ref(false)
const rejectVisible = ref(false), rejectRemark = ref(''), rejectId = ref(null)

const formatTime = (t) => t ? dayjs(t).format('YYYY/MM/DD HH:mm') : '-'

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getTalkList({ page: page.value, size: 10, collegeId: userStore.userInfo?.id })
    list.value = res.data?.records || []
    total.value = Number(res.data?.total || 0)
  } finally { loading.value = false }
}

const audit = async (id, status) => { await auditTalk(id,{status,remark:'审核通过'}); ElMessage.success('审核通过'); fetchList() }
const openReject = (id) => { rejectId.value=id; rejectRemark.value=''; rejectVisible.value=true }
const confirmReject = async () => { await auditTalk(rejectId.value,{status:2,remark:rejectRemark.value}); ElMessage.success('已拒绝'); rejectVisible.value=false; fetchList() }

onMounted(fetchList)
</script>

<style scoped>
.college-talks-page{padding-bottom:40px}
.page-header-ribbon{display:flex;align-items:center;gap:16px;padding:24px 28px;border-radius:16px;margin-bottom:20px;background:linear-gradient(135deg,rgba(139,92,246,.08),rgba(109,40,217,.06));border:1px solid rgba(139,92,246,.12)}
.ribbon-icon{width:48px;height:48px;border-radius:13px;background:linear-gradient(135deg,#8B5CF6,#6D28D9);display:flex;align-items:center;justify-content:center;color:white;flex-shrink:0;box-shadow:0 4px 14px rgba(139,92,246,.35)}
.ribbon-text h2{font-size:18px;font-weight:700;color:#1E293B;margin:0 0 4px}.ribbon-text p{font-size:13px;color:#94A3B8;margin:0}

.talks-list{display:flex;flex-direction:column;gap:12px}
.talk-card{
  display:flex;justify-content:space-between;align-items:center;
  background:#fff;border:1px solid rgba(226,232,240,.85);border-radius:14px;padding:20px 24px;
  transition:all .25s cubic-bezier(.4,0,.2,1);position:relative;overflow:hidden
}
.talk-card::before{content:'';position:absolute;top:0;left:0;width:4px;height:100%;border-radius:14px 0 0 14px}
.talk-card.audit-0::before{background:linear-gradient(180deg,#F59E0B,#D97706)}
.talk-card.audit-1::before{background:linear-gradient(180deg,#10B981,#059669)}
.talk-card.audit-2::before{background:linear-gradient(180deg,#EF4444,#DC2626)}
.talk-card:hover{box-shadow:0 4px 20px rgba(0,0,0,.05);transform:translateY(-1px)}

.tc-left{flex:1;min-width:0}
.tc-header{display:flex;align-items:center;gap:10px;margin-bottom:10px}
.tc-title{font-size:16px;font-weight:650;color:#1E293B}
.tc-info{display:flex;gap:18px;flex-wrap:wrap}
.tc-info span{display:flex;align-items:center;gap:5px;font-size:13px;color:#64748B}
.tc-info span .el-icon{color:#94A3B8;font-size:15px}

.tc-actions{display:flex;gap:8px;flex-shrink:0;margin-left:20px;align-items:center}
.glass-btn{display:inline-flex;align-items:center;justify-content:center;gap:6px;padding:7px 16px;border-radius:10px;border:none;font-size:12.5px;font-weight:600;cursor:pointer;transition:all .28s ease;background:linear-gradient(135deg,#3B82F6,#2563EB);color:white;box-shadow:0 2px 10px rgba(59,130,246,.25)}
.glass-btn.primary:hover:not(:disabled){transform:translateY(-1px);box-shadow:0 4px 14px rgba(59,130,246,.35)}
.glass-btn.danger{background:linear-gradient(135deg,#EF4444,#DC2626);box-shadow:0 2px 10px rgba(239,68,68,.25)}.glass-btn.danger:hover{transform:translateY(-1px);box-shadow:0 4px 14px rgba(239,68,68,.35)}
.glass-btn.ghost{background:#F1F5F9;color:#64748B;border:1px solid #E2E8F0;box-shadow:none}.glass-btn.ghost:hover{background:#EEF2FF;color:#3B82F6;border-color:#BFDBFE}
.handled-badge{font-size:13px;color:#94A3B8;font-weight:500}

.pagination-wrap{display:flex;justify-content:center;margin-top:20px}.empty-state{text-align:center;padding:60px 20px}
@media(max-width:768px){.talk-card{flex-direction:column;align-items:flex-start;gap:14px}.tc-actions{margin-left:0;width:100%}}
</style>