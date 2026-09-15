import { http } from '@/api/http'
import type { Result, ResultDto } from '@/api/wrappers'
import type { CommissionDTO } from '@/types/dto/CommissionDTO'
import type { ExaminerDTO1 } from '@/types/dto/ExaminerDTO'
import type { InscriptionDTO1 } from '@/types/dto/InscriptionDTO'
import type { PostulerProfCandidatDTO } from '@/types/dto/PostulerDTO'
import type { ProfesseurDTO } from '@/types/dto/ProfesseurDTO'
import type { SujetDTO } from '@/types/dto/SujetDTO'

export async function getProfesseurs(): Promise<Result<ProfesseurDTO>> {
  const { data } = await http.get<Result<ProfesseurDTO>>('/get-professeurs/')
  return data
}

export async function getMySujets(): Promise<Result<SujetDTO>> {
  const { data } = await http.get<Result<SujetDTO>>('/sujets/')
  return data
}

export interface SujetFormFields {
  titre: string
  description: string
  coDirecteurId: number
  formationDoctoraleId: number
  publier?: boolean
}

export async function createSujet(fields: SujetFormFields): Promise<SujetDTO> {
  const { data } = await http.post<SujetDTO>('/sujets/', fields)
  return data
}

export async function updateSujet(id: number, fields: SujetFormFields): Promise<SujetDTO> {
  const { data } = await http.put<SujetDTO>(`/sujets/${id}/`, fields)
  return data
}

export async function deleteSujet(id: number): Promise<void> {
  await http.delete(`/sujets/${id}/`)
}

/** GET /api/get-professeur-candidats/ — candidats having applied to one of the professeur's sujets. */
export async function getMyCandidats(): Promise<ResultDto<PostulerProfCandidatDTO>> {
  const { data } = await http.get<ResultDto<PostulerProfCandidatDTO>>('/get-professeur-candidats/')
  return data
}

/** GET /api/examiner/ — paginated, 50 per page. `offset` is a row offset, not a page number. */
export async function getMyResultats(offset?: number): Promise<Result<ExaminerDTO1>> {
  const { data } = await http.get<Result<ExaminerDTO1>>('/examiner/', { params: { offset } })
  return data
}

/** GET /api/inscrits/ — paginated, 50 per page. `offset` is a row offset, not a page number. */
export async function getMyInscrits(offset?: number): Promise<Result<InscriptionDTO1>> {
  const { data } = await http.get<Result<InscriptionDTO1>>('/inscrits/', { params: { offset } })
  return data
}

export interface ExaminerGradeFields {
  noteDossier: number
  noteEntretien: number
  decision: string
  publier: boolean
}

/**
 * PUT /api/examiner/{id}/ — grades an Examiner row created when a commission
 * was scheduled. Setting decision to exactly "Liste principale" AND
 * publier=true auto-creates an Inscription for the candidat server-side
 * (publier is required too, otherwise the candidat's inscription status —
 * which has no publier gate of its own — would leak the decision early).
 */
export async function updateExaminer(id: number, fields: ExaminerGradeFields) {
  const { data } = await http.put(`/examiner/${id}/`, fields)
  return data
}

/** PUT /api/examiner/{id}/ — présélection step: accept/reject a candidat for the interview before grading. */
export async function updateExaminerValidation(id: number, valider: boolean) {
  const { data } = await http.put(`/examiner/${id}/`, { valider })
  return data
}

export interface CreateCommissionFields {
  dateCommission: string
  heure: string
  lieu: string
  professeurIds: number[]
  postulerIds: number[]
}

/** POST /api/commissions/ — schedules a jury session and pre-creates one ungraded Examiner row per selected postulation. */
export async function createCommission(fields: CreateCommissionFields): Promise<CommissionDTO> {
  const { data } = await http.post<CommissionDTO>('/commissions/', fields)
  return data
}
