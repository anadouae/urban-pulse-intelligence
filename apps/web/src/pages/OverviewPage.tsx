import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { fetchEnvironment, fetchLatestTraffic, fetchStatus } from '../api'

export function OverviewPage() {
  const status = useQuery({ queryKey: ['status'], queryFn: fetchStatus })
  const traffic = useQuery({ queryKey: ['traffic-latest'], queryFn: fetchLatestTraffic, refetchInterval: 4000 })
  const env = useQuery({ queryKey: ['environment'], queryFn: fetchEnvironment })

  const segments = traffic.data ?? []
  const severe = segments.filter((s) => s.congestionLevel === 'severe' || s.congestionLevel === 'heavy').length
  const avgSpeed =
    segments.length === 0
      ? 0
      : segments.reduce((a, s) => a + s.avgSpeedKmh, 0) / segments.length

  return (
    <div className="grid gap-6">
      <section className="grid gap-2">
        <h2 className="text-2xl font-semibold tracking-tight">Operations overview</h2>
        <p className="max-w-3xl text-sm text-[#93a4c3]">
          Live console for the Harbor District procedural twin. Every metric below is labeled by provenance —
          this milestone runs on <span className="text-[#3ecf8e]">SIMULATED</span> traffic.
        </p>
      </section>

      <section className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
        <Stat title="Avg speed" value={`${avgSpeed.toFixed(1)} km/h`} hint="Across segments" />
        <Stat title="Stressed roads" value={String(severe)} hint="Heavy + severe" />
        <Stat title="Aggregates" value={String(status.data?.aggregateCount ?? '…')} hint="5-min windows" />
        <Stat title="AQ zones" value={String(env.data?.length ?? '…')} hint="Simulated indicators" />
      </section>

      <section className="grid gap-3 md:grid-cols-3">
        <QuickLink to="/twin" title="Inspect 3D twin" body="Orbit the procedural city and congestion layers." />
        <QuickLink to="/forecast" title="Run forecasts" body="Compare ridge predictions against history." />
        <QuickLink to="/scenarios" title="Compare scenarios" body="What-if demand, capacity, and incidents." />
      </section>
    </div>
  )
}

function Stat({ title, value, hint }: { title: string; value: string; hint: string }) {
  return (
    <div className="rounded-xl border border-white/10 bg-[#121a2b] px-4 py-4">
      <p className="text-xs tracking-wide text-[#93a4c3] uppercase">{title}</p>
      <p className="mt-2 text-2xl font-semibold">{value}</p>
      <p className="mt-1 text-xs text-[#93a4c3]">{hint}</p>
    </div>
  )
}

function QuickLink({ to, title, body }: { to: string; title: string; body: string }) {
  return (
    <Link
      to={to}
      className="rounded-xl border border-white/10 bg-[#121a2b] px-4 py-4 transition hover:border-[#3ecf8e]/40 hover:bg-[#152036]"
    >
      <p className="font-medium">{title}</p>
      <p className="mt-1 text-sm text-[#93a4c3]">{body}</p>
    </Link>
  )
}
