import { getUserInfo } from '@/api/endpoints/auth.api'
import { probeCedDirecteur } from '@/api/endpoints/ced.api'
import type { AuthUser, Portal } from './auth.types'
import { useAuthStore } from './auth.store'

interface RawLoginTokens {
  access: string
  refresh: string
}

/**
 * The three login endpoints (candidat/professeur/scolarite) return three
 * divergent shapes (TokenResponse/AuthProf/UserInfo) — none of them reliable
 * for identity beyond access/refresh (e.g. candidat's TokenResponse carries
 * no name at all). This always re-derives the canonical AuthUser from
 * GET /api/get-user-info/ instead of trusting the login response body.
 */
export async function completeLogin(portal: Portal, tokens: RawLoginTokens): Promise<void> {
  useAuthStore.getState().setAccessToken(tokens.access)

  const info = await getUserInfo()
  const user: AuthUser = {
    email: info.email,
    nom: info.nom,
    prenom: info.prenom,
    pathPhoto: info.pathPhoto,
    groups: info.groups,
    phoneNumber: info.phoneNumber,
    address: info.address,
    isActive: info.isActive,
  }

  useAuthStore.getState().setSession({
    accessToken: tokens.access,
    refreshToken: tokens.refresh,
    user,
    portal,
  })

  if (typeof info.laboratoireDirecteurId === 'number') {
    useAuthStore.getState().setLaboratoireDirecteurId(info.laboratoireDirecteurId)
  }

  if (portal === 'professeur') {
    const isCedDirecteur = await probeCedDirecteur()
    useAuthStore.getState().setCedDirecteur(isCedDirecteur)
  }
}
