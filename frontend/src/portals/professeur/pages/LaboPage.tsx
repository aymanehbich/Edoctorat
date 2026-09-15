import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { ClipboardListIcon, UserCheckIcon, UsersIcon } from 'lucide-react'
import { useState } from 'react'
import { toast } from 'sonner'
import { ApiError } from '@/api/apiError'
import { getLaboMembres, getLaboResultats, publierResultatsLabo } from '@/api/endpoints/labo.api'
import { updateExaminerValidation } from '@/api/endpoints/professeur.api'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { ConfirmDialog } from '@/components/feedback/ConfirmDialog'
import { EmptyState } from '@/components/feedback/EmptyState'
import { ErrorBanner } from '@/components/feedback/ErrorBanner'
import { LoadingSkeleton } from '@/components/feedback/LoadingSkeleton'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { PortalPageHeader } from '@/portals/shared/layout/PortalLayout'
import type { ExaminerDTO } from '@/types/dto/ExaminerDTO'

const LABO_RESULTATS_QUERY_KEY = ['labo-resultats']

export function ProfesseurLaboPage() {
  return (
    <>
      <PortalPageHeader
        title="Mon laboratoire"
        description="Membres, présélection et résultats d'examen de votre laboratoire."
      />

      <Tabs defaultValue="membres">
        <TabsList>
          <TabsTrigger value="membres">Membres</TabsTrigger>
          <TabsTrigger value="preselection">Présélection</TabsTrigger>
          <TabsTrigger value="resultats">Résultats d'examen</TabsTrigger>
        </TabsList>

        <TabsContent value="membres">
          <LaboMembresTab />
        </TabsContent>
        <TabsContent value="preselection">
          <LaboPreselectionTab />
        </TabsContent>
        <TabsContent value="resultats">
          <LaboResultatsTab />
        </TabsContent>
      </Tabs>
    </>
  )
}

