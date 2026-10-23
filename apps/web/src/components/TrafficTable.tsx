import type { TrafficSegment } from '../api'

export function TrafficTable({ segments }: { segments: TrafficSegment[] }) {
  return (
    <div className="overflow-x-auto rounded-xl border border-white/10 bg-[#121a2b]">
      <table className="min-w-full text-left text-sm">
        <thead className="border-b border-white/10 text-[#93a4c3]">
          <tr>
            <th className="px-3 py-2 font-medium">Segment</th>
            <th className="px-3 py-2 font-medium">Avg speed</th>
            <th className="px-3 py-2 font-medium">Vehicles</th>
            <th className="px-3 py-2 font-medium">Congestion</th>
            <th className="px-3 py-2 font-medium">Provenance</th>
          </tr>
        </thead>
        <tbody>
          {segments.map((s) => (
            <tr key={s.spatialEntityId} className="border-b border-white/5 last:border-0">
              <td className="px-3 py-2">{s.name}</td>
              <td className="px-3 py-2">{s.avgSpeedKmh.toFixed(1)} km/h</td>
              <td className="px-3 py-2">{s.vehicleCount.toFixed(1)}</td>
              <td className="px-3 py-2 capitalize">{s.congestionLevel}</td>
              <td className="px-3 py-2 font-mono text-xs text-[#3ecf8e]">
                {s.sourceKind ?? 'n/a'}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
