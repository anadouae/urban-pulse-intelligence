import { useQuery } from '@tanstack/react-query'
import { fetchStatus } from '../api'

const stages = [
  { name: 'Ingestion generator', detail: 'Python synthetic traffic events (schema v1)', status: 'ready' },
  { name: 'Java API validate + store', detail: 'Spring Boot + H2 aggregates (5-min windows)', status: 'ready' },
  { name: 'Analytics serving', detail: 'REST traffic / environment / scenarios', status: 'ready' },
  { name: 'ML inference', detail: 'FastAPI ridge forecast + IsolationForest', status: 'ready' },
  { name: 'Kafka / Spark', detail: 'Compose stubs present — wire-up in next hardening pass', status: 'planned' },
]

export function PipelinePage() {
  const status = useQuery({ queryKey: ['status'], queryFn: fetchStatus, refetchInterval: 4000 })

  return (
    <div className="grid gap-5">
      <div>
        <h2 className="text-2xl font-semibold">Pipeline & provenance</h2>
        <p className="mt-1 text-sm text-[#93a4c3]">
          Honest system map — planned stages are labeled, never shown as live production.
        </p>
      </div>

      <div className="grid gap-3 sm:grid-cols-3">
        <Card label="Events ingested" value={String(status.data?.eventCount ?? '…')} />
        <Card label="Aggregate windows" value={String(status.data?.aggregateCount ?? '…')} />
        <Card label="Spatial entities" value={String(status.data?.spatialEntityCount ?? '…')} />
      </div>

      <ol className="grid gap-3">
        {stages.map((s) => (
          <li key={s.name} className="rounded-xl border border-white/10 bg-[#121a2b] px-4 py-3">
            <div className="flex items-center justify-between gap-3">
              <p className="font-medium">{s.name}</p>
              <span
                className={`rounded px-2 py-0.5 font-mono text-xs ${
                  s.status === 'ready' ? 'bg-[#3ecf8e]/15 text-[#3ecf8e]' : 'bg-white/5 text-[#93a4c3]'
                }`}
              >
                {s.status}
              </span>
            </div>
            <p className="mt-1 text-sm text-[#93a4c3]">{s.detail}</p>
          </li>
        ))}
      </ol>
    </div>
  )
}

function Card({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-xl border border-white/10 bg-[#121a2b] px-4 py-4">
      <p className="text-xs text-[#93a4c3] uppercase">{label}</p>
      <p className="mt-2 text-2xl font-semibold">{value}</p>
    </div>
  )
}
