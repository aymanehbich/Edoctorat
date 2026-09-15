import { http } from '@/api/http'
import type { AuthProf, LoginRequest, TokenResponse, UserInfo, UserInfoResponse } from '@/types/dto/Auth'

export async function loginCandidat(body: LoginRequest): Promise<TokenResponse> {
  const { data } = await http.post<TokenResponse>('/token/', body)
  return data
}

export async function loginScolarite(body: LoginRequest): Promise<UserInfo> {
  const { data } = await http.post<UserInfo>('/login_scolarite/', body)
  return data
}

export async function verifyIsProf(googleIdToken: string): Promise<AuthProf> {
  const { data } = await http.post<AuthProf>('/verify-is-prof/', { token: googleIdToken })
  return data
}

export async function getUserInfo(): Promise<UserInfoResponse> {
  const { data } = await http.get<UserInfoResponse>('/get-user-info/')
  return data
}

export async function refreshToken(refresh: string): Promise<TokenResponse> {
  const { data } = await http.post<TokenResponse>('/token/refresh/', { refresh })
  return data
}
