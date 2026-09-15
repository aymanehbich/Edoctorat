import { useQuery, useQueryClient } from '@tanstack/react-query'
import { ChevronLeftIcon, ChevronRightIcon, ClipboardListIcon, PencilIcon } from 'lucide-react'
import { useState } from 'react'
import { ApiError } from '@/api/apiError'
import { getMyResultats } from '@/api/endpoints/professeur.api'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { EmptyState } from '@/components/feedback/EmptyState'
import { ErrorBanner } from '@/components/feedback/ErrorBanner'
import { LoadingSkeleton } from '@/components/feedback/LoadingSkeleton'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { ExaminerGradeDialog, type GradableExaminer } from '@/portals/professeur/components/ExaminerGradeDialog'
import { PortalPageHeader } from '@/portals/shared/layout/PortalLayout'

const PAGE_SIZE = 50
const RESULTATS_QUERY_KEY = 'professeur-resultats'

export function ProfesseurResultatsPage() {
  const [offset, setOffset] = useState(0)
  const [grading, setGrading] = useState<GradableExaminer | null>(null)
  const queryClient = useQueryClient()

  const { data, isLoading, isError, error, refetch } = useQuery({
    queryKey: [RESULTATS_QUERY_KEY, offset],
    queryFn: () => getMyResultats(offset),
  })

  return (
    <>
      <PortalPageHeader
        title="Résultats d'examen"
        description="Résultats des candidats examinés en commission pour vos sujets."
      />

      {isLoading && <LoadingSkeleton rows={4} />}

      {isError && (
        <ErrorBanner
          message={error instanceof ApiError ? error.message : 'Impossible de charger les résultats.'}
          onRetry={() => refetch()}
        />
      )}

      {!isLoading && !isError && data && data.results.length === 0 && (
        <EmptyState icon={ClipboardListIcon} title="Aucun résultat" description="Aucun candidat n'a encore été examiné." />
      )}

      {!isLoading && !isError && data && data.results.length > 0 && (
        <>
          <div className="rounded-md border">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Candidat</TableHead>
                  <TableHead>Sujet</TableHead>
                  <TableHead>Note dossier</TableHead>
                  <TableHead>Note entretien</TableHead>
                  <TableHead>Décision</TableHead>
                  <TableHead className="w-px" />
                </TableRow>
              </TableHeader>
              <TableBody>
                {data.results.map((examiner) => (
                  <TableRow key={examiner.id}>
                    <TableCell className="font-medium">
                      {examiner.candidat.prenom} {examiner.candidat.nom}
                    </TableCell>
                    <TableCell className="max-w-64 truncate">{examiner.sujet.titre}</TableCell>
                    <TableCell>{examiner.noteDossier}</TableCell>
                    <TableCell>{examiner.noteEntretien}</TableCell>
                    <TableCell>
                      <Badge variant={examiner.decision === 'Liste principale' ? 'default' : 'secondary'}>
                        {examiner.decision || 'En attente'}
                      </Badge>
                    </TableCell>
                    <TableCell>
                      <Button
                        variant="ghost"
                        size="icon"
                        aria-label="Noter"
                        onClick={() =>
                          setGrading({
                            id: examiner.id,
                            noteDossier: examiner.noteDossier,
                            noteEntretien: examiner.noteEntretien,
                            decision: examiner.decision,
                            publier: examiner.publier,
                          })
                        }
                      >
                        <PencilIcon className="size-4" />
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>

          <div className="mt-4 flex items-center justify-between">
            <p className="text-sm text-muted-foreground">
              {data.count} résultat{data.count > 1 ? 's' : ''} au total
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

      <ExaminerGradeDialog
        examiner={grading}
        open={grading !== null}
        onOpenChange={(open) => !open && setGrading(null)}
        onGraded={() => queryClient.invalidateQueries({ queryKey: [RESULTATS_QUERY_KEY] })}
      />
    </>
  )
}
