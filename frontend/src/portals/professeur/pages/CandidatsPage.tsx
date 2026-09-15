import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { CalendarPlusIcon, PaperclipIcon, UsersIcon } from 'lucide-react'
import { useState } from 'react'
import { toast } from 'sonner'
import { ApiError } from '@/api/apiError'
import { createCommission, getMyCandidats, getProfesseurs } from '@/api/endpoints/professeur.api'
import { Button } from '@/components/ui/button'
import { Checkbox } from '@/components/ui/checkbox'
import { EmptyState } from '@/components/feedback/EmptyState'
import { ErrorBanner } from '@/components/feedback/ErrorBanner'
import { LoadingSkeleton } from '@/components/feedback/LoadingSkeleton'
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { PortalPageHeader } from '@/portals/shared/layout/PortalLayout'

const CANDIDATS_QUERY_KEY = ['professeur-candidats']

export function ProfesseurCandidatsPage() {
  const [selected, setSelected] = useState<Set<number>>(new Set())
  const [scheduleOpen, setScheduleOpen] = useState(false)

  const { data, isLoading, isError, error, refetch } = useQuery({
    queryKey: CANDIDATS_QUERY_KEY,
    queryFn: getMyCandidats,
  })

  const toggle = (id: number) => {
    setSelected((prev) => {
      const next = new Set(prev)
      if (next.has(id)) next.delete(id)
      else next.add(id)
      return next
    })
  }

  return (
    <>
      <PortalPageHeader
        title="Candidats"
        description="Candidats ayant postulé à l'un de vos sujets de thèse."
        action={
          <Button disabled={selected.size === 0} onClick={() => setScheduleOpen(true)}>
            <CalendarPlusIcon className="size-4" />
            Programmer une commission {selected.size > 0 && `(${selected.size})`}
          </Button>
        }
      />

      {isLoading && <LoadingSkeleton rows={4} />}

      {isError && (
        <ErrorBanner
          message={error instanceof ApiError ? error.message : 'Impossible de charger les candidats.'}
          onRetry={() => refetch()}
        />
      )}

      {!isLoading && !isError && data && data.results.length === 0 && (
        <EmptyState icon={UsersIcon} title="Aucun candidat" description="Personne n'a encore postulé à vos sujets." />
      )}

      {!isLoading && !isError && data && data.results.length > 0 && (
        <div className="rounded-md border">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead className="w-px" />
                <TableHead>Candidat</TableHead>
                <TableHead>CNE</TableHead>
                <TableHead>Email</TableHead>
                <TableHead>Sujet</TableHead>
                <TableHead>Dossier</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {data.results.map((postuler) => (
                <TableRow key={postuler.id}>
                  <TableCell>
                    <Checkbox
                      checked={selected.has(postuler.id)}
                      onCheckedChange={() => toggle(postuler.id)}
                      aria-label="Sélectionner"
                    />
                  </TableCell>
                  <TableCell className="font-medium">
                    {postuler.candidat.prenom} {postuler.candidat.nom}
                  </TableCell>
                  <TableCell>{postuler.candidat.cne}</TableCell>
                  <TableCell>{postuler.candidat.email}</TableCell>
                  <TableCell className="max-w-64 truncate">{postuler.sujet.titre}</TableCell>
                  <TableCell>
                    {postuler.pathFile ? (
                      <a
                        href={postuler.pathFile}
                        target="_blank"
                        rel="noreferrer"
                        className="inline-flex items-center gap-1 text-xs text-muted-foreground hover:text-foreground hover:underline"
                      >
                        <PaperclipIcon className="size-3" />
                        Voir le fichier
                      </a>
                    ) : (
                      <span className="text-xs text-muted-foreground">—</span>
                    )}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>
      )}

      <ScheduleCommissionDialog
        open={scheduleOpen}
        onOpenChange={setScheduleOpen}
        postulerIds={[...selected]}
        onScheduled={() => setSelected(new Set())}
      />
    </>
  )
}

function ScheduleCommissionDialog({
  open,
  onOpenChange,
  postulerIds,
  onScheduled,
}: {
  open: boolean
  onOpenChange: (open: boolean) => void
  postulerIds: number[]
  onScheduled: () => void
}) {
  const queryClient = useQueryClient()
  const [dateCommission, setDateCommission] = useState('')
  const [heure, setHeure] = useState('')
  const [lieu, setLieu] = useState('')
  const [jury, setJury] = useState<Set<number>>(new Set())

  const { data: professeurs } = useQuery({ queryKey: ['professeurs'], queryFn: getProfesseurs })

  const toggleJury = (id: number) => {
    setJury((prev) => {
      const next = new Set(prev)
      if (next.has(id)) next.delete(id)
      else next.add(id)
      return next
    })
  }

  const mutation = useMutation({
    mutationFn: () =>
      createCommission({
        dateCommission,
        heure,
        lieu,
        professeurIds: [...jury],
        postulerIds,
      }),
    onSuccess: () => {
      toast.success('Commission programmée.')
      queryClient.invalidateQueries({ queryKey: CANDIDATS_QUERY_KEY })
      onScheduled()
      onOpenChange(false)
      setDateCommission('')
      setHeure('')
      setLieu('')
      setJury(new Set())
    },
    onError: (error) => {
      toast.error(error instanceof ApiError ? error.message : "Échec de la programmation de la commission.")
    },
  })

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-h-[85vh] overflow-y-auto sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>Programmer une commission</DialogTitle>
        </DialogHeader>

        <form
          className="space-y-4"
          onSubmit={(e) => {
            e.preventDefault()
            mutation.mutate()
          }}
        >
          <p className="text-sm text-muted-foreground">
            {postulerIds.length} candidature{postulerIds.length > 1 ? 's' : ''} sera{postulerIds.length > 1 ? 'ont' : ''}{' '}
            examinée{postulerIds.length > 1 ? 's' : ''} lors de cette session.
          </p>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="dateCommission">Date</Label>
              <Input
                id="dateCommission"
                type="date"
                required
                value={dateCommission}
                onChange={(e) => setDateCommission(e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="heure">Heure</Label>
              <Input id="heure" type="time" required value={heure} onChange={(e) => setHeure(e.target.value)} />
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="lieu">Lieu</Label>
            <Input id="lieu" required value={lieu} onChange={(e) => setLieu(e.target.value)} />
          </div>

          <div className="space-y-2">
            <Label>Jury</Label>
            <div className="max-h-40 space-y-2 overflow-y-auto rounded-md border p-3">
              {professeurs?.results.map((professeur) => (
                <label key={professeur.id} className="flex items-center gap-2 text-sm">
                  <Checkbox
                    checked={jury.has(professeur.id)}
                    onCheckedChange={() => toggleJury(professeur.id)}
                  />
                  {professeur.prenom} {professeur.nom}
                </label>
              ))}
            </div>
          </div>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Annuler
            </Button>
            <Button type="submit" disabled={mutation.isPending || jury.size === 0}>
              {mutation.isPending ? 'Programmation…' : 'Programmer'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
