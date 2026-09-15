import { useAuthStore } from '@/auth/auth.store'
import { PortalPageHeader } from '@/portals/shared/layout/PortalLayout'

export function ScolariteDashboardPage() {
  const user = useAuthStore((s) => s.user)

  return (
    <PortalPageHeader
      title={`Bienvenue${user ? `, ${user.prenom}` : ''}`}
      description="Validez les dossiers d'inscription des candidats admis."
    />
  )
}
