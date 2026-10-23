import { useQuery } from '@tanstack/react-query'
import { fetchLatestTraffic } from '../api'
import { CityTwin } from '../components/CityTwin'
import { TrafficTable } from '../components/TrafficTable'

export function TwinPage() {
  const traffic = useQuery({ queryKey: ['traffic-latest'], queryFn: fetchLatestTraffic, refetchInterval: 4000 })
  const segments = traffic.data ?? []

  return (
    <div className="grid gap-4">
      <div>
        <h2 className="text-2xl font-semibold">Interactive digital twin</h2>
        <p className="mt-1 text-sm text-[#93a4c3]">
          Procedural Harbor District — road colors encode congestion from the Java analytics API.
        </p>
      </div>
      <CityTwin segments={segments} />
      <div className="flex flex-wrap gap-3 text-xs text-[#93a4c3]">
        <span className="inline-flex items-center gap-2"><span className="inline-block h-2.5 w-2.5 rounded-sm bg-[#3ecf8e]" /> Free</span>
        <span className="inline-flex items-center gap-2"><span className="inline-block h-2.5 w-2.5 rounded-sm bg-[#f0b429]" /> Moderate</span>
        <span className="inline-flex items-center gap-2"><span className="inline-block h-2.5 w-2.5 rounded-sm bg-[#f78c6c]" /> Heavy</span>
        <span className="inline-flex items-center gap-2"><span className="inline-block h-2.5 w-2.5 rounded-sm bg-[#f07178]" /> Severe</span>
      </div>
      <TrafficTable segments={segments} />
    </div>
  )
}
