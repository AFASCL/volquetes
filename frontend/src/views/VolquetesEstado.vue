<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import AppEmptyState from '@/components/AppEmptyState.vue'
import AppErrorState from '@/components/AppErrorState.vue'
import * as volquetesApi from '@/services/api/volquetes'
import type { VolqueteResponse, VolqueteEstado } from '@/types/volquetes'

const ESTADOS: { value: VolqueteEstado | null; label: string; color: string }[] = [
  { value: null, label: 'Todos', color: 'gray' },
  { value: 'DISPONIBLE', label: 'Disponible', color: 'green' },
  { value: 'EN_CLIENTE', label: 'En cliente', color: 'blue' },
  { value: 'EN_TRANSITO', label: 'En tránsito', color: 'yellow' },
  { value: 'FUERA_DE_SERVICIO', label: 'Fuera de servicio', color: 'red' },
]

function estadoLabel(estado: VolqueteEstado): string {
  return ESTADOS.find((e) => e.value === estado)?.label ?? estado
}

function estadoColor(estado: VolqueteEstado): string {
  const estadoInfo = ESTADOS.find((e) => e.value === estado)
  if (!estadoInfo) return 'gray'
  return estadoInfo.color
}

const list = ref<VolqueteResponse[]>([])
const totalElements = ref(0)
const loading = ref(false)
const error = ref('')
const estadoFiltro = ref<VolqueteEstado | null>(null)
const pollingInterval = ref<number | null>(null)
const pollingEnabled = ref(false)
const POLLING_INTERVAL_MS = 30000 // 30 segundos

const estadoFiltroLabel = computed(() => {
  return ESTADOS.find((e) => e.value === estadoFiltro.value)?.label ?? 'Todos'
})

const emptyMessage = computed(() =>
  estadoFiltro.value
    ? `No hay volquetes en estado "${estadoFiltroLabel.value}".`
    : 'No hay volquetes en el inventario.'
)

function setEstadoFiltro(estado: VolqueteEstado | null) {
  estadoFiltro.value = estado
  loadList()
}

async function loadList() {
  loading.value = true
  error.value = ''
  try {
    const res = await volquetesApi.listVolquetes({
      page: 0,
      size: 1000, // v1: sin paginación en esta vista, mostrar todos los volquetes filtrados
      estado: estadoFiltro.value || undefined,
    })
    list.value = res.content
    totalElements.value = res.totalElements
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Error al cargar estado del inventario.'
  } finally {
    loading.value = false
  }
}

function togglePolling() {
  if (pollingEnabled.value) {
    stopPolling()
  } else {
    startPolling()
  }
}

function startPolling() {
  pollingEnabled.value = true
  pollingInterval.value = window.setInterval(() => {
    loadList()
  }, POLLING_INTERVAL_MS)
}

function stopPolling() {
  pollingEnabled.value = false
  if (pollingInterval.value != null) {
    clearInterval(pollingInterval.value)
    pollingInterval.value = null
  }
}

onMounted(() => {
  loadList()
})

onUnmounted(() => {
  stopPolling()
})
</script>

<template>
  <div class="space-y-4">
    <div class="flex flex-wrap items-center justify-between gap-4">
      <h1 class="page-title-app">Estado del Inventario</h1>
      <div class="flex gap-2">
        <button type="button" class="btn-secondary" @click="loadList">Actualizar</button>
        <button
          type="button"
          :class="[
            'inline-flex items-center justify-center gap-2 rounded-xl px-4 py-2.5 text-sm font-medium focus:outline-none focus:ring-2 focus:ring-offset-2',
            pollingEnabled
              ? 'border border-red-300 bg-red-50 text-red-700 hover:bg-red-100 focus:ring-red-300'
              : 'btn-secondary',
          ]"
          @click="togglePolling"
        >
          {{ pollingEnabled ? 'Detener auto-actualización' : 'Auto-actualizar (30s)' }}
        </button>
      </div>
    </div>

    <!-- Filtros por estado (chips) -->
    <div class="flex flex-wrap gap-2">
      <button
        v-for="estadoInfo in ESTADOS"
        :key="estadoInfo.value ?? 'todos'"
        type="button"
        :class="[
          'rounded-full px-4 py-2 text-sm font-medium transition-colors focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2',
          estadoFiltro === estadoInfo.value
            ? estadoInfo.color === 'gray'
              ? 'bg-slate-600 text-white'
              : estadoInfo.color === 'green'
                ? 'bg-green-600 text-white'
                : estadoInfo.color === 'blue'
                  ? 'bg-primary text-white'
                  : estadoInfo.color === 'yellow'
                    ? 'bg-amber-500 text-white'
                    : 'bg-red-600 text-white'
            : 'bg-white border border-slate-300 text-slate-700 hover:bg-slate-50',
        ]"
        @click="setEstadoFiltro(estadoInfo.value)"
      >
        {{ estadoInfo.label }}
      </button>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="flex justify-center py-12">
      <p class="text-slate-500">Cargando...</p>
    </div>

    <!-- Error + retry -->
    <AppErrorState v-else-if="error" :message="error" @retry="loadList" />

    <!-- Empty -->
    <AppEmptyState v-else-if="list.length === 0" :message="emptyMessage" />

    <!-- Listado (cards o grid) -->
    <div v-else class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
      <div
        v-for="volquete in list"
        :key="volquete.id"
        class="card-app p-4 transition-shadow hover:shadow-md"
      >
        <div class="flex items-start justify-between">
          <div class="flex-1">
            <h3 class="text-lg font-semibold text-slate-900">{{ volquete.codigoInterno }}</h3>
            <p class="text-sm text-slate-600">{{ volquete.codigoExterno }}</p>
          </div>
          <span
            :class="[
              'rounded-full px-3 py-1 text-xs font-medium',
              estadoColor(volquete.estadoActual) === 'green'
                ? 'bg-green-100 text-green-800'
                : estadoColor(volquete.estadoActual) === 'blue'
                  ? 'bg-primary/15 text-primary'
                  : estadoColor(volquete.estadoActual) === 'yellow'
                    ? 'bg-amber-100 text-amber-800'
                    : estadoColor(volquete.estadoActual) === 'red'
                      ? 'bg-red-100 text-red-800'
                      : 'bg-slate-100 text-slate-800',
            ]"
          >
            {{ estadoLabel(volquete.estadoActual) }}
          </span>
        </div>
      </div>
    </div>

    <!-- Contador total -->
    <div v-if="!loading && !error" class="text-sm text-slate-600">
      <p>
        Mostrando {{ list.length }} de {{ totalElements }}
        {{ totalElements === 1 ? 'volquete' : 'volquetes' }}
        <span v-if="estadoFiltro"> en estado "{{ estadoFiltroLabel }}"</span>
      </p>
    </div>
  </div>
</template>
