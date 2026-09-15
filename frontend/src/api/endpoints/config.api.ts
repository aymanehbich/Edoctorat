import { http } from '@/api/http'
import type { Config } from '@/types/dto/Config'

/** Can legitimately return null when the config table is empty — callers must handle that. */
export async function getBaseConfig(): Promise<Config | null> {
  const { data } = await http.get<Config | null>('/get-base-config/')
  return data
}
