<script setup lang="ts">
defineProps<{
  message: string
}>()
</script>

<template>
  <div
    class="flex items-center gap-2 rounded-lg border-l-4 border-red-400 bg-red-50/80 px-3 py-2 text-sm font-medium text-red-800"
    role="alert"
  >
    <span class="shrink-0 text-red-500" aria-hidden="true">!</span>
    <span>{{ message }}</span>
  </div>
</template>
