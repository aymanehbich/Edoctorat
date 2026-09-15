import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { PaperclipIcon, SendIcon, Trash2Icon } from 'lucide-react'
import { useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { toast } from 'sonner'
import { ApiError } from '@/api/apiError'
import { attachPostulationFile, deletePostulation, getMyPostulations } from '@/api/endpoints/postuler.api'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { ConfirmDialog } from '@/components/feedback/ConfirmDialog'
import { EmptyState } from '@/components/feedback/EmptyState'
import { ErrorBanner } from '@/components/feedback/ErrorBanner'
import { LoadingSkeleton } from '@/components/feedback/LoadingSkeleton'
import { PortalPageHeader } from '@/portals/shared/layout/PortalLayout'

const POSTULATIONS_QUERY_KEY = ['candidat-postulations']

export function CandidaturesPage() {
  const queryClient = useQueryClient()
  const [deletingId, setDeletingId] = useState<number | null>(null)
  const fileInputRefs = useRef<Record<number, HTMLInputElement | null>>({})

  const { data, isLoading, isError, error, refetch } = useQuery({
    queryKey: POSTULATIONS_QUERY_KEY,
    queryFn: getMyPostulations,
  })

  const deleteMutation = useMutation({
    mutationFn: deletePostulation,
    onSuccess: () => {
      toast.success('Candidature retirée.')
      queryClient.invalidateQueries({ queryKey: POSTULATIONS_QUERY_KEY })
      setDeletingId(null)
    },
    onError: (error) => {
      toast.error(error instanceof ApiError ? error.message : 'Échec de la suppression.')
    },
  })

  const attachMutation = useMutation({
    mutationFn: (vars: { id: number; file: File }) => attachPostulationFile(vars.id, vars.file),
    onSuccess: () => {
      toast.success('Pièce jointe envoyée.')
      queryClient.invalidateQueries({ queryKey: POSTULATIONS_QUERY_KEY })
    },
    onError: (error) => {
      toast.error(error instanceof ApiError ? error.message : "Échec de l'envoi de la pièce jointe.")
    },
  })

  return (
    <>
      <PortalPageHeader
        title="Mes candidatures"
        description="Suivez l'état de vos candidatures aux sujets de thèse."
        action={
          <Button asChild variant="outline">
            <Link to="/candidat/sujets">Parcourir les sujets</Link>
          </Button>
        }
      />

      {isLoading && <LoadingSkeleton rows={3} />}

      {isError && (
        <ErrorBanner
          message={error instanceof ApiError ? error.message : 'Impossible de charger vos candidatures.'}
          onRetry={() => refetch()}
        />
      )}

      {!isLoading && !isError && data && data.results.length === 0 && (
        <EmptyState
          icon={SendIcon}
          title="Aucune candidature"
          description="Parcourez les sujets disponibles pour déposer votre première candidature."
          action={
            <Button asChild size="sm">
              <Link to="/candidat/sujets">Parcourir les sujets</Link>
            </Button>
          }
        />
      )}

      {!isLoading && !isError && data && data.results.length > 0 && (
        <div className="space-y-4">
          {data.results.map((postuler) => (
            <Card key={postuler.id}>
              <CardHeader className="flex flex-row items-start justify-between gap-2">
                <div>
                  <CardTitle className="text-base">{postuler.sujetTitre ?? postuler.sujet.titre}</CardTitle>
                  <p className="text-sm text-muted-foreground">
                    {postuler.sujet.professeur.prenom} {postuler.sujet.professeur.nom}
                  </p>
                </div>
                <Button
                  variant="ghost"
                  size="icon"
                  onClick={() => setDeletingId(postuler.id)}
                  aria-label="Retirer la candidature"
                >
                  <Trash2Icon className="size-4 text-destructive" />
                </Button>
              </CardHeader>
              <CardContent className="flex flex-wrap items-center gap-3">
                {postuler.pathFile ? (
                  <a
                    href={postuler.pathFile}
                    target="_blank"
                    rel="noreferrer"
                    className="inline-flex items-center gap-1 rounded-md border px-2 py-1 text-xs text-muted-foreground hover:bg-accent"
                  >
                    <PaperclipIcon className="size-3" />
                    Pièce jointe
                  </a>
                ) : (
                  <span className="text-xs text-muted-foreground">Aucune pièce jointe</span>
                )}
                <input
                  ref={(el) => {
                    fileInputRefs.current[postuler.id] = el
                  }}
                  type="file"
                  accept="application/pdf"
                  hidden
                  onChange={(e) => {
                    const file = e.target.files?.[0]
                    if (file) attachMutation.mutate({ id: postuler.id, file })
                  }}
                />
                <Button
                  variant="outline"
                  size="sm"
                  disabled={attachMutation.isPending}
                  onClick={() => fileInputRefs.current[postuler.id]?.click()}
                >
                  <PaperclipIcon className="size-4" />
                  {postuler.pathFile ? 'Remplacer le fichier' : 'Joindre un fichier'}
                </Button>
              </CardContent>
            </Card>
          ))}
        </div>
      )}

      <ConfirmDialog
        open={deletingId !== null}
        onOpenChange={(open) => !open && setDeletingId(null)}
        title="Retirer cette candidature ?"
        description="Cette action est irréversible."
        destructive
        confirmLabel="Retirer"
        loading={deleteMutation.isPending}
        onConfirm={() => deletingId !== null && deleteMutation.mutate(deletingId)}
      />
    </>
  )
}
