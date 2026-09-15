import type { CedDTO } from './CedDTO'
import type { EtablissementDTO } from './EtablissementDTO'

/** Rich variant — nests full Ced/Etablissement objects. Used by SujetDTO1. */
export interface FormationDoctoraleDTO {
  id: number
  pathImage: string
  initiale: string
  titre: string
  axeDeRecherche: string
  dateAccreditation: string
  ced: CedDTO
  etablissement: EtablissementDTO
}

/**
 * Light variant (Java class name: FormationDto) — nests a raw ced id and a
 * plain-string etablissement (the Etablissement's String PK, not an object).
 * Used by SujetDTO. NOT interchangeable with FormationDoctoraleDTO.
 */
export interface FormationDtoLight {
  id: number
  ced: number
  etablissement: string
  axeDeRecherche: string
  pathImage: string
  titre: string
  initiale: string
  dateAccreditation: string
}
