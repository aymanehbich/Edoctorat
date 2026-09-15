import { useQuery } from '@tanstack/react-query'
import { CalendarCheckIcon, ClipboardListIcon, DownloadIcon } from 'lucide-react'
import { ApiError } from '@/api/apiError'
import { getMyResultats } from '@/api/endpoints/candidat.api'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { EmptyState } from '@/components/feedback/EmptyState'
import { ErrorBanner } from '@/components/feedback/ErrorBanner'
import { LoadingSkeleton } from '@/components/feedback/LoadingSkeleton'
import { downloadAttestationInscription } from '@/lib/inscriptionPdf'
import { PortalPageHeader } from '@/portals/shared/layout/PortalLayout'

export function CandidatResultatsPage() {
  const { data, isLoading, isError, error, refetch } = useQuery({
    queryKey: ['candidat-resultats'],
    queryFn: getMyResultats,
  })

  return (
    <>
      <PortalPageHeader
        title="Mes résultats"
        description="Suivez vos résultats d'examen et votre statut d'inscription."
      />

      {isLoading && <LoadingSkeleton rows={3} />}

      {isError && (
        <ErrorBanner
          message={error instanceof ApiError ? error.message : 'Impossible de charger vos résultats.'}
          onRetry={() => refetch()}
        />
      )}

      {!isLoading && !isError && data && (
        <div className="space-y-8">
          <section>
            <h2 className="mb-3 text-sm font-semibold text-muted-foreground">Résultats d'examen</h2>
            {data.examens.length === 0 ? (
              <EmptyState
                icon={ClipboardListIcon}
                title="Aucun résultat publié"
                description="Vos résultats apparaîtront ici une fois publiés par le jury."
              />
            ) : (
              <div className="grid gap-4 sm:grid-cols-2">
                {data.examens.map((examen) => (
                  <Card key={examen.id}>
                    <CardHeader>
                      <CardTitle className="text-base leading-snug">{examen.sujet.titre}</CardTitle>
                    </CardHeader>
                    <CardContent className="space-y-2">
                      <div className="flex flex-wrap items-center gap-2">
                        <Badge variant={examen.decision === 'Liste principale' ? 'default' : 'secondary'}>
                          {examen.decision || 'En attente'}
                        </Badge>
                      </div>
                      <p className="text-sm text-muted-foreground">
                        Note dossier : {examen.noteDossier} · Note entretien : {examen.noteEntretien}
                      </p>
                    </CardContent>
                  </Card>
                ))}
              </div>
            )}
          </section>

          <section>
            <h2 className="mb-3 text-sm font-semibold text-muted-foreground">Statut d'inscription</h2>
            {data.inscriptions.length === 0 ? (
              <EmptyState
                icon={CalendarCheckIcon}
                title="Aucune inscription"
                description="Votre inscription apparaîtra ici après votre admission."
              />
            ) : (
              <div className="grid gap-4 sm:grid-cols-2">
                {data.inscriptions.map((inscription) => (
                  <Card key={inscription.id}>
                    <CardHeader>
                      <CardTitle className="text-base leading-snug">{inscription.sujet.titre}</CardTitle>
                    </CardHeader>
                    <CardContent className="space-y-2">
                      <Badge variant={inscription.valider ? 'default' : 'secondary'}>
                        {inscription.valider ? 'Inscription validée' : "En attente de validation"}
                      </Badge>
                      {inscription.remarque && (
                        <p className="text-sm text-muted-foreground">{inscription.remarque}</p>
                      )}
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => downloadAttestationInscription(inscription)}
                      >
                        <DownloadIcon />
                        Télécharger l'attestation
                      </Button>
                    </CardContent>
                  </Card>
                ))}
              </div>
            )}
          </section>
        </div>
      )}
    </>
  )
}
