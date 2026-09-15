import { GraduationCapIcon, LandmarkIcon, UserRoundIcon, UsersRoundIcon } from 'lucide-react'
import type { LucideIcon } from 'lucide-react'
import { Link } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'

const PORTALS: { icon: LucideIcon; title: string; description: string; to: string }[] = [
  {
    icon: UserRoundIcon,
    title: 'Candidat',
    description: 'Déposez votre candidature, suivez votre dossier et votre inscription.',
    to: '/login/candidat',
  },
  {
    icon: UsersRoundIcon,
    title: 'Professeur',
    description: 'Proposez des sujets de thèse et suivez vos candidats.',
    to: '/login/professeur',
  },
  {
    icon: LandmarkIcon,
    title: 'Scolarité',
    description: 'Validez les dossiers d’inscription des candidats admis.',
    to: '/login/scolarite',
  },
]

export function LandingPage() {
  return (
    <div className="min-h-screen bg-muted/40">
      <header className="border-b bg-background">
        <div className="mx-auto flex max-w-5xl items-center gap-2 px-4 py-4">
          <GraduationCapIcon className="size-6 text-primary" />
          <span className="text-lg font-semibold">e-Doctorat</span>
        </div>
      </header>

      <main className="mx-auto max-w-5xl px-4 py-16">
        <div className="mb-12 text-center">
          <h1 className="text-3xl font-semibold tracking-tight text-foreground sm:text-4xl">
            Plateforme des études doctorales
          </h1>
          <p className="mx-auto mt-3 max-w-2xl text-muted-foreground">
            Candidatures, sujets de thèse, inscriptions et suivi des dossiers doctoraux — choisissez votre espace.
          </p>
        </div>

        <div className="grid gap-6 sm:grid-cols-3">
          {PORTALS.map(({ icon: Icon, title, description, to }) => (
            <Card key={to} className="flex flex-col">
              <CardHeader>
                <Icon className="mb-2 size-8 text-primary" />
                <CardTitle>{title}</CardTitle>
                <CardDescription>{description}</CardDescription>
              </CardHeader>
              <CardContent className="mt-auto">
                <Button asChild className="w-full">
                  <Link to={to}>Accéder</Link>
                </Button>
              </CardContent>
            </Card>
          ))}
        </div>
      </main>
    </div>
  )
}
