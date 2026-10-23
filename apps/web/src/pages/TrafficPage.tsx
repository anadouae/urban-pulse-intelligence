import { useQuery } from '@tanstack/react-query'
import {
  Bar,
  BarChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'
import { fetchLatestTraffic } from '../api'
import { TrafficTable } from '../components/TrafficTable'

export function TrafficPage() {
  const traffic = useQuery({ queryKey: ['traffic-latest'], queryFn: fetchLatestTraffic, refetchInterval: 4000 })
  const segments = traffic.data ?? []
  const chartData = segments.map((s) => ({
    name: s.name.replace(' Avenue', '').replace(' Street', ''),
    speed: s.avgSpeedKmh,
    vehicles: s.vehicleCount,
  }))

  return (
    <div className="grid gap-5">
      <div>
        <h2 className="text-2xl font-semibold">Traffic analytics</h2>
        <p className="mt-1 text-sm text-[#93a4c3]">
          Units: km/h and vehicle counts per 5-minute window · source: SIMULATED aggregates.
        </p>
      </div>
      <div className="h-72 rounded-xl border border-white/10 bg-[#121a2b] p-3">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart data={chartData}>
            <CartesianGrid stroke="#243352" vertical={false} />
            <XAxis dataKey="name" stroke="#93a4c3" fontSize={12} />
            <YAxis stroke="#93a4c3" fontSize={12} />
            <Tooltip
              contentStyle={{ background: '#0b1220', border: '1px solid #243352', borderRadius: 8 }}
            />
            <Bar dataKey="speed" fill="#3ecf8e" name="Avg speed (km/h)" radius={[4, 4, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
      </div>
      <TrafficTable segments={segments} />
    </div>
  )
}
