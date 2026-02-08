<script setup lang="ts">
import { RouterLink } from 'vue-router'
</script>

<template>
  <div class="min-h-[60vh] flex flex-col items-center justify-center text-center px-4">
    <h1 class="page-title-app">403 — Sin permiso</h1>
    <p class="mt-2 text-slate-600">No tenés permiso para acceder a este recurso.</p>
    <RouterLink to="/" class="btn-primary mt-6">
      Volver al inicio
    </RouterLink>
  </div>
</template>
