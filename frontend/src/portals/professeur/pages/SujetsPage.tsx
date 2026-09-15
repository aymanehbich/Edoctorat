import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { FlaskConicalIcon, PencilIcon, PlusIcon, Trash2Icon } from 'lucide-react'
import { useState } from 'react'
import { toast } from 'sonner'
import { ApiError } from '@/api/apiError'
import { getFormationsDoctorales } from '@/api/endpoints/formation.api'
import {
  createSujet,
  deleteSujet,
  getMySujets,
  getProfesseurs,
  updateSujet,
  type SujetFormFields,
} from '@/api/endpoints/professeur.api'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { ConfirmDialog } from '@/components/feedback/ConfirmDialog'
import { EmptyState } from '@/components/feedback/EmptyState'
import { ErrorBanner } from '@/components/feedback/ErrorBanner'
import { LoadingSkeleton } from '@/components/feedback/LoadingSkeleton'
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Textarea } from '@/components/ui/textarea'
import { PortalPageHeader } from '@/portals/shared/layout/PortalLayout'
import type { SujetDTO } from '@/types/dto/SujetDTO'

const SUJETS_QUERY_KEY = ['professeur-sujets']

const EMPTY_FIELDS: SujetFormFields = {
  titre: '',
  description: '',
  coDirecteurId: 0,
  formationDoctoraleId: 0,
}

