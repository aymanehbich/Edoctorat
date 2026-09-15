import {
  BuildingIcon,
  CalendarCheckIcon,
  ClipboardListIcon,
  FlaskConicalIcon,
  LandmarkIcon,
  LayoutDashboardIcon,
  UsersIcon,
} from 'lucide-react'
import { useAuthStore } from '@/auth/auth.store'
import { PortalLayout, type PortalNavItem } from '@/portals/shared/layout/PortalLayout'

const BASE_NAV_ITEMS: PortalNavItem[] = [
  { to: '/professeur', label: 'Tableau de bord', icon: LayoutDashboardIcon, end: true },
  { to: '/professeur/sujets', label: 'Mes sujets', icon: FlaskConicalIcon },
  { to: '/professeur/candidats', label: 'Candidats', icon: UsersIcon },
  { to: '/professeur/resultats', label: "Résultats d'examen", icon: ClipboardListIcon },
  { to: '/professeur/inscrits', label: 'Inscrits', icon: CalendarCheckIcon },
]

export function ProfesseurLayout() {
  const isCedDirecteur = useAuthStore((s) => s.isCedDirecteur)
  const laboratoireDirecteurId = useAuthStore((s) => s.laboratoireDirecteurId)

  const navItems: PortalNavItem[] = [...BASE_NAV_ITEMS]
  if (isCedDirecteur) {
    navItems.push({ to: '/ced', label: 'Espace CED', icon: LandmarkIcon })
  }
  if (laboratoireDirecteurId != null) {
    navItems.push({ to: '/labo', label: 'Mon laboratoire', icon: BuildingIcon })
  }

  return <PortalLayout portalLabel="Professeur" navItems={navItems} />
}
