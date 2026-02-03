<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import * as clientesApi from '@/services/api/clientes'
import type { ClienteResponse, ClienteRequest, ClienteTipo } from '@/types/clientes'

const TIPOS: { value: ClienteTipo; label: string }[] = [
  { value: 'COMUN', label: 'Común' },
  { value: 'ABONO', label: 'Abono' },
]

const list = ref<ClienteResponse[]>([])
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

const form = ref<ClienteRequest>({
  nombre: '',
  telefono: '',
  email: '',
  direccionPrincipal: '',
  tipo: 'COMUN',
})

const isEditing = computed(() => editingId.value != null)
const modalTitle = computed(() => (isEditing.value ? 'Editar cliente' : 'Nuevo cliente'))

function clearForm() {
  form.value = {
    nombre: '',
    telefono: '',
    email: '',
    direccionPrincipal: '',
    tipo: 'COMUN',
  }
  editingId.value = null
  formError.value = ''
}

function setFeedback(type: 'success' | 'error', message: string) {
  feedback.value = { type, message }
  setTimeout(() => { feedback.value = null }, 5000)
}

function validateForm(): boolean {
  const n = form.value.nombre?.trim()
  if (!n) {
    formError.value = 'El nombre es obligatorio.'
    return false
  }
  const email = form.value.email?.trim()
  if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    formError.value = 'El email no tiene formato válido.'
    return false
  }
  formError.value = ''
  return true
}

async function loadList() {
  loading.value = true
  error.value = ''
  try {
    const res = await clientesApi.listClientes({ page: page.value, size: size.value })
    list.value = res.content
    totalElements.value = res.totalElements
    totalPages.value = res.totalPages
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Error al cargar clientes.'
  } finally {
    loading.value = false
  }
}

function openCreate() {
  clearForm()
  modalOpen.value = true
}

function openEdit(row: ClienteResponse) {
  editingId.value = row.id
  form.value = {
    nombre: row.nombre,
    telefono: row.telefono ?? '',
    email: row.email ?? '',
    direccionPrincipal: row.direccionPrincipal ?? '',
    tipo: row.tipo,
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
    const body: ClienteRequest = {
      nombre: form.value.nombre.trim(),
      telefono: form.value.telefono?.trim() || undefined,
      email: form.value.email?.trim() || undefined,
      direccionPrincipal: form.value.direccionPrincipal?.trim() || undefined,
      tipo: form.value.tipo,
    }
    if (editingId.value != null) {
      await clientesApi.updateCliente(editingId.value, body)
      setFeedback('success', 'Cliente actualizado.')
    } else {
      await clientesApi.createCliente(body)
      setFeedback('success', 'Cliente creado.')
    }
    closeModal()
    await loadList()
  } catch (e) {
    formError.value = e instanceof Error ? e.message : 'Error al guardar.'
  } finally {
    submitting.value = false
  }
}

async function confirmDelete(row: ClienteResponse) {
  if (!window.confirm(`¿Eliminar a "${row.nombre}"?`)) return
  try {
    await clientesApi.deleteCliente(row.id)
    setFeedback('success', 'Cliente eliminado.')
    await loadList()
  } catch (e) {
    setFeedback('error', e instanceof Error ? e.message : 'Error al eliminar.')
  }
}

onMounted(() => loadList())
</script>

