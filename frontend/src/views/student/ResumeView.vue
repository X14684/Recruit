<template>
  <div class="resume-page">
    <div class="page-header-ribbon">
      <div class="ribbon-icon"><el-icon :size="22"><Document /></el-icon></div>
      <div class="ribbon-text">
        <h2>简历管理</h2>
        <p>上传并管理你的个人简历文件</p>
      </div>
    </div>

    <div class="resume-body">
      <!-- 上传区域 -->
      <div class="upload-section">
        <div class="upload-hint">
          <div class="hint-icon"><el-icon><InfoFilled /></el-icon></div>
          <span>上传简历后，投递职位时将自动附带您的简历文件</span>
        </div>

        <div class="upload-card" :class="{ 'has-file': form.resumeUrl }">
          <div v-if="!form.resumeUrl" class="upload-zone" @dragover.prevent @drop.prevent>
            <el-upload
              drag
              action="/api/student/resume/upload"
              :headers="headers"
              :on-success="onUploadSuccess"
              accept=".pdf,.doc,.docx"
              :show-file-list="false"
            >
              <div class="upload-content">
                <div class="upload-icon-wrap">
                  <el-icon :size="42" color="#94A3B8"><UploadFilled /></el-icon>
                  <div class="upload-glow"></div>
                </div>
                <div class="upload-text">
                  <p class="ut-main">拖拽文件到此处或 <em>点击上传</em></p>
                  <p class="ut-sub">支持 PDF、Word 格式，文件大小不超过 10MB</p>
                </div>
              </div>
            </el-upload>
          </div>

          <!-- 已上传文件 -->
          <div v-else class="current-resume">
            <div class="file-display">
              <div class="file-icon">
                <el-icon :size="28" color="#fff"><Document /></el-icon>
              </div>
              <div class="file-info">
                <div class="file-name">{{ getFileName(form.resumeUrl) }}</div>
                <div class="file-status">已上传 · 可用于投递</div>
              </div>
            </div>
            <div class="file-actions">
              <button class="glass-btn primary" @click="openResume">
                <el-icon :size="14"><View /></el-icon> 预览简历
              </button>
              <label class="glass-btn ghost">
                <el-icon :size="14"><UploadFilled /></el-icon> 重新上传
                <el-upload action="/api/student/resume/upload" :headers="headers"
                  :on-success="onUploadSuccess" accept=".pdf,.doc,.docx" :show-file-list="false"
                  style="position:absolute;opacity:0;width:0;height:0;" />
              </label>
            </div>
          </div>
        </div>
      </div>

      <!-- 提示卡片 -->
      <div class="tip-cards">
        <div class="tip-item">
          <div class="ti-icon pdf"><el-icon><Document /></el-icon></div>
          <div><b>PDF 格式</b><br/><span>推荐使用，兼容性最好</span></div>
        </div>
        <div class="tip-item">
          <div class="ti-icon word"><el-icon><EditPen /></el-icon></div>
          <div><b>Word 格式</b><br/><span>支持 .doc / .docx</span></div>
        </div>
        <div class="tip-item">
          <div class="ti-icon size"><el-icon><Histogram /></el-icon></div>
          <div><b>大小限制</b><br/><span>最大 10MB</span></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getStudentProfile, updateStudentProfile } from '@/api'

