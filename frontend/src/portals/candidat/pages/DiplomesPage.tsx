import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { FileIcon, PencilIcon, PlusIcon, Trash2Icon } from 'lucide-react'
import { useRef, useState } from 'react'
import { toast } from 'sonner'
import { ApiError } from '@/api/apiError'
import {
  createDiplome,
  deleteDiplome,
  getMyDiplomes,
  updateDiplomeWithFiles,
  type DiplomeFormFields,
} from '@/api/endpoints/diplome.api'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { ConfirmDialog } from '@/components/feedback/ConfirmDialog'
import { EmptyState } from '@/components/feedback/EmptyState'
import { ErrorBanner } from '@/components/feedback/ErrorBanner'
import { LoadingSkeleton } from '@/components/feedback/LoadingSkeleton'
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { PortalPageHeader } from '@/portals/shared/layout/PortalLayout'
import type { DiplomeDTO } from '@/types/dto/DiplomeDTO'

const DIPLOMES_QUERY_KEY = ['candidat-diplomes']

const EMPTY_FIELDS: DiplomeFormFields = {
  intitule: '',
  type: '',
  dateCommission: '',
  mention: '',
  pays: '',
  etablissement: '',
  specialite: '',
  ville: '',
  province: '',
  moyen_generale: 0,
}

export function CandidatDiplomesPage() {
  const queryClient = useQueryClient()
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<DiplomeDTO | null>(null)
  const [deletingId, setDeletingId] = useState<number | null>(null)

  const { data, isLoading, isError, error, refetch } = useQuery({
    queryKey: DIPLOMES_QUERY_KEY,
    queryFn: getMyDiplomes,
  })

  const deleteMutation = useMutation({
    mutationFn: deleteDiplome,
    onSuccess: () => {
      toast.success('Diplôme supprimé.')
      queryClient.invalidateQueries({ queryKey: DIPLOMES_QUERY_KEY })
      setDeletingId(null)
    },
    onError: (error) => {
      toast.error(error instanceof ApiError ? error.message : 'Échec de la suppression.')
    },
  })

  const openCreate = () => {
    setEditing(null)
    setDialogOpen(true)
  }

  const openEdit = (diplome: DiplomeDTO) => {
    setEditing(diplome)
    setDialogOpen(true)
  }

  return (
    <>
      <PortalPageHeader
        title="Diplômes"
        description="Ajoutez vos diplômes et relevés de notes."
        action={
          <Button onClick={openCreate}>
            <PlusIcon className="size-4" />
            Ajouter un diplôme
          </Button>
        }
      />

      {isLoading && <LoadingSkeleton rows={3} />}

      {isError && (
        <ErrorBanner
          message={error instanceof ApiError ? error.message : 'Impossible de charger vos diplômes.'}
          onRetry={() => refetch()}
        />
      )}

      {!isLoading && !isError && data && data.results.length === 0 && (
        <EmptyState
          icon={FileIcon}
          title="Aucun diplôme"
          description="Ajoutez votre premier diplôme pour compléter votre dossier."
          action={
            <Button onClick={openCreate} size="sm">
              <PlusIcon className="size-4" />
              Ajouter un diplôme
            </Button>
          }
        />
      )}

      {!isLoading && !isError && data && data.results.length > 0 && (
        <div className="grid gap-4 sm:grid-cols-2">
          {data.results.map((diplome) => (
            <Card key={diplome.id}>
              <CardHeader className="flex flex-row items-start justify-between gap-2">
                <div>
                  <CardTitle className="text-base">{diplome.intitule}</CardTitle>
                  <p className="text-sm text-muted-foreground">
                    {diplome.type} · {diplome.mention}
                  </p>
                </div>
                <div className="flex gap-1">
                  <Button variant="ghost" size="icon" onClick={() => openEdit(diplome)} aria-label="Modifier">
                    <PencilIcon className="size-4" />
                  </Button>
                  <Button
                    variant="ghost"
                    size="icon"
                    onClick={() => setDeletingId(diplome.id)}
                    aria-label="Supprimer"
                  >
                    <Trash2Icon className="size-4 text-destructive" />
                  </Button>
                </div>
              </CardHeader>
              <CardContent className="space-y-2 text-sm">
                <p>
                  {diplome.etablissement} — {diplome.ville}, {diplome.pays}
                </p>
                <p className="text-muted-foreground">
                  {diplome.specialite} · Moyenne : {diplome.moyen_generale}
                </p>
                {diplome.annexes.length > 0 && (
                  <div className="flex flex-wrap gap-2 pt-1">
                    {diplome.annexes.map((annexe) => (
                      <a
                        key={annexe.id}
                        href={annexe.pathFile}
                        target="_blank"
                        rel="noreferrer"
                        className="inline-flex items-center gap-1 rounded-md border px-2 py-1 text-xs text-muted-foreground hover:bg-accent"
                      >
                        <FileIcon className="size-3" />
                        {annexe.typeAnnexe}
                      </a>
                    ))}
                  </div>
                )}
              </CardContent>
            </Card>
          ))}
        </div>
      )}

      <DiplomeFormDialog open={dialogOpen} onOpenChange={setDialogOpen} editing={editing} />

      <ConfirmDialog
        open={deletingId !== null}
        onOpenChange={(open) => !open && setDeletingId(null)}
        title="Supprimer ce diplôme ?"
        description="Cette action est irréversible."
        destructive
        confirmLabel="Supprimer"
        loading={deleteMutation.isPending}
        onConfirm={() => deletingId !== null && deleteMutation.mutate(deletingId)}
      />
    </>
  )
}

