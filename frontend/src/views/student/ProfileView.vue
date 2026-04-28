<template>
  <div class="profile-page">
    <div class="page-header-ribbon">
      <div class="ribbon-icon"><el-icon :size="22"><User /></el-icon></div>
      <div class="ribbon-text">
        <h2>个人信息</h2>
        <p>管理你的基本资料和求职意向</p>
      </div>
      <div class="ribbon-action">
        <button class="glass-btn primary" @click="save" :disabled="saving">
          <span v-if="!saving">保存修改</span>
          <span v-else class="btn-loading"></span>
        </button>
      </div>
    </div>

    <div class="profile-body" v-if="form.id">
      <!-- 基本信息 -->
      <div class="section-card">
        <div class="section-header">
          <div class="sh-icon blue"><el-icon><User /></el-icon></div>
          <h3>基本信息</h3>
        </div>
        <div class="form-grid">
          <div class="fg-item">
            <label><el-icon><EditPen /></el-icon> 姓名</label>
            <el-input v-model="form.realName" placeholder="请输入真实姓名" />
          </div>
          <div class="fg-item">
            <label><el-icon><Postcard /></el-icon> 学号</label>
            <el-input v-model="form.studentNo" disabled />
            <span class="input-hint">学号不可修改</span>
          </div>
          <div class="fg-item">
            <label><el-icon><Male /></el-icon> 性别</label>
            <div class="gender-select">
              <span v-for="(g, i) in [{l:'男',v:1},{l:'女',v:0}]" :key="i"
                :class="['gender-opt', {active: form.gender === g.v}]" @click="form.gender = g.v">
                {{ g.l }}
              </span>
            </div>
          </div>
          <div class="fg-item">
            <label><el-icon><Phone /></el-icon> 手机号</label>
            <el-input v-model="form.phone" placeholder="请输入手机号" />
          </div>
          <div class="fg-item">
            <label><el-icon><Message /></el-icon> 邮箱</label>
            <el-input v-model="form.email" placeholder="请输入邮箱地址" />
          </div>
          <div class="fg-item">
            <label><el-icon><Reading /></el-icon> 专业</label>
            <el-input v-model="form.major" placeholder="所学专业" />
          </div>
          <div class="fg-item">
            <label><el-icon><Calendar /></el-icon> 年级</label>
            <el-input v-model="form.grade" placeholder="入学年份" />
          </div>
          <div class="fg-item">
            <label><el-icon><TrophyBase /></el-icon> 学历</label>
            <el-select v-model="form.education" placeholder="选择学历">
              <el-option v-for="e in educations" :key="e" :label="e" :value="e" />
            </el-select>
          </div>
          <div class="fg-item">
            <label><el-icon><AlarmClock /></el-icon> 毕业时间</label>
            <el-date-picker v-model="form.graduationDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
          </div>
          <div class="fg-item full-width">
            <label><el-icon><Document /></el-icon> 个人简介</label>
            <el-input v-model="form.introduction" type="textarea" :rows="3" placeholder="介绍一下自己吧..." />
          </div>
        </div>
      </div>

      <!-- 求职意向 -->
      <div class="section-card accent-card">
        <div class="section-header">
          <div class="sh-icon purple"><el-icon><Aim /></el-icon></div>
          <h3>求职意向</h3>
        </div>
        <div class="form-grid intention-grid">
          <div class="fg-item">
            <label><el-icon><Briefcase /></el-icon> 意向职位</label>
            <el-input v-model="form.intentionPosition" placeholder="期望的职位方向" />
          </div>
          <div class="fg-item">
            <label><el-icon><LocationInformation /></el-icon> 意向城市</label>
            <el-input v-model="form.intentionCity" placeholder="期望工作的城市" />
          </div>
          <div class="fg-item">
            <label><el-icon><OfficeBuilding /></el-icon> 意向行业</label>
            <el-input v-model="form.intentionIndustry" placeholder="期望的行业领域" />
          </div>
          <div class="fg-item">
            <label><el-icon><Coin /></el-icon> 期望薪资</label>
            <el-input v-model="form.intentionSalary" placeholder="如：8K-12K" />
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
import { getStudentProfile, updateStudentProfile } from '@/api'

const form = ref({})
const saving = ref(false)
const educations = ['大专', '本科', '硕士', '博士']

onMounted(async () => {
  const res = await getStudentProfile()
  form.value = res.data || {}
})

