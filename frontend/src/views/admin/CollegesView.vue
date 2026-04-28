<template>
  <div class="admin-colleges-page">
    <div class="page-header-ribbon">
      <div class="ribbon-icon"><el-icon :size="22"><School /></el-icon></div>
      <div class="ribbon-text">
        <h2>学院管理</h2>
        <p>管理系统内的所有学院账号</p>
      </div>
      <div class="ribbon-action">
        <button class="glass-btn primary" @click="openAdd">
          <el-icon :size="14"><Plus /></el-icon> 新增学院
        </button>
      </div>
    </div>

    <div class="table-card" v-loading="loading">
      <el-table :data="list" style="width:100%">
        <el-table-column label="学院名称" prop="collegeName" min-width="150">
          <template #default="{ row }"><span class="name-bold">{{ row.collegeName }}</span></template>
        </el-table-column>
        <el-table-column label="编码" prop="collegeCode" width="100" />
        <el-table-column label="登录账号" prop="username" width="120" />
        <el-table-column label="负责人" prop="directorName" width="100" />
        <el-table-column label="电话" prop="phone" width="130" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <span :class="'status-dot ' + (row.status===1?'green':'red')"></span>{{ row.status===1?'正常':'禁用' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <button class="action-link amber" @click="openResetPwd(row)">重置密码</button>
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

    <!-- 新增学院 -->
    <el-dialog v-model="addVisible" title="新增学院" width="520px" :close-on-click-modal="false">
      <el-form :model="form" ref="formRef" label-width="90px">
        <el-form-item label="学院名称" prop="collegeName" :rules="[{required:true,message:'必填'}]">
          <el-input v-model="form.collegeName" placeholder="请输入学院名称" />
        </el-form-item>
        <el-form-item label="学院编码"><el-input v-model="form.collegeCode" placeholder="如：CS、EE" /></el-form-item>
        <el-form-item label="登录账号" prop="username" :rules="[{required:true,message:'必填'}]">
          <el-input v-model="form.username" placeholder="登录用户名" />
        </el-form-item>
        <el-form-item label="初始密码" prop="password" :rules="[{required:true,message:'必填'},{min:6,message:'至少6位'}]">
          <el-input v-model="form.password" type="password" show-password placeholder="至少6位" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="负责人"><el-input v-model="form.directorName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="联系电话"><el-input v-model="form.phone" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="联系邮箱"><el-input v-model="form.email" /></el-form-item>
      </el-form>
      <template #footer>
        <button class="glass-btn ghost" @click="addVisible=false">取消</button>
        <button class="glass-btn primary" :loading="saving" @click="save">确认添加</button>
      </template>
    </el-dialog>

    <!-- 重置密码 -->
    <el-dialog v-model="pwdVisible" title="重置学院密码" width="420px">
      <el-form :model="pwdForm" ref="pwdFormRef" label-width="90px">
        <el-form-item label="学院账号"><el-input :value="currentCollege?.username" disabled /></el-form-item>
        <el-form-item label="新密码" prop="newPassword" :rules="[{required:true,message:'请输入新密码'},{min:6,message:'至少6位'}]">
          <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="至少6位" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword"
          :rules="[{required:true,message:'请确认密码'},{validator:confirmPwdValidator,trigger:'blur'}]">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <button class="glass-btn ghost" @click="pwdVisible=false">取消</button>
        <button class="glass-btn primary" :loading="pwdSaving" @click="doResetPwd">确认重置</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminCollegeList, addCollege, toggleCollegeStatus, resetCollegePassword } from '@/api'

const list = ref([]), total = ref(0), page = ref(1), loading = ref(false)
const addVisible = ref(false), saving = ref(false), formRef = ref()
const form = reactive({ collegeName:'', collegeCode:'', username:'', password:'', directorName:'', phone:'', email:'' })
// 重置密码相关
const pwdVisible = ref(false), pwdSaving = ref(false), pwdFormRef = ref(), currentCollege = ref(null)
const pwdForm = reactive({ newPassword:'', confirmPassword:'' })
const confirmPwdValidator = (rule,value,callback) => value !== pwdForm.newPassword ? callback(new Error('两次密码不一致')) : callback()

const fetchList = async () => {
  loading.value = true
  try { const res = await getAdminCollegeList({ page: page.value, size: 10 }); list.value = res.data?.records || []; total.value = Number(res.data?.total||0) }
  finally { loading.value = false }
}
const openAdd = () => { Object.assign(form,{collegeName:'',collegeCode:'',username:'',password:'',directorName:'',phone:'',email:''}); addVisible=true }
const save = async () => { await formRef.value.validate(); saving.value=true; try{await addCollege(form); ElMessage.success('添加成功'); addVisible=false; fetchList()}finally{saving=false}}
const toggleStatus = async (row) => { const s=row.status===1?0:1; await toggleCollegeStatus(row.id,{status:s}); row.status=s; ElMessage.success('操作成功') }
const openResetPwd = (row) => { currentCollege.value=row; Object.assign(pwdForm,{newPassword:'',confirmPassword:''}); pwdFormRef.value?.resetFields(); pwdVisible=true }
const doResetPwd = async () => { await pwdFormRef.value.validate(); pwdSaving=true; try{await resetCollegePassword(currentCollege.value.id,{newPassword:pwdForm.newPassword}); ElMessage.success(`账号 ${currentCollege.value.username} 密码重置成功`); pwdVisible=false; pwdFormRef.resetFields()}finally{pwdSaving=false}}
onMounted(fetchList)
</script>

<style scoped>
.admin-colleges-page{padding-bottom:40px}
.page-header-ribbon{display:flex;align-items:center;gap:16px;padding:24px 28px;border-radius:16px;margin-bottom:20px;background:linear-gradient(135deg,rgba(139,92,246,.08),rgba(109,40,217,.06));border:1px solid rgba(139,92,246,.12)}
.ribbon-icon{width:48px;height:48px;border-radius:13px;background:linear-gradient(135deg,#8B5CF6,#6D28D9);display:flex;align-items:center;justify-content:center;color:white;flex-shrink:0;box-shadow:0 4px 14px rgba(139,92,246,.35)}
.ribbon-text h2{font-size:18px;font-weight:700;color:#1E293B;margin:0 0 4px}.ribbon-text p{font-size:13px;color:#94A3B8;margin:0}.ribbon-action{margin-left:auto}
.glass-btn{display:inline-flex;align-items:center;gap:7px;padding:9px 20px;border-radius:11px;border:none;font-size:13.5px;font-weight:600;cursor:pointer;transition:all .28s ease;background:linear-gradient(135deg,#3B82F6,#2563EB);color:white;box-shadow:0 3px 12px rgba(59,130,246,.3)}.glass-btn.primary:hover{transform:translateY(-1px);box-shadow:0 5px 16px rgba(59,130,246,.4)}.glass-btn.danger{background:linear-gradient(135deg,#EF4444,#DC2626);box-shadow:0 2px 10px rgba(239,68,68,.25)}.glass-btn.danger:hover{transform:translateY(-1px)}.glass-btn.ghost{background:#F1F5F9;color:#64748B;border:1px solid #E2E8F0;box-shadow:none}.glass-btn.ghost:hover{background:#EEF2FF;color:#3B82F6;border-color:#BFDBFE}

.table-card{background:#fff;border:1px solid rgba(226,232,240,.85);border-radius:16px;padding:24px;box-shadow:0 1px 3px rgba(0,0,0,.04)}
.name-bold{font-weight:600;color:#1E293B}.status-dot{display:inline-block;width:8px;height:8px;border-radius:50%;margin-right:4px}.status-dot.green{background:#22C55E;box-shadow:0 0 5px rgba(34,197,94,.4)}.status-dot.red{background:#EF4444;box-shadow:0 0 5px rgba(239,68,68,.4)}

.action-link{display:inline-flex;align-items:center;gap:4px;padding:5px 12px;border-radius:8px;border:none;background:none;font-size:12.5px;font-weight:550;cursor:pointer;transition:all .2s ease;color:#D97706}.action-link.amber:hover{background:rgba(245,158,11,.08);color:#B45309}.action-toggle{padding:5px 14px;border-radius:8px;border:none;font-size:12px;font-weight:600;cursor:pointer;transition:all .2s ease;color:white}.action-toggle.success{background:linear-gradient(135deg,#10B981,#059669)}.action-toggle.danger{background:linear-gradient(135deg,#EF4444,#DC2626)}.action-toggle:hover{transform:scale(1.04)}
.pagination-wrap{display:flex;justify-content:center;margin-top:20px}@media(max-width:768px){.page-header-ribbon{flex-direction:column;align-items:flex-start};.ribbon-action{margin-left:0;margin-top:12px}}
</style>