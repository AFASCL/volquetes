<script setup lang="ts">
defineOptions({ inheritAttrs: false })

withDefaults(
  defineProps<{
    name: 'plus' | 'pencil' | 'trash' | 'eye' | 'qr' | 'arrow-path' | 'arrow-down-tray' | 'check' | 'x-mark' | 'exclamation-triangle' | 'arrow-path-retry'
    class?: string
  }>(),
  { class: 'h-5 w-5' }
)
</script>

<template>
  <svg
    xmlns="http://www.w3.org/2000/svg"
    fill="none"
    viewBox="0 0 24 24"
    stroke-width="1.5"
    stroke="currentColor"
    :class="[$props.class]"
    aria-hidden="true"
  >
    <!-- Plus -->
    <path
      v-if="name === 'plus'"
      stroke-linecap="round"
      stroke-linejoin="round"
      d="M12 4.5v15m7.5-7.5h-15"
    />
    <!-- Pencil -->
    <path
      v-else-if="name === 'pencil'"
      stroke-linecap="round"
      stroke-linejoin="round"
      d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L10.582 16.07a4.5 4.5 0 0 1-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 0 1 1.13-1.897l8.932-8.931Zm0 0L19.5 7.125"
    />
    <!-- Trash -->
    <path
      v-else-if="name === 'trash'"
      stroke-linecap="round"
      stroke-linejoin="round"
      d="m14.74 9-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 0 1-2.244 2.077H8.084a2.25 2.25 0 0 1-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 0 0-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 0 1 3.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 0 0-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 0 0-7.5 0"
    />
    <!-- Eye -->
    <g v-else-if="name === 'eye'">
      <path
        stroke-linecap="round"
        stroke-linejoin="round"
        d="M2.036 12.322a1.012 1.012 0 0 1 0-.639C3.423 7.51 7.36 4.5 12 4.5c4.638 0 8.573 3.007 9.963 7.178.07.207.07.431 0 .639C20.577 16.49 16.64 19.5 12 19.5c-4.638 0-8.573-3.007-9.963-7.178Z"
      />
      <path
        stroke-linecap="round"
        stroke-linejoin="round"
        d="M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z"
      />
    </g>
    <!-- QR (grid) -->
    <path
      v-else-if="name === 'qr'"
      stroke-linecap="round"
      stroke-linejoin="round"
      d="M3.75 4.5a.75.75 0 0 0 0 1.5h.75v3.75a.75.75 0 0 1-1.5 0V4.5H2.25a.75.75 0 0 0 0 1.5h1.5zm0 9h.75v3.75a.75.75 0 0 1-1.5 0V13.5H2.25a.75.75 0 0 0 0 1.5h1.5zM20.25 4.5a.75.75 0 0 0 0 1.5h1.5v3.75a.75.75 0 0 1-1.5 0V4.5h-.75zm0 9a.75.75 0 0 0 0 1.5h1.5v.75a.75.75 0 0 1-1.5 0V13.5h-.75zM6 10.5a.75.75 0 0 1 .75-.75h1.5a.75.75 0 0 1 0 1.5h-1.5a.75.75 0 0 1-.75-.75zm0 3a.75.75 0 0 1 .75-.75h1.5a.75.75 0 0 1 0 1.5h-1.5a.75.75 0 0 1-.75-.75zM18 10.5a.75.75 0 0 1 .75-.75h.75a.75.75 0 0 1 0 1.5h-.75a.75.75 0 0 1-.75-.75zm0 3a.75.75 0 0 1 .75-.75h.75a.75.75 0 0 1 0 1.5h-.75a.75.75 0 0 1-.75-.75z"
    />
    <!-- Arrow path (cambiar estado) -->
    <path
      v-else-if="name === 'arrow-path'"
      stroke-linecap="round"
      stroke-linejoin="round"
      d="M16.023 9.348h4.992v-.001M2.985 19.644v-4.992m0 0h4.992m-4.993 0 3.181 3.183a8.25 8.25 0 0 0 13.803-3.7M4.031 9.865a8.25 8.25 0 0 1 13.803-3.7l3.181 3.182m0-4.991v4.99"
    />
    <!-- Arrow down tray (export) -->
    <path
      v-else-if="name === 'arrow-down-tray'"
      stroke-linecap="round"
      stroke-linejoin="round"
      d="M3 16.5v2.25A2.25 2.25 0 0 0 5.25 21h13.5A2.25 2.25 0 0 0 21 18.75V16.5M16.5 12 12 16.5m0 0L7.5 12m4.5 4.5V3"
    />
    <!-- Check -->
    <path
      v-else-if="name === 'check'"
      stroke-linecap="round"
      stroke-linejoin="round"
      d="m4.5 12.75 6 6 9-13.5"
    />
    <!-- X mark -->
    <path
      v-else-if="name === 'x-mark'"
      stroke-linecap="round"
      stroke-linejoin="round"
      d="M6 18 18 6M6 6l12 12"
    />
    <!-- Exclamation triangle -->
    <path
      v-else-if="name === 'exclamation-triangle'"
      stroke-linecap="round"
      stroke-linejoin="round"
      d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126ZM12 15.75h.007v.008H12v-.008Z"
    />
    <!-- Arrow path (retry) -->
    <path
      v-else-if="name === 'arrow-path-retry'"
      stroke-linecap="round"
      stroke-linejoin="round"
      d="M16.023 9.348h4.992v-.001M2.985 19.644v-4.992m0 0h4.992m-4.993 0 3.181 3.183a8.25 8.25 0 0 0 13.803-3.7M4.031 9.865a8.25 8.25 0 0 1 13.803-3.7l3.181 3.182m0-4.991v4.99"
    />
  </svg>
</template>
