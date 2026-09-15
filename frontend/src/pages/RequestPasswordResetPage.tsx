import { zodResolver } from '@hookform/resolvers/zod'
import { CheckCircle2Icon } from 'lucide-react'
import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { Link } from 'react-router-dom'
import { z } from 'zod'
import { requestPasswordReset } from '@/api/endpoints/passwordReset.api'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'

const schema = z.object({
  email: z.string().email('Adresse e-mail invalide.'),
})

type FormValues = z.infer<typeof schema>

export function RequestPasswordResetPage() {
  const [sent, setSent] = useState(false)
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<FormValues>({ resolver: zodResolver(schema) })

  const onSubmit = async (values: FormValues) => {
    // Backend responds 200/400 with no body either way, and does not reveal
    // whether the email exists — show the same confirmation regardless.
    await requestPasswordReset({
      email: values.email,
      redirectUrl: `${window.location.origin}/reinitialiser-mot-de-passe`,
    }).catch(() => {
      // intentionally swallowed — see comment above
    })
    setSent(true)
  }

  if (sent) {
    return (
      <Card>
        <CardContent className="flex flex-col items-center gap-3 py-8 text-center">
          <CheckCircle2Icon className="size-10 text-success" />
          <p className="font-medium">E-mail envoyé</p>
          <p className="text-sm text-muted-foreground">
            Si un compte existe avec cette adresse, un lien de réinitialisation vient de lui être envoyé.
          </p>
          <Link to="/" className="text-sm text-primary hover:underline">
            Retour à l'accueil
          </Link>
        </CardContent>
      </Card>
    )
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Mot de passe oublié</CardTitle>
        <CardDescription>Saisissez votre adresse e-mail pour recevoir un lien de réinitialisation.</CardDescription>
      </CardHeader>
      <CardContent>
        <form className="space-y-4" onSubmit={handleSubmit(onSubmit)} noValidate>
          <div className="space-y-2">
            <Label htmlFor="email">Adresse e-mail</Label>
            <Input id="email" type="email" autoComplete="email" {...register('email')} />
            {errors.email && <p className="text-sm text-destructive">{errors.email.message}</p>}
          </div>
          <Button type="submit" className="w-full" disabled={isSubmitting}>
            {isSubmitting ? 'Envoi…' : 'Envoyer le lien'}
          </Button>
        </form>
      </CardContent>
    </Card>
  )
}
