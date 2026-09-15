import type { ExaminerDTO } from './ExaminerDTO'
import type { InscriptionDTO } from './InscriptionDTO'

/** GET /api/candidat-resultats/ — candidat-scoped. `examens` only includes publier=true rows. */
export interface CandidatResultatsDTO {
  examens: ExaminerDTO[]
  inscriptions: InscriptionDTO[]
}
