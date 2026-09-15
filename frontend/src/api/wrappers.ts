/**
 * The backend has three structurally-similar but distinct paginated wrapper
 * classes. They are NOT guaranteed to stay structurally identical, so they
 * are kept as separate generics rather than unified into one type.
 */

/** dto/Result.java — count: Long */
export interface Result<T> {
  count: number
  next: string | null
  previous: string | null
  results: T[]
}

/** dto/candidat/ResultDto.java — count: int, plus msg */
export interface ResultDto<T> {
  count: number
  next: string | null
  previous: string | null
  results: T[]
  msg: string | null
}

/** dto/inscription/PagedResponse.java — used only by GET /api/scolarite/ */
export interface PagedResponse<T> {
  results: T[]
  count: number | null
  next: string | null
  previous: string | null
}
