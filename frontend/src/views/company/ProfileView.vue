<template>
  <div class="company-profile-page">
    <div class="page-header-ribbon">
      <div class="ribbon-icon"><el-icon :size="22"><OfficeBuilding /></el-icon></div>
      <div class="ribbon-text">
        <h2>企业信息</h2>
        <p>管理你的企业基本资料</p>
      </div>
      <div class="ribbon-action">
        <button class="glass-btn primary" @click="save" :disabled="saving">
          <span v-if="!saving">保存</span>
          <span v-else class="btn-loading"></span>
        </button>
      </div>
    </div>

    <div class="profile-body" v-if="form.id">
      <div class="section-card">
        <div class="section-header">
          <div class="sh-icon blue"><el-icon><OfficeBuilding /></el-icon></div>
          <h3>基本信息</h3>
        </div>
        <div class="form-grid">
          <div class="fg-item full-width">
            <label><el-icon><HomeFilled /></el-icon> 企业名称</label>
            <el-input v-model="form.companyName" placeholder="请输入企业全称" />
          </div>
          <div class="fg-item">
            <label><el-icon><User /></el-icon> 联系人</label>
            <el-input v-model="form.contactName" placeholder="HR联系人姓名" />
          </div>
          <div class="fg-item">
            <label><el-icon><Phone /></el-icon> 联系电话</label>
            <el-input v-model="form.contactPhone" placeholder="联系电话" />
          </div>
          <div class="fg-item">
            <label><el-icon><Message /></el-icon> 联系邮箱</label>
            <el-input v-model="form.contactEmail" placeholder="联系邮箱" />
          </div>
          <div class="fg-item">
            <label><el-icon><Box /></el-icon> 所属行业</label>
            <el-input v-model="form.industry" placeholder="行业领域" />
          </div>
          <div class="fg-item">
            <label><el-icon><Histogram /></el-icon> 企业规模</label>
            <el-input v-model="form.scale" placeholder="如：100-499人" />
          </div>
          <div class="fg-item">
            <label><el-icon><Flag /></el-icon> 企业性质</label>
            <el-input v-model="form.nature" placeholder="如：民营企业/国企" />
          </div>
          <div class="fg-item">
            <label><el-icon><LocationInformation /></el-icon> 注册地址</label>
            <el-input v-model="form.address" placeholder="注册地址" />
          </div>
          <div class="fg-item full-width">
            <label><el-icon><Document /></el-icon> 企业简介</label>
            <el-input v-model="form.introduction" type="textarea" :rows="4" placeholder="简要介绍企业..." />
          </div>
        </div>
      </div>
    </div>

    <div v-else class="loading-skeleton">
      <el-skeleton :rows="10" animated />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCompanyProfile, updateCompanyProfile } from '@/api'

const form = ref({})
const saving = ref(false)

onMounted(async () => {
  const res = await getCompanyProfile()
  form.value = res.data || {}
})

const save = async () => {
  saving.value = true
  try {
    await updateCompanyProfile(form.value)
    ElMessage.success('保存成功')
  } finally { saving.value = false }
}
</script>

<style scoped>
.company-profile-page { padding-bottom: 40px; }

.page-header-ribbon {
  display: flex; align-items: center; gap: 16px;
  padding: 24px 28px; border-radius: 16px; margin-bottom: 24px;
  background: linear-gradient(135deg, rgba(16,185,129,0.08), rgba(5,150,105,0.06));
  border: 1px solid rgba(16,185,129,0.12);
}
.ribbon-icon {
  width: 48px; height: 48px; border-radius: 13px;
  background: linear-gradient(135deg, #10B981, #059669);
  display: flex; align-items: center; justify-content: center;
  color: white; flex-shrink: 0;
  box-shadow: 0 4px 14px rgba(16,185,129,0.35);
}
.ribbon-text h2 { font-size: 18px; font-weight: 700; color: #1E293B; margin: 0 0 4px; }
.ribbon-text p { font-size: 13px; color: #94A3B8; margin: 0; }
.ribbon-action { margin-left: auto; }

.glass-btn {
  display: inline-flex; align-items: center; gap: 8px;
  padding: 10px 24px; border-radius: 11px; border: none;
  font-size: 14px; font-weight: 600; cursor: pointer;
  transition: all 0.28s cubic-bezier(0.4,0,0.2,1);
  background: linear-gradient(135deg, #3B82F6, #2563EB); color: white;
  box-shadow: 0 4px 15px rgba(59,130,246,0.35);
}
.glass-btn.primary:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(59,130,246,0.45); }
.glass-btn:disabled { opacity: 0.65; cursor: not-allowed; }

.btn-loading {
  display: inline-block; width: 16px; height: 16px;
  border: 2px solid rgba(255,255,255,.3); border-top-color: white;
  border-radius: 50%; animation: btnSpin .55s linear infinite;
}
@keyframes btnSpin { to{ transform: rotate(360deg)} }

.section-card {
  background: #fff; border: 1px solid rgba(226,232,240,0.85); border-radius: 16px;
  padding: 26px; box-shadow: 0 1px 3px rgba(0,0,0,0.04), 0 8px 30px rgba(0,0,0,0.03);
  transition: all 0.3s ease;
}
.section-card:hover { box-shadow: 0 4px 20px rgba(0,0,0,0.06), 0 8px 35px rgba(0,0,0,0.04); }

.section-header { display: flex; align-items: center; gap: 10px; margin-bottom: 22px; }
.sh-icon { width: 34px; height: 34px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 16px; flex-shrink: 0; }
.sh-icon.blue { background: rgba(59,130,246,0.1); color: #3B82F6; }
.section-header h3 { font-size: 16px; font-weight: 650; color: #1E293B; margin: 0; }

.form-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 18px; }
.fg-item { display: flex; flex-direction: column; gap: 7px; }
.fg-item.full-width { grid-column: 1 / -1; }
.fg-item > label { font-size: 13px; font-weight: 550; color: #475569; display: flex; align-items: center; gap: 5px; }
.fg-item > label .el-icon { font-size: 14px; color: #94A3B8; }

::v-deep(.fg-item .el-input__wrapper),
::v-deep(.fg-item .el-select .el-input__wrapper),
::v-deep(.fg-item .el-textarea__inner) {
  border-radius: 10px !important; transition: all 0.25s ease;
  box-shadow: 0 0 0 1px #E2E8F0 inset !important;
}
::v-deep(.fg-item .el-input.is-focus .el-input__wrapper),
::v-deep(.fg-item .el-select .el-select--focused .el-input__wrapper) {
  box-shadow: 0 0 0 2px rgba(59,130,246,0.25), 0 0 0 4px rgba(59,130,246,0.08) !important;
}

.loading-skeleton { background: #fff; border-radius: 16px; padding: 32px; border: 1px solid rgba(226,232,240,0.85); }

@media (max-width: 768px) {
  .page-header-ribbon { flex-direction: column; align-items: flex-start; }
  .ribbon-action { margin-left: 0; margin-top: 12px; }
  .form-grid { grid-template-columns: 1fr; }
}
</style>
