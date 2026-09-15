import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { ApiError } from '@/api/apiError'
import { verifyIsProf } from '@/api/endpoints/auth.api'
import { completeLogin } from '@/auth/normalizeLogin'
import { GoogleSignInButton } from '@/components/auth/GoogleSignInButton'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'

export function ProfesseurLoginPage() {
  const navigate = useNavigate()
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  const handleIdToken = async (idToken: string) => {
    setError(null)
    setLoading(true)
    try {
      const tokens = await verifyIsProf(idToken)
      await completeLogin('professeur', tokens)
      navigate('/professeur')
    } catch (err) {
      setError(
        err instanceof ApiError
          ? err.message
          : "Échec de la connexion. Vérifiez que ce compte Google est bien associé à un compte professeur.",
      )
    } finally {
      setLoading(false)
    }
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Connexion professeur</CardTitle>
        <CardDescription>Connectez-vous avec le compte Google associé à votre profil professeur.</CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        <GoogleSignInButton onIdToken={handleIdToken} />
        {loading && <p className="text-center text-sm text-muted-foreground">Connexion en cours…</p>}
        {error && <p className="text-center text-sm text-destructive">{error}</p>}
      </CardContent>
    </Card>
  )
}
