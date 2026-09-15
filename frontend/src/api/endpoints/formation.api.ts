import { http } from '@/api/http'
import type { Result } from '@/api/wrappers'
import type { FormationDoctoraleDTO } from '@/types/dto/FormationDoctoraleDTO'

export async function getFormationsDoctorales(): Promise<Result<FormationDoctoraleDTO>> {
  const { data } = await http.get<Result<FormationDoctoraleDTO>>('/formation-doctorale/')
  return data
}
