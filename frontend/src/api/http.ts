import axios, { AxiosHeaders, type InternalAxiosRequestConfig } from 'axios'
import { useAuthStore } from '@/auth/auth.store'
import type { TokenResponse } from '@/types/dto/Auth'
import { ApiError, isAuthFailure, parseErrorBody } from './apiError'

declare module 'axios' {
  interface AxiosRequestConfig {
    _retry?: boolean
    /** Set on calls that should bypass the auth-failure refresh/redirect flow (e.g. director probes). */
    skipAuthRecovery?: boolean
  }
}

export const http = axios.create({
  baseURL: '/api',
  withCredentials: true,
})

http.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const { accessToken } = useAuthStore.getState()
  const url = (config.url ?? '').split('?')[0].replace(/\/+$/, '')
  const publicAuthPaths = [
    '/token', '/token/refresh', '/login_scolarite', '/verify-is-prof',
    '/confirm-email', '/verify-token', '/register/candidat',
    '/request-password-reset', '/perform-password-reset',
  ]
  const isPublicAuthRequest = publicAuthPaths.some((path) => url.endsWith(path))

  if (accessToken && !isPublicAuthRequest) {
    config.headers = config.headers ?? new AxiosHeaders()
    config.headers.set('Authorization', `Bearer ${accessToken}`)
  }
  return config
})

let refreshPromise: Promise<string> | null = null

async function performRefresh(): Promise<string> {
  const refreshToken = useAuthStore.getState().getRefreshToken()
  if (!refreshToken) {
    throw new ApiError(401, 'Aucun jeton de rafraîchissement disponible.', null)
  }
  const response = await axios.post<TokenResponse>(
    '/api/token/refresh/',
    { refresh: refreshToken },
    { withCredentials: true },
  )
  useAuthStore.getState().setAccessToken(response.data.access)
  return response.data.access
}

/** Coalesces concurrent refresh attempts into a single in-flight request. */
function refreshAccessToken(): Promise<string> {
  if (!refreshPromise) {
    refreshPromise = performRefresh().finally(() => {
      refreshPromise = null
    })
  }
  return refreshPromise
}

http.interceptors.response.use(
  (response) => {
    if (response.headers['x-token-expiring'] === 'true') {
      refreshAccessToken().catch(() => {
        // Proactive refresh failing is not itself fatal — the next real
        // request will hit the reactive auth-failure path below.
      })
    }
    return response
  },
  async (error) => {
    if (!axios.isAxiosError(error) || !error.config) {
      return Promise.reject(error)
    }

    const apiError = parseErrorBody(error)
    const config = error.config

    if (config.skipAuthRecovery || !isAuthFailure(apiError)) {
      return Promise.reject(apiError)
    }

    if (config._retry) {
      useAuthStore.getState().logout()
      return Promise.reject(apiError)
    }

    try {
      const newAccessToken = await refreshAccessToken()
      config._retry = true
      config.headers = config.headers ?? new AxiosHeaders()
      config.headers.set('Authorization', `Bearer ${newAccessToken}`)
      return http.request(config)
    } catch {
      useAuthStore.getState().logout()
      return Promise.reject(apiError)
    }
  },
)
