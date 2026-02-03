<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <header class="border-b bg-white">
      <nav class="mx-auto flex max-w-7xl items-center justify-between px-4 py-3 sm:px-6 lg:px-8">
        <RouterLink to="/" class="text-lg font-medium text-gray-800">Volquetes</RouterLink>
        <div class="flex gap-4">
          <RouterLink
            to="/"
            class="text-gray-600 hover:text-gray-900 focus-visible:ring-2 focus-visible:ring-blue-500 rounded"
          >
            Inicio
          </RouterLink>
        </div>
      </nav>
    </header>
    <main class="mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8">
      <RouterView />
    </main>
  </div>
</template>