function LaboMembresTab() {
  const { data, isLoading, isError, error, refetch } = useQuery({
    queryKey: ['labo-membres'],
    queryFn: getLaboMembres,
  })

  return (
    <div className="mt-4">
      {isLoading && <LoadingSkeleton rows={4} />}

      {isError && (
        <ErrorBanner
          message={error instanceof ApiError ? error.message : 'Impossible de charger les membres.'}
          onRetry={() => refetch()}
        />
      )}

      {!isLoading && !isError && data && data.results.length === 0 && (
        <EmptyState icon={UsersIcon} title="Aucun membre" description="Aucun professeur n'est rattaché à votre laboratoire." />
      )}

      {!isLoading && !isError && data && data.results.length > 0 && (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {data.results.map((professeur) => (
            <div key={professeur.id} className="flex items-center gap-3 rounded-lg border p-4">
              <Avatar>
                <AvatarFallback>
                  {professeur.prenom.charAt(0)}
                  {professeur.nom.charAt(0)}
                </AvatarFallback>
              </Avatar>
              <p className="font-medium leading-snug">
                {professeur.prenom} {professeur.nom}
              </p>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

function LaboPreselectionTab() {
  const queryClient = useQueryClient()
  const { data, isLoading, isError, error, refetch } = useQuery({
    queryKey: LABO_RESULTATS_QUERY_KEY,
    queryFn: getLaboResultats,
  })

  const mutation = useMutation({
    mutationFn: ({ id, valider }: { id: number; valider: boolean }) => updateExaminerValidation(id, valider),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: LABO_RESULTATS_QUERY_KEY })
    },
    onError: (err) => {
      toast.error(err instanceof ApiError ? err.message : "Échec de l'enregistrement de la présélection.")
    },
  })

  const pending = data?.results.filter((examiner) => !examiner.decision) ?? []

  return (
    <div className="mt-4">
      {isLoading && <LoadingSkeleton rows={4} />}

      {isError && (
        <ErrorBanner
          message={error instanceof ApiError ? error.message : 'Impossible de charger la présélection.'}
          onRetry={() => refetch()}
        />
      )}

      {!isLoading && !isError && pending.length === 0 && (
        <EmptyState
          icon={UserCheckIcon}
          title="Aucun candidat à présélectionner"
          description="Aucun candidat en attente de présélection avant l'entretien."
        />
      )}

      {!isLoading && !isError && pending.length > 0 && (
        <div className="rounded-md border">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Candidat</TableHead>
                <TableHead>Sujet</TableHead>
                <TableHead>Statut</TableHead>
                <TableHead className="w-px" />
              </TableRow>
            </TableHeader>
            <TableBody>
              {pending.map((examiner) => (
                <TableRow key={examiner.id}>
                  <TableCell className="font-medium">
                    {examiner.candidat.prenom} {examiner.candidat.nom}
                  </TableCell>
                  <TableCell className="max-w-64 truncate">{examiner.sujet.titre}</TableCell>
                  <TableCell>
                    <Badge variant={examiner.valider ? 'default' : examiner.valider === false ? 'secondary' : 'outline'}>
                      {examiner.valider ? 'Accepté' : examiner.valider === false ? 'Rejeté' : 'En attente'}
                    </Badge>
                  </TableCell>
                  <TableCell className="flex justify-end gap-2">
                    <Button
                      size="sm"
                      variant="outline"
                      disabled={mutation.isPending}
                      onClick={() => mutation.mutate({ id: examiner.id, valider: false })}
                    >
                      Rejeter
                    </Button>
                    <Button
                      size="sm"
                      disabled={mutation.isPending}
                      onClick={() => mutation.mutate({ id: examiner.id, valider: true })}
                    >
                      Accepter
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>
      )}
    </div>
  )
}

function LaboResultatsTab() {
  const queryClient = useQueryClient()
  const [confirming, setConfirming] = useState<{ decision: string; label: string } | null>(null)
  const { data, isLoading, isError, error, refetch } = useQuery({
    queryKey: LABO_RESULTATS_QUERY_KEY,
    queryFn: getLaboResultats,
  })

  const publishMutation = useMutation({
    mutationFn: (decision: string) => publierResultatsLabo(decision),
    onSuccess: () => {
      toast.success('Résultats publiés.')
      queryClient.invalidateQueries({ queryKey: LABO_RESULTATS_QUERY_KEY })
      setConfirming(null)
    },
    onError: (err) => {
      toast.error(err instanceof ApiError ? err.message : 'Échec de la publication des résultats.')
    },
  })

  return (
    <div className="mt-4 space-y-4">
      <div className="flex flex-wrap justify-end gap-2">
        <Button variant="outline" onClick={() => setConfirming({ decision: "Liste d'attente", label: "la liste d'attente" })}>
          Publier liste d'attente
        </Button>
        <Button onClick={() => setConfirming({ decision: 'Liste principale', label: 'la liste principale' })}>
          Publier liste principale
        </Button>
      </div>

      {isLoading && <LoadingSkeleton rows={4} />}

      {isError && (
        <ErrorBanner
          message={error instanceof ApiError ? error.message : 'Impossible de charger les résultats.'}
          onRetry={() => refetch()}
        />
      )}

      {!isLoading && !isError && data && data.results.length === 0 && (
        <EmptyState
          icon={ClipboardListIcon}
          title="Aucun résultat"
          description="Aucune session d'examen n'a encore eu lieu dans votre laboratoire."
        />
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
                <TableHead>Publié</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {data.results.map((examiner: ExaminerDTO) => (
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
                    <Badge variant={examiner.publier ? 'default' : 'outline'}>{examiner.publier ? 'Oui' : 'Non'}</Badge>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>
      )}

      <ConfirmDialog
        open={confirming !== null}
        onOpenChange={(open) => !open && setConfirming(null)}
        title="Publier les résultats"
        description={confirming ? `Publier ${confirming.label} rendra ces décisions visibles aux candidats concernés.` : undefined}
        confirmLabel="Publier"
        loading={publishMutation.isPending}
        onConfirm={() => confirming && publishMutation.mutate(confirming.decision)}
      />
    </div>
  )
}
