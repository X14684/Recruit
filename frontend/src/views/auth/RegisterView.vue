<template>
  <div class="register-page">
    <!-- 背景装饰 -->
    <div class="bg-orb orb-1"></div>
    <div class="bg-orb orb-2"></div>
    <div class="bg-grid"></div>

    <div class="register-container">
      <!-- 顶部Logo -->
      <div class="reg-header">
        <div class="logo-circle">
          <el-icon :size="28" color="#fff"><School /></el-icon>
        </div>
        <h1>校园求职招聘系统</h1>
        <p>创建账号，开启你的求职之旅</p>
      </div>

      <!-- 注册表单 -->
      <div class="form-panel">
        <el-tabs v-model="activeTab" class="register-tabs">
          <el-tab-pane name="student">
            <template #label>
              <span class="tab-label"><el-icon><User /></el-icon>学生注册</span>
            </template>
          </el-tab-pane>
          <el-tab-pane name="company">
            <template #label>
              <span class="tab-label"><el-icon><OfficeBuilding /></el-icon>企业注册</span>
            </template>
          </el-tab-pane>
        </el-tabs>

        <!-- 学生注册 -->
        <div v-show="activeTab === 'student'" class="form-body">
          <el-form :model="studentForm" :rules="studentRules" ref="studentFormRef" label-width="85px">
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="姓名" prop="realName">
                  <el-input v-model="studentForm.realName" placeholder="真实姓名" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="学号" prop="studentNo">
                  <el-input v-model="studentForm.studentNo" placeholder="学号" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="用户名" prop="username">
                  <el-input v-model="studentForm.username" placeholder="登录用户名" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="密码" prop="password">
                  <el-input v-model="studentForm.password" type="password" placeholder="不少于6位" show-password />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="studentForm.email" placeholder="邮箱地址" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="手机号">
                  <el-input v-model="studentForm.phone" placeholder="手机号" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="所属学院">
                  <el-select v-model="studentForm.collegeId" filterable placeholder="🏫 搜索或选择学院" style="width:100%" popper-class="glass-select-dropdown">
                    <el-option v-for="c in colleges" :key="c.id" :label="c.collegeName" :value="c.id">
                      <div class="college-opt"><span class="co-name">{{ c.collegeName }}</span><span class="co-count">{{ c.studentCount || '' }}名学生</span></div>
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="专业">
                  <el-select v-model="studentForm.major" filterable allow-create default-first-option placeholder="📚 选择或输入专业" style="width:100%" popper-class="glass-select-dropdown">
                    <el-option-group v-for="grp in majorGroups" :key="grp.label" :label="grp.label">
                      <el-option v-for="m in grp.options" :key="m" :label="m" :value="m" />
                    </el-option-group>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="年级">
                  <el-select v-model="studentForm.grade" placeholder="📅 选择入学年份" style="width:100%" popper-class="glass-select-dropdown">
                    <el-option v-for="g in gradeOptions" :key="g.value" :label="g.label" :value="g.value">
                      <div class="grade-opt"><span>{{ g.label }}</span><span class="grade-hint">{{ g.hint }}</span></div>
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item>
              <button type="primary" class="submit-btn" :loading="loading" @click="handleStudentRegister">
                立即注册
              </button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 企业注册 -->
        <div v-show="activeTab === 'company'" class="form-body">
          <el-form :model="companyForm" :rules="companyRules" ref="companyFormRef" label-width="85px">
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="企业名称" prop="companyName">
                  <el-input v-model="companyForm.companyName" placeholder="企业全称" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="用户名" prop="username">
                  <el-input v-model="companyForm.username" placeholder="登录账号" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="密码" prop="password">
                  <el-input v-model="companyForm.password" type="password" show-password />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="联系人" prop="contactName">
                  <el-input v-model="companyForm.contactName" placeholder="HR姓名" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="联系电话" prop="contactPhone">
                  <el-input v-model="companyForm.contactPhone" placeholder="联系电话" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="联系邮箱" prop="contactEmail">
                  <el-input v-model="companyForm.contactEmail" placeholder="邮箱" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="所属行业">
                  <el-select v-model="companyForm.industry" placeholder="🏢 选择所属行业" style="width:100%" popper-class="glass-select-dropdown">
                    <el-option-group v-for="grp in industryGroups" :key="grp.label" :label="grp.label">
                      <el-option v-for="i in grp.options" :key="i.value" :label="i.label" :value="i.value" />
                    </el-option-group>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="企业规模">
                  <el-select v-model="companyForm.scale" placeholder="👥 选择企业规模" style="width:100%" popper-class="glass-select-dropdown">
                    <el-option v-for="s in scaleOptions" :key="s.value" :label="s.label" :value="s.value">
                      <div class="scale-opt"><span class="scale-label">{{ s.label }}</span><span class="scale-icon">{{ s.icon }}</span></div>
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="企业地址">
                  <el-input v-model="companyForm.address" placeholder="注册地址" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="企业简介">
                  <el-input v-model="companyForm.introduction" type="textarea" :rows="3" placeholder="简要介绍企业" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item>
              <button type="primary" class="submit-btn green" :loading="loading" @click="handleCompanyRegister">
                提交注册申请
              </button>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <div class="login-link">已有账号？<router-link to="/login">立即登录</router-link></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { studentRegister, companyRegister, getCollegeList } from '@/api'

