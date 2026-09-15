import { http } from '@/api/http'
import type { Result } from '@/api/wrappers'
import type { SujetDTO } from '@/types/dto/SujetDTO'

/** GET /api/get-published-subjects/ — returns only sujets with publier=true. */
export async function getPublishedSujets(): Promise<Result<SujetDTO>> {
  const { data } = await http.get<Result<SujetDTO>>('/get-published-subjects/')
  return data
}

export async function getSujetById(id: number): Promise<SujetDTO> {
  const { data } = await http.get<SujetDTO>(`/sujets/${id}/`)
  return data
}
