/**
 * Servicio API de Clientes. Contrato: memory-bank/04-api-documentation.md
 */
import { apiRequest } from './client'
import type {
  ClienteRequest,
  ClienteResponse,
  ClienteSelectorItem,
  ClientesPageResponse,
  ErrorResponse,
} from '@/types/clientes'

function getErrorMessage(status: number, data: unknown): string {
  const err = data as ErrorResponse | undefined
  const parts: string[] = []
  if (err?.message) parts.push(err.message)
  if (err?.details?.length) parts.push(err.details.join('. '))
  if (parts.length) return parts.join(' — ')
  switch (status) {
    case 400:
      return 'Datos inválidos.'
    case 404:
      return 'Cliente no encontrado.'
    case 409:
      return 'No se puede eliminar: tiene pedidos asociados.'
    case 422:
      return 'Error de validación.'
    case 500:
      return 'Error del servidor. Intentá de nuevo.'
    default:
      return `Error ${status}.`
  }
}

export async function listClientes(params?: {
  page?: number
  size?: number
  sort?: string
}): Promise<ClientesPageResponse> {
  const search = new URLSearchParams()
  if (params?.page != null) search.set('page', String(params.page))
  if (params?.size != null) search.set('size', String(params.size))
  if (params?.sort) search.set('sort', params.sort)
  const qs = search.toString()
  const path = `/api/clientes${qs ? '?' + qs : ''}`
  const { data, status } = await apiRequest<ClientesPageResponse>(path, { method: 'GET' })
  if (status >= 400) throw new Error(getErrorMessage(status, data))
  return data as ClientesPageResponse
}

export async function listClientesSelector(): Promise<ClienteSelectorItem[]> {
  const { data, status } = await apiRequest<ClienteSelectorItem[]>('/api/clientes/selector', {
    method: 'GET',
  })
  if (status >= 400) throw new Error(getErrorMessage(status, data))
  return (Array.isArray(data) ? data : []) as ClienteSelectorItem[]
}

export async function getCliente(id: number): Promise<ClienteResponse> {
  const { data, status } = await apiRequest<ClienteResponse>(`/api/clientes/${id}`, {
    method: 'GET',
  })
  if (status >= 400) throw new Error(getErrorMessage(status, data))
  return data as ClienteResponse
}

export async function createCliente(body: ClienteRequest): Promise<ClienteResponse> {
  const { data, status } = await apiRequest<ClienteResponse>('/api/clientes', {
    method: 'POST',
    body: JSON.stringify(body),
  })
  if (status >= 400) throw new Error(getErrorMessage(status, data))
  return data as ClienteResponse
}

export async function updateCliente(id: number, body: ClienteRequest): Promise<ClienteResponse> {
  const { data, status } = await apiRequest<ClienteResponse>(`/api/clientes/${id}`, {
    method: 'PUT',
    body: JSON.stringify(body),
  })
  if (status >= 400) throw new Error(getErrorMessage(status, data))
  return data as ClienteResponse
}

export async function deleteCliente(id: number): Promise<void> {
  const { data, status } = await apiRequest<unknown>(`/api/clientes/${id}`, {
    method: 'DELETE',
  })
  if (status >= 400) throw new Error(getErrorMessage(status, data))
}
