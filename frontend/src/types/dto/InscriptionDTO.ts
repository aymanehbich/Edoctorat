import type { CandidatDTO, CandidatInscriptionDTO } from './CandidatDTO'
import type { SujetDTO1 } from './SujetDTO1'

/** Used by GET /api/get-all-inscriptions/ (unscoped — do not expose to candidat/professeur portals). */
export interface InscriptionDTO {
  id: number
  /** Typo preserved exactly as sent by the backend: "Dipose" not "Dispose". */
  dateDiposeDossier: string
  remarque: string
  valider: boolean
  pathFile: string | null
  candidat: CandidatDTO
  sujet: SujetDTO1
}

export interface SujetInscriptionDTO {
  id: number
  titre: string
}

/** Used by GET /api/inscrits/ (professeur-scoped). Spelling is correct here, unlike InscriptionDTO. */
export interface InscriptionDTO1 {
  id: number
  dateDisposeDossier: string
  remarque: string
  valider: boolean
  candidat: CandidatInscriptionDTO
  sujet: SujetInscriptionDTO
}
