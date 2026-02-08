/**
 * Tipos alineados con memory-bank/04-api-documentation.md (Pedidos).
 */

export type PedidoEstado =
  | 'NUEVO'
  | 'ASIGNADO'
  | 'ENTREGADO'
  | 'RETIRADO'
  | 'CERRADO'
  | 'CANCELADO'

export interface PedidoRequest {
  clienteId: number
  volqueteId: number
  direccionEntrega: string
  fechaEntregaPrevista?: string
}

/** Request para PATCH /api/pedidos/{id}/estado. Para ASIGNADO son obligatorios choferId, camionId, fechaEntregaPrevista. */
export interface PedidoEstadoRequest {
  estado: PedidoEstado
  choferId?: number
  camionId?: number
  fechaEntregaPrevista?: string
}

export interface PedidoResponse {
  id: number
  clienteId: number
  volqueteId: number
  direccionEntrega: string
  estado: PedidoEstado
  fechaCreacion: string
  fechaEntregaPrevista?: string
  fechaEntregaReal?: string
  fechaRetiroReal?: string
  choferId?: number
  camionId?: number
  clienteNombre?: string
  volqueteCodigoInterno?: string
  choferNombre?: string
  camionPatente?: string
}

export interface PedidosPageResponse {
  content: PedidoResponse[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export interface ErrorResponse {
  code?: string
  message?: string
  details?: string[]
  timestamp?: string
}