function DiplomeFormDialog({
  open,
  onOpenChange,
  editing,
}: {
  open: boolean
  onOpenChange: (open: boolean) => void
  editing: DiplomeDTO | null
}) {
  const queryClient = useQueryClient()
  const diplomeFileRef = useRef<HTMLInputElement>(null)
  const releveFileRef = useRef<HTMLInputElement>(null)
  const releveFilesRef = useRef<HTMLInputElement>(null)
  const [fields, setFields] = useState<DiplomeFormFields>(EMPTY_FIELDS)

  const isEdit = editing !== null

  const resetToEditing = (d: DiplomeDTO | null) => {
    setFields(
      d
        ? {
            intitule: d.intitule,
            type: d.type,
            dateCommission: d.dateCommission?.slice(0, 10) ?? '',
            mention: d.mention,
            pays: d.pays,
            etablissement: d.etablissement,
            specialite: d.specialite,
            ville: d.ville,
            province: d.province,
            moyen_generale: d.moyen_generale,
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
        await updateDiplomeWithFiles(editing.id, fields, {
          diplomeFile: diplomeFileRef.current?.files?.[0],
          releveFile: releveFileRef.current?.files?.[0],
        })
      } else {
        await createDiplome(fields, {
          diplomeFile: diplomeFileRef.current?.files?.[0],
          releveFile: releveFileRef.current?.files?.[0],
          relevefile: releveFilesRef.current?.files ? Array.from(releveFilesRef.current.files) : undefined,
        })
      }
    },
    onSuccess: () => {
      toast.success(isEdit ? 'Diplôme mis à jour.' : 'Diplôme ajouté.')
      queryClient.invalidateQueries({ queryKey: DIPLOMES_QUERY_KEY })
      onOpenChange(false)
    },
    onError: (error) => {
      toast.error(error instanceof ApiError ? error.message : "Échec de l'enregistrement du diplôme.")
    },
  })

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogContent className="max-h-[85vh] overflow-y-auto sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>{isEdit ? 'Modifier le diplôme' : 'Ajouter un diplôme'}</DialogTitle>
        </DialogHeader>

        <form
          className="space-y-4"
          onSubmit={(e) => {
            e.preventDefault()
            mutation.mutate()
          }}
        >
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="intitule">Intitulé</Label>
              <Input
                id="intitule"
                required
                value={fields.intitule}
                onChange={(e) => setFields((f) => ({ ...f, intitule: e.target.value }))}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="type">Type</Label>
              <Input
                id="type"
                required
                value={fields.type}
                onChange={(e) => setFields((f) => ({ ...f, type: e.target.value }))}
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="dateCommission">Date d'obtention</Label>
              <Input
                id="dateCommission"
                type="date"
                required
                value={fields.dateCommission}
                onChange={(e) => setFields((f) => ({ ...f, dateCommission: e.target.value }))}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="mention">Mention</Label>
              <Input
                id="mention"
                value={fields.mention}
                onChange={(e) => setFields((f) => ({ ...f, mention: e.target.value }))}
              />
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="etablissement">Établissement</Label>
            <Input
              id="etablissement"
              value={fields.etablissement}
              onChange={(e) => setFields((f) => ({ ...f, etablissement: e.target.value }))}
            />
          </div>

          <div className="grid grid-cols-3 gap-4">
            <div className="space-y-2">
              <Label htmlFor="ville">Ville</Label>
              <Input
                id="ville"
                value={fields.ville}
                onChange={(e) => setFields((f) => ({ ...f, ville: e.target.value }))}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="province">Province</Label>
              <Input
                id="province"
                value={fields.province}
                onChange={(e) => setFields((f) => ({ ...f, province: e.target.value }))}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="pays">Pays</Label>
              <Input
                id="pays"
                value={fields.pays}
                onChange={(e) => setFields((f) => ({ ...f, pays: e.target.value }))}
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="specialite">Spécialité</Label>
              <Input
                id="specialite"
                value={fields.specialite}
                onChange={(e) => setFields((f) => ({ ...f, specialite: e.target.value }))}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="moyen_generale">Moyenne générale</Label>
              <Input
                id="moyen_generale"
                type="number"
                step="0.01"
                required
                value={fields.moyen_generale}
                onChange={(e) => setFields((f) => ({ ...f, moyen_generale: Number(e.target.value) }))}
              />
            </div>
          </div>

          <div className="space-y-3 rounded-md border p-3">
            <div className="space-y-1">
              <Label htmlFor="diplomeFile">Copie du diplôme (PDF)</Label>
              <Input id="diplomeFile" ref={diplomeFileRef} type="file" accept="application/pdf" />
            </div>
            <div className="space-y-1">
              <Label htmlFor="releveFile">Relevé de notes (PDF)</Label>
              <Input id="releveFile" ref={releveFileRef} type="file" accept="application/pdf" />
            </div>
            {!isEdit && (
              <div className="space-y-1">
                <Label htmlFor="relevefile">Relevés supplémentaires (PDF, plusieurs fichiers)</Label>
                <Input id="relevefile" ref={releveFilesRef} type="file" accept="application/pdf" multiple />
              </div>
            )}
          </div>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Annuler
            </Button>
            <Button type="submit" disabled={mutation.isPending}>
              {mutation.isPending ? 'Enregistrement…' : 'Enregistrer'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
