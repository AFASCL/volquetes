<script setup lang="ts">
import ActionIcons from '@/components/icons/ActionIcons.vue'

defineProps<{
  message: string
}>()

defineEmits<{
  retry: []
}>()
</script>

<template>
  <div
    class="flex flex-col items-center justify-center rounded-2xl border border-red-200/80 bg-red-50/30 px-8 py-12 text-center"
  >
    <div
      class="mb-4 flex h-14 w-14 items-center justify-center rounded-full bg-red-100 text-red-600"
      aria-hidden="true"
    >
      <ActionIcons name="exclamation-triangle" class="h-8 w-8" />
    </div>
    <p class="text-red-800 font-medium">{{ message }}</p>
    <button
      type="button"
      class="mt-4 inline-flex items-center gap-2 rounded-xl border border-red-200 bg-white px-4 py-2.5 text-sm font-medium text-red-700 shadow-sm transition-app hover:bg-red-50 focus:outline-none focus:ring-2 focus:ring-red-200 focus:ring-offset-2"
      @click="$emit('retry')"
    >
      <ActionIcons name="arrow-path-retry" class="h-4 w-4" />
      Reintentar
    </button>
  </div>
</template>
