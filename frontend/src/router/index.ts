import { createRouter, createWebHistory } from 'vue-router'

import { setupRouterGuards } from './guard'
import { routes } from './routes'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior(_to, _from, savedPosition) {
    return savedPosition ?? { top: 0 }
  },
})

setupRouterGuards(router)

export default router