const route = useRoute(), router = useRouter()
const activeTab = ref(route.query.type || 'student'), loading = ref(false)
const colleges = ref([]), studentFormRef = ref(), companyFormRef = ref()

/* ===== 学生端：专业分组（支持搜索+自定义输入）===== */
const majorGroups = [
  { label: '🖥️ 计算机与电子信息', options: ['计算机科学与技术','软件工程','人工智能','数据科学','网络工程','信息安全','物联网工程','数字媒体技术'] },
  { label: '⚙️ 工程与制造', options: ['机械工程','电气工程及其自动化','自动化','材料科学与工程','土木工程','化学工程与工艺','车辆工程'] },
  { label: '💰 经济与管理', options: ['金融学','会计学','财务管理','市场营销','工商管理','国际经济与贸易','人力资源管理','物流管理'] },
  { label: '📐 理学基础', options: ['数学与应用数学','物理学','化学','生物科学','统计学','应用心理学'] },
  { label: '⚖️ 人文社科', options: ['法学','汉语言文学','新闻传播学','英语','日语','哲学','历史学','社会学'] },
  { label: '🎨 艺术与设计', options: ['视觉传达设计','环境设计','产品设计','动画','音乐学','表演','广播电视编导'] },
  { label: '🏥 医药卫生', options: ['临床医学','口腔医学','药学','护理学','预防医学','中医学'] },
  { label: '🌾 农林生态', options: ['农学','园艺','动物科学','林学','食品科学与工程','生态学'] },
]

/* 年级选项（动态计算） */
const currentYear = new Date().getFullYear()
const gradeOptions = [
  { value: String(currentYear), label: `${currentYear}级（大一）`, hint: '2025年入学' },
  { value: String(currentYear - 1), label: `${currentYear - 1}级（大二）`, hint: '2024年入学' },
  { value: String(currentYear - 2), label: `${currentYear - 2}级（大三）`, hint: '2023年入学' },
  { value: String(currentYear - 3), label: `${currentYear - 3}级（大四）`, hint: '2022年入学' },
  { value: String(currentYear - 4), label: `${currentYear - 4}级（研一/已毕业）`, hint: '2021年及以前' },
]

/* ===== 企业端：行业分组 ===== */
const industryGroups = [
  { label: '💻 数字科技', options: [
    { label: '互联网 / 软件开发', value: '互联网' }, { label: '人工智能 / 大数据', value: '人工智能' },
    { label: '云计算 / SaaS', value: '云计算' }, { label: '半导体 / 芯片', value: '半导体' },
    { label: '游戏 / 娱乐科技', value: '游戏' }
  ]},
  { label: '💰 金融与商业服务', options: [
    { label: '银行 / 保险', value: '金融' }, { label: '证券 / 投资基金', value: '证券投资' },
    { label: '咨询 / 专业服务', value: '咨询' }, { label: '法律 / 会计事务所', value: '法律服务' }
  ]},
  { label: '🏭 制造与能源', options: [
    { label: '汽车 / 智能制造', value: '制造业' }, { label: '新能源 / 光伏', value: '新能源' },
    { label: '航空航天 / 国防', value: '航空航天' }, { label: '化工 / 材料', value: '化工' }
  ]},
  { label: '🏥 医疗健康与生命科学', options: [
    { label: '制药 / 生物技术', value: '医疗健康' }, { label: '医疗器械', value: '医疗器械' },
    { label: '医疗服务 / 医院', value: '医疗服务' }
  ]},
  { label: '📚 教育与文化传媒', options: [
    { label: '教育 / 培训', value: '教育' }, { label: '广告 / 公关 / 传媒', value: '传媒' },
    { label: '影视 / 内容创作', value: '影视' }
  ]},
  { label: '🏗️ 房地产与建筑', options: [
    { label: '房地产开发', value: '房地产' }, { label: '建筑工程 / 设计院', value: '建筑工程' },
    { label: '物业管理', value: '物业管理' }
  ]},
  { label: '🛒 消费品与零售', options: [
    { label: '快消品 / 食品饮料', value: '快消' }, { label: '电商 / 零售', value: '零售' },
    { label: '时尚 / 服装', value: '时尚' }
  ]},
  { label: '🚗 物流与交通', options: [
    { label: '物流 / 供应链', value: '物流' }, { label: '交通运输', value: '交通运输' }
  ]},
  { label: '📦 其他行业', options: [
    { label: '政府 / 公共事业', value: '公共事业' }, { label: '非营利组织 / NGO', value: 'NGO' },
    { label: '其他', value: '其他' }
  ]},
]

