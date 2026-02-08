<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ActionIcons from '@/components/icons/ActionIcons.vue'
import AppAlert from '@/components/AppAlert.vue'
import AppEmptyState from '@/components/AppEmptyState.vue'
import AppErrorState from '@/components/AppErrorState.vue'
import AppFormError from '@/components/AppFormError.vue'
import * as volquetesApi from '@/services/api/volquetes'
import type { VolqueteResponse, VolqueteRequest, VolqueteEstado } from '@/types/volquetes'

const router = useRouter()

function ensureAdmin() {
  const profileRaw = localStorage.getItem('auth_profile')
  const profile = profileRaw ? JSON.parse(profileRaw) : null
  const isAdmin = profile?.role === 'ROLE_ADMIN'
  if (!isAdmin) router.push('/401')
}

const ESTADOS: { value: VolqueteEstado; label: string }[] = [
  { value: 'DISPONIBLE', label: 'Disponible' },
  { value: 'EN_CLIENTE', label: 'En cliente' },
  { value: 'EN_TRANSITO', label: 'En tránsito' },
  { value: 'FUERA_DE_SERVICIO', label: 'Fuera de servicio' },
]

function estadoLabel(estado: VolqueteEstado): string {
  return ESTADOS.find((e) => e.value === estado)?.label ?? estado
}

/** Clases para badge de estado: Disponible (green), En Obra/En cliente (amber), Mantenimiento (red), En tránsito (slate). */
function estadoBadgeClass(estado: VolqueteEstado): string {
  const base = 'inline-flex rounded-full px-2.5 py-0.5 text-xs font-medium text-white'
  switch (estado) {
    case 'DISPONIBLE':
      return `${base} bg-[#22c55e]`   // Green 500
    case 'EN_CLIENTE':
      return `${base} bg-[#f59e0b]`   // Amber 500 — En Obra
    case 'FUERA_DE_SERVICIO':
      return `${base} bg-[#ef4444]`   // Red 500 — Mantenimiento
    case 'EN_TRANSITO':
      return `${base} bg-slate-500`
    default:
      return `${base} bg-slate-500`
  }
}

/** URL pública para generar QR a partir del código externo (sin dependencias nuevas). */
function qrImageUrl(codigoExterno: string): string {
  return `https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=${encodeURIComponent(codigoExterno)}`
}

const list = ref<VolqueteResponse[]>([])
const totalElements = ref(0)
const totalPages = ref(0)
const page = ref(0)
const size = ref(20)
const loading = ref(false)
const error = ref('')
const feedback = ref<{ type: 'success' | 'error'; message: string } | null>(null)
const modalOpen = ref(false)
const editingId = ref<number | null>(null)
const submitting = ref(false)
const formError = ref('')
const qrModalOpen = ref(false)
const qrCodigoExterno = ref('')
const exportLoading = ref(false)

const form = ref<VolqueteRequest>({
  codigoInterno: '',
  codigoExterno: '',
  estadoInicial: 'DISPONIBLE',
})

const isEditing = computed(() => editingId.value != null)
const modalTitle = computed(() => (isEditing.value ? 'Editar volquete' : 'Nuevo volquete'))
const qrImageSrc = computed(() => (qrCodigoExterno.value ? qrImageUrl(qrCodigoExterno.value) : ''))

function clearForm() {
  form.value = {
    codigoInterno: '',
    codigoExterno: '',
    estadoInicial: 'DISPONIBLE',
  }
  editingId.value = null
  formError.value = ''
}

function setFeedback(type: 'success' | 'error', message: string) {
  feedback.value = { type, message }
  setTimeout(() => { feedback.value = null }, 5000)
}

function validateForm(): boolean {
  const ci = form.value.codigoInterno?.trim()
  const ce = form.value.codigoExterno?.trim()
  if (!ci) {
    formError.value = 'El código interno es obligatorio.'
    return false
  }
  if (!ce) {
    formError.value = 'El código externo es obligatorio.'
    return false
  }
  formError.value = ''
  return true
}

async function loadList() {
  loading.value = true
  error.value = ''
  try {
    const res = await volquetesApi.listVolquetes({ page: page.value, size: size.value })
    list.value = res.content
    totalElements.value = res.totalElements
    totalPages.value = res.totalPages
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Error al cargar volquetes.'
  } finally {
    loading.value = false
  }
}

function openCreate() {
  clearForm()
  modalOpen.value = true
}

function openEdit(row: VolqueteResponse) {
  editingId.value = row.id
  form.value = {
    codigoInterno: row.codigoInterno,
    codigoExterno: row.codigoExterno,
    estadoInicial: row.estadoActual,
  }
  formError.value = ''
  modalOpen.value = true
}

function closeModal() {
  modalOpen.value = false
  clearForm()
}

