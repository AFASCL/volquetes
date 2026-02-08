<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()
const token = ref('')
const error = ref('')
/** 'admin' = Administrador, 'user' = Usuario (Chofer) */
const tipoUsuario = ref<'admin' | 'user'>('admin')

const AUTH_LAST_ROUTE_KEY = 'auth_lastRoute'

function login() {
  error.value = ''
  if (!token.value.trim()) {
    error.value = 'Ingresá el token.'
    return
  }
  const tokenValue = token.value.trim()
  const profile =
    tipoUsuario.value === 'admin'
      ? { permisos: ['HOME'], role: 'ROLE_ADMIN' as const }
      : { permisos: ['HOME'], role: 'ROLE_USER' as const }
  auth.setAuth(tokenValue, profile)
  const last = localStorage.getItem(AUTH_LAST_ROUTE_KEY)
  localStorage.removeItem(AUTH_LAST_ROUTE_KEY)
  router.push(last || '/')
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-b from-slate-50 to-slate-100 p-4">
    <div class="w-full max-w-md">
      <div class="rounded-2xl border border-slate-200/80 bg-white p-8 shadow-xl shadow-slate-200/50 sm:p-10">
        <div class="mb-8 flex flex-col items-center text-center">
          <img src="/logo.png" alt="Volquetes" class="logo-sin-fondo h-28 w-auto object-contain mb-6" />
          <h1 class="text-2xl font-bold text-slate-800">Sistema de Volquetes</h1>
          <p class="mt-2 text-sm text-slate-500">Ingresá para continuar</p>
        </div>
        <form @submit.prevent="login" class="space-y-5">
          <div>
            <label for="tipo-usuario" class="label-app">Ingresar como</label>
            <select id="tipo-usuario" v-model="tipoUsuario" class="input-app">
              <option value="admin">Administrador</option>
              <option value="user">Usuario (Chofer)</option>
            </select>
          </div>
          <div>
            <label for="token" class="label-app">Token de acceso</label>
            <input
              id="token"
              v-model="token"
              type="text"
              autocomplete="off"
              placeholder="ej. dev (cualquier valor en desarrollo)"
              class="input-app"
            />
            <p class="helper-app">En desarrollo: ingresá cualquier texto (ej. dev) y Entrar.</p>
          </div>
          <div
            v-if="error"
            class="flex items-center gap-2 rounded-lg border-l-4 border-red-400 bg-red-50/80 px-3 py-2.5 text-sm font-medium text-red-800"
            role="alert"
          >
            <span class="shrink-0 text-red-500" aria-hidden="true">!</span>
            <span>{{ error }}</span>
          </div>
          <button type="submit" class="btn-primary w-full py-3 text-base">
            Entrar
          </button>
        </form>
      </div>
      <p class="mt-6 text-center text-xs text-slate-500">
        Gestión de inventario, pedidos y clientes
      </p>
    </div>
  </div>
</template>
