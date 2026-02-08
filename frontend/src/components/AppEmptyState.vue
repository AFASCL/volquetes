<script setup lang="ts">
defineProps<{
  message: string
  hint?: string
}>()
</script>

<template>
  <div
    class="flex flex-col items-center justify-center rounded-2xl border border-slate-200/80 bg-slate-50/50 px-8 py-14 text-center"
  >
    <div
      class="mb-4 flex h-14 w-14 items-center justify-center rounded-full bg-slate-200/80 text-slate-400"
      aria-hidden="true"
    >
      <svg class="h-7 w-7" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="1.5"
          d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"
        />
      </svg>
    </div>
    <p class="text-slate-700 font-medium">{{ message }}</p>
    <p v-if="hint" class="mt-1 text-sm text-slate-500">{{ hint }}</p>
  </div>
</template>
