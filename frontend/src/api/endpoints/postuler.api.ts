import { http } from '@/api/http'
import type { Result } from '@/api/wrappers'
import type { PostulerDTO } from '@/types/dto/PostulerDTO'

export async function getMyPostulations(): Promise<Result<PostulerDTO>> {
  const { data } = await http.get<Result<PostulerDTO>>('/candidat-postules/')
  return data
}

export async function applyToSujet(sujetId: number): Promise<PostulerDTO> {
  const { data } = await http.post<PostulerDTO>('/candidat-postules/', { sujet: sujetId })
  return data
}

export async function deletePostulation(id: number): Promise<void> {
  await http.delete(`/candidat-postules/${id}`)
}

/** Response body on success/failure is a plain string, e.g. "File uploaded successfully" — not JSON. */
export async function attachPostulationFile(id: number, file: File): Promise<string> {
  const formData = new FormData()
  formData.append('pathFile', file)
  const { data } = await http.patch<string>(`/candidat-postules/${id}/`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return data
}
