import type { CandidatDTO, CandidatExaminerDTO } from './CandidatDTO'
import type { CommissionDTO } from './CommissionDTO'
import type { SujetDTO } from './SujetDTO'
import type { SujetDTO1 } from './SujetDTO1'

/** Full nested variant. */
export interface ExaminerDTO {
  id: number
  decision: string
  noteEntretien: number
  noteDossier: number
  publier: boolean | null
  valider: boolean | null
  commission: CommissionDTO
  candidat: CandidatDTO
  sujet: SujetDTO1
}

/**
 * Flatter variant used by GET /api/examiner/ (professeur-scoped). Note
 * `commission` here is just the numeric id, not a nested CommissionDTO —
 * do not conflate with ExaminerDTO.
 */
export interface ExaminerDTO1 {
  id: number
  sujet: SujetDTO
  cne: string
  noteDossier: number
  noteEntretien: number
  decision: string
  commission: number
  candidat: CandidatExaminerDTO
  publier: boolean
}
