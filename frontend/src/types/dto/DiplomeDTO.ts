import type { AnnexeDTO } from './AnnexeDTO'

export interface DiplomeDTO {
  id: number
  intitule: string
  type: string
  /** java.util.Date — verify exact JSON format (ISO string vs epoch millis) against a live response. */
  dateCommission: string
  mention: string
  pays: string
  etablissement: string
  specialite: string
  ville: string
  province: string
  moyen_generale: number
  candidatId: number
  annexes: AnnexeDTO[]
}

/**
 * Java class name: candidatDiplomes. Nested inside CandidatDTO.diplomes —
 * a denormalized diploma shape without candidatId (since it's already nested
 * under that candidat), otherwise the same fields as DiplomeDTO.
 */
export interface CandidatDiplome {
  id: number
  intitule: string
  type: string
  dateCommission: string
  mention: string
  pays: string
  etablissement: string
  specialite: string
  ville: string
  province: string
  moyen_generale: number
  annexes: AnnexeDTO[]
}