function openQr(row: VolqueteResponse) {
  qrCodigoExterno.value = row.codigoExterno
  qrModalOpen.value = true
}

function closeQrModal() {
  qrModalOpen.value = false
  qrCodigoExterno.value = ''
}

async function submitForm() {
  if (!validateForm()) return
  submitting.value = true
  formError.value = ''
  try {
    const body: VolqueteRequest = {
      codigoInterno: form.value.codigoInterno.trim(),
      codigoExterno: form.value.codigoExterno.trim(),
    }
    if (editingId.value != null) {
      await volquetesApi.updateVolquete(editingId.value, body)
      setFeedback('success', 'Volquete actualizado.')
    } else {
      body.estadoInicial = form.value.estadoInicial ?? 'DISPONIBLE'
      await volquetesApi.createVolquete(body)
      setFeedback('success', 'Volquete creado.')
    }
    closeModal()
    await loadList()
  } catch (e) {
    formError.value = e instanceof Error ? e.message : 'Error al guardar.'
  } finally {
    submitting.value = false
  }
}

async function confirmDelete(row: VolqueteResponse) {
  if (!window.confirm(`¿Eliminar volquete "${row.codigoInterno}" (${row.codigoExterno})?`)) return
  try {
    await volquetesApi.deleteVolquete(row.id)
    setFeedback('success', 'Volquete eliminado.')
    await loadList()
  } catch (e) {
    setFeedback('error', e instanceof Error ? e.message : 'Error al eliminar.')
  }
}

async function prevPage() {
  if (page.value === 0) return
  page.value--
  await loadList()
}

async function nextPage() {
  if (page.value >= totalPages.value - 1) return
  page.value++
  await loadList()
}

const EXPORT_FILENAME = 'inventario-volquetes.csv'

function triggerDownload(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  a.setAttribute('aria-hidden', 'true')
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)
}

async function exportarInventario() {
  exportLoading.value = true
  try {
    const blob = await volquetesApi.exportarInventario()
    triggerDownload(blob, EXPORT_FILENAME)
    setFeedback('success', 'Inventario exportado.')
  } catch (e) {
    setFeedback('error', e instanceof Error ? e.message : 'Error al exportar.')
  } finally {
    exportLoading.value = false
  }
}

onMounted(() => {
  ensureAdmin()
  loadList()
})
</script>

