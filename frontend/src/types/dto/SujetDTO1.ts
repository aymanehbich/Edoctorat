import type { FormationDoctoraleDTO } from './FormationDoctoraleDTO'
import type { LaboratoireDTO } from './LaboratoireDTO'
import type { ProfesseurDTO } from './ProfesseurDTO'

/**
 * Rich variant. Returned by GET /api/sujetslabo/, /api/get-all-sujets/, and
 * nested inside PostulerDTO/InscriptionDTO/ExaminerDTO. NOT interchangeable
 * with SujetDTO — formationDoctorale here is the rich nested DTO.
 */
export interface SujetDTO1 {
  id: number
  professeur: ProfesseurDTO
  coDirecteur: ProfesseurDTO | null
  titre: string
  description: string
  formationDoctorale: FormationDoctoraleDTO
  publier: boolean
  laboratoire: LaboratoireDTO
}
