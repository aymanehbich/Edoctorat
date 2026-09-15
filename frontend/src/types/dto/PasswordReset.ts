/** POST /api/request-password-reset/ request body. Response is 200/400, no body. */
export interface PasswordResetRequestDTO {
  email: string
  redirectUrl: string
}

/** PATCH /api/perform-password-reset/ request body. Response is 200/400, no body. */
export interface PasswordResetDTO {
  password: string
  token: string
}
