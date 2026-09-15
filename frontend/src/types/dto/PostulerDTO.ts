import type { CandidatDTO, CandidatProfDTO } from './CandidatDTO'
import type { SujetDTO } from './SujetDTO'
import type { SujetDTO1 } from './SujetDTO1'

/** Used by candidat-facing GET/POST /api/candidat-postules/. */
export interface PostulerDTO {
  id: number
  pathFile: string | null
  sujet: SujetDTO1
  sujetTitre: string
  candidat: CandidatDTO
}

/** Used only by professeur-facing GET /api/get-professeur-candidats/. */
export interface PostulerProfCandidatDTO {
  id: number
  candidat: CandidatProfDTO
  sujet: SujetDTO
  pathFile: string | null
  /** Accented JSON key exactly as sent by the backend — do not rename. */
  'validé': boolean
}
