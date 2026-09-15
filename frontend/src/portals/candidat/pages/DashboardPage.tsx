import { useAuthStore } from '@/auth/auth.store'
import { PortalPageHeader } from '@/portals/shared/layout/PortalLayout'

export function CandidatDashboardPage() {
  const user = useAuthStore((s) => s.user)

  return (
    <PortalPageHeader
      title={`Bienvenue${user ? `, ${user.prenom}` : ''}`}
      description="Retrouvez ici l'état de votre dossier de candidature au doctorat."
    />
  )
}
