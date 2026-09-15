import { useMutation } from '@tanstack/react-query'
import { useState } from 'react'
import { toast } from 'sonner'
import { ApiError } from '@/api/apiError'
import { updateExaminer } from '@/api/endpoints/professeur.api'
import { Button } from '@/components/ui/button'
import { Checkbox } from '@/components/ui/checkbox'
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'

const DECISIONS = ['Liste principale', "Liste d'attente", 'Rejeter', 'Absent']

/** Minimal shape needed to grade — matches both ExaminerDTO and ExaminerDTO1. */
export interface GradableExaminer {
  id: number
  noteDossier: number
  noteEntretien: number
  decision: string
  publier: boolean
}

export function ExaminerGradeDialog({
  examiner,
  open,
  onOpenChange,
  onGraded,
}: {
  examiner: GradableExaminer | null
  open: boolean
  onOpenChange: (open: boolean) => void
  onGraded: () => void
}) {
  const [noteDossier, setNoteDossier] = useState(0)
  const [noteEntretien, setNoteEntretien] = useState(0)
  const [decision, setDecision] = useState('')
  const [publier, setPublier] = useState(true)

  const handleOpenChange = (next: boolean) => {
    if (next && examiner) {
      setNoteDossier(examiner.noteDossier ?? 0)
      setNoteEntretien(examiner.noteEntretien ?? 0)
      setDecision(examiner.decision ?? '')
      setPublier(examiner.publier ?? true)
    }
    onOpenChange(next)
  }

  const mutation = useMutation({
    mutationFn: async () => {
      if (!examiner) return
      await updateExaminer(examiner.id, { noteDossier, noteEntretien, decision, publier })
    },
    onSuccess: () => {
      toast.success('Résultat enregistré.')
      onGraded()
      onOpenChange(false)
    },
    onError: (error) => {
      toast.error(error instanceof ApiError ? error.message : "Échec de l'enregistrement du résultat.")
    },
  })

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Noter le candidat</DialogTitle>
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
              <Label htmlFor="noteDossier">Note dossier</Label>
              <Input
                id="noteDossier"
                type="number"
                step="0.01"
                min={0}
                max={20}
                value={noteDossier}
                onChange={(e) => setNoteDossier(Number(e.target.value))}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="noteEntretien">Note entretien</Label>
              <Input
                id="noteEntretien"
                type="number"
                step="0.01"
                min={0}
                max={20}
                value={noteEntretien}
                onChange={(e) => setNoteEntretien(Number(e.target.value))}
              />
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="decision">Décision</Label>
            <Select value={decision || undefined} onValueChange={setDecision}>
              <SelectTrigger id="decision" className="w-full">
                <SelectValue placeholder="Sélectionner une décision" />
              </SelectTrigger>
              <SelectContent>
                {DECISIONS.map((d) => (
                  <SelectItem key={d} value={d}>
                    {d}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            <p className="text-xs text-muted-foreground">
              La décision « Liste principale » inscrit automatiquement le candidat sur le sujet.
            </p>
          </div>

          <label className="flex items-center gap-2 text-sm">
            <Checkbox checked={publier} onCheckedChange={(checked) => setPublier(checked === true)} />
            Publier ce résultat au candidat
          </label>

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
