import { http } from '@/api/http'
import type { CandidatDTO } from '@/types/dto/CandidatDTO'
import type { CandidatResultatsDTO } from '@/types/dto/CandidatResultatsDTO'

export async function getMyCandidatInfo(): Promise<CandidatDTO> {
  const { data } = await http.get<CandidatDTO>('/candidat-info/')
  return data
}

/** GET /api/candidat-resultats/ — the logged-in candidat's own published exam results and enrollment status. */
export async function getMyResultats(): Promise<CandidatResultatsDTO> {
  const { data } = await http.get<CandidatResultatsDTO>('/candidat-resultats/')
  return data
}

export async function getCandidatProfile(id: number): Promise<CandidatDTO> {
  const { data } = await http.get<CandidatDTO>(`/get-candidat-profile/${id}`)
  return data
}

/**
 * Multipart update. Text fields are read server-side via request.getParameter
 * with these exact names (note: email is "mailCandidat", not "email").
 * File fields are matched by substring in the field NAME (case-insensitive):
 * any name containing "cv" -> pathCv, any name containing "photo"/"image"/"picture" -> pathPhoto.
 */
export interface UpdateCandidatInfoFields {
  nom?: string
  prenom?: string
  cne?: string
  pays?: string
  mailCandidat?: string
  cin?: string
  nomCandidatAr?: string
  prenomCandidatAr?: string
  adresse?: string
  adresseAr?: string
  sexe?: string
  villeDeNaissance?: string
  villeDeNaissanceAr?: string
  ville?: string
  dateDeNaissance?: string
  academie?: string
  telCandidat?: string
  situation_familiale?: string
  fonctionnaire?: string
}

export async function updateMyCandidatInfo(
  fields: UpdateCandidatInfoFields,
  files: { cv?: File; photo?: File },
): Promise<CandidatDTO> {
  const formData = new FormData()
  for (const [key, value] of Object.entries(fields)) {
    if (value !== undefined) formData.append(key, value)
  }
  if (files.cv) formData.append('cv', files.cv)
  if (files.photo) formData.append('photo', files.photo)

  const { data } = await http.put<CandidatDTO>('/candidat-info/', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return data
}
