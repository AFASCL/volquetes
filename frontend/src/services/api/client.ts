/**
 * Cliente HTTP centralizado.
 * Base URL por env: VITE_API_BASE_URL (default http://localhost:8080).
 * 401 -> /login ; 403 -> /401
 */

const AUTH_TOKEN_KEY = 'auth_token'

function getBaseUrl(): string {
  const url = import.meta.env.VITE_API_BASE_URL
  return typeof url === 'string' && url ? url.replace(/\/$/, '') : 'http://localhost:8080'
}

function getToken(): string | null {
  return localStorage.getItem(AUTH_TOKEN_KEY)
}

export async function apiRequest<T>(
  path: string,
  options: RequestInit = {}
): Promise<{ data: T; status: number }> {
  const base = getBaseUrl()
  const url = path.startsWith('http') ? path : `${base}${path.startsWith('/') ? path : '/' + path}`

  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string>),
  }

  const token = getToken()
  if (token) {
    ;(headers as Record<string, string>)['Authorization'] = `Bearer ${token}`
  }

  const res = await fetch(url, { ...options, headers })

  if (res.status === 401) {
    window.location.hash = '#/login'
    throw new Error('No autenticado')
  }

  if (res.status === 403) {
    window.location.hash = '#/401'
    throw new Error('Sin permiso')
  }

  const contentType = res.headers.get('Content-Type') ?? ''
  let data: T
  if (contentType.includes('application/json')) {
    try {
      data = (await res.json()) as T
    } catch {
      data = undefined as T
    }
  } else {
    data = undefined as T
  }

  return { data, status: res.status }
}

export function getApiBaseUrl(): string {
  return getBaseUrl()
}
