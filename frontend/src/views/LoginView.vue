<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const token = ref('')
const error = ref('')

const AUTH_TOKEN_KEY = 'auth_token'
const AUTH_PROFILE_KEY = 'auth_profile'
const AUTH_LAST_ROUTE_KEY = 'auth_lastRoute'

function login() {
  error.value = ''
  if (!token.value.trim()) {
    error.value = 'Ingresá el token.'
    return
  }
  localStorage.setItem(AUTH_TOKEN_KEY, token.value.trim())
  // Perfil mínimo para desarrollo: ADMIN bypass
  localStorage.setItem(
    AUTH_PROFILE_KEY,
    JSON.stringify({ permisos: ['HOME'], role: 'ROLE_ADMIN' })
  )
  const last = localStorage.getItem(AUTH_LAST_ROUTE_KEY)
  localStorage.removeItem(AUTH_LAST_ROUTE_KEY)
  router.push(last || '/')
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100 p-4">
    <div class="w-full max-w-sm rounded-lg bg-white p-6 shadow">
      <h1 class="text-xl font-semibold text-gray-800 mb-4">Login</h1>
      <form @submit.prevent="login" class="space-y-4">
        <div>
          <label for="token" class="block text-sm font-medium text-gray-700">Token</label>
          <input
            id="token"
            v-model="token"
            type="text"
            class="mt-1 block w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
            placeholder="ej. dev (cualquier valor en desarrollo)"
          />
          <p class="mt-1 text-xs text-gray-500">En desarrollo: ingresá cualquier texto (ej. dev) y Entrar.</p>
        </div>
        <p v-if="error" class="text-sm text-red-600">{{ error }}</p>
        <button
          type="submit"
          class="w-full rounded bg-blue-600 px-4 py-2 text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
        >
          Entrar
        </button>
      </form>
    </div>
  </div>
</template>
