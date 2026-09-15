import { http } from '@/api/http'
import type { ResultDto } from '@/api/wrappers'
import type { CommissionDTO } from '@/types/dto/CommissionDTO'
import type { ExaminerDTO } from '@/types/dto/ExaminerDTO'
import type { SujetDTO1 } from '@/types/dto/SujetDTO1'

/**
 * Exploits a confirmed backend side effect: GET /api/get-ced-sujets/ 200s for
 * a professeur who directs a Ced (Professeur.ced is non-null), and throws an
 * unguarded NPE -> 500 for any other professeur. There is no clean "am I a
 * director" field, so this probe is the reliable signal. skipAuthRecovery
 * prevents a negative result from triggering the global session-refresh/logout
 * flow, since a 500 here is an expected outcome, not a real auth failure.
 */
export async function probeCedDirecteur(): Promise<boolean> {
  try {
    await http.get('/get-ced-sujets/', { skipAuthRecovery: true })
    return true
  } catch {
    return false
  }
}

/** GET /api/get-ced-sujets/ — sujets proposed under any formation of the director's Ced. */
export async function getCedSujets(): Promise<ResultDto<SujetDTO1>> {
  const { data } = await http.get<ResultDto<SujetDTO1>>('/get-ced-sujets/')
  return data
}

/**
 * GET /api/get-ced-candidats/ — despite the name, returns exam sessions
 * (ExaminerDTO) where the director sits on the commission as an examiner,
 * not a plain candidat list. Backend quirk, not a frontend typo.
 */
export async function getCedCandidats(): Promise<ResultDto<ExaminerDTO>> {
  const { data } = await http.get<ResultDto<ExaminerDTO>>('/get-ced-candidats/')
  return data
}

/** GET /api/get-ced-commissions/ — commissions held at labs the director's Ced covers. */
export async function getCedCommissions(): Promise<ResultDto<CommissionDTO>> {
  const { data } = await http.get<ResultDto<CommissionDTO>>('/get-ced-commissions/')
  return data
}

/**
 * GET /api/get-ced-resultats/ — exam results for commissions held at labs the
 * director directs (scoped by Laboratoire.directeur server-side, not by Ced).
 */
export async function getCedResultats(): Promise<ResultDto<ExaminerDTO>> {
  const { data } = await http.get<ResultDto<ExaminerDTO>>('/get-ced-resultats/')
  return data
}
