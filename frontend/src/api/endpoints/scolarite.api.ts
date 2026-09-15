import { http } from '@/api/http'
import type { PagedResponse } from '@/api/wrappers'
import type { InscriptionScolariteDTO, ValidationRequest } from '@/types/dto/InscriptionScolariteDTO'

/** GET /api/scolarite/ — paginated, 50 per page. `offset` is a row offset, not a page number. */
export async function getInscriptions(offset?: number): Promise<PagedResponse<InscriptionScolariteDTO>> {
  const { data } = await http.get<PagedResponse<InscriptionScolariteDTO>>('/scolarite/', { params: { offset } })
  return data
}

/** PATCH /api/scolarite/{id}/ — sets the validation decision (valider) and remarque on a dossier. */
export async function updateValidation(id: number, request: ValidationRequest): Promise<InscriptionScolariteDTO> {
  const { data } = await http.patch<InscriptionScolariteDTO>(`/scolarite/${id}/`, request)
  return data
}
