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

export type HistoryPoint = {
  windowStart: string
  avgSpeedKmh: number
  vehicleCount: number
  sourceKind: SourceKind
}

export type EnvironmentReading = {
  zoneId: string
  zoneName: string
  pm25: number
  no2: number
  airQualityIndex: string
  sourceKind: SourceKind
  notes: string
}

export type Scenario = {
  id: number
  name: string
  assumptions: string
  demandMultiplier: number
  capacityMultiplier: number
  incidentMinutes: number
  predictedAvgSpeedKmh: number
  predictedCongestionIndex: number
  evaluationNotes: string
  createdAt: string
}

export type ForecastResponse = {
  segment_id: string
  model: string
  baseline_mae: number | null
  model_mae: number | null
  notes: string
  predictions: Array<{
    t: number
    value: number
    source_kind: string
    model: string
    lower: number | null
    upper: number | null
  }>
}

export type AnomalyResponse = {
  segment_id: string
  method: string
  anomaly_count: number
  notes: string
  points: Array<{
    t: number
    value: number
    score: number
    is_anomaly: boolean
    source_kind: string
  }>
}

const API_BASE = import.meta.env.VITE_API_BASE ?? ''

async function getJson<T>(path: string): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`)
  if (!res.ok) throw new Error(`${path} failed: ${res.status}`)
  return res.json() as Promise<T>
}

async function postJson<T>(path: string, body?: unknown): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`, {
    method: 'POST',
    headers: body ? { 'Content-Type': 'application/json' } : undefined,
    body: body ? JSON.stringify(body) : undefined,
  })
  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || `${path} failed: ${res.status}`)
  }
  return res.json() as Promise<T>
}

export const fetchStatus = () => getJson<SystemStatus>('/api/v1/status')
export const fetchLatestTraffic = () => getJson<TrafficSegment[]>('/api/v1/analytics/traffic/latest')
export const fetchHistory = (segmentId: string) =>
  getJson<HistoryPoint[]>(`/api/v1/analytics/traffic/${segmentId}/history`)
export const fetchEnvironment = () => getJson<EnvironmentReading[]>('/api/v1/environment/latest')
export const fetchScenarios = () => getJson<Scenario[]>('/api/v1/scenarios')
export const createScenario = (payload: {
  name: string
  assumptions: string
  demandMultiplier: number
  capacityMultiplier: number
  incidentMinutes: number
}) => postJson<Scenario>('/api/v1/scenarios', payload)
export const fetchForecast = (segmentId: string) =>
  postJson<ForecastResponse>(`/api/v1/ml/forecast/${segmentId}?horizon=6&model=ridge`)
export const fetchAnomalies = (segmentId: string) =>
  postJson<AnomalyResponse>(`/api/v1/ml/anomalies/${segmentId}`)
