/**
 * Servicio API Camiones (selector). Contrato: memory-bank/04-api-documentation.md
 */
import { apiRequest } from './client'
import type { CamionSelectorItem } from '@/types/camiones'

function getErrorMessage(status: number): string {
  switch (status) {
    case 401:
      return 'No autenticado.'
    case 403:
      return 'Sin permiso.'
    default:
      return `Error ${status}.`
  }
}

export async function listCamionesSelector(): Promise<CamionSelectorItem[]> {
  const { data, status } = await apiRequest<CamionSelectorItem[]>('/api/camiones/selector', {
    method: 'GET',
  })
  if (status >= 400) throw new Error(getErrorMessage(status))
  return (Array.isArray(data) ? data : []) as CamionSelectorItem[]
}
