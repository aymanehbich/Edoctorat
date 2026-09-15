import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { ChevronLeftIcon, ChevronRightIcon, ClipboardCheckIcon } from 'lucide-react'
import { useState } from 'react'
import { toast } from 'sonner'
import { ApiError } from '@/api/apiError'
import { getInscriptions, updateValidation } from '@/api/endpoints/scolarite.api'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { EmptyState } from '@/components/feedback/EmptyState'
import { ErrorBanner } from '@/components/feedback/ErrorBanner'
import { LoadingSkeleton } from '@/components/feedback/LoadingSkeleton'
import { Label } from '@/components/ui/label'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Textarea } from '@/components/ui/textarea'
import { PortalPageHeader } from '@/portals/shared/layout/PortalLayout'
import type { InscriptionScolariteDTO } from '@/types/dto/InscriptionScolariteDTO'

const PAGE_SIZE = 50
const INSCRIPTIONS_QUERY_KEY = ['scolarite-inscriptions']

function StatutBadge({ valider }: { valider: boolean | null }) {
  if (valider === true) return <Badge>Validé</Badge>
  if (valider === false) return <Badge variant="destructive">Rejeté</Badge>
  return <Badge variant="secondary">En attente</Badge>
}

export function ScolariteInscriptionsPage() {
  const [offset, setOffset] = useState(0)
  const [reviewing, setReviewing] = useState<InscriptionScolariteDTO | null>(null)

  const { data, isLoading, isError, error, refetch } = useQuery({
    queryKey: [...INSCRIPTIONS_QUERY_KEY, offset],
    queryFn: () => getInscriptions(offset),
  })

  return (
    <>
      <PortalPageHeader
        title="Inscriptions"
        description="Validez les dossiers d'inscription des candidats admis."
      />

      {isLoading && <LoadingSkeleton rows={4} />}

      {isError && (
        <ErrorBanner
          message={error instanceof ApiError ? error.message : 'Impossible de charger les inscriptions.'}
          onRetry={() => refetch()}
        />
      )}

      {!isLoading && !isError && data && data.results.length === 0 && (
        <EmptyState
          icon={ClipboardCheckIcon}
          title="Aucune inscription"
          description="Aucun dossier d'inscription n'est en attente de traitement."
        />
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
                  <TableHead className="text-right">Actions</TableHead>
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
                      <StatutBadge valider={inscription.valider} />
                    </TableCell>
                    <TableCell className="text-right">
                      <Button variant="outline" size="sm" onClick={() => setReviewing(inscription)}>
                        Examiner
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>

          <div className="mt-4 flex items-center justify-between">
            <p className="text-sm text-muted-foreground">
              {data.count} inscription{(data.count ?? 0) > 1 ? 's' : ''} au total
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

      <ReviewDialog
        inscription={reviewing}
        onOpenChange={(open) => !open && setReviewing(null)}
      />
    </>
  )
}

function ReviewDialog({
  inscription,
  onOpenChange,
}: {
  inscription: InscriptionScolariteDTO | null
  onOpenChange: (open: boolean) => void
}) {
  const queryClient = useQueryClient()
  const [remarque, setRemarque] = useState('')

  const handleOpenChange = (open: boolean) => {
    if (open && inscription) setRemarque(inscription.remarque ?? '')
    onOpenChange(open)
  }

  const mutation = useMutation({
    mutationFn: (valider: boolean) => {
      if (!inscription) throw new Error('No inscription selected')
      return updateValidation(inscription.id, { remarque, valider })
    },
    onSuccess: () => {
      toast.success('Dossier mis à jour.')
      queryClient.invalidateQueries({ queryKey: INSCRIPTIONS_QUERY_KEY })
      onOpenChange(false)
    },
    onError: (error) => {
      toast.error(error instanceof ApiError ? error.message : "Échec de la mise à jour du dossier.")
    },
  })

  return (
    <Dialog open={inscription !== null} onOpenChange={handleOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Examiner le dossier</DialogTitle>
        </DialogHeader>

        {inscription && (
          <div className="space-y-4">
            <div className="space-y-1 text-sm">
              <p className="font-medium">
                {inscription.candidat.prenom} {inscription.candidat.nom}
              </p>
              <p className="text-muted-foreground">{inscription.sujet.titre}</p>
            </div>

            <div className="space-y-2">
              <Label htmlFor="remarque">Remarque</Label>
              <Textarea
                id="remarque"
                rows={4}
                value={remarque}
                onChange={(e) => setRemarque(e.target.value)}
                placeholder="Motif de la décision (optionnel)"
              />
            </div>
          </div>
        )}

        <DialogFooter>
          <Button type="button" variant="outline" onClick={() => onOpenChange(false)} disabled={mutation.isPending}>
            Annuler
          </Button>
          <Button
            type="button"
            variant="destructive"
            disabled={mutation.isPending}
            onClick={() => mutation.mutate(false)}
          >
            Rejeter
          </Button>
          <Button type="button" disabled={mutation.isPending} onClick={() => mutation.mutate(true)}>
            Valider
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}