/* 企业规模（带图标描述） */
const scaleOptions = [
  { value: '1-49人', label: '初创团队（1-49人）', icon: '🌱' },
  { value: '50-99人', label: '小型企业（50-99人）', icon: '🏢' },
  { value: '100-499人', label: '中型企业（100-499人）', icon: '🏛️' },
  { value: '500-999人', label: '中大型（500-999人）', icon: '🏬' },
  { value: '1000-9999人', label: '大型企业（1000-9999人）', icon: '🏭' },
  { value: '10000人以上', label: '超大型集团（10000+人）', icon: '🌍' },
]

const studentForm = reactive({ studentNo:'', realName:'', username:'', password:'', email:'', phone:'', collegeId:'', major:'', grade:'' })
const companyForm = reactive({ companyName:'', username:'', password:'', contactName:'', contactPhone:'', contactEmail:'', industry:'', scale:'', address:'', introduction:'' })

const studentRules = {
  realName:[{required:true,message:'请输入姓名'}], studentNo:[{required:true,message:'请输入学号'}],
  username:[{required:true,message:'请输入用户名'}], password:[{required:true,message:'请输入密码'},{min:6,message:'不少于6位'}],
  email:[{required:true,message:'请输入邮箱'},{type:'email',message:'邮箱格式不正确'}]
}
const companyRules = {
  companyName:[{required:true,message:'请输入企业名称'}], username:[{required:true,message:'请输入用户名'}],
  password:[{required:true,message:'请输入密码'},{min:6,message:'不少于6位'}], contactName:[{required:true,message:'请输入联系人'}],
  contactPhone:[{required:true,message:'请输入联系电话'}], contactEmail:[{required:true,message:'请输入联系邮箱'},{type:'email',message:'格式不正确'}]
}

onMounted(async () => {
  try { const res = await getCollegeList({ page: 1, size: 100 }); colleges.value = res.data.records || [] } 
  catch (e) { console.warn('获取学院列表失败:', e) }
})

const handleStudentRegister = async () => {
  const valid=await studentFormRef.value.validate().catch(()=>false); if(!valid)return
  loading.value=true
  try{await studentRegister(studentForm); ElMessage.success('注册成功，请登录'); router.push('/login')}
  catch(e){ElMessage.error(e?.message||'注册失败')}finally{loading.value=false}
}

const handleCompanyRegister = async () => {
  const valid=await companyFormRef.value.validate().catch(()=>false); if(!valid)return
  loading.value=true
  try{await companyRegister(companyForm); ElMessage.success('注册申请已提交'); router.push('/login')}
  catch(e){ElMessage.error(e?.message||'提交失败')}finally{loading.value=false}
}
</script>

