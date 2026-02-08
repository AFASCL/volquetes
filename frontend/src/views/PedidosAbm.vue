<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useAuthStore } from '@/stores/auth'
import * as pedidosApi from '@/services/api/pedidos'
import * as clientesApi from '@/services/api/clientes'
import * as volquetesApi from '@/services/api/volquetes'
import * as choferesApi from '@/services/api/choferes'
import * as camionesApi from '@/services/api/camiones'
import ActionIcons from '@/components/icons/ActionIcons.vue'
import AppAlert from '@/components/AppAlert.vue'
import AppEmptyState from '@/components/AppEmptyState.vue'
import AppErrorState from '@/components/AppErrorState.vue'
import AppFormError from '@/components/AppFormError.vue'
import type {
  PedidoResponse,
  PedidoRequest,
  PedidoEstado,
  PedidoEstadoRequest,
} from '@/types/pedidos'
import type { ClienteSelectorItem } from '@/types/clientes'
import type { VolqueteResponse } from '@/types/volquetes'
import type { ChoferSelectorItem } from '@/types/choferes'
import type { CamionSelectorItem } from '@/types/camiones'

const auth = useAuthStore()

const ESTADOS_PEDIDO: { value: PedidoEstado; label: string }[] = [
  { value: 'NUEVO', label: 'Nuevo' },
  { value: 'ASIGNADO', label: 'Asignado' },
  { value: 'ENTREGADO', label: 'Entregado' },
  { value: 'RETIRADO', label: 'Retirado' },
  { value: 'CERRADO', label: 'Cerrado' },
  { value: 'CANCELADO', label: 'Cancelado' },
]

/** Transiciones permitidas (memory-bank 04). CERRADO y CANCELADO sin siguientes. */
const SIGUIENTES_ESTADO: Record<PedidoEstado, PedidoEstado[]> = {
  NUEVO: ['ASIGNADO', 'CANCELADO'],
  ASIGNADO: ['ENTREGADO', 'CANCELADO'],
  ENTREGADO: ['RETIRADO'],
  RETIRADO: ['CERRADO'],
  CERRADO: [],
  CANCELADO: [],
}

function estadoLabel(estado: PedidoEstado): string {
  return ESTADOS_PEDIDO.find((e) => e.value === estado)?.label ?? estado
}

function getSiguientesEstados(estado: PedidoEstado): PedidoEstado[] {
  return SIGUIENTES_ESTADO[estado] ?? []
}

const list = ref<PedidoResponse[]>([])
const totalElements = ref(0)
const totalPages = ref(0)
const page = ref(0)
const size = ref(20)
const loading = ref(false)
const error = ref('')
const feedback = ref<{ type: 'success' | 'error'; message: string } | null>(null)

const filterEstado = ref<PedidoEstado | ''>('')
const filterClienteId = ref<number | ''>('')
const filterVolqueteId = ref<number | ''>('')

const clientesSelector = ref<ClienteSelectorItem[]>([])
const volquetesList = ref<VolqueteResponse[]>([])
const choferesSelector = ref<ChoferSelectorItem[]>([])
const camionesSelector = ref<CamionSelectorItem[]>([])

const modalOpen = ref(false)
const editingId = ref<number | null>(null)
const submitting = ref(false)
const formError = ref('')
const form = ref<PedidoRequest>({
  clienteId: 0,
  volqueteId: 0,
  direccionEntrega: '',
  fechaEntregaPrevista: undefined,
})

const detailModalOpen = ref(false)
const detailPedido = ref<PedidoResponse | null>(null)
const detailLoading = ref(false)

const estadoModalOpen = ref(false)
const estadoModalPedido = ref<{ id: number; estado: PedidoEstado } | null>(null)
const estadoTarget = ref<PedidoEstado | null>(null)
const estadoAsignadoForm = ref({
  choferId: '' as number | '',
  camionId: '' as number | '',
  fechaEntregaPrevista: '',
})
const estadoSubmitting = ref(false)
const estadoFormError = ref('')

