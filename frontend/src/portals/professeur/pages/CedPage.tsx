import { useQuery, useQueryClient } from '@tanstack/react-query'
import { FlaskConicalIcon, LandmarkIcon, PencilIcon, UsersIcon } from 'lucide-react'
import { useState } from 'react'
import { ApiError } from '@/api/apiError'
import { getCedCandidats, getCedCommissions, getCedResultats, getCedSujets } from '@/api/endpoints/ced.api'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { EmptyState } from '@/components/feedback/EmptyState'
import { ErrorBanner } from '@/components/feedback/ErrorBanner'
import { LoadingSkeleton } from '@/components/feedback/LoadingSkeleton'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { ExaminerGradeDialog, type GradableExaminer } from '@/portals/professeur/components/ExaminerGradeDialog'
import { PortalPageHeader } from '@/portals/shared/layout/PortalLayout'
import type { ExaminerDTO } from '@/types/dto/ExaminerDTO'

export function EspaceCedPage() {
  return (
    <>
      <PortalPageHeader title="Espace CED" description="Vue d'ensemble des sujets, candidats et commissions de votre CED." />

      <Tabs defaultValue="sujets">
        <TabsList>
          <TabsTrigger value="sujets">Sujets</TabsTrigger>
          <TabsTrigger value="candidats">Candidats</TabsTrigger>
          <TabsTrigger value="commissions">Commissions</TabsTrigger>
          <TabsTrigger value="resultats">Résultats</TabsTrigger>
        </TabsList>

        <TabsContent value="sujets">
          <CedSujetsTab />
        </TabsContent>
        <TabsContent value="candidats">
          <CedExaminerTab
            queryKey={['ced-candidats']}
            queryFn={getCedCandidats}
            emptyTitle="Aucune session d'examen"
            emptyDescription="Vous ne siégez actuellement dans aucune commission d'examen."
            errorMessage="Impossible de charger les candidats."
          />
        </TabsContent>
        <TabsContent value="commissions">
          <CedCommissionsTab />
        </TabsContent>
        <TabsContent value="resultats">
          <CedExaminerTab
            queryKey={['ced-resultats']}
            queryFn={getCedResultats}
            emptyTitle="Aucun résultat"
            emptyDescription="Aucun résultat d'examen pour les laboratoires que vous dirigez."
            errorMessage="Impossible de charger les résultats."
          />
        </TabsContent>
      </Tabs>
    </>
  )
}

function CedSujetsTab() {
  const { data, isLoading, isError, error, refetch } = useQuery({
    queryKey: ['ced-sujets'],
    queryFn: getCedSujets,
  })

  return (
    <div className="mt-4">
      {isLoading && <LoadingSkeleton rows={4} />}

      {isError && (
        <ErrorBanner
          message={error instanceof ApiError ? error.message : 'Impossible de charger les sujets.'}
          onRetry={() => refetch()}
        />
      )}

      {!isLoading && !isError && data && data.results.length === 0 && (
        <EmptyState icon={FlaskConicalIcon} title="Aucun sujet" description="Aucun sujet n'a été proposé dans votre CED." />
      )}

      {!isLoading && !isError && data && data.results.length > 0 && (
        <div className="grid gap-4 sm:grid-cols-2">
          {data.results.map((sujet) => (
            <div key={sujet.id} className="rounded-lg border p-4">
              <p className="font-medium leading-snug">{sujet.titre}</p>
              <p className="mt-1 text-sm text-muted-foreground">
                {sujet.professeur.prenom} {sujet.professeur.nom} · {sujet.formationDoctorale?.titre}
              </p>
              <div className="mt-3 flex flex-wrap items-center gap-2">
                <Badge variant={sujet.publier ? 'default' : 'secondary'}>{sujet.publier ? 'Publié' : 'Non publié'}</Badge>
                {sujet.laboratoire && <Badge variant="outline">{sujet.laboratoire.nomLaboratoire}</Badge>}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

function CedCommissionsTab() {
  const { data, isLoading, isError, error, refetch } = useQuery({
    queryKey: ['ced-commissions'],
    queryFn: getCedCommissions,
  })

  return (
    <div className="mt-4">
      {isLoading && <LoadingSkeleton rows={4} />}

      {isError && (
        <ErrorBanner
          message={error instanceof ApiError ? error.message : 'Impossible de charger les commissions.'}
          onRetry={() => refetch()}
        />
      )}

      {!isLoading && !isError && data && data.results.length === 0 && (
        <EmptyState icon={LandmarkIcon} title="Aucune commission" description="Aucune commission n'est encore programmée." />
      )}

      {!isLoading && !isError && data && data.results.length > 0 && (
        <div className="space-y-4">
          {data.results.map((commission) => (
            <div key={commission.id} className="rounded-lg border p-4">
              <div className="flex flex-wrap items-center justify-between gap-2">
                <p className="font-medium">
                  {commission.dateCommission} · {commission.heure}
                </p>
                <Badge variant="outline">Laboratoire #{commission.labo}</Badge>
              </div>
              <p className="mt-1 text-sm text-muted-foreground">{commission.lieu}</p>

              {commission.participants.length > 0 && (
                <div className="mt-3 flex flex-wrap gap-2">
                  {commission.participants.map((p) => (
                    <Badge key={p.id} variant="secondary">
                      {p.prenom} {p.nom}
                    </Badge>
                  ))}
                </div>
              )}

              {commission.sujets.length > 0 && (
                <ul className="mt-3 space-y-1 text-sm text-muted-foreground">
                  {commission.sujets.map((s) => (
                    <li key={s.id}>• {s.titre}</li>
                  ))}
                </ul>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

function CedExaminerTab({
  queryKey,
  queryFn,
  emptyTitle,
  emptyDescription,
  errorMessage,
}: {
  queryKey: string[]
  queryFn: () => Promise<{ count: number; results: ExaminerDTO[] }>
  emptyTitle: string
  emptyDescription: string
  errorMessage: string
}) {
  const queryClient = useQueryClient()
  const [grading, setGrading] = useState<GradableExaminer | null>(null)
  const { data, isLoading, isError, error, refetch } = useQuery({ queryKey, queryFn })

  return (
    <div className="mt-4">
      {isLoading && <LoadingSkeleton rows={4} />}

      {isError && (
        <ErrorBanner message={error instanceof ApiError ? error.message : errorMessage} onRetry={() => refetch()} />
      )}

      {!isLoading && !isError && data && data.results.length === 0 && (
        <EmptyState icon={UsersIcon} title={emptyTitle} description={emptyDescription} />
      )}

      {!isLoading && !isError && data && data.results.length > 0 && (
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
                          publier: examiner.publier ?? false,
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
      )}

      <ExaminerGradeDialog
        examiner={grading}
        open={grading !== null}
        onOpenChange={(open) => !open && setGrading(null)}
        onGraded={() => queryClient.invalidateQueries({ queryKey })}
      />
    </div>
  )
}