<template>
  <div class="space-y-4">
    <div class="flex flex-wrap items-center justify-between gap-4">
      <h1 class="text-2xl font-semibold text-gray-800">Clientes</h1>
      <button
        type="button"
        class="rounded bg-blue-600 px-4 py-2 text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
        @click="openCreate"
      >
        Nuevo
      </button>
    </div>

    <div
      v-if="feedback"
      :class="[
        'rounded border p-3 text-sm',
        feedback.type === 'success'
          ? 'border-green-200 bg-green-50 text-green-800'
          : 'border-red-200 bg-red-50 text-red-800',
      ]"
      role="alert"
    >
      {{ feedback.message }}
    </div>

    <!-- Loading -->
    <div v-if="loading" class="flex justify-center py-12">
      <p class="text-gray-500">Cargando...</p>
    </div>

    <!-- Error + retry -->
    <div v-else-if="error" class="rounded border border-red-200 bg-red-50 p-6 text-center">
      <p class="text-red-800">{{ error }}</p>
      <button
        type="button"
        class="mt-4 rounded bg-red-600 px-4 py-2 text-white hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500"
        @click="loadList"
      >
        Reintentar
      </button>
    </div>

    <!-- Empty -->
    <div
      v-else-if="list.length === 0"
      class="rounded border border-gray-200 bg-white p-12 text-center text-gray-600"
    >
      <p>No hay clientes. Creá uno con el botón Nuevo.</p>
    </div>

    <!-- Table -->
    <div v-else class="overflow-x-auto rounded border border-gray-200 bg-white">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th scope="col" class="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">
              Nombre
            </th>
            <th scope="col" class="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">
              Tipo
            </th>
            <th scope="col" class="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">
              Teléfono
            </th>
            <th scope="col" class="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">
              Email
            </th>
            <th scope="col" class="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">
              Dirección
            </th>
            <th scope="col" class="relative px-4 py-3">
              <span class="sr-only">Acciones</span>
            </th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-200 bg-white">
          <tr v-for="row in list" :key="row.id" class="hover:bg-gray-50">
            <td class="whitespace-nowrap px-4 py-3 text-sm text-gray-900">{{ row.nombre }}</td>
            <td class="whitespace-nowrap px-4 py-3 text-sm text-gray-600">{{ row.tipo }}</td>
            <td class="whitespace-nowrap px-4 py-3 text-sm text-gray-600">{{ row.telefono ?? '—' }}</td>
            <td class="whitespace-nowrap px-4 py-3 text-sm text-gray-600">{{ row.email ?? '—' }}</td>
            <td class="max-w-xs truncate px-4 py-3 text-sm text-gray-600">
              {{ row.direccionPrincipal ?? '—' }}
            </td>
            <td class="whitespace-nowrap px-4 py-3 text-right text-sm">
              <button
                type="button"
                class="text-blue-600 hover:text-blue-800 focus:outline-none focus:ring-2 focus:ring-blue-500 rounded mr-2"
                aria-label="Editar cliente"
                @click="openEdit(row)"
              >
                Editar
              </button>
              <button
                type="button"
                class="text-red-600 hover:text-red-800 focus:outline-none focus:ring-2 focus:ring-red-500 rounded"
                aria-label="Eliminar cliente"
                @click="confirmDelete(row)"
              >
                Eliminar
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      <div
        v-if="totalPages > 1"
        class="flex flex-wrap items-center justify-between gap-2 border-t px-4 py-2 text-sm text-gray-500"
      >
        <span>Página {{ page + 1 }} de {{ totalPages }} ({{ totalElements }} en total)</span>
        <div class="flex gap-2">
          <button
            type="button"
            :disabled="page === 0"
            class="rounded border border-gray-300 bg-white px-3 py-1 disabled:opacity-50 hover:bg-gray-50"
            @click="page--; loadList()"
          >
            Anterior
          </button>
          <button
            type="button"
            :disabled="page >= totalPages - 1"
            class="rounded border border-gray-300 bg-white px-3 py-1 disabled:opacity-50 hover:bg-gray-50"
            @click="page++; loadList()"
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
      <div class="w-full max-w-md rounded-lg bg-white p-6 shadow-xl">
        <h2 id="modal-title" class="text-lg font-semibold text-gray-800">{{ modalTitle }}</h2>
        <form @submit.prevent="submitForm" class="mt-4 space-y-4">
          <div>
            <label for="form-nombre" class="block text-sm font-medium text-gray-700">Nombre *</label>
            <input
              id="form-nombre"
              v-model="form.nombre"
              type="text"
              required
              class="mt-1 block w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
              maxlength="255"
            />
          </div>
          <div>
            <label for="form-tipo" class="block text-sm font-medium text-gray-700">Tipo</label>
            <select
              id="form-tipo"
              v-model="form.tipo"
              class="mt-1 block w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
            >
              <option v-for="t in TIPOS" :key="t.value" :value="t.value">{{ t.label }}</option>
            </select>
          </div>
          <div>
            <label for="form-telefono" class="block text-sm font-medium text-gray-700">Teléfono</label>
            <input
              id="form-telefono"
              v-model="form.telefono"
              type="text"
              class="mt-1 block w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
              maxlength="50"
            />
          </div>
          <div>
            <label for="form-email" class="block text-sm font-medium text-gray-700">Email</label>
            <input
              id="form-email"
              v-model="form.email"
              type="email"
              class="mt-1 block w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
              maxlength="255"
            />
          </div>
          <div>
            <label for="form-direccion" class="block text-sm font-medium text-gray-700"
              >Dirección principal</label
            >
            <input
              id="form-direccion"
              v-model="form.direccionPrincipal"
              type="text"
              class="mt-1 block w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
              maxlength="500"
            />
          </div>
          <p v-if="formError" class="text-sm text-red-600">{{ formError }}</p>
          <div class="flex justify-end gap-2 pt-2">
            <button
              type="button"
              class="rounded border border-gray-300 bg-white px-4 py-2 text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500"
              @click="closeModal"
            >
              Cancelar
            </button>
            <button
              type="submit"
              :disabled="submitting"
              class="rounded bg-blue-600 px-4 py-2 text-white hover:bg-blue-700 disabled:opacity-50 focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              {{ submitting ? 'Guardando...' : 'Guardar' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
