import type { CandidatScolariteDTO } from './CandidatDTO'

export interface SujetScolariteDTO {
  id: number
  titre: string
  formationDoctorale: string
  ced: string
  laboratoire: string
}

/** Used by GET /api/scolarite/ and PATCH /api/scolarite/{id}/ response. */
export interface InscriptionScolariteDTO {
  id: number
  dateDisposeDossier: string
  remarque: string
  valider: boolean | null
  candidat: CandidatScolariteDTO
  sujet: SujetScolariteDTO
}

/** Defined on the backend but not currently wired to any controller — treat as possibly unused. */
export interface PostulerScolariteDTO {
  id: number
  pathFile: string | null
  sujet: SujetScolariteDTO
  candidat: CandidatScolariteDTO
}

/** PATCH /api/scolarite/{id}/ request body. */
export interface ValidationRequest {
  remarque: string
  valider: boolean | null
}
