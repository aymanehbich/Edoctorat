import { http } from '@/api/http'
import type { PreRegistrationDTO, RegistrationDTO } from '@/types/dto/Registration'

export async function confirmEmail(body: PreRegistrationDTO): Promise<void> {
  await http.post('/confirm-email/', body)
}

export async function verifyRegistrationToken(token: string): Promise<PreRegistrationDTO> {
  const { data } = await http.post<PreRegistrationDTO>('/verify-token/', { token })
  return data
}

export async function registerCandidat(body: RegistrationDTO): Promise<void> {
  await http.post('/register/candidat/', body)
}
