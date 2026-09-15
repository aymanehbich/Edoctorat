/**
 * GET /api/get-base-config/ returns the raw JPA entity, or a literal `null`
 * body if the config table is empty — callers must handle null explicitly.
 */
export interface Config {
  id: number
  maxSujetPostuler: number
  dateDebutPostulerSujetCandidat: string
  dateDebutModifierSujetProf: string
  dateFinPostulerSujetCandidat: string
  dateFinModifierSujetProf: string
}
