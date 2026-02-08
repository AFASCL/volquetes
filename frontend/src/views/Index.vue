<script setup lang="ts">
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { computed } from 'vue'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()

function isActive(path: string): boolean {
  if (path === '/') return route.path === '/' || route.path === ''
  return route.path.startsWith(path)
}

const userDisplayName = computed(() =>
  auth.hasRoleAdmin ? 'Administrador' : 'Usuario'
)

const userInitial = computed(() => userDisplayName.value.charAt(0).toUpperCase())

function logout() {
  auth.clearAuth()
  router.push('/login')
}
</script>

<template>
  <div class="flex min-h-screen bg-slate-50/80">
    <!-- Sidebar — diseño 2026: oscuro, acento en activo, transiciones suaves -->
    <aside
      class="fixed inset-y-0 left-0 z-20 flex w-60 flex-col border-r border-slate-700/50"
      style="background-color: #1E293B; box-shadow: 4px 0 24px -4px rgb(0 0 0 / 0.12);"
      aria-label="Menú principal"
    >
      <div class="flex h-16 items-center border-b border-slate-600/40 px-4">
        <RouterLink
          to="/"
          class="flex items-center gap-3 transition-app focus:outline-none focus:ring-2 focus:ring-blue-400 focus:ring-offset-2 focus:ring-offset-[#1E293B] rounded-lg py-1"
        >
          <img src="/logo.png" alt="Volquetes" class="logo-sin-fondo h-14 w-auto object-contain" />
          <span class="text-lg font-semibold tracking-tight text-white hidden sm:inline">Volquetes</span>
        </RouterLink>
      </div>
      <nav class="flex-1 overflow-y-auto py-5 px-3">
        <ul class="space-y-0.5">
          <li>
            <RouterLink
              to="/"
              :class="[
                'flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium transition-app focus:outline-none focus-visible:ring-2 focus-visible:ring-blue-400 focus-visible:ring-offset-2 focus-visible:ring-offset-[#1E293B]',
                route.path === '/' || route.path === ''
                  ? 'bg-slate-600/70 text-white shadow-inner'
                  : 'text-slate-300 hover:bg-slate-600/50 hover:text-white',
              ]"
            >
              <svg class="h-5 w-5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
              </svg>
              Dashboard
            </RouterLink>
          </li>
          <li v-if="auth.hasRoleAdmin">
            <RouterLink
              to="/volquetes"
              :class="[
                'flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium transition-app focus:outline-none focus-visible:ring-2 focus-visible:ring-blue-400 focus-visible:ring-offset-2 focus-visible:ring-offset-[#1E293B]',
                isActive('/volquetes')
                  ? 'bg-slate-600/70 text-white shadow-inner'
                  : 'text-slate-300 hover:bg-slate-600/50 hover:text-white',
              ]"
            >
              <svg class="h-5 w-5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
              </svg>
              Gestión de Volquetes
            </RouterLink>
          </li>
          <li v-if="auth.isAuthenticated">
            <RouterLink
              to="/volquetes-estado"
              :class="[
                'flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium transition-app focus:outline-none focus-visible:ring-2 focus-visible:ring-blue-400 focus-visible:ring-offset-2 focus-visible:ring-offset-[#1E293B]',
                isActive('/volquetes-estado')
                  ? 'bg-slate-600/70 text-white shadow-inner'
                  : 'text-slate-300 hover:bg-slate-600/50 hover:text-white',
              ]"
            >
              <svg class="h-5 w-5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
              </svg>
              Estado Inventario
            </RouterLink>
          </li>
          <li v-if="auth.hasRoleAdmin">
            <RouterLink
              to="/clientes"
              :class="[
                'flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium transition-app focus:outline-none focus-visible:ring-2 focus-visible:ring-blue-400 focus-visible:ring-offset-2 focus-visible:ring-offset-[#1E293B]',
                isActive('/clientes')
                  ? 'bg-slate-600/70 text-white shadow-inner'
                  : 'text-slate-300 hover:bg-slate-600/50 hover:text-white',
              ]"
            >
              <svg class="h-5 w-5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
              </svg>
              Clientes
            </RouterLink>
          </li>
          <li v-if="auth.isAuthenticated">
            <RouterLink
              to="/pedidos"
              :class="[
                'flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium transition-app focus:outline-none focus-visible:ring-2 focus-visible:ring-blue-400 focus-visible:ring-offset-2 focus-visible:ring-offset-[#1E293B]',
                isActive('/pedidos')
                  ? 'bg-slate-600/70 text-white shadow-inner'
                  : 'text-slate-300 hover:bg-slate-600/50 hover:text-white',
              ]"
            >
              <svg class="h-5 w-5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 20l-5.196-12C3.916 6.416 4.5 6 5.196 6h13.608c.696 0 1.28.416 1.572 1l-5.196 12a2 2 0 01-3.572 0z" />
              </svg>
              Pedidos
            </RouterLink>
          </li>
          <li v-if="auth.hasRoleAdmin">
            <RouterLink
              to="/reportes"
              :class="[
                'flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium transition-app focus:outline-none focus-visible:ring-2 focus-visible:ring-blue-400 focus-visible:ring-offset-2 focus-visible:ring-offset-[#1E293B]',
                isActive('/reportes')
                  ? 'bg-slate-600/70 text-white shadow-inner'
                  : 'text-slate-300 hover:bg-slate-600/50 hover:text-white',
              ]"
            >
              <svg class="h-5 w-5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.5a2 2 0 012 2v5.5a2 2 0 01-2 2z" />
              </svg>
              Reportes
            </RouterLink>
          </li>
        </ul>
      </nav>
    </aside>

    <!-- Contenedor: Header + contenido -->
    <div class="flex flex-1 flex-col pl-60 min-h-screen">
      <!-- Header superior — estilo 2026: glass, sombra suave -->
      <header class="sticky top-0 z-10 flex h-16 items-center justify-end gap-4 border-b border-slate-200/80 bg-white/90 px-6 shadow-sm backdrop-blur-md">
        <div class="flex items-center gap-4">
          <div class="hidden items-center gap-3 sm:flex">
            <div
              class="flex h-9 w-9 shrink-0 items-center justify-center rounded-full text-sm font-semibold text-white shadow-sm"
              style="background-color: #2563EB"
              aria-hidden="true"
            >
              {{ userInitial }}
            </div>
            <span class="text-sm font-medium text-slate-800">{{ userDisplayName }}</span>
          </div>
          <button
            type="button"
            class="flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-3.5 py-2 text-sm font-medium text-slate-600 shadow-sm transition-app hover:border-slate-300 hover:bg-slate-50 hover:text-slate-800 focus:outline-none focus-visible:ring-2 focus-visible:ring-[#2563EB] focus-visible:ring-offset-2"
            aria-label="Cerrar sesión"
            @click="logout"
          >
            <svg class="h-5 w-5 text-slate-500" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
            </svg>
            <span class="hidden sm:inline">Cerrar sesión</span>
          </button>
        </div>
      </header>

      <!-- Área principal -->
      <main class="flex-1">
        <div class="mx-auto max-w-6xl px-4 py-8 sm:px-6 lg:px-8">
          <RouterView />
        </div>
      </main>
    </div>
  </div>
</template>
