import { useQuery } from '@tanstack/react-query'
import { fetchEnvironment } from '../api'

export function EnvironmentPage() {
  const env = useQuery({ queryKey: ['environment'], queryFn: fetchEnvironment, refetchInterval: 5000 })

  return (
    <div className="grid gap-5">
      <div>
        <h2 className="text-2xl font-semibold">Environmental indicators</h2>
        <p className="mt-1 text-sm text-[#93a4c3]">
          PM2.5 / NO₂ derived from simulated traffic load — not municipal air-quality sensors.
        </p>
      </div>
      <div className="grid gap-3 md:grid-cols-2">
        {(env.data ?? []).map((z) => (
          <article key={z.zoneId} className="rounded-xl border border-white/10 bg-[#121a2b] p-4">
            <div className="flex items-start justify-between gap-3">
              <h3 className="font-medium">{z.zoneName}</h3>
              <span className="rounded bg-white/5 px-2 py-0.5 font-mono text-xs text-[#3ecf8e]">
                {z.sourceKind}
              </span>
            </div>
            <dl className="mt-3 grid grid-cols-3 gap-2 text-sm">
              <div>
                <dt className="text-[#93a4c3]">PM2.5</dt>
                <dd className="text-lg font-semibold">{z.pm25}</dd>
              </div>
              <div>
                <dt className="text-[#93a4c3]">NO₂</dt>
                <dd className="text-lg font-semibold">{z.no2}</dd>
              </div>
              <div>
                <dt className="text-[#93a4c3]">AQI band</dt>
                <dd className="text-lg font-semibold capitalize">{z.airQualityIndex.replaceAll('_', ' ')}</dd>
              </div>
            </dl>
            <p className="mt-3 text-xs text-[#93a4c3]">{z.notes}</p>
          </article>
        ))}
      </div>
    </div>
  )
}
