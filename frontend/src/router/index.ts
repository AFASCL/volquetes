import { createRouter, createWebHashHistory } from 'vue-router'

export type AuthProfile = {
  permisos: string[]
  role?: string
}

declare module 'vue-router' {
  interface RouteMeta {
    permisoRequerido?: string
    public?: boolean
  }
}

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { public: true },
    },
    {
      path: '/401',
      name: 'error401',
      component: () => import('@/views/Error401.vue'),
      meta: { public: true },
    },
    {
      path: '/404',
      name: 'error404',
      component: () => import('@/views/Error404.vue'),
      meta: { public: true },
    },
    {
      path: '/500',
      name: 'error500',
      component: () => import('@/views/Error500.vue'),
      meta: { public: true },
    },
    {
      path: '/',
      component: () => import('@/views/Index.vue'),
      meta: { permisoRequerido: 'HOME' },
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('@/views/HomeView.vue'),
          meta: { permisoRequerido: 'HOME' },
        },
      ],
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      redirect: '/404',
    },
  ],
})

const AUTH_TOKEN_KEY = 'auth_token'
const AUTH_PROFILE_KEY = 'auth_profile'
const AUTH_LAST_ROUTE_KEY = 'auth_lastRoute'

function getToken(): string | null {
  return localStorage.getItem(AUTH_TOKEN_KEY)
}

function getProfile(): AuthProfile | null {
  const raw = localStorage.getItem(AUTH_PROFILE_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw) as AuthProfile
  } catch {
    return null
  }
}

router.beforeEach((to, _from, next) => {
  if (to.meta.public) {
    next()
    return
  }

  const token = getToken()
  if (!token) {
    localStorage.setItem(AUTH_LAST_ROUTE_KEY, to.fullPath)
    next({ path: '/login' })
    return
  }

  const permiso = to.meta.permisoRequerido as string | undefined
  if (!permiso) {
    next()
    return
  }

  const profile = getProfile()
  const isAdmin = profile?.role === 'ROLE_ADMIN'
  const hasPermiso = profile?.permisos?.includes(permiso) ?? false

  if (isAdmin || hasPermiso) {
    next()
    return
  }

  next({ path: '/401' })
})

export default router
