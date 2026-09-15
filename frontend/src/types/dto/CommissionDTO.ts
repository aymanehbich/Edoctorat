import type { ProfesseurDTO } from './ProfesseurDTO'
import type { SujetDTO } from './SujetDTO'

export interface CommissionDTO {
  id: number
  dateCommission: string
  heure: string
  valider: boolean
  lieu: string
  labo: number
  participants: ProfesseurDTO[]
  sujets: SujetDTO[]
}
