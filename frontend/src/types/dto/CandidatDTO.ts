import type { CandidatDiplome } from './DiplomeDTO'

export interface CandidatDTO {
  id: number
  cne: string
  pays: string
  nom: string
  prenom: string
  email: string
  cin: string
  nomCandidatAr: string
  prenomCandidatAr: string
  adresse: string
  adresseAr: string
  sexe: string
  villeDeNaissance: string
  villeDeNaissanceAr: string
  ville: string
  /** Stored as plain string in the DB, not a real Date type. */
  dateDeNaissance: string
  /** Exact backend casing: capital C in "HandiCape". */
  typeDeHandiCape: string
  academie: string
  telCandidat: string
  pathCv: string | null
  pathPhoto: string | null
  /**
   * Backend model field is Boolean but RegistrationService assigns it an int
   * literal (0) on registration — treat the runtime type as unverified until
   * confirmed against a live response.
   */
  etatDossier: boolean | null
  /** snake_case on the wire, not camelCase — do not rename. */
  situation_familiale: string
  fonctionnaire: string
  diplomes: CandidatDiplome[]
}

/** Minimal candidat projection used inside PostulerProfCandidatDTO. */
export interface CandidatProfDTO {
  id: number
  nom: string
  cne: string
  prenom: string
  email: string
}

/** Minimal candidat projection used inside InscriptionDTO1. */
export interface CandidatInscriptionDTO {
  id: number
  cne: string
  nom: string
  prenom: string
}

/** Minimal candidat projection used inside ExaminerDTO1. */
export interface CandidatExaminerDTO {
  id: number
  cne: string
  nom: string
  prenom: string
}

/** Minimal candidat projection used inside InscriptionScolariteDTO/PostulerScolariteDTO. */
export interface CandidatScolariteDTO {
  id: number
  cne: string
  nom: string
  prenom: string
  photo: string
}
