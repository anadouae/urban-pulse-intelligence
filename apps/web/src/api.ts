export type SourceKind = 'SIMULATED' | 'OBSERVED' | 'PREDICTED' | null

export type TrafficSegment = {
  spatialEntityId: string
  name: string
  x: number
  z: number
  length: number
  headingDeg: number
  avgSpeedKmh: number
  vehicleCount: number
  sampleCount: number
  windowStart: string | null
  windowEnd: string | null
  sourceKind: SourceKind
  congestionLevel: string
}

export type SystemStatus = {
  cityId: string
  cityLabel: string
  milestone: string
  eventCount: number
  aggregateCount: number
  spatialEntityCount: number
  serverTime: string
  dataMode: string
  notes: string
}

const API_BASE = import.meta.env.VITE_API_BASE ?? ''

async function getJson<T>(path: string): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`)
  if (!res.ok) {
    throw new Error(`${path} failed: ${res.status}`)
  }
  return res.json() as Promise<T>
}

export function fetchStatus() {
  return getJson<SystemStatus>('/api/v1/status')
}

export function fetchLatestTraffic() {
  return getJson<TrafficSegment[]>('/api/v1/analytics/traffic/latest')
}
