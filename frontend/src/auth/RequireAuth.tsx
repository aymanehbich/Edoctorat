import { Navigate, Outlet } from 'react-router-dom'
import type { Portal } from './auth.types'
import { useAuthStore } from './auth.store'

const LOGIN_PATH: Record<Portal, string> = {
  candidat: '/login/candidat',
  professeur: '/login/professeur',
  scolarite: '/login/scolarite',
}

/** Route guard for a nested layout route: <Route element={<RequireAuth portal="candidat" />}>. */
export function RequireAuth({ portal }: { portal: Portal }) {
  const status = useAuthStore((s) => s.status)
  const currentPortal = useAuthStore((s) => s.portal)

  if (status === 'idle' || status === 'loading') {
    return (
      <div className="flex min-h-screen items-center justify-center text-muted-foreground">
        Chargement…
      </div>
    )
  }

  if (status === 'unauthenticated') {
    return <Navigate to={LOGIN_PATH[portal]} replace />
  }

  if (currentPortal !== portal) {
    return <Navigate to={LOGIN_PATH[portal]} replace />
  }

  return <Outlet />
}
