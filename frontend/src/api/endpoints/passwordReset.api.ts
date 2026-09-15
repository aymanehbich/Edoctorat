import { http } from '@/api/http'
import type { PasswordResetDTO, PasswordResetRequestDTO } from '@/types/dto/PasswordReset'

/** Response is 200/400 with no body either way. */
export async function requestPasswordReset(body: PasswordResetRequestDTO): Promise<void> {
  await http.post('/request-password-reset/', body)
}

export async function performPasswordReset(body: PasswordResetDTO): Promise<void> {
  await http.patch('/perform-password-reset/', body)
}
