import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { ArrowLeftIcon, CheckCircle2Icon } from 'lucide-react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { toast } from 'sonner'
import { ApiError } from '@/api/apiError'
import { getBaseConfig } from '@/api/endpoints/config.api'
import { applyToSujet, getMyPostulations } from '@/api/endpoints/postuler.api'
import { getSujetById } from '@/api/endpoints/sujet.api'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { ErrorBanner } from '@/components/feedback/ErrorBanner'
import { LoadingSkeleton } from '@/components/feedback/LoadingSkeleton'
import { PortalPageHeader } from '@/portals/shared/layout/PortalLayout'

export function SujetDetailPage() {
  const { id } = useParams<{ id: string }>()
  const sujetId = Number(id)
  const navigate = useNavigate()
  const queryClient = useQueryClient()

  const sujetQuery = useQuery({
    queryKey: ['sujet', sujetId],
    queryFn: () => getSujetById(sujetId),
    enabled: Number.isFinite(sujetId),
  })
  const postulationsQuery = useQuery({ queryKey: ['candidat-postulations'], queryFn: getMyPostulations })
  const configQuery = useQuery({ queryKey: ['base-config'], queryFn: getBaseConfig })

  const applyMutation = useMutation({
    mutationFn: () => applyToSujet(sujetId),
    onSuccess: () => {
      toast.success('Candidature envoyée avec succès.')
      queryClient.invalidateQueries({ queryKey: ['candidat-postulations'] })
    },
    onError: (error) => {
      toast.error(error instanceof ApiError ? error.message : "Échec de l'envoi de la candidature.")
    },
  })

  if (sujetQuery.isLoading) {
    return (
      <>
        <PortalPageHeader title="Sujet de thèse" />
        <LoadingSkeleton rows={4} />
      </>
    )
  }

  if (sujetQuery.isError || !sujetQuery.data) {
    return (
      <>
        <PortalPageHeader title="Sujet de thèse" />
        <ErrorBanner
          message={sujetQuery.error instanceof ApiError ? sujetQuery.error.message : 'Sujet introuvable.'}
          onRetry={() => sujetQuery.refetch()}
        />
      </>
    )
  }

  const sujet = sujetQuery.data
  const alreadyApplied = postulationsQuery.data?.results.some((p) => p.sujet.id === sujetId) ?? false
  const config = configQuery.data
  const withinWindow = config
    ? isWithinWindow(config.dateDebutPostulerSujetCandidat, config.dateFinPostulerSujetCandidat)
    : true
  const reachedMax =
    config && postulationsQuery.data ? postulationsQuery.data.results.length >= config.maxSujetPostuler : false

  return (
    <>
      <Link
        to="/candidat/sujets"
        className="mb-4 inline-flex items-center gap-1 text-sm text-muted-foreground hover:text-foreground"
      >
        <ArrowLeftIcon className="size-4" />
        Retour aux sujets
      </Link>

      <PortalPageHeader title={sujet.titre} />

      <div className="grid gap-6 lg:grid-cols-3">
        <Card className="lg:col-span-2">
          <CardHeader>
            <CardTitle className="text-base">Description</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="whitespace-pre-wrap text-sm text-muted-foreground">{sujet.description}</p>
          </CardContent>
        </Card>

        <div className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle className="text-base">Encadrement</CardTitle>
            </CardHeader>
            <CardContent className="space-y-2 text-sm">
              <p>
                <span className="text-muted-foreground">Directeur : </span>
                {sujet.professeur.prenom} {sujet.professeur.nom}
              </p>
              {sujet.coDirecteur && (
                <p>
                  <span className="text-muted-foreground">Co-directeur : </span>
                  {sujet.coDirecteur.prenom} {sujet.coDirecteur.nom}
                </p>
              )}
              {sujet.formationDoctorale && (
                <p>
                  <Badge variant="outline">{sujet.formationDoctorale.titre}</Badge>
                </p>
              )}
            </CardContent>
          </Card>

          <Card>
            <CardContent className="pt-6">
              {alreadyApplied ? (
                <div className="flex items-center gap-2 text-sm text-success">
                  <CheckCircle2Icon className="size-4" />
                  Vous avez déjà postulé à ce sujet.
                </div>
              ) : (
                <div className="space-y-2">
                  <Button
                    className="w-full"
                    disabled={applyMutation.isPending || !withinWindow || reachedMax}
                    onClick={() => applyMutation.mutate()}
                  >
                    {applyMutation.isPending ? 'Envoi…' : 'Postuler à ce sujet'}
                  </Button>
                  {!withinWindow && (
                    <p className="text-xs text-muted-foreground">
                      La période de candidature pour ce sujet n'est pas ouverte.
                    </p>
                  )}
                  {withinWindow && reachedMax && (
                    <p className="text-xs text-muted-foreground">
                      Vous avez atteint le nombre maximal de candidatures autorisées.
                    </p>
                  )}
                </div>
              )}
            </CardContent>
          </Card>
        </div>
      </div>

      {applyMutation.isSuccess && (
        <div className="mt-4">
          <Button variant="link" className="px-0" onClick={() => navigate('/candidat/candidatures')}>
            Voir mes candidatures →
          </Button>
        </div>
      )}
    </>
  )
}

function isWithinWindow(start?: string, end?: string): boolean {
  if (!start || !end) return true
  const now = Date.now()
  const startTime = new Date(start).getTime()
  const endTime = new Date(end).getTime()
  if (Number.isNaN(startTime) || Number.isNaN(endTime)) return true
  return now >= startTime && now <= endTime
}
