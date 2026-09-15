import { ClipboardCheckIcon, LayoutDashboardIcon } from 'lucide-react'
import { PortalLayout, type PortalNavItem } from '@/portals/shared/layout/PortalLayout'

const NAV_ITEMS: PortalNavItem[] = [
  { to: '/scolarite', label: 'Tableau de bord', icon: LayoutDashboardIcon, end: true },
  { to: '/scolarite/inscriptions', label: 'Inscriptions', icon: ClipboardCheckIcon },
]

export function ScolariteLayout() {
  return <PortalLayout portalLabel="Scolarité" navItems={NAV_ITEMS} />
}
