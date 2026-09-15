import { ConstructionIcon } from 'lucide-react'
import { EmptyState } from './EmptyState'

/** Temporary placeholder for pages not yet built in the current delivery phase. */
export function ComingSoon({ title }: { title: string }) {
  return <EmptyState icon={ConstructionIcon} title={title} description="Cette page est en cours de construction." />
}
