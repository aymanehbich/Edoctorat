import { zodResolver } from '@hookform/resolvers/zod'
import { CheckCircle2Icon, GraduationCapIcon } from 'lucide-react'
import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import { Link, useSearchParams } from 'react-router-dom'
import { z } from 'zod'
import { ApiError } from '@/api/apiError'
import { confirmEmail, registerCandidat, verifyRegistrationToken } from '@/api/endpoints/registration.api'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { ErrorBanner } from '@/components/feedback/ErrorBanner'
import { LoadingSkeleton } from '@/components/feedback/LoadingSkeleton'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'

export function CandidatRegisterPage() {
  const [searchParams] = useSearchParams()
  const token = searchParams.get('token')

  return (
    <div className="flex min-h-screen items-start justify-center bg-muted/40 px-4 py-12">
      <div className="w-full max-w-xl space-y-6">
        <Link to="/" className="flex items-center justify-center gap-2 text-foreground">
          <GraduationCapIcon className="size-7 text-primary" />
          <span className="text-lg font-semibold">e-Doctorat</span>
        </Link>
        {token ? <CompleteRegistrationStep token={token} /> : <PreRegisterStep />}
      </div>
    </div>
  )
}

const preRegisterSchema = z.object({
  email: z.string().email('Adresse e-mail invalide.'),
  nom: z.string().min(1, 'Le nom est requis.'),
  prenom: z.string().min(1, 'Le prénom est requis.'),
})

type PreRegisterValues = z.infer<typeof preRegisterSchema>

function PreRegisterStep() {
  const [sent, setSent] = useState(false)
  const [serverError, setServerError] = useState<string | null>(null)
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<PreRegisterValues>({ resolver: zodResolver(preRegisterSchema) })

  const onSubmit = async (values: PreRegisterValues) => {
    setServerError(null)
    try {
      await confirmEmail({ ...values, origin: `${window.location.origin}/inscription` })
      setSent(true)
    } catch (error) {
      setServerError(error instanceof ApiError ? error.message : "Échec de l'envoi de l'e-mail.")
    }
  }

  if (sent) {
    return (
      <Card>
        <CardContent className="flex flex-col items-center gap-3 py-8 text-center">
          <CheckCircle2Icon className="size-10 text-success" />
          <p className="font-medium">E-mail envoyé</p>
          <p className="text-sm text-muted-foreground">
            Consultez votre boîte de réception et cliquez sur le lien de confirmation pour poursuivre votre
            inscription.
          </p>
        </CardContent>
      </Card>
    )
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Créer un compte candidat</CardTitle>
        <CardDescription>Commencez par vérifier votre adresse e-mail.</CardDescription>
      </CardHeader>
      <CardContent>
        <form className="space-y-4" onSubmit={handleSubmit(onSubmit)} noValidate>
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="nom">Nom</Label>
              <Input id="nom" {...register('nom')} />
              {errors.nom && <p className="text-sm text-destructive">{errors.nom.message}</p>}
            </div>
            <div className="space-y-2">
              <Label htmlFor="prenom">Prénom</Label>
              <Input id="prenom" {...register('prenom')} />
              {errors.prenom && <p className="text-sm text-destructive">{errors.prenom.message}</p>}
            </div>
          </div>
          <div className="space-y-2">
            <Label htmlFor="email">Adresse e-mail</Label>
            <Input id="email" type="email" autoComplete="email" {...register('email')} />
            {errors.email && <p className="text-sm text-destructive">{errors.email.message}</p>}
          </div>
          {serverError && <p className="text-sm text-destructive">{serverError}</p>}
          <Button type="submit" className="w-full" disabled={isSubmitting}>
            {isSubmitting ? 'Envoi…' : 'Continuer'}
          </Button>
          <p className="text-center text-sm text-muted-foreground">
            Déjà un compte ?{' '}
            <Link to="/login/candidat" className="text-primary hover:underline">
              Se connecter
            </Link>
          </p>
        </form>
      </CardContent>
    </Card>
  )
}

