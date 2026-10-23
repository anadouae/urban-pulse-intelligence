import { NavLink, Outlet } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { fetchStatus } from '../api'

const links = [
  ['/', 'Overview'],
  ['/twin', '3D Twin'],
  ['/traffic', 'Traffic'],
  ['/environment', 'Environment'],
  ['/forecast', 'Forecast'],
  ['/scenarios', 'Scenarios'],
  ['/pipeline', 'Pipeline'],
] as const

export function Shell() {
  const status = useQuery({ queryKey: ['status'], queryFn: fetchStatus, refetchInterval: 5000 })

  return (
    <div className="min-h-screen bg-[radial-gradient(1000px_500px_at_0%_-10%,#1b2a48_0%,#0b1220_50%)] text-[#e8eefc]">
      <header className="border-b border-white/10 bg-[#0b1220]/80 backdrop-blur">
        <div className="mx-auto flex max-w-7xl flex-col gap-4 px-4 py-4 md:flex-row md:items-center md:justify-between md:px-6">
          <div>
            <p className="text-[11px] tracking-[0.28em] text-[#3ecf8e] uppercase">Urban Pulse Intelligence</p>
            <h1 className="text-xl font-semibold tracking-tight md:text-2xl">
              {status.data?.cityLabel ?? 'Harbor District'}
            </h1>
          </div>
          <div className="flex flex-wrap gap-3 text-xs text-[#93a4c3]">
            <Badge label="Milestone" value={status.data?.milestone ?? '…'} />
            <Badge label="Mode" value={status.data?.dataMode ?? '…'} />
            <Badge label="Events" value={String(status.data?.eventCount ?? '…')} />
          </div>
        </div>
        <nav className="mx-auto flex max-w-7xl gap-1 overflow-x-auto px-4 pb-3 md:px-6">
          {links.map(([to, label]) => (
            <NavLink
              key={to}
              to={to}
              end={to === '/'}
              className={({ isActive }) =>
                `rounded-md px-3 py-1.5 text-sm whitespace-nowrap transition ${
                  isActive
                    ? 'bg-white/10 text-white'
                    : 'text-[#93a4c3] hover:bg-white/5 hover:text-white'
                }`
              }
            >
              {label}
            </NavLink>
          ))}
        </nav>
      </header>
      <main className="mx-auto max-w-7xl px-4 py-6 md:px-6">
        <Outlet />
      </main>
      <footer className="mx-auto max-w-7xl px-4 pb-8 text-xs text-[#93a4c3] md:px-6">
        {status.data?.notes}
      </footer>
    </div>
  )
}

function Badge({ label, value }: { label: string; value: string }) {
  return (
    <span className="rounded-md border border-white/10 bg-[#121a2b] px-2.5 py-1">
      <span className="text-[#93a4c3]">{label}: </span>
      <span className="font-mono text-[#e8eefc]">{value}</span>
    </span>
  )
}