const isEditing = computed(() => editingId.value != null)
const modalTitle = computed(() => (isEditing.value ? 'Editar pedido' : 'Nuevo pedido'))
const siguienteEstados = computed(() => {
  const p = estadoModalPedido.value
  return p ? getSiguientesEstados(p.estado) : []
})
const needsAsignadoForm = computed(() => estadoTarget.value === 'ASIGNADO')

function setFeedback(type: 'success' | 'error', message: string) {
  feedback.value = { type, message }
  setTimeout(() => { feedback.value = null }, 5000)
}

async function loadClientesSelector() {
  try {
    clientesSelector.value = await clientesApi.listClientesSelector()
  } catch {
    clientesSelector.value = []
  }
}

async function loadVolquetesForSelector() {
  try {
    const res = await volquetesApi.listVolquetes({ size: 500 })
    volquetesList.value = res.content
  } catch {
    volquetesList.value = []
  }
}

async function loadChoferesSelector() {
  try {
    choferesSelector.value = await choferesApi.listChoferesSelector()
  } catch {
    choferesSelector.value = []
  }
}

async function loadCamionesSelector() {
  try {
    camionesSelector.value = await camionesApi.listCamionesSelector()
  } catch {
    camionesSelector.value = []
  }
}

async function loadList() {
  loading.value = true
  error.value = ''
  try {
    const res = await pedidosApi.listPedidos({
      page: page.value,
      size: size.value,
      estado: filterEstado.value || undefined,
      clienteId: filterClienteId.value === '' ? undefined : (filterClienteId.value as number),
      volqueteId: filterVolqueteId.value === '' ? undefined : (filterVolqueteId.value as number),
    })
    list.value = res.content
    totalElements.value = res.totalElements
    totalPages.value = res.totalPages
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Error al cargar pedidos.'
  } finally {
    loading.value = false
  }
}

watch([filterEstado, filterClienteId, filterVolqueteId], () => {
  page.value = 0
  loadList()
})

function clearForm() {
  form.value = {
    clienteId: 0,
    volqueteId: 0,
    direccionEntrega: '',
    fechaEntregaPrevista: undefined,
  }
  editingId.value = null
  formError.value = ''
}

function validateForm(): boolean {
  if (!form.value.clienteId) {
    formError.value = 'Seleccioná un cliente.'
    return false
  }
  if (!form.value.volqueteId) {
    formError.value = 'Seleccioná un volquete.'
    return false
  }
  if (!form.value.direccionEntrega?.trim()) {
    formError.value = 'La dirección de entrega es obligatoria.'
    return false
  }
  formError.value = ''
  return true
}

function openCreate() {
  clearForm()
  modalOpen.value = true
}

function openEdit(row: PedidoResponse) {
  editingId.value = row.id
  form.value = {
    clienteId: row.clienteId,
    volqueteId: row.volqueteId,
    direccionEntrega: row.direccionEntrega,
    fechaEntregaPrevista: row.fechaEntregaPrevista ? toDatetimeLocal(row.fechaEntregaPrevista) : undefined,
  }
  formError.value = ''
  modalOpen.value = true
}

function closeModal() {
  modalOpen.value = false
  clearForm()
}

async function submitForm() {
  if (!validateForm()) return
  submitting.value = true
  formError.value = ''
  try {
    const body: PedidoRequest = {
      clienteId: form.value.clienteId,
      volqueteId: form.value.volqueteId,
      direccionEntrega: form.value.direccionEntrega.trim(),
      fechaEntregaPrevista: form.value.fechaEntregaPrevista?.trim()
        ? new Date(form.value.fechaEntregaPrevista.trim()).toISOString()
        : undefined,
    }
    if (editingId.value != null) {
      await pedidosApi.updatePedido(editingId.value, body)
      setFeedback('success', 'Pedido actualizado.')
    } else {
      await pedidosApi.createPedido(body)
      setFeedback('success', 'Pedido creado.')
    }
    closeModal()
    await loadList()
  } catch (e) {
    formError.value = e instanceof Error ? e.message : 'Error al guardar.'
  } finally {
    submitting.value = false
  }
}

