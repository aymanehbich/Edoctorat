import { useAuthStore } from '@/auth/auth.store'
import { PortalPageHeader } from '@/portals/shared/layout/PortalLayout'

export function ProfesseurDashboardPage() {
  const user = useAuthStore((s) => s.user)

  return (
    <PortalPageHeader
      title={`Bienvenue${user ? `, ${user.prenom}` : ''}`}
      description="Retrouvez ici vos sujets de thèse, vos candidats et vos résultats d'examen."
    />
  )
}
