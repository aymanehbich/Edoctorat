import type { FormationDtoLight } from './FormationDoctoraleDTO'
import type { ProfesseurDTO } from './ProfesseurDTO'

/** Light variant. Returned by GET /api/sujets/, /api/sujets/{id}/, /api/get-published-subjects/. */
export interface SujetDTO {
  id: number
  professeur: ProfesseurDTO
  coDirecteur: ProfesseurDTO | null
  titre: string
  description: string
  formationDoctorale: FormationDtoLight
  publier: boolean
  valider: boolean
}
