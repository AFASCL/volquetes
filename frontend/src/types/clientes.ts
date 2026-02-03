/**
 * Tipos alineados con memory-bank/04-api-documentation.md (Clientes).
 */

export type ClienteTipo = 'COMUN' | 'ABONO'

export interface ClienteRequest {
  nombre: string
  telefono?: string
  email?: string
  direccionPrincipal?: string
  tipo: ClienteTipo
}

export interface ClienteResponse {
  id: number
  nombre: string
  telefono?: string
  email?: string
  direccionPrincipal?: string
  tipo: ClienteTipo
}

export interface ClientesPageResponse {
  content: ClienteResponse[]
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
