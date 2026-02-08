/**
 * Servicio API de Pedidos. Contrato: memory-bank/04-api-documentation.md
 */
import { apiRequest } from './client'
import type {
  PedidoRequest,
  PedidoResponse,
  PedidosPageResponse,
  PedidoEstado,
  PedidoEstadoRequest,
  ErrorResponse,
} from '@/types/pedidos'

function getErrorMessage(status: number, data: unknown): string {
  const err = data as ErrorResponse | undefined
  const parts: string[] = []
  if (err?.message) parts.push(err.message)
  if (err?.details?.length) parts.push(err.details.join('. '))
  if (parts.length) return parts.join(' — ')
  switch (status) {
    case 400:
      return 'Parámetros o datos inválidos.'
    case 404:
      return 'Pedido, cliente o volquete no encontrado.'
    case 409:
      return 'El volquete ya está en uso en otro pedido activo.'
    case 422:
      return (
        (err?.message as string) ||
        'Transición no permitida o datos faltantes (para Asignado: chofer, camión y fecha).'
      )
    case 500:
      return 'Error del servidor. Intentá de nuevo.'
    default:
      return `Error ${status}.`
  }
}

export async function listPedidos(params?: {
  page?: number
  size?: number
  sort?: string
  estado?: PedidoEstado
  clienteId?: number
  volqueteId?: number
}): Promise<PedidosPageResponse> {
  const search = new URLSearchParams()
  if (params?.page != null) search.set('page', String(params.page))
  if (params?.size != null) search.set('size', String(params.size))
  if (params?.sort) search.set('sort', params.sort)
  if (params?.estado) search.set('estado', params.estado)
  if (params?.clienteId != null) search.set('clienteId', String(params.clienteId))
  if (params?.volqueteId != null) search.set('volqueteId', String(params.volqueteId))
  const qs = search.toString()
  const path = `/api/pedidos${qs ? '?' + qs : ''}`
  const { data, status } = await apiRequest<PedidosPageResponse>(path, { method: 'GET' })
  if (status >= 400) throw new Error(getErrorMessage(status, data))
  return data as PedidosPageResponse
}

export async function getPedido(id: number): Promise<PedidoResponse> {
  const { data, status } = await apiRequest<PedidoResponse>(`/api/pedidos/${id}`, {
    method: 'GET',
  })
  if (status >= 400) throw new Error(getErrorMessage(status, data))
  return data as PedidoResponse
}

export async function createPedido(body: PedidoRequest): Promise<PedidoResponse> {
  const { data, status } = await apiRequest<PedidoResponse>('/api/pedidos', {
    method: 'POST',
    body: JSON.stringify(body),
  })
  if (status >= 400) throw new Error(getErrorMessage(status, data))
  return data as PedidoResponse
}

export async function updatePedido(id: number, body: PedidoRequest): Promise<PedidoResponse> {
  const { data, status } = await apiRequest<PedidoResponse>(`/api/pedidos/${id}`, {
    method: 'PUT',
    body: JSON.stringify(body),
  })
  if (status >= 400) throw new Error(getErrorMessage(status, data))
  return data as PedidoResponse
}

export async function cambiarEstado(
  id: number,
  body: PedidoEstadoRequest
): Promise<PedidoResponse> {
  const { data, status } = await apiRequest<PedidoResponse>(`/api/pedidos/${id}/estado`, {
    method: 'PATCH',
    body: JSON.stringify(body),
  })
  if (status >= 400) throw new Error(getErrorMessage(status, data))
  return data as PedidoResponse
}