<template>
  <div class="space-y-4">
    <div class="flex flex-wrap items-center justify-between gap-4">
      <h1 class="page-title-app">Volquetes</h1>
      <div class="flex flex-wrap items-center gap-2">
        <button
          type="button"
          class="btn-secondary"
          :disabled="exportLoading"
          aria-label="Exportar inventario a CSV"
          title="Exportar inventario"
          @click="exportarInventario"
        >
          <ActionIcons name="arrow-down-tray" class="h-5 w-5" />
          <span>{{ exportLoading ? 'Exportando...' : 'Exportar' }}</span>
        </button>
        <button type="button" class="btn-primary" title="Nuevo volquete" @click="openCreate">
          <ActionIcons name="plus" class="h-5 w-5" />
          <span>Nuevo</span>
        </button>
      </div>
    </div>

    <AppAlert v-if="feedback" :type="feedback.type" :message="feedback.message" />

    <!-- Loading -->
    <div v-if="loading" class="flex justify-center py-12">
      <p class="text-slate-500">Cargando...</p>
    </div>

    <!-- Error + retry -->
    <AppErrorState v-else-if="error" :message="error" @retry="loadList" />

    <!-- Empty -->
    <AppEmptyState
      v-else-if="list.length === 0"
      message="No hay volquetes."
      hint="Creá uno con el botón Nuevo."
    />

    <!-- Table -->
    <div v-else class="overflow-x-auto rounded border border-gray-200 bg-white">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th scope="col" class="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">
              Código interno
            </th>
            <th scope="col" class="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">
              Código externo
            </th>
            <th scope="col" class="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">
              Estado
            </th>
            <th scope="col" class="relative px-4 py-3">
              <span class="sr-only">Acciones</span>
            </th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-200 bg-white">
          <tr v-for="row in list" :key="row.id" class="hover:bg-gray-50">
            <td class="whitespace-nowrap px-4 py-3 text-sm text-gray-900">{{ row.codigoInterno }}</td>
            <td class="whitespace-nowrap px-4 py-3 text-sm text-gray-600">{{ row.codigoExterno }}</td>
            <td class="whitespace-nowrap px-4 py-3 text-sm">
              <span :class="estadoBadgeClass(row.estadoActual)">
                {{ estadoLabel(row.estadoActual) }}
              </span>
            </td>
            <td class="whitespace-nowrap px-4 py-3 text-right">
              <div class="inline-flex items-center gap-1">
                <button
                  type="button"
                  class="rounded-lg p-2 text-slate-500 transition-app hover:bg-slate-100 hover:text-slate-700 focus:outline-none focus:ring-2 focus:ring-slate-300"
                  aria-label="Ver QR del volquete"
                  title="Ver QR"
                  @click="openQr(row)"
                >
                  <ActionIcons name="qr" class="h-4 w-4" />
                </button>
                <button
                  type="button"
                  class="rounded-lg p-2 text-slate-500 transition-app hover:bg-slate-100 hover:text-slate-700 focus:outline-none focus:ring-2 focus:ring-slate-300"
                  aria-label="Editar volquete"
                  title="Editar"
                  @click="openEdit(row)"
                >
                  <ActionIcons name="pencil" class="h-4 w-4" />
                </button>
                <button
                  type="button"
                  class="rounded-lg p-2 text-slate-500 transition-app hover:bg-red-50 hover:text-red-600 focus:outline-none focus:ring-2 focus:ring-red-200"
                  aria-label="Eliminar volquete"
                  title="Eliminar"
                  @click="confirmDelete(row)"
                >
                  <ActionIcons name="trash" class="h-4 w-4" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <div
        v-if="totalPages > 1"
        class="flex flex-wrap items-center justify-between gap-2 border-t border-slate-200 px-4 py-2 text-sm text-slate-500"
      >
        <span>Página {{ page + 1 }} de {{ totalPages }} ({{ totalElements }} en total)</span>
        <div class="flex gap-2">
          <button type="button" :disabled="page === 0" class="btn-pagination" @click="prevPage">
            Anterior
          </button>
          <button
            type="button"
            :disabled="page >= totalPages - 1"
            class="btn-pagination"
            @click="nextPage"
          >
            Siguiente
          </button>
        </div>
      </div>
    </div>

    <!-- Modal Alta/Edición -->
    <div
      v-if="modalOpen"
      class="fixed inset-0 z-10 flex items-center justify-center bg-black/50 p-4"
      role="dialog"
      aria-modal="true"
      aria-labelledby="modal-title"
    >
      <div class="w-full max-w-md rounded-2xl bg-white p-6 shadow-xl">
        <h2 id="modal-title" class="text-lg font-semibold text-slate-800">{{ modalTitle }}</h2>
        <form @submit.prevent="submitForm" class="mt-4 space-y-4">
          <div>
            <label for="form-codigo-interno" class="label-app">Código interno *</label>
            <input
              id="form-codigo-interno"
              v-model="form.codigoInterno"
              type="text"
              required
              class="input-app"
              maxlength="50"
            />
          </div>
          <div>
            <label for="form-codigo-externo" class="label-app">Código externo *</label>
            <input
              id="form-codigo-externo"
              v-model="form.codigoExterno"
              type="text"
              required
              class="input-app"
              maxlength="50"
            />
          </div>
          <div v-if="!isEditing">
            <label for="form-estado" class="label-app">Estado inicial</label>
            <select id="form-estado" v-model="form.estadoInicial" class="input-app">
              <option v-for="e in ESTADOS" :key="e.value" :value="e.value">{{ e.label }}</option>
            </select>
          </div>
          <AppFormError v-if="formError" :message="formError" />
          <div class="flex justify-end gap-3 pt-4">
            <button type="button" class="btn-secondary" @click="closeModal">
              <ActionIcons name="x-mark" class="h-4 w-4" />
              <span>Cancelar</span>
            </button>
            <button type="submit" :disabled="submitting" class="btn-primary">
              <ActionIcons name="check" class="h-4 w-4" />
              <span>{{ submitting ? 'Guardando...' : 'Guardar' }}</span>
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Modal QR -->
    <div
      v-if="qrModalOpen"
      class="fixed inset-0 z-20 flex items-center justify-center bg-black/50 p-4"
      role="dialog"
      aria-modal="true"
      aria-labelledby="qr-modal-title"
    >
      <div class="rounded-2xl bg-white p-6 shadow-xl">
        <h2 id="qr-modal-title" class="text-lg font-semibold text-slate-800">Código QR</h2>
        <p class="mt-1 text-sm text-slate-600">Código externo: {{ qrCodigoExterno }}</p>
        <div class="mt-4 flex flex-col items-center gap-4">
          <img
            v-if="qrImageSrc"
            :src="qrImageSrc"
            alt="QR del código externo"
            class="h-[150px] w-[150px] rounded-lg border border-slate-200"
          />
          <a
            :href="qrImageSrc"
            target="_blank"
            rel="noopener noreferrer"
            class="btn-secondary"
          >
            Abrir / descargar imagen
          </a>
        </div>
        <div class="mt-4 flex justify-end">
          <button type="button" class="btn-secondary" @click="closeQrModal">Cerrar</button>
        </div>
      </div>
    </div>
  </div>
</template>
