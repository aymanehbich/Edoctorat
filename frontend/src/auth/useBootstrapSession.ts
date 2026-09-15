import { useEffect } from 'react'
import { refreshToken as refreshTokenRequest } from '@/api/endpoints/auth.api'
import { useAuthStore } from './auth.store'
import { completeLogin } from './normalizeLogin'

/**
 * Runs once on app load. If a refresh token survived from an earlier tab
 * session, mint a fresh access token (the JSESSIONID cookie is already
 * present in the browser) and rehydrate the user. Otherwise the user is
 * treated as logged out — we never try to infer auth state from the cookie
 * alone, since GET /api/get-user-info/ requires the Authorization header.
 */
export function useBootstrapSession() {
  useEffect(() => {
    const store = useAuthStore.getState()
    const refresh = store.getRefreshToken()
    const portal = store.getStoredPortal()

    if (!refresh || !portal) {
      store.setStatus('unauthenticated')
      return
    }

    store.setStatus('loading')
    refreshTokenRequest(refresh)
      .then((tokens) => completeLogin(portal, tokens))
      .catch(() => {
        store.logout()
      })
  }, [])
}
