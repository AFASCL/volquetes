<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { computed } from 'vue'

const auth = useAuthStore()
const greeting = computed(() =>
  auth.hasRoleAdmin ? 'Administrador' : 'Usuario'
)
</script>

<template>
  <div class="space-y-6">
    <div>
      <h1 class="page-title-app">Dashboard</h1>
      <p class="mt-1 text-slate-600">
        Hola, {{ greeting }}. Bienvenido al sistema de gestión de volquetes.
      </p>
    </div>
    <div class="card-app p-6">
      <div class="flex flex-wrap items-center gap-4">
        <div class="flex h-14 w-14 shrink-0 items-center justify-center rounded-xl bg-slate-100">
          <img src="/logo.png" alt="" class="logo-sin-fondo h-12 w-auto object-contain" aria-hidden="true" />
        </div>
        <div>
          <h2 class="text-lg font-medium text-slate-800">Sistema de Volquetes</h2>
          <p class="mt-0.5 text-sm text-slate-500">
            Gestión de inventario, clientes, pedidos y reportes desde el menú lateral.
          </p>
        </div>
      </div>
    </div>
  </div>
</template>
