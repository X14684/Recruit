import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/LoginView.vue'),
    meta: { title: '登录', guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/RegisterView.vue'),
    meta: { title: '注册', guest: true }
  },

  // ========== 公共首页 ==========
  {
    path: '/home',
    component: () => import('@/views/layout/PublicLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/public/HomeView.vue'),
        meta: { title: '首页' }
      }
    ]
  },
  // 独立路由（保持布局一致）
  {
    path: '/jobs',
    component: () => import('@/views/layout/PublicLayout.vue'),
    children: [
      {
        path: '',
        name: 'JobList',
        component: () => import('@/views/public/JobListView.vue'),
        meta: { title: '职位列表' }
      },
      {
        path: ':id',
        name: 'JobDetail',
        component: () => import('@/views/public/JobDetailView.vue'),
        meta: { title: '职位详情' }
      }
    ]
  },
  {
    path: '/companies/:id',
    component: () => import('@/views/layout/PublicLayout.vue'),
    children: [
      {
        path: '',
        name: 'CompanyDetail',
        component: () => import('@/views/public/CompanyDetailView.vue'),
        meta: { title: '企业详情' }
      }
    ]
  },
  {
    path: '/talks',
    component: () => import('@/views/layout/PublicLayout.vue'),
    children: [
      {
        path: '',
        name: 'TalkList',
        component: () => import('@/views/public/TalkListView.vue'),
        meta: { title: '宣讲会' }
      }
    ]
  },

  // ========== 学生端 ==========
  {
    path: '/student',
    component: () => import('@/views/layout/DashboardLayout.vue'),
    meta: { requiresAuth: true, role: 'STUDENT' },
    children: [
      {
        path: 'dashboard',
        name: 'StudentDashboard',
        component: () => import('@/views/student/DashboardView.vue'),
        meta: { title: '学生中心' }
      },
      {
        path: 'profile',
        name: 'StudentProfile',
        component: () => import('@/views/student/ProfileView.vue'),
        meta: { title: '个人信息' }
      },
      {
        path: 'applications',
        name: 'StudentApplications',
        component: () => import('@/views/student/ApplicationsView.vue'),
        meta: { title: '投递记录' }
      },
      {
        path: 'resume',
        name: 'StudentResume',
        component: () => import('@/views/student/ResumeView.vue'),
        meta: { title: '我的简历' }
      }
    ]
  },

  // ========== 企业端 ==========
  {
    path: '/company',
    component: () => import('@/views/layout/DashboardLayout.vue'),
    meta: { requiresAuth: true, role: 'COMPANY' },
    children: [
      {
        path: 'dashboard',
        name: 'CompanyDashboard',
        component: () => import('@/views/company/DashboardView.vue'),
        meta: { title: '企业中心' }
      },
      {
        path: 'profile',
        name: 'CompanyProfile',
        component: () => import('@/views/company/ProfileView.vue'),
        meta: { title: '企业信息' }
      },
      {
        path: 'jobs',
        name: 'CompanyJobs',
        component: () => import('@/views/company/JobsView.vue'),
        meta: { title: '职位管理' }
      },
      {
        path: 'applications',
        name: 'CompanyApplications',
        component: () => import('@/views/company/ApplicationsView.vue'),
        meta: { title: '收到的投递' }
      },
      {
        path: 'talks',
        name: 'CompanyTalks',
        component: () => import('@/views/company/TalksView.vue'),
        meta: { title: '宣讲会管理' }
      }
    ]
  },

  // ========== 学院端 ==========
  {
    path: '/college',
    component: () => import('@/views/layout/DashboardLayout.vue'),
    meta: { requiresAuth: true, role: 'COLLEGE' },
    children: [
      {
        path: 'dashboard',
        name: 'CollegeDashboard',
        component: () => import('@/views/college/DashboardView.vue'),
        meta: { title: '学院中心' }
      },
      {
        path: 'students',
        name: 'CollegeStudents',
        component: () => import('@/views/college/StudentsView.vue'),
        meta: { title: '学生管理' }
      },
      {
        path: 'talks',
        name: 'CollegeTalks',
        component: () => import('@/views/college/TalksView.vue'),
        meta: { title: '宣讲会审核' }
      },
      {
        path: 'statistics',
        name: 'CollegeStatistics',
        component: () => import('@/views/college/StatisticsView.vue'),
        meta: { title: '就业统计' }
      }
    ]
  },

  // ========== 管理员端 ==========
  {
    path: '/admin',
    component: () => import('@/views/layout/DashboardLayout.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/DashboardView.vue'),
        meta: { title: '管理后台' }
      },
      {
        path: 'students',
        name: 'AdminStudents',
        component: () => import('@/views/admin/StudentsView.vue'),
        meta: { title: '学生管理' }
      },
      {
        path: 'companies',
        name: 'AdminCompanies',
        component: () => import('@/views/admin/CompaniesView.vue'),
        meta: { title: '企业管理' }
      },
      {
        path: 'colleges',
        name: 'AdminColleges',
        component: () => import('@/views/admin/CollegesView.vue'),
        meta: { title: '学院管理' }
      },
      {
        path: 'jobs',
        name: 'AdminJobs',
        component: () => import('@/views/admin/JobsView.vue'),
        meta: { title: '职位审核' }
      },
      {
        path: 'employment-statistics',
        name: 'AdminEmploymentStatistics',
        component: () => import('@/views/admin/EmploymentStatisticsView.vue'),
        meta: { title: '就业统计' }
      }
    ]
  },

  { path: '/:pathMatch(.*)*', redirect: '/home' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = (to.meta.title ? `${to.meta.title} - ` : '') + '校园求职招聘系统'

  const userStore = useUserStore()

  // 1. 需要登录但未登录 → 去登录页
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
    return
  }

  // 2. 已登录但访问需要特定角色的页面，且角色不匹配
  // 注意：只拦截明确设置了 role 的路由（如 /student/*, /company/* 等）
  if (to.meta.requiresAuth && to.meta.role && userStore.role !== to.meta.role) {
    // 跳到该用户对应的首页
    const roleMap = {
      STUDENT: '/student/dashboard',
      COMPANY: '/company/dashboard',
      COLLEGE: '/college/dashboard',
      ADMIN: '/admin/dashboard'
    }
    const target = roleMap[userStore.role]
    if (target) {
      next(target)
    } else {
      // 角色未知，清除登录状态并去登录页
      userStore.logout()
      next('/login')
    }
    return
  }

  next()
})

export default router