const save = async () => {
  saving.value = true
  try {
    await updateStudentProfile(form.value)
    ElMessage.success('保存成功')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.profile-page { padding-bottom: 40px; }

/* ---- 页头丝带 ---- */
.page-header-ribbon {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px 28px;
  background: linear-gradient(135deg, rgba(59,130,246,0.08), rgba(139,92,246,0.06));
  border: 1px solid rgba(59,130,246,0.12);
  border-radius: 16px;
  margin-bottom: 24px;
}
.ribbon-icon {
  width: 48px; height: 48px; border-radius: 13px;
  background: linear-gradient(135deg, #3B82F6, #6366F1);
  display: flex; align-items: center; justify-content: center;
  color: white; flex-shrink: 0;
  box-shadow: 0 4px 14px rgba(59,130,246,0.35);
}
.ribbon-text h2 { font-size: 18px; font-weight: 700; color: #1E293B; margin: 0 0 4px; }
.ribbon-text p { font-size: 13px; color: #94A3B8; margin: 0; }
.ribbon-action { margin-left: auto; }

.glass-btn {
  display: inline-flex; align-items: center; gap: 8px;
  padding: 10px 24px; border-radius: 11px; border: none;
  font-size: 14px; font-weight: 600; cursor: pointer;
  transition: all 0.28s cubic-bezier(0.4,0,0.2,1); letter-spacing: 0.01em;
  background: linear-gradient(135deg, #e2e8f0, #cbd5e1); color: #475569;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.glass-btn.primary {
  background: linear-gradient(135deg, #3B82F6, #2563EB);
  color: white;
  box-shadow: 0 4px 15px rgba(59,130,246,0.35);
}
.glass-btn.primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(59,130,246,0.45);
}
.glass-btn:disabled { opacity: 0.65; cursor: not-allowed; }

.btn-loading {
  display: inline-block; width: 16px; height: 16px;
  border: 2px solid rgba(255,255,255,.3);
  border-top-color: white; border-radius: 50%;
  animation: btnSpin .55s linear infinite;
}
@keyframes btnSpin { to{ transform: rotate(360deg)} }

/* ---- 区块卡片 ---- */
.section-card {
  background: #fff;
  border: 1px solid rgba(226,232,240,0.85);
  border-radius: 16px;
  padding: 26px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04), 0 8px 30px rgba(0,0,0,0.03);
  transition: all 0.3s ease;
  margin-bottom: 20px;
}
.section-card:hover {
  box-shadow: 0 4px 20px rgba(0,0,0,0.06), 0 8px 35px rgba(0,0,0,0.04);
  border-color: rgba(203,213,225,0.9);
}
.accent-card {
  position: relative;
  overflow: hidden;
}
.accent-card::before {
  content: '';
  position: absolute; top: 0; left: 0; right: 0; height: 3px;
  background: linear-gradient(90deg, #3B82F6, #8B5CF6);
}

.section-header {
  display: flex; align-items: center; gap: 10px;
  margin-bottom: 22px;
}
.sh-icon {
  width: 34px; height: 34px; border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  font-size: 16px; flex-shrink: 0;
}
.sh-icon.blue { background: rgba(59,130,246,0.1); color: #3B82F6; }
.sh-icon.purple { background: rgba(139,92,246,0.1); color: #8B5CF6; }
.section-header h3 { font-size: 16px; font-weight: 650; color: #1E293B; margin: 0; }

/* ---- 表单网格 ---- */
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18px;
}
.fg-item { display: flex; flex-direction: column; gap: 7px; }
.fg-item.full-width { grid-column: 1 / -1; }
.fg-item > label {
  font-size: 13px; font-weight: 550; color: #475569;
  display: flex; align-items: center; gap: 5px;
}
.fg-item > label .el-icon { font-size: 14px; color: #94A3B8; }
.input-hint { font-size: 11.5px; color: #94A3B8; margin-top: -3px; }

::v-deep(.fg-item .el-input__wrapper),
::v-deep(.fg-item .el-select .el-input__wrapper),
::v-deep(.fg-item .el-textarea__inner) {
  border-radius: 10px !important;
  transition: all 0.25s ease;
  box-shadow: 0 0 0 1px #E2E8F0 inset !important;
}
::v-deep(.fg-item .el-input__wrapper:hover),
::v-deep(.fg-item .el-select .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #CBD5E1 inset !important;
}
::v-deep(.fg-item .el-input.is-focus .el-input__wrapper),
::v-deep(.fg-item .el-select .el-select--focused .el-input__wrapper) {
  box-shadow: 0 0 0 2px rgba(59,130,246,0.25), 0 0 0 4px rgba(59,130,246,0.08) !important;
  border-color: transparent !important;
}

/* 性别选择器 */
.gender-select { display: flex; gap: 10px; height: fit-content; }
.gender-opt {
  padding: 8px 22px; border-radius: 9px; cursor: pointer;
  border: 1px solid #E2E8F0; font-size: 13.5px; font-weight: 500;
  background: #F8FAFC; color: #64748B; transition: all 0.25s ease;
}
.gender-opt.active {
  background: rgba(59,130,246,0.08); border-color: #3B82F6;
  color: #2563EB; font-weight: 600;
}

.loading-skeleton {
  background: #fff; border-radius: 16px; padding: 32px;
  border: 1px solid rgba(226,232,240,0.85);
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
}

@media (max-width: 768px) {
  .page-header-ribbon { flex-direction: column; align-items: flex-start; }
  .ribbon-action { margin-left: 0; margin-top: 12px; }
  .form-grid { grid-template-columns: 1fr; }
  .intention-grid { grid-template-columns: 1fr !important; }
}
</style>
