<script setup lang="ts">
import { RouterLink } from 'vue-router'
</script>

<template>
  <div class="min-h-[60vh] flex flex-col items-center justify-center text-center px-4">
    <h1 class="text-2xl font-semibold text-gray-800">500 — Error del servidor</h1>
    <p class="mt-2 text-gray-600">Ocurrió un error. Intentá de nuevo más tarde.</p>
    <RouterLink
      to="/"
      class="mt-6 rounded bg-gray-800 px-4 py-2 text-white hover:bg-gray-700 focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2"
    >
      Volver al inicio
    </RouterLink>
  </div>
</template>
