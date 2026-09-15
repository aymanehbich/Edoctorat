import { create } from 'zustand'
import type { AuthStatus, AuthUser, Portal } from './auth.types'

const REFRESH_TOKEN_KEY = 'edoc.refreshToken'
const PORTAL_KEY = 'edoc.portal'

interface AuthState {
  status: AuthStatus
  accessToken: string | null
  user: AuthUser | null
  portal: Portal | null
  /** Ced-director status, discovered via a probe call after professeur login/rehydration. */
  isCedDirecteur: boolean
  /** Read directly from get-user-info once the backend exposes it (Phase 0 backend addition). */
  laboratoireDirecteurId: number | null

  setSession: (params: { accessToken: string; refreshToken: string; user: AuthUser; portal: Portal }) => void
  setAccessToken: (accessToken: string) => void
  setCedDirecteur: (value: boolean) => void
  setLaboratoireDirecteurId: (value: number | null) => void
  setStatus: (status: AuthStatus) => void
  logout: () => void
  getRefreshToken: () => string | null
  getStoredPortal: () => Portal | null
}

export const useAuthStore = create<AuthState>((set) => ({
  status: 'idle',
  accessToken: null,
  user: null,
  portal: null,
  isCedDirecteur: false,
  laboratoireDirecteurId: null,

  setSession: ({ accessToken, refreshToken, user, portal }) => {
    sessionStorage.setItem(REFRESH_TOKEN_KEY, refreshToken)
    sessionStorage.setItem(PORTAL_KEY, portal)
    set({ accessToken, user, portal, status: 'authenticated' })
  },

  setAccessToken: (accessToken) => set({ accessToken }),

  setCedDirecteur: (value) => set({ isCedDirecteur: value }),

  setLaboratoireDirecteurId: (value) => set({ laboratoireDirecteurId: value }),

  setStatus: (status) => set({ status }),

  logout: () => {
    sessionStorage.removeItem(REFRESH_TOKEN_KEY)
    sessionStorage.removeItem(PORTAL_KEY)
    set({
      status: 'unauthenticated',
      accessToken: null,
      user: null,
      portal: null,
      isCedDirecteur: false,
      laboratoireDirecteurId: null,
    })
  },

  getRefreshToken: () => sessionStorage.getItem(REFRESH_TOKEN_KEY),

  getStoredPortal: () => sessionStorage.getItem(PORTAL_KEY) as Portal | null,
}))
