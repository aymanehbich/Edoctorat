export type Portal = 'candidat' | 'professeur' | 'scolarite'

/** Canonical client-side identity, always built from GET /api/get-user-info/. */
export interface AuthUser {
  email: string
  nom: string
  prenom: string
  pathPhoto: string[]
  groups: string[]
  phoneNumber: string
  address: string
  isActive: boolean
}

export type AuthStatus = 'idle' | 'loading' | 'authenticated' | 'unauthenticated'
