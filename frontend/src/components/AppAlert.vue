<script setup lang="ts">
import ActionIcons from '@/components/icons/ActionIcons.vue'

withDefaults(
  defineProps<{
    type: 'success' | 'error'
    message: string
  }>(),
  {}
)
</script>

<template>
  <div
    role="alert"
    :class="[
      'flex items-start gap-3 rounded-xl px-4 py-3 text-sm shadow-sm',
      type === 'success'
        ? 'border border-emerald-200/80 bg-emerald-50/90 text-emerald-800'
        : 'border border-red-200/80 bg-red-50/90 text-red-800',
    ]"
  >
    <span
      v-if="type === 'success'"
      class="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-emerald-100 text-emerald-600"
    >
      <ActionIcons name="check" class="h-5 w-5" />
    </span>
    <span
      v-else
      class="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-red-100 text-red-600"
    >
      <ActionIcons name="exclamation-triangle" class="h-5 w-5" />
    </span>
    <p class="flex-1 pt-0.5 font-medium">{{ message }}</p>
  </div>
</template>