const completeSchema = z
  .object({
    cin: z.string().min(1, 'Le CIN est requis.'),
    cne: z.string().min(1, 'Le CNE est requis.'),
    dateDeNaissance: z.string().min(1, 'La date de naissance est requise.'),
    password: z.string().min(8, 'Le mot de passe doit contenir au moins 8 caractères.'),
    confirmPassword: z.string(),
    pays: z.string().min(1, 'Le pays est requis.'),
    sexe: z.string().min(1, 'Le sexe est requis.'),
    situationFamiliale: z.string().min(1, 'La situation familiale est requise.'),
    typeDehandicape: z.string().optional(),
    villeDeNaissance: z.string().min(1, 'La ville de naissance est requise.'),
    villeDeNaissanceAr: z.string().optional(),
    ville: z.string().min(1, 'La ville de résidence est requise.'),
    telephone: z.string().min(1, 'Le téléphone est requis.'),
    adresse: z.string().min(1, "L'adresse est requise."),
    nomCandidatAr: z.string().optional(),
    prenomCandidatAr: z.string().optional(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: 'Les mots de passe ne correspondent pas.',
    path: ['confirmPassword'],
  })

type CompleteValues = z.infer<typeof completeSchema>

function CompleteRegistrationStep({ token }: { token: string }) {
  const [state, setState] = useState<'loading' | 'invalid' | 'ready' | 'done'>('loading')
  const [identity, setIdentity] = useState<{ email: string; nom: string; prenom: string } | null>(null)
  const [serverError, setServerError] = useState<string | null>(null)
  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors, isSubmitting },
  } = useForm<CompleteValues>({
    resolver: zodResolver(completeSchema),
    defaultValues: { typeDehandicape: 'Aucun', villeDeNaissanceAr: '', nomCandidatAr: '', prenomCandidatAr: '' },
  })

  useEffect(() => {
    verifyRegistrationToken(token)
      .then((dto) => {
        setIdentity({ email: dto.email, nom: dto.nom, prenom: dto.prenom })
        setState('ready')
      })
      .catch(() => setState('invalid'))
  }, [token])

  const onSubmit = async (values: CompleteValues) => {
    if (!identity) return
    setServerError(null)
    try {
      await registerCandidat({
        token,
        email: identity.email,
        nom: identity.nom,
        prenom: identity.prenom,
        ...values,
        typeDehandicape: values.typeDehandicape ?? 'Aucun',
        villeDeNaissanceAr: values.villeDeNaissanceAr ?? '',
        nomCandidatAr: values.nomCandidatAr ?? '',
        prenomCandidatAr: values.prenomCandidatAr ?? '',
      })
      setState('done')
    } catch (error) {
      setServerError(error instanceof ApiError ? error.message : "Échec de l'inscription.")
    }
  }

  if (state === 'loading') {
    return (
      <Card>
        <CardContent className="py-8">
          <LoadingSkeleton rows={4} />
        </CardContent>
      </Card>
    )
  }

  if (state === 'invalid') {
    return (
      <Card>
        <CardContent className="py-8">
          <ErrorBanner message="Ce lien de confirmation est invalide ou a expiré." />
          <p className="mt-4 text-center text-sm text-muted-foreground">
            <Link to="/inscription" className="text-primary hover:underline">
              Recommencer l'inscription
            </Link>
          </p>
        </CardContent>
      </Card>
    )
  }

  if (state === 'done') {
    return (
      <Card>
        <CardContent className="flex flex-col items-center gap-3 py-8 text-center">
          <CheckCircle2Icon className="size-10 text-success" />
          <p className="font-medium">Compte créé avec succès</p>
          <Button asChild>
            <Link to="/login/candidat">Se connecter</Link>
          </Button>
        </CardContent>
      </Card>
    )
  }

  return (
    <Card className="max-w-xl">
      <CardHeader>
        <CardTitle>Finaliser mon inscription</CardTitle>
        <CardDescription>
          {identity?.prenom} {identity?.nom} — {identity?.email}
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form className="space-y-4" onSubmit={handleSubmit(onSubmit)} noValidate>
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="cin">CIN</Label>
              <Input id="cin" {...register('cin')} />
              {errors.cin && <p className="text-sm text-destructive">{errors.cin.message}</p>}
            </div>
            <div className="space-y-2">
              <Label htmlFor="cne">CNE</Label>
              <Input id="cne" {...register('cne')} />
              {errors.cne && <p className="text-sm text-destructive">{errors.cne.message}</p>}
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="dateDeNaissance">Date de naissance</Label>
              <Input id="dateDeNaissance" type="date" {...register('dateDeNaissance')} />
              {errors.dateDeNaissance && (
                <p className="text-sm text-destructive">{errors.dateDeNaissance.message}</p>
              )}
            </div>
            <div className="space-y-2">
              <Label htmlFor="sexe">Sexe</Label>
              <Select onValueChange={(v) => setValue('sexe', v, { shouldValidate: true })}>
                <SelectTrigger id="sexe">
                  <SelectValue placeholder="Sélectionner" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="M">Masculin</SelectItem>
                  <SelectItem value="F">Féminin</SelectItem>
                </SelectContent>
              </Select>
              {errors.sexe && <p className="text-sm text-destructive">{errors.sexe.message}</p>}
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="villeDeNaissance">Ville de naissance</Label>
              <Input id="villeDeNaissance" {...register('villeDeNaissance')} />
              {errors.villeDeNaissance && (
                <p className="text-sm text-destructive">{errors.villeDeNaissance.message}</p>
              )}
            </div>
            <div className="space-y-2">
              <Label htmlFor="pays">Pays</Label>
              <Input id="pays" {...register('pays')} />
              {errors.pays && <p className="text-sm text-destructive">{errors.pays.message}</p>}
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="ville">Ville de résidence</Label>
            <Input id="ville" {...register('ville')} />
            {errors.ville && <p className="text-sm text-destructive">{errors.ville.message}</p>}
          </div>

          <div className="space-y-2">
            <Label htmlFor="adresse">Adresse</Label>
            <Input id="adresse" {...register('adresse')} />
            {errors.adresse && <p className="text-sm text-destructive">{errors.adresse.message}</p>}
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="telephone">Téléphone</Label>
              <Input id="telephone" {...register('telephone')} />
              {errors.telephone && <p className="text-sm text-destructive">{errors.telephone.message}</p>}
            </div>
            <div className="space-y-2">
              <Label htmlFor="situationFamiliale">Situation familiale</Label>
              <Select onValueChange={(v) => setValue('situationFamiliale', v, { shouldValidate: true })}>
                <SelectTrigger id="situationFamiliale">
                  <SelectValue placeholder="Sélectionner" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="Célibataire">Célibataire</SelectItem>
                  <SelectItem value="Marié(e)">Marié(e)</SelectItem>
                  <SelectItem value="Divorcé(e)">Divorcé(e)</SelectItem>
                  <SelectItem value="Veuf(ve)">Veuf(ve)</SelectItem>
                </SelectContent>
              </Select>
              {errors.situationFamiliale && (
                <p className="text-sm text-destructive">{errors.situationFamiliale.message}</p>
              )}
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="password">Mot de passe</Label>
              <Input id="password" type="password" autoComplete="new-password" {...register('password')} />
              {errors.password && <p className="text-sm text-destructive">{errors.password.message}</p>}
            </div>
            <div className="space-y-2">
              <Label htmlFor="confirmPassword">Confirmer le mot de passe</Label>
              <Input
                id="confirmPassword"
                type="password"
                autoComplete="new-password"
                {...register('confirmPassword')}
              />
              {errors.confirmPassword && (
                <p className="text-sm text-destructive">{errors.confirmPassword.message}</p>
              )}
            </div>
          </div>

          {serverError && <p className="text-sm text-destructive">{serverError}</p>}
          <Button type="submit" className="w-full" disabled={isSubmitting}>
            {isSubmitting ? 'Création du compte…' : 'Créer mon compte'}
          </Button>
        </form>
      </CardContent>
    </Card>
  )
}
