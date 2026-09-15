import { useQuery } from '@tanstack/react-query'
import { CalendarCheckIcon, ChevronLeftIcon, ChevronRightIcon } from 'lucide-react'
import { useState } from 'react'
import { ApiError } from '@/api/apiError'
import { getMyInscrits } from '@/api/endpoints/professeur.api'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { EmptyState } from '@/components/feedback/EmptyState'
import { ErrorBanner } from '@/components/feedback/ErrorBanner'
import { LoadingSkeleton } from '@/components/feedback/LoadingSkeleton'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { PortalPageHeader } from '@/portals/shared/layout/PortalLayout'

const PAGE_SIZE = 50

export function ProfesseurInscritsPage() {
  const [offset, setOffset] = useState(0)

  const { data, isLoading, isError, error, refetch } = useQuery({
    queryKey: ['professeur-inscrits', offset],
    queryFn: () => getMyInscrits(offset),
  })

  return (
    <>
      <PortalPageHeader
        title="Inscrits"
        description="Candidats inscrits sur vos sujets de thèse."
      />

      {isLoading && <LoadingSkeleton rows={4} />}

      {isError && (
        <ErrorBanner
          message={error instanceof ApiError ? error.message : 'Impossible de charger les inscrits.'}
          onRetry={() => refetch()}
        />
      )}

      {!isLoading && !isError && data && data.results.length === 0 && (
        <EmptyState icon={CalendarCheckIcon} title="Aucun inscrit" description="Aucun candidat n'est encore inscrit sur vos sujets." />
      )}

      {!isLoading && !isError && data && data.results.length > 0 && (
        <>
          <div className="rounded-md border">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Candidat</TableHead>
                  <TableHead>CNE</TableHead>
                  <TableHead>Sujet</TableHead>
                  <TableHead>Date de dépôt</TableHead>
                  <TableHead>Remarque</TableHead>
                  <TableHead>Statut</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {data.results.map((inscription) => (
                  <TableRow key={inscription.id}>
                    <TableCell className="font-medium">
                      {inscription.candidat.prenom} {inscription.candidat.nom}
                    </TableCell>
                    <TableCell>{inscription.candidat.cne}</TableCell>
                    <TableCell className="max-w-64 truncate">{inscription.sujet.titre}</TableCell>
                    <TableCell>{inscription.dateDisposeDossier}</TableCell>
                    <TableCell className="max-w-48 truncate text-muted-foreground">
                      {inscription.remarque || '—'}
                    </TableCell>
                    <TableCell>
                      <Badge variant={inscription.valider ? 'default' : 'secondary'}>
                        {inscription.valider ? 'Validé' : 'En attente'}
                      </Badge>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>

          <div className="mt-4 flex items-center justify-between">
            <p className="text-sm text-muted-foreground">
              {data.count} inscrit{data.count > 1 ? 's' : ''} au total
            </p>
            <div className="flex gap-2">
              <Button
                variant="outline"
                size="sm"
                disabled={offset === 0}
                onClick={() => setOffset((o) => Math.max(0, o - PAGE_SIZE))}
              >
                <ChevronLeftIcon className="size-4" />
                Précédent
              </Button>
              <Button
                variant="outline"
                size="sm"
                disabled={!data.next}
                onClick={() => setOffset((o) => o + PAGE_SIZE)}
              >
                Suivant
                <ChevronRightIcon className="size-4" />
              </Button>
            </div>
          </div>
        </>
      )}
    </>
  )
}
