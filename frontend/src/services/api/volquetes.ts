/**
 * Servicio API de Volquetes. Contrato: memory-bank/04-api-documentation.md
 */
import { apiRequest, apiRequestBlob } from './client'
import type {
  VolqueteRequest,
  VolqueteResponse,
  VolquetesPageResponse,
  ErrorResponse,
  VolqueteEstado,
} from '@/types/volquetes'

function getErrorMessage(status: number, data: unknown): string {
  const err = data as ErrorResponse | undefined
  const parts: string[] = []
  if (err?.message) parts.push(err.message)
  if (err?.details?.length) parts.push(err.details.join('. '))
  if (parts.length) return parts.join(' — ')
  switch (status) {
    case 400:
      return 'Parámetros inválidos (ej. estado no permitido).'
    case 404:
      return 'Volquete no encontrado.'
    case 409:
      return 'Ya existe un volquete con ese código interno o externo.'
    case 422:
      return 'Error de validación.'
    case 500:
      return 'Error del servidor. Intentá de nuevo.'
    default:
      return `Error ${status}.`
  }
}

export async function listVolquetes(params?: {
  page?: number
  size?: number
  sort?: string
  estado?: VolqueteEstado
}): Promise<VolquetesPageResponse> {
  const search = new URLSearchParams()
  if (params?.page != null) search.set('page', String(params.page))
  if (params?.size != null) search.set('size', String(params.size))
  if (params?.sort) search.set('sort', params.sort)
  if (params?.estado) search.set('estado', params.estado)
  const qs = search.toString()
  const path = `/api/volquetes${qs ? '?' + qs : ''}`
  const { data, status } = await apiRequest<VolquetesPageResponse>(path, { method: 'GET' })
  if (status >= 400) throw new Error(getErrorMessage(status, data))
  return data as VolquetesPageResponse
}

export async function getVolquete(id: number): Promise<VolqueteResponse> {
  const { data, status } = await apiRequest<VolqueteResponse>(`/api/volquetes/${id}`, {
    method: 'GET',
  })
  if (status >= 400) throw new Error(getErrorMessage(status, data))
  return data as VolqueteResponse
}

export async function createVolquete(body: VolqueteRequest): Promise<VolqueteResponse> {
  const { data, status } = await apiRequest<VolqueteResponse>('/api/volquetes', {
    method: 'POST',
    body: JSON.stringify(body),
  })
  if (status >= 400) throw new Error(getErrorMessage(status, data))
  return data as VolqueteResponse
}

export async function updateVolquete(id: number, body: VolqueteRequest): Promise<VolqueteResponse> {
  const { data, status } = await apiRequest<VolqueteResponse>(`/api/volquetes/${id}`, {
    method: 'PUT',
    body: JSON.stringify(body),
  })
  if (status >= 400) throw new Error(getErrorMessage(status, data))
  return data as VolqueteResponse
}

export async function deleteVolquete(id: number): Promise<void> {
  const { data, status } = await apiRequest<unknown>(`/api/volquetes/${id}`, {
    method: 'DELETE',
  })
  if (status >= 400) throw new Error(getErrorMessage(status, data))
}

/**
 * Exportar inventario completo en CSV (GET /api/volquetes/export).
 * Devuelve el Blob para que la vista dispare la descarga con nombre inventario-volquetes.csv.
 */
export async function exportarInventario(): Promise<Blob> {
  const { data } = await apiRequestBlob('/api/volquetes/export', { method: 'GET' })
  return data
}
