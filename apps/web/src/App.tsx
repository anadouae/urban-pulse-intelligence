import { useQuery } from '@tanstack/react-query'
import { fetchLatestTraffic, fetchStatus } from './api'
import { CityTwin } from './components/CityTwin'
import { TrafficTable } from './components/TrafficTable'

export default function App() {
  const statusQuery = useQuery({
    queryKey: ['status'],
    queryFn: fetchStatus,
    refetchInterval: 5000,
  })

  const trafficQuery = useQuery({
    queryKey: ['traffic-latest'],
    queryFn: fetchLatestTraffic,
    refetchInterval: 4000,
  })

  const status = statusQuery.data
  const segments = trafficQuery.data ?? []

  return (
    <div className="mx-auto flex min-h-screen max-w-6xl flex-col gap-6 px-4 py-6 md:px-8">
      <header className="flex flex-col gap-3 border-b border-white/10 pb-5 md:flex-row md:items-end md:justify-between">
        <div>
          <p className="text-xs tracking-[0.22em] text-[#3ecf8e] uppercase">Urban Pulse Intelligence</p>
          <h1 className="mt-1 text-3xl font-semibold tracking-tight text-[#e8eefc] md:text-4xl">
            Harbor District console
          </h1>
          <p className="mt-2 max-w-2xl text-sm text-[#93a4c3]">
            Milestone 1 vertical slice — synthetic traffic events flow into a Java API and paint a
            procedural 3D twin. Values are labeled <span className="text-[#3ecf8e]">SIMULATED</span>,
            not real municipal telemetry.
          </p>
        </div>
        <div className="rounded-lg border border-white/10 bg-[#121a2b] px-4 py-3 text-sm text-[#93a4c3]">
          <div>
            Mode:{' '}
            <span className="font-mono text-[#e8eefc]">{status?.dataMode ?? '…'}</span>
          </div>
          <div>
            Events: <span className="font-mono text-[#e8eefc]">{status?.eventCount ?? '…'}</span>
          </div>
          <div>
            Milestone: <span className="font-mono text-[#e8eefc]">{status?.milestone ?? '…'}</span>
          </div>
        </div>
      </header>

      {(statusQuery.isError || trafficQuery.isError) && (
        <div className="rounded-lg border border-[#f07178]/40 bg-[#f07178]/10 px-4 py-3 text-sm text-[#f07178]">
          API unreachable. Start the Java service on port 3847, then run the ingestion script.
        </div>
      )}

      {(statusQuery.isLoading || trafficQuery.isLoading) && (
        <div className="rounded-lg border border-white/10 bg-[#121a2b] px-4 py-3 text-sm text-[#93a4c3]">
          Loading live analytics…
        </div>
      )}

      <section className="grid gap-4">
        <div className="flex items-center justify-between gap-3">
          <h2 className="text-lg font-medium">Digital twin (procedural demo city)</h2>
          <p className="text-xs text-[#93a4c3]">Drag to orbit · scroll to zoom</p>
        </div>
        <CityTwin segments={segments} />
        <div className="flex flex-wrap gap-3 text-xs text-[#93a4c3]">
          <Legend swatch="#3ecf8e" label="Free" />
          <Legend swatch="#f0b429" label="Moderate" />
          <Legend swatch="#f78c6c" label="Heavy" />
          <Legend swatch="#f07178" label="Severe" />
          <Legend swatch="#5b6b88" label="No data" />
        </div>
      </section>

      <section className="grid gap-3">
        <h2 className="text-lg font-medium">Latest traffic windows</h2>
        {segments.length === 0 ? (
          <div className="rounded-xl border border-dashed border-white/15 px-4 py-8 text-sm text-[#93a4c3]">
            No aggregates yet. Run{' '}
            <code className="rounded bg-white/5 px-1.5 py-0.5 text-[#e8eefc]">
              python3 services/ingestion/generate_traffic.py
            </code>
          </div>
        ) : (
          <TrafficTable segments={segments} />
        )}
      </section>

      {status?.notes && (
        <footer className="pb-8 text-xs leading-relaxed text-[#93a4c3]">{status.notes}</footer>
      )}
    </div>
  )
}

function Legend({ swatch, label }: { swatch: string; label: string }) {
  return (
    <span className="inline-flex items-center gap-2">
      <span className="inline-block h-2.5 w-2.5 rounded-sm" style={{ background: swatch }} />
      {label}
    </span>
  )
}
