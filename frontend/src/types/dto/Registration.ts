/** POST /api/confirm-email/ request body & POST /api/verify-token/ response. */
export interface PreRegistrationDTO {
  email: string
  nom: string
  prenom: string
  origin: string
}

/** POST /api/register/candidat/ request body. token/dateDeNaissance/password are required by the backend. */
export interface RegistrationDTO {
  token: string
  adresse: string
  cin: string
  cne: string
  /** "yyyy-MM-dd" */
  dateDeNaissance: string
  email: string
  nom: string
  nomCandidatAr: string
  password: string
  pays: string
  prenom: string
  prenomCandidatAr: string
  sexe: string
  situationFamiliale: string
  typeDehandicape: string
  villeDeNaissance: string
  villeDeNaissanceAr: string
  ville: string
  telephone: string
}
