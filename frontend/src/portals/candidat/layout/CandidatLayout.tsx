import { ClipboardListIcon, FileTextIcon, FlaskConicalIcon, LayoutDashboardIcon, SendIcon, UserIcon } from 'lucide-react'
import { PortalLayout, type PortalNavItem } from '@/portals/shared/layout/PortalLayout'

const NAV_ITEMS: PortalNavItem[] = [
  { to: '/candidat', label: 'Tableau de bord', icon: LayoutDashboardIcon, end: true },
  { to: '/candidat/profil', label: 'Mon profil', icon: UserIcon },
  { to: '/candidat/diplomes', label: 'Diplômes', icon: FileTextIcon },
  { to: '/candidat/sujets', label: 'Sujets de thèse', icon: FlaskConicalIcon },
  { to: '/candidat/candidatures', label: 'Mes candidatures', icon: SendIcon },
  { to: '/candidat/resultats', label: 'Mes résultats', icon: ClipboardListIcon },
]

export function CandidatLayout() {
  return <PortalLayout portalLabel="Candidat" navItems={NAV_ITEMS} />
}
