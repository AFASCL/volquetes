import { defineStore } from 'pinia'

const AUTH_TOKEN_KEY = 'auth_token'
const AUTH_PROFILE_KEY = 'auth_profile'

export type AuthProfile = {
  permisos: string[]
  role?: string
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(AUTH_TOKEN_KEY) as string | null,
    profile: (() => {
      const raw = localStorage.getItem(AUTH_PROFILE_KEY)
      if (!raw) return null
      try {
        return JSON.parse(raw) as AuthProfile
      } catch {
        return null
      }
    })(),
  }),

  getters: {
    isAuthenticated(state): boolean {
      return !!state.token
    },
    hasRoleAdmin(state): boolean {
      return state.profile?.role === 'ROLE_ADMIN'
    },
  },

  actions: {
    setAuth(token: string, profile: AuthProfile) {
      this.token = token
      this.profile = profile
      localStorage.setItem(AUTH_TOKEN_KEY, token)
      localStorage.setItem(AUTH_PROFILE_KEY, JSON.stringify(profile))
    },
    clearAuth() {
      this.token = null
      this.profile = null
      localStorage.removeItem(AUTH_TOKEN_KEY)
      localStorage.removeItem(AUTH_PROFILE_KEY)
    },
  },
})
