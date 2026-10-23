import { useMutation, useQuery } from '@tanstack/react-query'
import { useMemo, useState } from 'react'
import {
  CartesianGrid,
  Legend,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'
import { fetchAnomalies, fetchForecast, fetchHistory, fetchLatestTraffic } from '../api'

export function ForecastPage() {
  const traffic = useQuery({ queryKey: ['traffic-latest'], queryFn: fetchLatestTraffic })
  const segments = traffic.data ?? []
  const [segmentId, setSegmentId] = useState('seg-ave-mid')

  const history = useQuery({
    queryKey: ['history', segmentId],
    queryFn: () => fetchHistory(segmentId),
  })

  const forecast = useMutation({ mutationFn: () => fetchForecast(segmentId) })
  const anomalies = useMutation({ mutationFn: () => fetchAnomalies(segmentId) })

  const chartData = useMemo(() => {
    const hist = (history.data ?? []).map((h) => ({
      label: new Date(h.windowStart).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      observed: h.avgSpeedKmh,
      predicted: null as number | null,
    }))
    const preds = (forecast.data?.predictions ?? []).map((p) => ({
      label: new Date(p.t * 1000).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      observed: null as number | null,
      predicted: p.value,
    }))
    return [...hist, ...preds]
  }, [history.data, forecast.data])

  return (
    <div className="grid gap-5">
      <div className="flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
        <div>
          <h2 className="text-2xl font-semibold">Forecast & anomalies</h2>
          <p className="mt-1 text-sm text-[#93a4c3]">
            Ridge window model vs history · IsolationForest flags · outputs labeled PREDICTED.
          </p>
        </div>
        <div className="flex flex-wrap gap-2">
          <select
            className="rounded-md border border-white/10 bg-[#121a2b] px-3 py-2 text-sm"
            value={segmentId}
            onChange={(e) => setSegmentId(e.target.value)}
          >
            {segments.map((s) => (
              <option key={s.spatialEntityId} value={s.spatialEntityId}>
                {s.name}
              </option>
            ))}
          </select>
          <button
            className="rounded-md bg-[#3ecf8e] px-3 py-2 text-sm font-medium text-[#0b1220]"
            onClick={() => forecast.mutate()}
          >
            Run forecast
          </button>
          <button
            className="rounded-md border border-white/15 px-3 py-2 text-sm"
            onClick={() => anomalies.mutate()}
          >
            Detect anomalies
          </button>
        </div>
      </div>

      <div className="h-80 rounded-xl border border-white/10 bg-[#121a2b] p-3">
        <ResponsiveContainer width="100%" height="100%">
          <LineChart data={chartData}>
            <CartesianGrid stroke="#243352" />
            <XAxis dataKey="label" stroke="#93a4c3" fontSize={11} />
            <YAxis stroke="#93a4c3" fontSize={11} unit=" km/h" />
            <Tooltip contentStyle={{ background: '#0b1220', border: '1px solid #243352' }} />
            <Legend />
            <Line type="monotone" dataKey="observed" stroke="#93a4c3" dot={false} name="History (SIMULATED)" />
            <Line type="monotone" dataKey="predicted" stroke="#3ecf8e" strokeDasharray="4 4" name="Forecast (PREDICTED)" />
          </LineChart>
        </ResponsiveContainer>
      </div>

      {forecast.data && (
        <p className="text-sm text-[#93a4c3]">
          Model <span className="text-[#e8eefc]">{forecast.data.model}</span>
          {forecast.data.model_mae != null && (
            <> · MAE {forecast.data.model_mae.toFixed(2)} (baseline {forecast.data.baseline_mae?.toFixed(2)})</>
          )}
          {' · '}{forecast.data.notes}
        </p>
      )}

      {anomalies.data && (
        <div className="rounded-xl border border-white/10 bg-[#121a2b] p-4 text-sm">
          <p className="font-medium">
            Anomalies: {anomalies.data.anomaly_count} ({anomalies.data.method})
          </p>
          <p className="mt-1 text-[#93a4c3]">{anomalies.data.notes}</p>
        </div>
      )}

      {(forecast.error || anomalies.error) && (
        <p className="text-sm text-[#f07178]">
          {(forecast.error as Error)?.message || (anomalies.error as Error)?.message}
        </p>
      )}
    </div>
  )
}
