import { useQuery, useQueryClient, useMutation } from '@tanstack/react-query'
import { FileTextIcon, ImageIcon } from 'lucide-react'
import { useEffect, useRef, useState } from 'react'
import { useForm } from 'react-hook-form'
import { toast } from 'sonner'
import { ApiError } from '@/api/apiError'
import { getMyCandidatInfo, updateMyCandidatInfo, type UpdateCandidatInfoFields } from '@/api/endpoints/candidat.api'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { ErrorBanner } from '@/components/feedback/ErrorBanner'
import { LoadingSkeleton } from '@/components/feedback/LoadingSkeleton'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { PortalPageHeader } from '@/portals/shared/layout/PortalLayout'
import type { CandidatDTO } from '@/types/dto/CandidatDTO'

const CANDIDAT_INFO_QUERY_KEY = ['candidat-info']

export function CandidatProfilePage() {
  const { data, isLoading, isError, error, refetch } = useQuery({
    queryKey: CANDIDAT_INFO_QUERY_KEY,
    queryFn: getMyCandidatInfo,
  })

  if (isLoading) {
    return (
      <>
        <PortalPageHeader title="Mon profil" />
        <LoadingSkeleton rows={6} />
      </>
    )
  }

  if (isError || !data) {
    return (
      <>
        <PortalPageHeader title="Mon profil" />
        <ErrorBanner
          message={error instanceof ApiError ? error.message : 'Impossible de charger votre profil.'}
          onRetry={() => refetch()}
        />
      </>
    )
  }

  return (
    <>
      <PortalPageHeader title="Mon profil" description="Consultez et mettez à jour vos informations personnelles." />
      <ProfileForm candidat={data} />
    </>
  )
}