async function openDetail(row: PedidoResponse) {
  detailPedido.value = null
  detailLoading.value = true
  detailModalOpen.value = true
  try {
    detailPedido.value = await pedidosApi.getPedido(row.id)
  } catch (e) {
    setFeedback('error', e instanceof Error ? e.message : 'Error al cargar detalle.')
    detailModalOpen.value = false
  } finally {
    detailLoading.value = false
  }
}

function closeDetailModal() {
  detailModalOpen.value = false
  detailPedido.value = null
}

function openEstadoModal(pedido: PedidoResponse) {
  estadoModalPedido.value = { id: pedido.id, estado: pedido.estado }
  estadoTarget.value = null
  estadoAsignadoForm.value = { choferId: '', camionId: '', fechaEntregaPrevista: '' }
  estadoFormError.value = ''
  estadoModalOpen.value = true
  if (getSiguientesEstados(pedido.estado).includes('ASIGNADO')) {
    loadChoferesSelector()
    loadCamionesSelector()
  }
}

function closeEstadoModal() {
  estadoModalOpen.value = false
  estadoModalPedido.value = null
  estadoTarget.value = null
  estadoFormError.value = ''
}

function setEstadoTarget(estado: PedidoEstado | null) {
  estadoTarget.value = estado
  estadoFormError.value = ''
  if (estado === 'ASIGNADO') {
    estadoAsignadoForm.value = { choferId: '', camionId: '', fechaEntregaPrevista: '' }
    loadChoferesSelector()
    loadCamionesSelector()
  }
}

function onEstadoTargetSelect(ev: Event) {
  const v = (ev.target as HTMLSelectElement).value
  setEstadoTarget(v ? (v as PedidoEstado) : null)
}

function validateEstadoForm(): boolean {
  if (estadoTarget.value !== 'ASIGNADO') return true
  if (!estadoAsignadoForm.value.choferId) {
    estadoFormError.value = 'Seleccioná un chofer.'
    return false
  }
  if (!estadoAsignadoForm.value.camionId) {
    estadoFormError.value = 'Seleccioná un camión.'
    return false
  }
  if (!estadoAsignadoForm.value.fechaEntregaPrevista?.trim()) {
    estadoFormError.value = 'La fecha/hora de entrega prevista es obligatoria.'
    return false
  }
  estadoFormError.value = ''
  return true
}

async function submitEstado() {
  const ped = estadoModalPedido.value
  const target = estadoTarget.value
  if (!ped || !target) return
  if (!validateEstadoForm()) return

  estadoSubmitting.value = true
  estadoFormError.value = ''
  try {
    const body: PedidoEstadoRequest = {
      estado: target,
    }
    if (target === 'ASIGNADO') {
      body.choferId = estadoAsignadoForm.value.choferId as number
      body.camionId = estadoAsignadoForm.value.camionId as number
      const fecha = estadoAsignadoForm.value.fechaEntregaPrevista.trim()
      body.fechaEntregaPrevista = new Date(fecha).toISOString()
    }
    await pedidosApi.cambiarEstado(ped.id, body)
    setFeedback('success', `Estado actualizado a ${estadoLabel(target)}.`)
    closeEstadoModal()
    await loadList()
    if (detailModalOpen.value && detailPedido.value?.id === ped.id) {
      detailPedido.value = await pedidosApi.getPedido(ped.id)
    }
  } catch (e) {
    estadoFormError.value = e instanceof Error ? e.message : 'Error al cambiar estado.'
  } finally {
    estadoSubmitting.value = false
  }
}

