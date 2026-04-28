<template>
  <div class="admin-students-page">
    <div class="page-header-ribbon">
      <div class="ribbon-icon"><el-icon :size="22"><User /></el-icon></div>
      <div class="ribbon-text">
        <h2>学生管理</h2>
        <p>管理系统内所有学生账号</p>
      </div>
    </div>

    <div class="search-bar">
      <div class="search-input-wrap">
        <el-icon><Search /></el-icon>
        <input v-model="keyword" placeholder="搜索学生..." @keyup.enter="fetchList" />
      </div>
      <button class="glass-btn primary" @click="fetchList">搜索</button>
    </div>

    <div class="table-card" v-loading="loading">
      <el-table :data="list" style="width:100%">
        <el-table-column label="学号" prop="studentNo" width="120" />
        <el-table-column label="姓名" prop="realName" width="100">
          <template #default="{ row }"><span class="name-bold">{{ row.realName }}</span></template>
        </el-table-column>
        <el-table-column label="学院ID" prop="collegeId" width="80" />
        <el-table-column label="专业" prop="major" min-width="130" />
        <el-table-column label="年级" prop="grade" width="80" />
        <el-table-column label="邮箱" prop="email" min-width="170" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <span :class="'status-dot ' + (row.status===1?'green':'red')"></span>
            {{ row.status===1?'正常':'禁用' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="110" fixed="right">
          <template #default="{ row }">
            <button :class="['action-toggle', row.status===1?'danger':'success']" @click="toggleStatus(row)">
              {{ row.status===1?'禁用':'启用' }}
            </button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap" v-if="total > 0">
        <el-pagination background layout="prev, pager, next" :total="total" :page-size="10"
          v-model:current-page="page" @current-change="fetchList" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getStudentList, toggleStudentStatus } from '@/api'

const list = ref([]), total = ref(0), page = ref(1), loading = ref(false), keyword = ref('')

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getStudentList({ page: page.value, size: 10, keyword: keyword.value })
    list.value = res.data?.records || []
    total.value = Number(res.data?.total || 0)
  } finally { loading.value = false }
}

const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  await toggleStudentStatus(row.id, { status: newStatus }); row.status = newStatus; ElMessage.success('操作成功')
}
onMounted(fetchList)
</script>

<style scoped>
.admin-students-page{padding-bottom:40px}
.page-header-ribbon{display:flex;align-items:center;gap:16px;padding:24px 28px;border-radius:16px;margin-bottom:20px;background:linear-gradient(135deg,rgba(59,130,246,.08),rgba(99,102,241,.06));border:1px solid rgba(59,130,246,.12)}
.ribbon-icon{width:48px;height:48px;border-radius:13px;background:linear-gradient(135deg,#3B82F6,#2563EB);display:flex;align-items:center;justify-content:center;color:white;flex-shrink:0;box-shadow:0 4px 14px rgba(59,130,246,.35)}
.ribbon-text h2{font-size:18px;font-weight:700;color:#1E293B;margin:0 0 4px}.ribbon-text p{font-size:13px;color:#94A3B8;margin:0}

.search-bar{display:flex;gap:12px;margin-bottom:20px}
.search-input-wrap{display:flex;align-items:center;gap:8px;padding:10px 16px;border-radius:12px;background:#fff;border:1px solid rgba(226,232,240,.85);flex:1;max-width:280px;transition:all .25s ease}
.search-input-wrap:focus-within{border-color:rgba(59,130,246,.4);box-shadow:0 0 0 3px rgba(59,130,246,.06)}
.search-input-wrap .el-icon{color:#94A3B8;font-size:16px;flex-shrink:0}.search-input-wrap input{border:none;outline:none;background:transparent;font-size:13.5px;color:#334155;width:100%}.search-input-wrap input::placeholder{color:#94A3B8}

.glass-btn{display:inline-flex;align-items:center;gap:7px;padding:10px 22px;border-radius:11px;border:none;font-size:13.5px;font-weight:600;cursor:pointer;transition:all .28s ease;background:linear-gradient(135deg,#3B82F6,#2563EB);color:white;box-shadow:0 3px 12px rgba(59,130,246,.3)}
.glass-btn.primary:hover{transform:translateY(-1px);box-shadow:0 5px 16px rgba(59,130,246,.4)}

.table-card{background:#fff;border:1px solid rgba(226,232,240,.85);border-radius:16px;padding:24px;box-shadow:0 1px 3px rgba(0,0,0,.04)}
.name-bold{font-weight:600;color:#1E293B}.status-dot{display:inline-block;width:8px;height:8px;border-radius:50%;margin-right:4px}.status-dot.green{background:#22C55E;box-shadow:0 0 5px rgba(34,197,94,.4)}.status-dot.red{background:#EF4444;box-shadow:0 0 5px rgba(239,68,68,.4)}

.action-toggle{padding:5px 14px;border-radius:8px;border:none;font-size:12px;font-weight:600;cursor:pointer;transition:all .2s ease;color:white}.action-toggle.success{background:linear-gradient(135deg,#10B981,#059669)}.action-toggle.danger{background:linear-gradient(135deg,#EF4444,#DC2626)}.action-toggle:hover{transform:scale(1.04)}
.pagination-wrap{display:flex;justify-content:center;margin-top:20px}@media(max-width:768px){.search-bar{flex-direction:column}.search-input-wrap{max-width:none}}
</style>