function ProfileForm({ candidat }: { candidat: CandidatDTO }) {
  const queryClient = useQueryClient()
  const cvInputRef = useRef<HTMLInputElement>(null)
  const photoInputRef = useRef<HTMLInputElement>(null)
  const [photoPreview, setPhotoPreview] = useState<string | null>(null)

  const { register, handleSubmit, reset } = useForm<UpdateCandidatInfoFields>({
    defaultValues: {
      nom: candidat.nom,
      prenom: candidat.prenom,
      cne: candidat.cne,
      pays: candidat.pays,
      mailCandidat: candidat.email,
      cin: candidat.cin,
      nomCandidatAr: candidat.nomCandidatAr,
      prenomCandidatAr: candidat.prenomCandidatAr,
      adresse: candidat.adresse,
      adresseAr: candidat.adresseAr,
      sexe: candidat.sexe,
      villeDeNaissance: candidat.villeDeNaissance,
      villeDeNaissanceAr: candidat.villeDeNaissanceAr,
      ville: candidat.ville,
      dateDeNaissance: candidat.dateDeNaissance,
      academie: candidat.academie,
      telCandidat: candidat.telCandidat,
      situation_familiale: candidat.situation_familiale,
      fonctionnaire: candidat.fonctionnaire,
    },
  })

  useEffect(() => {
    reset({
      nom: candidat.nom,
      prenom: candidat.prenom,
      cne: candidat.cne,
      pays: candidat.pays,
      mailCandidat: candidat.email,
      cin: candidat.cin,
      nomCandidatAr: candidat.nomCandidatAr,
      prenomCandidatAr: candidat.prenomCandidatAr,
      adresse: candidat.adresse,
      adresseAr: candidat.adresseAr,
      sexe: candidat.sexe,
      villeDeNaissance: candidat.villeDeNaissance,
      villeDeNaissanceAr: candidat.villeDeNaissanceAr,
      ville: candidat.ville,
      dateDeNaissance: candidat.dateDeNaissance,
      academie: candidat.academie,
      telCandidat: candidat.telCandidat,
      situation_familiale: candidat.situation_familiale,
      fonctionnaire: candidat.fonctionnaire,
    })
  }, [candidat, reset])

  const mutation = useMutation({
    mutationFn: (vars: { fields: UpdateCandidatInfoFields; files: { cv?: File; photo?: File } }) =>
      updateMyCandidatInfo(vars.fields, vars.files),
    onSuccess: () => {
      toast.success('Profil mis à jour avec succès.')
      queryClient.invalidateQueries({ queryKey: CANDIDAT_INFO_QUERY_KEY })
    },
    onError: (error) => {
      toast.error(error instanceof ApiError ? error.message : 'Échec de la mise à jour du profil.')
    },
  })

  const onSubmit = (values: UpdateCandidatInfoFields) => {
    const files: { cv?: File; photo?: File } = {}
    if (cvInputRef.current?.files?.[0]) files.cv = cvInputRef.current.files[0]
    if (photoInputRef.current?.files?.[0]) files.photo = photoInputRef.current.files[0]
    mutation.mutate({ fields: values, files })
  }

  // pathPhoto/pathCv already come back as absolute "/uploads/..." URLs — do not re-prefix.
  const photoUrl = photoPreview ?? candidat.pathPhoto ?? undefined

  return (
    <form className="space-y-6" onSubmit={handleSubmit(onSubmit)} noValidate>
      <Card>
        <CardHeader>
          <CardTitle className="text-base">Photo et documents</CardTitle>
        </CardHeader>
        <CardContent className="flex flex-wrap items-center gap-8">
          <div className="flex items-center gap-4">
            <Avatar className="size-16">
              <AvatarImage src={photoUrl} alt="" />
              <AvatarFallback>
                {candidat.prenom.charAt(0)}
                {candidat.nom.charAt(0)}
              </AvatarFallback>
            </Avatar>
            <div>
              <input
                ref={photoInputRef}
                type="file"
                accept="image/*"
                hidden
                onChange={(e) => {
                  const file = e.target.files?.[0]
                  if (file) setPhotoPreview(URL.createObjectURL(file))
                }}
              />
              <Button type="button" variant="outline" size="sm" onClick={() => photoInputRef.current?.click()}>
                <ImageIcon className="size-4" />
                Changer la photo
              </Button>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <input ref={cvInputRef} type="file" accept="application/pdf" hidden />
            <Button type="button" variant="outline" size="sm" onClick={() => cvInputRef.current?.click()}>
              <FileTextIcon className="size-4" />
              {candidat.pathCv ? 'Remplacer mon CV' : 'Ajouter mon CV'}
            </Button>
            {candidat.pathCv && (
              <a
                href={candidat.pathCv}
                target="_blank"
                rel="noreferrer"
                className="text-sm text-muted-foreground underline underline-offset-2 hover:text-foreground"
              >
                Voir mon CV
              </a>
            )}
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Identité</CardTitle>
        </CardHeader>
        <CardContent className="grid gap-4 sm:grid-cols-2">
          <Field label="Nom" id="nom" register={register('nom')} />
          <Field label="Prénom" id="prenom" register={register('prenom')} />
          <Field label="Nom (arabe)" id="nomCandidatAr" register={register('nomCandidatAr')} dir="rtl" />
          <Field label="Prénom (arabe)" id="prenomCandidatAr" register={register('prenomCandidatAr')} dir="rtl" />
          <Field label="CIN" id="cin" register={register('cin')} />
          <Field label="CNE" id="cne" register={register('cne')} />
          <Field label="Date de naissance" id="dateDeNaissance" type="date" register={register('dateDeNaissance')} />
          <Field label="Sexe" id="sexe" register={register('sexe')} />
          <Field label="Ville de naissance" id="villeDeNaissance" register={register('villeDeNaissance')} />
          <Field
            label="Ville de naissance (arabe)"
            id="villeDeNaissanceAr"
            register={register('villeDeNaissanceAr')}
            dir="rtl"
          />
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Coordonnées</CardTitle>
        </CardHeader>
        <CardContent className="grid gap-4 sm:grid-cols-2">
          <Field label="E-mail" id="mailCandidat" type="email" register={register('mailCandidat')} />
          <Field label="Téléphone" id="telCandidat" register={register('telCandidat')} />
          <Field label="Pays" id="pays" register={register('pays')} />
          <Field label="Ville de résidence" id="ville" register={register('ville')} />
          <Field label="Adresse" id="adresse" register={register('adresse')} className="sm:col-span-2" />
          <Field label="Adresse (arabe)" id="adresseAr" register={register('adresseAr')} dir="rtl" className="sm:col-span-2" />
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Situation</CardTitle>
        </CardHeader>
        <CardContent className="grid gap-4 sm:grid-cols-2">
          <Field label="Académie" id="academie" register={register('academie')} />
          <Field label="Situation familiale" id="situation_familiale" register={register('situation_familiale')} />
          <Field label="Fonctionnaire" id="fonctionnaire" register={register('fonctionnaire')} />
        </CardContent>
      </Card>

      <Button type="submit" disabled={mutation.isPending}>
        {mutation.isPending ? 'Enregistrement…' : 'Enregistrer les modifications'}
      </Button>
    </form>
  )
}

function Field({
  label,
  id,
  type = 'text',
  register,
  dir,
  className,
}: {
  label: string
  id: string
  type?: string
  register: ReturnType<ReturnType<typeof useForm<UpdateCandidatInfoFields>>['register']>
  dir?: 'rtl'
  className?: string
}) {
  return (
    <div className={`space-y-2 ${className ?? ''}`}>
      <Label htmlFor={id}>{label}</Label>
      <Input id={id} type={type} dir={dir} {...register} />
    </div>
  )
}
