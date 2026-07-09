import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', name: 'home', component: () => import('../views/HomeView.vue') },
  { path: '/events', name: 'events', component: () => import('../views/EventsView.vue') },
  { path: '/event/:id', name: 'event', component: () => import('../views/EventView.vue') },
  { path: '/event/:id/join', name: 'event-join', component: () => import('../views/EventJoinView.vue') },
  { path: '/my/registrations', name: 'my-registrations', component: () => import('../views/MyRegistrationsView.vue'), meta: { requiresAuth: true } },
  { path: '/login', name: 'login', component: () => import('../views/LoginView.vue') },
  { path: '/register', name: 'register', component: () => import('../views/RegisterView.vue') },
  { path: '/projects', name: 'projects', component: () => import('../views/ProjectsView.vue') },
  { path: '/root', name: 'admin', component: () => import('../views/admin/AdminView.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to) {
    if (to.hash) {
      return { el: to.hash, behavior: 'smooth' }
    }
    return { top: 0 }
  },
})

router.beforeEach((to) => {
  if (to.meta?.requiresAuth) {
    const token = localStorage.getItem('ignai_access_token')
    if (!token) {
      return { name: 'login', query: { redirect: to.fullPath } }
    }
  }
  return true
})

export default router