export function ProfesseurSujetsPage() {
  const queryClient = useQueryClient()
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<SujetDTO | null>(null)
  const [deletingId, setDeletingId] = useState<number | null>(null)

  const { data, isLoading, isError, error, refetch } = useQuery({
    queryKey: SUJETS_QUERY_KEY,
    queryFn: getMySujets,
  })

  const deleteMutation = useMutation({
    mutationFn: deleteSujet,
    onSuccess: () => {
      toast.success('Sujet supprimé.')
      queryClient.invalidateQueries({ queryKey: SUJETS_QUERY_KEY })
      setDeletingId(null)
    },
    onError: (error) => {
      toast.error(error instanceof ApiError ? error.message : 'Échec de la suppression.')
    },
  })

  const publishMutation = useMutation({
    mutationFn: (sujet: SujetDTO) =>
      updateSujet(sujet.id, {
        titre: sujet.titre,
        description: sujet.description,
        coDirecteurId: sujet.coDirecteur?.id ?? 0,
        formationDoctoraleId: sujet.formationDoctorale.id,
        publier: !sujet.publier,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: SUJETS_QUERY_KEY })
    },
    onError: (error) => {
      toast.error(error instanceof ApiError ? error.message : 'Échec de la mise à jour.')
    },
  })

  const openCreate = () => {
    setEditing(null)
    setDialogOpen(true)
  }

  const openEdit = (sujet: SujetDTO) => {
    setEditing(sujet)
    setDialogOpen(true)
  }

  return (
    <>
      <PortalPageHeader
        title="Mes sujets"
        description="Gérez les sujets de thèse que vous proposez."
        action={
          <Button onClick={openCreate}>
            <PlusIcon className="size-4" />
            Proposer un sujet
          </Button>
        }
      />

      {isLoading && <LoadingSkeleton rows={3} />}

      {isError && (
        <ErrorBanner
          message={error instanceof ApiError ? error.message : 'Impossible de charger vos sujets.'}
          onRetry={() => refetch()}
        />
      )}

      {!isLoading && !isError && data && data.results.length === 0 && (
        <EmptyState
          icon={FlaskConicalIcon}
          title="Aucun sujet"
          description="Proposez votre premier sujet de thèse."
          action={
            <Button onClick={openCreate} size="sm">
              <PlusIcon className="size-4" />
              Proposer un sujet
            </Button>
          }
        />
      )}

      {!isLoading && !isError && data && data.results.length > 0 && (
        <div className="grid gap-4 sm:grid-cols-2">
          {data.results.map((sujet) => (
            <Card key={sujet.id}>
              <CardHeader className="flex flex-row items-start justify-between gap-2">
                <div>
                  <CardTitle className="text-base leading-snug">{sujet.titre}</CardTitle>
                  <p className="text-sm text-muted-foreground">{sujet.formationDoctorale?.titre}</p>
                </div>
                <div className="flex shrink-0 gap-1">
                  <Button variant="ghost" size="icon" onClick={() => openEdit(sujet)} aria-label="Modifier">
                    <PencilIcon className="size-4" />
                  </Button>
                  <Button
                    variant="ghost"
                    size="icon"
                    onClick={() => setDeletingId(sujet.id)}
                    aria-label="Supprimer"
                  >
                    <Trash2Icon className="size-4 text-destructive" />
                  </Button>
                </div>
              </CardHeader>
              <CardContent className="space-y-3">
                <p className="line-clamp-3 text-sm text-muted-foreground">{sujet.description}</p>
                <div className="flex flex-wrap items-center gap-2">
                  <Badge
                    variant={sujet.publier ? 'default' : 'secondary'}
                    className="cursor-pointer"
                    onClick={() => publishMutation.mutate(sujet)}
                  >
                    {sujet.publier ? 'Publié' : 'Non publié'}
                  </Badge>
                  {sujet.coDirecteur && (
                    <Badge variant="outline">
                      Co-directeur : {sujet.coDirecteur.prenom} {sujet.coDirecteur.nom}
                    </Badge>
                  )}
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}

      <SujetFormDialog open={dialogOpen} onOpenChange={setDialogOpen} editing={editing} />

      <ConfirmDialog
        open={deletingId !== null}
        onOpenChange={(open) => !open && setDeletingId(null)}
        title="Supprimer ce sujet ?"
        description="Cette action est irréversible."
        destructive
        confirmLabel="Supprimer"
        loading={deleteMutation.isPending}
        onConfirm={() => deletingId !== null && deleteMutation.mutate(deletingId)}
      />
    </>
  )
}

function SujetFormDialog({
  open,
  onOpenChange,
  editing,
}: {
  open: boolean
  onOpenChange: (open: boolean) => void
  editing: SujetDTO | null
}) {
  const queryClient = useQueryClient()
  const [fields, setFields] = useState<SujetFormFields>(EMPTY_FIELDS)

  const isEdit = editing !== null

  const { data: professeurs } = useQuery({ queryKey: ['professeurs'], queryFn: getProfesseurs })
  const { data: formations } = useQuery({ queryKey: ['formations-doctorales'], queryFn: getFormationsDoctorales })

  const resetToEditing = (s: SujetDTO | null) => {
    setFields(
      s
        ? {
            titre: s.titre,
            description: s.description,
            coDirecteurId: s.coDirecteur?.id ?? 0,
            formationDoctoraleId: s.formationDoctorale?.id ?? 0,
            publier: s.publier,
          }
        : EMPTY_FIELDS,
    )
  }

  const handleOpenChange = (next: boolean) => {
    if (next) resetToEditing(editing)
    onOpenChange(next)
  }

  const mutation = useMutation({
    mutationFn: async () => {
      if (isEdit) {
        await updateSujet(editing.id, fields)
      } else {
        await createSujet(fields)
      }
    },
    onSuccess: () => {
      toast.success(isEdit ? 'Sujet mis à jour.' : 'Sujet proposé.')
      queryClient.invalidateQueries({ queryKey: SUJETS_QUERY_KEY })
      onOpenChange(false)
    },
    onError: (error) => {
      toast.error(error instanceof ApiError ? error.message : "Échec de l'enregistrement du sujet.")
    },
  })

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogContent className="max-h-[85vh] overflow-y-auto sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>{isEdit ? 'Modifier le sujet' : 'Proposer un sujet'}</DialogTitle>
        </DialogHeader>

        <form
          className="space-y-4"
          onSubmit={(e) => {
            e.preventDefault()
            mutation.mutate()
          }}
        >
          <div className="space-y-2">
            <Label htmlFor="titre">Titre</Label>
            <Input
              id="titre"
              required
              value={fields.titre}
              onChange={(e) => setFields((f) => ({ ...f, titre: e.target.value }))}
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="description">Description</Label>
            <Textarea
              id="description"
              required
              rows={4}
              value={fields.description}
              onChange={(e) => setFields((f) => ({ ...f, description: e.target.value }))}
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="formationDoctorale">Formation doctorale</Label>
            <Select
              value={fields.formationDoctoraleId ? String(fields.formationDoctoraleId) : undefined}
              onValueChange={(value) => setFields((f) => ({ ...f, formationDoctoraleId: Number(value) }))}
            >
              <SelectTrigger id="formationDoctorale" className="w-full">
                <SelectValue placeholder="Sélectionner une formation" />
              </SelectTrigger>
              <SelectContent>
                {formations?.results.map((formation) => (
                  <SelectItem key={formation.id} value={String(formation.id)}>
                    {formation.titre}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="space-y-2">
            <Label htmlFor="coDirecteur">Co-directeur</Label>
            <Select
              value={fields.coDirecteurId ? String(fields.coDirecteurId) : undefined}
              onValueChange={(value) => setFields((f) => ({ ...f, coDirecteurId: Number(value) }))}
            >
              <SelectTrigger id="coDirecteur" className="w-full">
                <SelectValue placeholder="Sélectionner un co-directeur" />
              </SelectTrigger>
              <SelectContent>
                {professeurs?.results.map((professeur) => (
                  <SelectItem key={professeur.id} value={String(professeur.id)}>
                    {professeur.prenom} {professeur.nom}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Annuler
            </Button>
            <Button
              type="submit"
              disabled={mutation.isPending || !fields.formationDoctoraleId || !fields.coDirecteurId}
            >
              {mutation.isPending ? 'Enregistrement…' : 'Enregistrer'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