function toDatetimeLocal(iso?: string): string {
  if (!iso) return ''
  const d = new Date(iso)
  if (isNaN(d.getTime())) return ''
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`
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

onMounted(() => {
  loadClientesSelector()
  loadVolquetesForSelector()
  loadList()
})
</script>

<template>
  <div class="space-y-4">
    <div class="flex flex-wrap items-center justify-between gap-4">
      <h1 class="page-title-app">Pedidos</h1>
      <button
        v-if="auth.hasRoleAdmin"
        type="button"
        class="btn-primary"
        title="Nuevo pedido"
        @click="openCreate"
      >
        <ActionIcons name="plus" class="h-5 w-5" />
        <span>Nuevo</span>
      </button>
    </div>

    <!-- Filtros -->
    <div class="filter-bar-app">
      <div class="flex items-center gap-2">
        <label for="filter-estado" class="label-app">Estado</label>
        <select id="filter-estado" v-model="filterEstado" class="input-app-sm">
          <option value="">Todos</option>
          <option v-for="e in ESTADOS_PEDIDO" :key="e.value" :value="e.value">
            {{ e.label }}
          </option>
        </select>
      </div>
      <div class="flex items-center gap-2">
        <label for="filter-cliente" class="label-app">Cliente</label>
        <select id="filter-cliente" v-model="filterClienteId" class="input-app-sm">
          <option value="">Todos</option>
          <option v-for="c in clientesSelector" :key="c.id" :value="c.id">
            {{ c.nombre }}
          </option>
        </select>
      </div>
      <div class="flex items-center gap-2">
        <label for="filter-volquete" class="label-app">Volquete</label>
        <select id="filter-volquete" v-model="filterVolqueteId" class="input-app-sm">
          <option value="">Todos</option>
          <option v-for="v in volquetesList" :key="v.id" :value="v.id">
            {{ v.codigoInterno }} ({{ v.codigoExterno }})
          </option>
        </select>
      </div>
    </div>

    <AppAlert v-if="feedback" :type="feedback.type" :message="feedback.message" />

    <div v-if="loading" class="flex justify-center py-12">
      <p class="text-slate-500">Cargando...</p>
    </div>

    <AppErrorState v-else-if="error" :message="error" @retry="loadList" />

    <AppEmptyState
      v-else-if="list.length === 0"
      message="No hay pedidos con los filtros aplicados."
      :hint="auth.hasRoleAdmin ? 'Creá uno con el botón Nuevo.' : undefined"
    />

    <div v-else class="overflow-x-auto rounded border border-gray-200 bg-white">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th scope="col" class="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">
              Cliente
            </th>
            <th scope="col" class="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">
              Dirección
            </th>
            <th scope="col" class="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">
              Volquete
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
            <td class="whitespace-nowrap px-4 py-3 text-sm text-gray-900">
              {{ row.clienteNombre ?? row.clienteId }}
            </td>
            <td class="max-w-xs truncate px-4 py-3 text-sm text-gray-600">
              {{ row.direccionEntrega || '—' }}
            </td>
            <td class="whitespace-nowrap px-4 py-3 text-sm text-gray-600">
              {{ row.volqueteCodigoInterno ?? row.volqueteId }}
            </td>
            <td class="whitespace-nowrap px-4 py-3 text-sm text-gray-600">
              {{ estadoLabel(row.estado) }}
            </td>
            <td class="whitespace-nowrap px-4 py-3 text-right">
              <div class="inline-flex items-center gap-1">
                <button
                  type="button"
                  class="rounded-lg p-2 text-slate-500 transition-app hover:bg-slate-100 hover:text-slate-700 focus:outline-none focus:ring-2 focus:ring-slate-300"
                  aria-label="Ver detalle del pedido"
                  title="Ver detalle"
                  @click="openDetail(row)"
                >
                  <ActionIcons name="eye" class="h-4 w-4" />
                </button>
                <button
                  v-if="getSiguientesEstados(row.estado).length > 0"
                  type="button"
                  class="rounded-lg p-2 text-slate-500 transition-app hover:bg-green-50 hover:text-green-600 focus:outline-none focus:ring-2 focus:ring-green-200"
                  aria-label="Cambiar estado del pedido"
                  title="Cambiar estado"
                  @click="openEstadoModal(row)"
                >
                  <ActionIcons name="arrow-path" class="h-4 w-4" />
                </button>
                <button
                  v-if="auth.hasRoleAdmin"
                  type="button"
                  class="rounded-lg p-2 text-slate-500 transition-app hover:bg-slate-100 hover:text-slate-700 focus:outline-none focus:ring-2 focus:ring-slate-300"
                  aria-label="Editar pedido"
                  title="Editar"
                  @click="openEdit(row)"
                >
                  <ActionIcons name="pencil" class="h-4 w-4" />
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

    <!-- Modal Alta/Edición (Admin) -->
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
            <label for="form-cliente" class="label-app">Cliente *</label>
            <select id="form-cliente" v-model="form.clienteId" required class="input-app">
              <option :value="0">Seleccionar</option>
              <option v-for="c in clientesSelector" :key="c.id" :value="c.id">
                {{ c.nombre }}
              </option>
            </select>
          </div>
          <div>
            <label for="form-direccion" class="label-app">Dirección de entrega *</label>
            <input
              id="form-direccion"
              v-model="form.direccionEntrega"
              type="text"
              class="input-app"
            />
          </div>
          <div>
            <label for="form-volquete" class="label-app">Volquete *</label>
            <select id="form-volquete" v-model="form.volqueteId" required class="input-app">
              <option :value="0">Seleccionar</option>
              <option v-for="v in volquetesList" :key="v.id" :value="v.id">
                {{ v.codigoInterno }} ({{ v.codigoExterno }})
              </option>
            </select>
          </div>
          <div>
            <label for="form-fecha-prevista" class="label-app"
              >Fecha entrega prevista (opcional)</label
            >
            <input
              id="form-fecha-prevista"
              v-model="form.fechaEntregaPrevista"
              type="datetime-local"
              class="input-app"
            />
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

    <!-- Modal Detalle -->
    <div
      v-if="detailModalOpen"
      class="fixed inset-0 z-20 flex items-center justify-center bg-black/50 p-4"
      role="dialog"
      aria-modal="true"
      aria-labelledby="detail-modal-title"
    >
      <div class="w-full max-w-lg rounded-2xl bg-white p-6 shadow-xl">
        <h2 id="detail-modal-title" class="text-lg font-semibold text-slate-800">
          Detalle del pedido
        </h2>
        <div v-if="detailLoading" class="py-6 text-center text-slate-500">Cargando...</div>
        <template v-else-if="detailPedido">
          <dl class="mt-4 space-y-2 text-sm">
            <div>
              <dt class="font-medium text-slate-500">Cliente</dt>
              <dd class="text-slate-900">{{ detailPedido.clienteNombre ?? detailPedido.clienteId }}</dd>
            </div>
            <div>
              <dt class="font-medium text-slate-500">Dirección de entrega</dt>
              <dd class="text-slate-900">{{ detailPedido.direccionEntrega || '—' }}</dd>
            </div>
            <div>
              <dt class="font-medium text-slate-500">Volquete</dt>
              <dd class="text-slate-900">
                {{ detailPedido.volqueteCodigoInterno ?? detailPedido.volqueteId }}
              </dd>
            </div>
            <div>
              <dt class="font-medium text-slate-500">Estado</dt>
              <dd class="text-slate-900">{{ estadoLabel(detailPedido.estado) }}</dd>
            </div>
            <div v-if="detailPedido.fechaEntregaPrevista">
              <dt class="font-medium text-slate-500">Fecha entrega prevista</dt>
              <dd class="text-slate-900">{{ detailPedido.fechaEntregaPrevista }}</dd>
            </div>
            <div v-if="detailPedido.choferNombre || detailPedido.choferId">
              <dt class="font-medium text-slate-500">Chofer</dt>
              <dd class="text-slate-900">{{ detailPedido.choferNombre ?? detailPedido.choferId }}</dd>
            </div>
            <div v-if="detailPedido.camionPatente || detailPedido.camionId">
              <dt class="font-medium text-slate-500">Camión</dt>
              <dd class="text-slate-900">{{ detailPedido.camionPatente ?? detailPedido.camionId }}</dd>
            </div>
          </dl>
          <div class="mt-4 flex flex-wrap gap-2">
            <button
              v-if="getSiguientesEstados(detailPedido.estado).length > 0"
              type="button"
              class="btn-primary"
              @click="closeDetailModal(); openEstadoModal(detailPedido)"
            >
              Cambiar estado
            </button>
            <button type="button" class="btn-secondary" @click="closeDetailModal">Cerrar</button>
          </div>
        </template>
      </div>
    </div>

    <!-- Modal Cambiar estado -->
    <div
      v-if="estadoModalOpen && estadoModalPedido"
      class="fixed inset-0 z-30 flex items-center justify-center bg-black/50 p-4"
      role="dialog"
      aria-modal="true"
      aria-labelledby="estado-modal-title"
    >
      <div class="w-full max-w-md rounded-2xl bg-white p-6 shadow-xl">
        <h2 id="estado-modal-title" class="text-lg font-semibold text-slate-800">
          Cambiar estado del pedido
        </h2>
        <p class="mt-1 text-sm text-slate-600">
          Estado actual: <strong>{{ estadoLabel(estadoModalPedido.estado) }}</strong>
        </p>
        <div class="mt-4">
          <label for="estado-target" class="label-app">Ingresar próximo estado</label>
          <div v-if="siguienteEstados.length === 0" class="mt-2 helper-app">
            No se puede cambiar el estado (pedido cerrado o cancelado).
          </div>
          <select
            v-else
            id="estado-target"
            :value="estadoTarget ?? ''"
            class="input-app"
            @change="onEstadoTargetSelect($event)"
          >
            <option value="">Seleccionar estado</option>
            <option v-for="s in siguienteEstados" :key="s" :value="s">
              {{ estadoLabel(s) }}
            </option>
          </select>
        </div>

        <!-- Form Asignado (chofer, camión, fecha) -->
        <div
          v-if="needsAsignadoForm"
          class="mt-4 space-y-4 rounded-xl border border-slate-200 bg-slate-50 p-4"
        >
          <p class="label-app">Completar datos de asignación</p>
          <div>
            <label for="estado-chofer" class="label-app">Chofer *</label>
            <select id="estado-chofer" v-model="estadoAsignadoForm.choferId" required class="input-app">
              <option value="">Seleccionar</option>
              <option v-for="ch in choferesSelector" :key="ch.id" :value="ch.id">
                {{ ch.nombre }}
              </option>
            </select>
          </div>
          <div>
            <label for="estado-camion" class="label-app">Camión *</label>
            <select id="estado-camion" v-model="estadoAsignadoForm.camionId" required class="input-app">
              <option value="">Seleccionar</option>
              <option v-for="ca in camionesSelector" :key="ca.id" :value="ca.id">
                {{ ca.patente }}
              </option>
            </select>
          </div>
          <div>
            <label for="estado-fecha" class="label-app">Fecha/hora entrega prevista *</label>
            <input
              id="estado-fecha"
              v-model="estadoAsignadoForm.fechaEntregaPrevista"
              type="datetime-local"
              required
              class="input-app"
            />
          </div>
        </div>

        <AppFormError v-if="estadoFormError" class="mt-3" :message="estadoFormError" />
        <div class="mt-4 flex justify-end gap-3">
          <button type="button" class="btn-secondary" @click="closeEstadoModal">
            <ActionIcons name="x-mark" class="h-4 w-4" />
            <span>Cancelar</span>
          </button>
          <button
            type="button"
            :disabled="!estadoTarget || estadoSubmitting"
            class="btn-primary"
            @click="submitEstado"
          >
            <ActionIcons name="arrow-path" class="h-4 w-4" />
            <span>{{ estadoSubmitting ? 'Enviando...' : 'Cambiar estado' }}</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