const form = ref({})
const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` }

const getFileName = (url) => url?.split('/').pop() || '简历文件'

const openResume = () => {
  if (form.value.resumeUrl) {
    // resumeUrl 格式：/uploads/resumes/202604/xxx.pdf
    // 去掉开头的 /uploads/ 得到 path 参数：resumes/202604/xxx.pdf
    const url = form.value.resumeUrl
    const filePath = url.startsWith('/uploads/') ? url.slice('/uploads/'.length) : url
    const previewUrl = `/api/common/file?path=${filePath}`
    window.open(previewUrl, '_blank')
  } else {
    ElMessage.warning('暂无简历文件')
  }
}

const onUploadSuccess = async (res) => {
  if (res.code === 200) {
    form.value.resumeUrl = res.data.url
    form.value.resumeName = res.data.filename
    await updateStudentProfile({ id: form.value.id, resumeUrl: res.data.url })
    ElMessage.success('简历上传成功')
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

onMounted(async () => {
  const res = await getStudentProfile()
  form.value = res.data || {}
})
</script>

<style scoped>
.resume-page { padding-bottom: 40px; }

/* ---- 页头丝带 ---- */
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

.upload-section { margin-bottom: 24px; }

.upload-hint {
  display: flex; align-items: center; gap: 10px;
  background: rgba(59,130,246,0.05); border: 1px solid rgba(59,130,246,0.1);
  border-radius: 11px; padding: 14px 20px; margin-bottom: 18px;
}
.hint-icon { color: #3B82F6; font-size: 17px; }
.upload-hint span { font-size: 13.5px; color: #475569; }

.upload-card {
  background: #fff; border: 2px dashed #E2E8F0;
  border-radius: 16px; overflow: hidden;
  transition: all 0.3s ease;
}
.upload-card:hover { border-color: #93C5FD; }
.upload-card.has-file {
  border-style: solid; border-color: rgba(16,185,129,0.25);
  background: linear-gradient(135deg, rgba(16,185,129,0.02), rgba(5,150,105,0.01));
}

::v-deep(.el-upload-dragger) {
  width: auto !important; height: auto !important;
  border: none !important; background: transparent !important;
  padding: 32px;
}

.upload-content { display: flex; align-items: center; gap: 22px; justify-content: center; }
.upload-icon-wrap {
  position: relative; width: 80px; height: 80px;
  border-radius: 18px; background: #F1F5F9;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.upload-glow {
  position: absolute; inset: -3px; border-radius: 21px;
  background: conic-gradient(from 180deg, #3B82F6, #60A5FA, #93C5FD, #3B82F6);
  opacity: 0.15; z-index: -1;
  animation: uploadGlow 4s linear infinite;
}
@keyframes uploadGlow { to{ transform: rotate(360deg)} }
.upload-text { text-align: left; }
.ut-main { font-size: 15px; color: #334155; font-weight: 500; margin: 0 0 6px; }
.ut-main em { color: #3B82F6; font-style: normal; font-weight: 600; }
.ut-sub { font-size: 13px; color: #94A3B8; margin: 0; }

/* ---- 已上传文件展示 ---- */
.current-resume { padding: 26px; display: flex; align-items: center; gap: 20px; }
.file-display { display: flex; align-items: center; gap: 14px; flex: 1; }
.file-icon {
  width: 52px; height: 52px; border-radius: 13px;
  background: linear-gradient(135deg, #10B981, #059669);
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0; box-shadow: 0 4px 12px rgba(16,185,129,0.25);
}
.file-name { font-size: 15px; font-weight: 600; color: #1E293B; margin-bottom: 3px; }
.file-status { font-size: 12.5px; color: #10B981; font-weight: 500; }
.file-actions { display: flex; gap: 10px; flex-shrink: 0; position: relative; }

.glass-btn {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 9px 18px; border-radius: 10px; border: none;
  font-size: 13px; font-weight: 600; cursor: pointer;
  transition: all 0.28s ease;
  background: linear-gradient(135deg, #e2e8f0, #cbd5e1); color: #475569;
}
.glass-btn.primary { background: linear-gradient(135deg, #3B82F6, #2563EB); color: white; box-shadow: 0 3px 12px rgba(59,130,246,0.3); }
.glass-btn.primary:hover { transform: translateY(-1px); box-shadow: 0 5px 16px rgba(59,130,246,0.4); }
.glass-btn.ghost { background: #F1F5F9; color: #64748B; border: 1px solid #E2E8F0; }
.glass-btn.ghost:hover { background: #EEF2FF; color: #3B82F6; border-color: #BFDBFE; }

/* ---- 底部提示卡 ---- */
.tip-cards { display: grid; grid-template-columns: repeat(3, 1fr); gap: 14px; }
.tip-item {
  display: flex; align-items: center; gap: 12px;
  background: #fff; border: 1px solid #F1F5F9;
  border-radius: 12px; padding: 18px 20px;
  transition: all 0.25s ease;
}
.tip-item:hover { transform: translateY(-2px); box-shadow: 0 4px 15px rgba(0,0,0,0.05); border-color: #E2E8F0; }
.ti-icon {
  width: 38px; height: 38px; border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  font-size: 17px; color: white; flex-shrink: 0;
}
.ti-icon.pdf { background: linear-gradient(135deg, #EF4444, #DC2626); }
.ti-icon.word { background: linear-gradient(135deg, #3B82F6, #2563EB); }
.ti-icon.size { background: linear-gradient(135deg, #F59E0B, #D97706); }
.tip-item b { font-size: 13px; color: #1E293B; display: block; margin-bottom: 2px; }
.tip-item span { font-size: 11.5px; color: #94A3B8; line-height: 1.4; }

@media (max-width: 768px) {
  .current-resume { flex-direction: column; align-items: flex-start; }
  .tip-cards { grid-template-columns: 1fr; }
}
</style>
