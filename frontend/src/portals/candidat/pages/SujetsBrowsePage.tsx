import { useQuery } from '@tanstack/react-query'
import { FlaskConicalIcon, SearchIcon } from 'lucide-react'
import { useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { ApiError } from '@/api/apiError'
import { getPublishedSujets } from '@/api/endpoints/sujet.api'
import { Badge } from '@/components/ui/badge'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { EmptyState } from '@/components/feedback/EmptyState'
import { ErrorBanner } from '@/components/feedback/ErrorBanner'
import { LoadingSkeleton } from '@/components/feedback/LoadingSkeleton'
import { Input } from '@/components/ui/input'
import { PortalPageHeader } from '@/portals/shared/layout/PortalLayout'

export function SujetsBrowsePage() {
  const [search, setSearch] = useState('')
  const { data, isLoading, isError, error, refetch } = useQuery({
    queryKey: ['published-sujets'],
    queryFn: getPublishedSujets,
  })

  const filtered = useMemo(() => {
    if (!data) return []
    const q = search.trim().toLowerCase()
    if (!q) return data.results
    return data.results.filter(
      (s) =>
        s.titre.toLowerCase().includes(q) ||
        s.description.toLowerCase().includes(q) ||
        `${s.professeur.prenom} ${s.professeur.nom}`.toLowerCase().includes(q),
    )
  }, [data, search])

  return (
    <>
      <PortalPageHeader
        title="Sujets de thèse"
        description="Parcourez les sujets proposés et déposez votre candidature."
      />

      <div className="relative mb-4 max-w-sm">
        <SearchIcon className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          placeholder="Rechercher un sujet, un professeur…"
          className="pl-9"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {isLoading && <LoadingSkeleton rows={4} />}

      {isError && (
        <ErrorBanner
          message={error instanceof ApiError ? error.message : 'Impossible de charger les sujets.'}
          onRetry={() => refetch()}
        />
      )}

      {!isLoading && !isError && filtered.length === 0 && (
        <EmptyState icon={FlaskConicalIcon} title="Aucun sujet trouvé" />
      )}

      {!isLoading && !isError && filtered.length > 0 && (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {filtered.map((sujet) => (
            <Link key={sujet.id} to={`/candidat/sujets/${sujet.id}`}>
              <Card className="h-full transition-colors hover:border-primary">
                <CardHeader>
                  <CardTitle className="text-base leading-snug">{sujet.titre}</CardTitle>
                </CardHeader>
                <CardContent className="space-y-3">
                  <p className="line-clamp-3 text-sm text-muted-foreground">{sujet.description}</p>
                  <div className="flex flex-wrap items-center gap-2">
                    <Badge variant="secondary">
                      {sujet.professeur.prenom} {sujet.professeur.nom}
                    </Badge>
                    {sujet.formationDoctorale && (
                      <Badge variant="outline">{sujet.formationDoctorale.titre}</Badge>
                    )}
                  </div>
                </CardContent>
              </Card>
            </Link>
          ))}
        </div>
      )}
    </>
  )
}
