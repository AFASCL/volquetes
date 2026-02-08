/**
 * Tipos alineados con memory-bank/04-api-documentation.md (Volquetes).
 */

export type VolqueteEstado = 'DISPONIBLE' | 'EN_CLIENTE' | 'EN_TRANSITO' | 'FUERA_DE_SERVICIO'

export interface VolqueteRequest {
  codigoInterno: string
  codigoExterno: string
  estadoInicial?: VolqueteEstado
}

export interface VolqueteResponse {
  id: number
  codigoInterno: string
  codigoExterno: string
  estadoActual: VolqueteEstado
}

export interface VolquetesPageResponse {
  content: VolqueteResponse[]
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
