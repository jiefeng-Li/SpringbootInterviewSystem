import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/login/index.vue'
import NotFound from '@/views/NotFound/index.vue';
import RegisterPage from '@/views/register/index.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/home',
      name: 'Home',
      component: () => import('@/views/home/index.vue'),
      children: [
          { path: '/job', name: 'Job', component: () => import('@/views/home/components/JobPage.vue') },
          { path: '/resume', name: 'Resume', component: () => import('@/views/home/components/ResumePage.vue') },
          { path: '/my-invote', name: 'MyInvote', component: () => import('@/views/home/components/MyInvotePage.vue') },
          { path: '/company', name: 'HomeCompany', component: () => import('@/views/home/components/CompanyPage.vue') },
          { path: '/home', name: 'HomePage', component: () => import('@/views/home/components/HomePage.vue') },
      ]
    },
    {
        path: '/comp',
        name: 'Company',
        component: () => import('@/views/comp/index.vue')
    },
    {
      path: '/register',
      name: 'Register',
      component: RegisterPage
    },
    {
      path: '/login',
      name: 'Login',
      component: Login
    },
    {
      path: '/api-test',
      name: 'ApiTest',
      component: () => import('@/api/ApiTest.vue')
    },
    {
      path: '/personal',
      name: 'Personal',
      component: () => import('@/views/personal/index.vue'),
      children: [
          { 
            path: '/personal/info', 
            name: 'PersonalInfoPage', 
            component: () => import('@/views/personal/components/PersonalInfo.vue'),
          },
          { 
            path: '/personal/info/edit', 
            name: 'PersonalInfoEdit', 
            component: () => import('@/views/personal/components/PersonalInfoEdit.vue') 
          },
          {
            path: '/personal/my',
            name: 'PersonalMy',
            component: () => import('@/views/personal/components/PersonalMy.vue')
          },
          {
            path: '/personal/settings',
            name: 'SystemSetting',
            component: () => import('@/views/personal/components/SystemSetting.vue')
          },
          {
            path: '/personal/help',
            name: 'HelpCenter',
            component: () => import('@/views/personal/components/HelpCenter.vue')
          }
      ]
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: NotFound
    }
  ],
})

export default router
