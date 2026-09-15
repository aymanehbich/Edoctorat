import { http } from '@/api/http'
import type { Result } from '@/api/wrappers'
import type { DiplomeDTO } from '@/types/dto/DiplomeDTO'

export interface DiplomeFormFields {
  intitule: string
  type: string
  /** "yyyy-MM-dd" */
  dateCommission: string
  mention: string
  pays: string
  etablissement: string
  specialite: string
  ville: string
  province: string
  moyen_generale: number
}

export async function getMyDiplomes(): Promise<Result<DiplomeDTO>> {
  const { data } = await http.get<Result<DiplomeDTO>>('/candidat-parcours/')
  return data
}

/**
 * Multipart create. Exact field names read server-side via request.getFile(s):
 * diplomeFile (single, saved as typeAnnexe "Diplôme"), releveFile (single, "Relevé"),
 * relevefile (lowercase f, multiple files, each also saved as "Relevé").
 */
export async function createDiplome(
  fields: DiplomeFormFields,
  files: { diplomeFile?: File; releveFile?: File; relevefile?: File[] },
): Promise<void> {
  const formData = new FormData()
  for (const [key, value] of Object.entries(fields)) {
    formData.append(key, String(value))
  }
  if (files.diplomeFile) formData.append('diplomeFile', files.diplomeFile)
  if (files.releveFile) formData.append('releveFile', files.releveFile)
  if (files.relevefile) {
    for (const f of files.relevefile) formData.append('relevefile', f)
  }
  await http.post('/candidat-parcours/', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

/**
 * Multipart partial update. All text fields optional (only applied if present).
 * Only single-file diplomeFile/releveFile replacement is reliable server-side —
 * multi-file relevefile[] replace on update is not wired up in the backend.
 */
export async function updateDiplomeWithFiles(
  diplomeId: number,
  fields: Partial<DiplomeFormFields>,
  files: { diplomeFile?: File; releveFile?: File },
): Promise<void> {
  const formData = new FormData()
  for (const [key, value] of Object.entries(fields)) {
    if (value !== undefined) formData.append(key, String(value))
  }
  if (files.diplomeFile) formData.append('diplomeFile', files.diplomeFile)
  if (files.releveFile) formData.append('releveFile', files.releveFile)
  await http.patch(`/candidat-parcours/${diplomeId}/`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

/** Raw JSON update — path shape shared across controllers, use with care. */
export async function updateDiplome(id: number, body: Partial<DiplomeFormFields>): Promise<void> {
  await http.put(`/${id}`, body)
}

export async function deleteDiplome(id: number): Promise<void> {
  await http.delete(`/${id}`)
}
