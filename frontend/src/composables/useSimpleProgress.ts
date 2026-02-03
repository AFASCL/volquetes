import { ref } from 'vue'

const loading = ref(false)

/**
 * Indicador de progreso global (spinner / loading).
 * Respetar en navegación y llamadas API según convención del repo.
 */
export function useSimpleProgress() {
  function start() {
    loading.value = true
  }

  function done() {
    loading.value = false
  }

  return {
    loading,
    start,
    done,
  }
}
