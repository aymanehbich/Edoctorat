import type { AxiosError } from 'axios'

const STATUS_MESSAGES: Record<number, string> = {
  400: 'Requête invalide.',
  401: 'Session expirée. Veuillez vous reconnecter.',
  403: "Vous n'avez pas accès à cette ressource.",
  404: 'Ressource introuvable.',
  500: 'Erreur serveur. Veuillez réessayer.',
}

/** Normalizes every backend error shape (JSON, plain text, empty body) into one type. */
export class ApiError extends Error {
  status: number
  raw: unknown

  constructor(status: number, message: string, raw: unknown) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.raw = raw
  }
}

export function parseErrorBody(error: AxiosError): ApiError {
  const status = error.response?.status ?? 0
  const data = error.response?.data

  if (data && typeof data === 'object') {
    const body = data as Record<string, unknown>
    const message = body.message ?? body.msg ?? body.error
    if (typeof message === 'string' && message.length > 0) {
      return new ApiError(status, message, data)
    }
  }

  if (typeof data === 'string' && data.trim().length > 0) {
    return new ApiError(status, data, data)
  }

  return new ApiError(status, STATUS_MESSAGES[status] ?? 'Une erreur est survenue.', data)
}

/**
 * A small allowlist of session/auth-shaped 500 messages the backend throws
 * (no @ControllerAdvice exists, so real auth failures surface as 500, not 401).
 * Matched against on top of real 401s to trigger the silent-refresh flow.
 */
const AUTH_FAILURE_PATTERNS = [
  /no active session/i,
  /no authentication found in session/i,
  /user not found/i,
  /authenticationexception/i,
]

export function isAuthFailure(error: ApiError): boolean {
  if (error.status === 401) return true
  if (error.status === 500) {
    return AUTH_FAILURE_PATTERNS.some((pattern) => pattern.test(error.message))
  }
  return false
}
