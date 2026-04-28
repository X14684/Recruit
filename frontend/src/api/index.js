import request from '@/utils/request'

// 统一登录
export const login = (data) => request.post('/admin/login', data)

// 学生注册
export const studentRegister = (data) => request.post('/student/register', data)

// 企业注册
export const companyRegister = (data) => request.post('/company/register', data)

// ========== 职位相关 ==========
export const getJobList = (params) => request.get('/job/list', { params })
export const getJobDetail = (id) => request.get(`/job/${id}`)
export const publishJob = (data) => request.post('/job', data)
export const updateJob = (id, data) => request.put(`/job/${id}`, data)
export const deleteJob = (id) => request.delete(`/job/${id}`)
export const toggleJobStatus = (id, data) => request.put(`/job/${id}/status`, data)
export const auditJob = (id, data) => request.put(`/job/${id}/audit`, data)

// ========== 管理员职位相关 ==========
export const getAdminJobList = (params) => request.get('/admin/job/list', { params })

// ========== 投递相关 ==========
export const applyJob = (jobId) => request.post(`/application/apply/${jobId}`)
export const hasApplied = (jobId) => request.get(`/application/hasApplied/${jobId}`)
export const getStudentApplications = (params) => request.get('/application/student/list', { params })
export const getCompanyApplications = (params) => request.get('/application/company/list', { params })
export const updateApplicationStatus = (id, data) => request.put(`/application/${id}/status`, data)

// ========== 学生相关 ==========
export const getStudentProfile = () => request.get('/student/profile')
export const updateStudentProfile = (data) => request.put('/student/profile', data)
export const getStudentList = (params) => request.get('/student/list', { params })

// ========== 企业相关 ==========
export const getCompanyProfile = () => request.get('/company/profile')
export const getCompanyDetail = (id) => request.get(`/company/${id}`)
export const updateCompanyProfile = (data) => request.put('/company/profile', data)
export const getCompanyList = (params) => request.get('/company/list', { params })
export const auditCompany = (id, data) => request.put(`/admin/company/${id}/audit`, data)

// ========== 学院相关 ==========
export const getCollegeList = (params) => request.get('/college/list', { params })
export const getCollegeProfile = () => request.get('/college/profile')
export const getTalkList = (params) => request.get('/college/talk/list', { params })
export const getMyTalkList = (params) => request.get('/college/talk/my', { params })
export const publishTalk = (data) => request.post('/college/talk', data)
export const auditTalk = (id, data) => request.put(`/college/talk/${id}/audit`, data)
export const signUpTalk = (id) => request.post(`/college/talk/${id}/signup`)

// ========== 管理员相关 ==========
export const getStatistics = () => request.get('/admin/statistics')
export const getAdminCollegeList = (params) => request.get('/admin/college/list', { params })
export const addCollege = (data) => request.post('/admin/college', data)
export const toggleCollegeStatus = (id, data) => request.put(`/admin/college/${id}/status`, data)
export const resetCollegePassword = (id, data) => request.put(`/admin/college/${id}/password`, data)
export const toggleStudentStatus = (id, data) => request.put(`/admin/student/${id}/status`, data)
export const getEmploymentStatistics = () => request.get('/admin/employment-statistics')

// ========== 学院就业统计 ==========
export const getCollegeEmploymentStats = () => request.get('/college/employment-statistics')
