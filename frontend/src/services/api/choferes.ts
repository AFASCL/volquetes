/**
 * Servicio API Choferes (selector). Contrato: memory-bank/04-api-documentation.md
 */
import { apiRequest } from './client'
import type { ChoferSelectorItem } from '@/types/choferes'

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

export async function listChoferesSelector(): Promise<ChoferSelectorItem[]> {
  const { data, status } = await apiRequest<ChoferSelectorItem[]>('/api/choferes/selector', {
    method: 'GET',
  })
  if (status >= 400) throw new Error(getErrorMessage(status))
  return (Array.isArray(data) ? data : []) as ChoferSelectorItem[]
}
