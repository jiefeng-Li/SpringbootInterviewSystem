import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/login/index.vue'
import NotFound from '@/views/NotFound/index.vue';
import RegisterPage from '@/views/register/index.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/home', redirect: '/' },
    {
      path: '/',
      name: 'Home',
      component: () => import('@/views/home/index.vue'),
      children: [
          { path: '/job', name: 'Job', component: () => import('@/views/home/components/JobPage.vue') },
          { path: '/resume', name: 'Resume', component: () => import('@/views/home/components/ResumePage.vue') },
          { path: '/my-invote', name: 'MyInvote', component: () => import('@/views/home/components/MyInvotePage.vue') },
          { path: '/company', name: 'Company', component: () => import('@/views/home/components/CompanyPage.vue') },
      ]
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
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: NotFound
    }
  ],
})

export default router