<style scoped>
.register-page{
  min-height:100vh;background:linear-gradient(135deg,#0F172A 0%,#1E293B 40%,#0F172A 100%);
  display:flex;align-items:center;justify-content:center;padding:40px 20px;position:relative;overflow:hidden
}
.bg-grid{
  position:absolute;inset:0;
  background-image:linear-gradient(rgba(255,255,255,.03) 1px,transparent 1px),linear-gradient(90deg,rgba(255,255,255,.03) 1px,transparent 1px);
  background-size:60px 60px;z-index:0;pointer-events:none
}
.bg-orb{position:absolute;border-radius:50%;filter:blur(80px);opacity:.25;z-index:0;pointer-events:none}
.orb-1{width:400px;height:400px;background:radial-gradient(circle,rgba(59,130,246,.4),transparent 70%);top:-100px;right:20%}
.orb-2{width:300px;height:300px;background:radial-gradient(circle,rgba(139,92,246,.3),transparent 70%);bottom:-80px;left:15%}

.register-container{
  position:relative;z-index:1;width:760px;
  background:rgba(15,23,42,.75);
  backdrop-filter:blur(30px) saturate(180%);
  border:1px solid rgba(255,255,255,.08);
  border-radius:22px;padding:36px 38px;
  box-shadow:0 30px 60px rgba(0,0,0,.35), inset 0 1px 0 rgba(255,255,255,.06)
}

.reg-header{text-align:center;margin-bottom:28px}
.logo-circle{
  width:52px;height:52px;border-radius:14px;
  background:linear-gradient(135deg,#3B82F6,#2563EB);
  display:inline-flex;align-items:center;justify-content:center;
  margin-bottom:14px;box-shadow:0 6px 20px rgba(59,130,246,.45)
}
.reg-header h1{font-size:22px;color:#F1F5F9;font-weight:700;margin:0 0 8px;letter-spacing:-.01em}
.reg-header p{font-size:13.5px;color:#94A3B8;margin:0}

.form-panel{
  background:rgba(255,255,255,.04);
  border:1px solid rgba(255,255,255,.07);
  border-radius:16px;padding:26px 28px
}
.register-tabs ::deep(.el-tabs__header){margin-bottom:18px}
.register-tabs ::deep(.el-tabs__nav-wrap::after){background:rgba(255,255,255,.08)}
.tab-label{display:flex;align-items:center;gap:6px;font-weight:550;font-size:14px;color:#CBD5E1}
.tab-label .el-icon{font-size:17px}

.form-body{}
.submit-btn{
  width:100%;padding:12px;border-radius:12px;border:none;font-size:15.5px;font-weight:700;color:white;
  cursor:pointer;transition:all .28s cubic-bezier(.4,0,.2,1);
  background:linear-gradient(135deg,#3B82F6,#2563EB);
  box-shadow:0 5px 22px rgba(59,130,246,.35)
}
.submit-btn:hover:not(:disabled){transform:translateY(-2px) scale(1.01);box-shadow:0 8px 30px rgba(59,130,246,.5)}
.submit-btn.green{background:linear-gradient(135deg,#10B981,#059669);box-shadow:0 5px 22px rgba(16,185,129,.35)}
.submit-btn.green:hover:not(:disabled){box-shadow:0 8px 30px rgba(16,185,129,.5)}

/* 输入框覆盖 */
::v-deep(.form-panel .el-input__wrapper),
::v-deep(.form-panel .el-textarea__inner),
::v-deep(.form-panel .el-select .el-input__wrapper){
  border-radius:10px!important;background:rgba(255,255,255,.05)!important;
  border-color:rgba(255,255,255,.1)!important;transition:all .25s ease!important
}
::v-deep(.form-panel .el-input__inner),
::v-deep(.form-panel .el-textarea__inner){color:#E2E8F0!important}
::v-deep(.form-panel .el-input.is-focus .el-input__wrapper){
  border-color:#60A5FA!important;box-shadow:0 0 0 3px rgba(96,165,250,.12)!important
}

.login-link{text-align:center;margin-top:18px;font-size:14px;color:#94A3B8}
.login-link a{color:#60A5FA;text-decoration:none;font-weight:550}.login-link a:hover{color:#93C5FD;text-decoration:underline}

@media(max-width:820px){.register-container{width:auto;max-width:95%;padding:24px}}

/* ===== 选择框自定义下拉项样式 ===== */
.college-opt{display:flex;justify-content:space-between;align-items:center;width:100%}
.co-name{font-weight:500;color:#E2E8F0}
.co-count{font-size:11.5px;color:#64748B;font-style:italic}

.grade-opt,.scale-opt{display:flex;justify-content:space-between;align-items:center;width:100%}
.grade-hint{font-size:11px;color:#64748B;font-style:italic}
.scale-label{font-weight:500;color:#E2E8F0}
.scale-opt .scale-icon{font-size:15px;margin-left:8px}

/* 下拉面板全局玻璃风格 */
:global(.glass-select-dropdown){
  border-radius:12px!important;
  background:rgba(22,27,45,.97)!important;
  border:1px solid rgba(255,255,255,.08)!important;
  box-shadow:0 16px 48px rgba(0,0,0,.4), inset 0 1px 0 rgba(255,255,255,.04)!important;
  backdrop-filter:blur(20px) saturate(180%)!important
}
:global(.glass-select-dropdown .el-select-dropdown__item){
  color:#CBD5E1;height:42px;line-height:42px;padding:0 14px;
  font-size:13.5px;border-radius:8px;transition:all .2s ease
}
:global(.glass-select-dropdown .el-select-dropdown__item:hover){
  background:rgba(59,130,246,.15)!important;color:#93C5FD!important
}
:global(.glass-select-dropdown .el-select-dropdown__item.is-selected){
  background:linear-gradient(135deg,rgba(59,130,246,.25),rgba(99,102,241,.18))!important;
  color:#60A5FA!important;font-weight:600
}
:global(.glass-select-dropdown .el-select-group__title){
  color:#94A3B8;font-size:11.5px;font-weight:700;text-transform:uppercase;
  letter-spacing:.05em;padding:10px 14px 4px;border-top:1px solid rgba(255,255,255,.05)
}
:global(.glass-select-dropdown .el-select-group__wrap:first-child .el-select-group__title){
  border-top:none
}
</style>