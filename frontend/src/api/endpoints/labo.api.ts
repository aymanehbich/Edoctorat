import { http } from '@/api/http'
import type { ResultDto } from '@/api/wrappers'
import type { ExaminerDTO } from '@/types/dto/ExaminerDTO'
import type { ProfesseurDTO } from '@/types/dto/ProfesseurDTO'

/** GET /api/labo_professeur/ — professeurs belonging to the director's laboratoire. */
export async function getLaboMembres(): Promise<ResultDto<ProfesseurDTO>> {
  const { data } = await http.get<ResultDto<ProfesseurDTO>>('/labo_professeur/')
  return data
}

/**
 * GET /api/labo_candidat/ — despite the name, returns exam sessions
 * (ExaminerDTO) from commissions held at the director's laboratoire, not a
 * plain candidat list. Backend quirk, not a frontend typo (mirrors the
 * get-ced-candidats/ naming quirk).
 */
export async function getLaboResultats(): Promise<ResultDto<ExaminerDTO>> {
  const { data } = await http.get<ResultDto<ExaminerDTO>>('/labo_candidat/')
  return data
}

/** PATCH /api/labo_publier_resultats/?decision=... — bulk-publishes every examiner row with that decision at the director's laboratoire. */
export async function publierResultatsLabo(decision: string): Promise<ResultDto<ExaminerDTO>> {
  const { data } = await http.patch<ResultDto<ExaminerDTO>>('/labo_publier_resultats/', null, { params: { decision } })
  return data
}
