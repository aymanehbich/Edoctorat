/** Request body for POST /api/token/ and POST /api/login_scolarite/. */
export interface LoginRequest {
  username: string
  password: string
}

/** POST /api/token/ response (candidat login) — no identity fields at all. */
export interface TokenResponse {
  access: string
  refresh: string
}

/** POST /api/login_scolarite/ response. */
export interface UserInfo {
  access: string
  refresh: string
  nom: string
  prenom: string
  email: string
  pathPhoto: string[]
  groups: string[]
}

/** GET /api/get-user-info/ response — the one canonical, consistent identity shape. */
export interface UserInfoResponse {
  email: string
  nom: string
  prenom: string
  pathPhoto: string[]
  groups: string[]
  phoneNumber: string
  address: string
  isActive: boolean
  /**
   * Planned additive backend field (not yet present until the Phase 0 backend
   * change lands): id of the Laboratoire this professeur directs, or null/absent
   * if they are not a director. Mirrors CedDTO.directeurId.
   */
  laboratoireDirecteurId?: number | null
}

/**
 * POST /api/verify-is-prof/ response (Google login). Known backend quirks:
 * - `grade` is currently always populated with the refresh token string, not a real grade.
 * - `nombreProposer`/`nombreEncadrer` are hardcoded to 5 server-side, not live data.
 * Do not build UI logic around those two fields' values yet.
 */
export interface AuthProf {
  access: string
  refresh: string
  email: string
  groups: string[]
  nom: string
  prenom: string
  pathPhoto: string | null
  grade: string
  nombreProposer: number
  nombreEncadrer: number
}
