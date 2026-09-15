/** typeAnnexe exact values used by the backend: "Diplôme" and "Relevé" (French, with accents). */
export type TypeAnnexe = 'Diplôme' | 'Relevé'

export interface AnnexeDTO {
  id: number
  typeAnnexe: TypeAnnexe
  titre: string
  pathFile: string
}
