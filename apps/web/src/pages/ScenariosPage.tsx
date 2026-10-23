import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import { createScenario, fetchScenarios } from '../api'

export function ScenariosPage() {
  const qc = useQueryClient()
  const list = useQuery({ queryKey: ['scenarios'], queryFn: fetchScenarios })
  const [name, setName] = useState('Evening demand surge')
  const [assumptions, setAssumptions] = useState(
    'Demand +40%, capacity unchanged, 20-min incident on Harbor Avenue.',
  )
  const [demand, setDemand] = useState(1.4)
  const [capacity, setCapacity] = useState(1.0)
  const [incident, setIncident] = useState(20)

  const create = useMutation({
    mutationFn: () =>
      createScenario({
        name,
        assumptions,
        demandMultiplier: demand,
        capacityMultiplier: capacity,
        incidentMinutes: incident,
      }),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['scenarios'] }),
  })

  return (
    <div className="grid gap-5 lg:grid-cols-[340px_1fr]">
      <form
        className="grid gap-3 rounded-xl border border-white/10 bg-[#121a2b] p-4"
        onSubmit={(e) => {
          e.preventDefault()
          create.mutate()
        }}
      >
        <h2 className="text-xl font-semibold">Scenario builder</h2>
        <p className="text-xs text-[#93a4c3]">
          Transparent what-if model — not a calibrated traffic assignment engine.
        </p>
        <label className="grid gap-1 text-sm">
          Name
          <input
            className="rounded-md border border-white/10 bg-[#0b1220] px-3 py-2"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />
        </label>
        <label className="grid gap-1 text-sm">
          Assumptions
          <textarea
            className="min-h-24 rounded-md border border-white/10 bg-[#0b1220] px-3 py-2"
            value={assumptions}
            onChange={(e) => setAssumptions(e.target.value)}
            required
          />
        </label>
        <Slider label="Demand ×" value={demand} min={0.2} max={3} step={0.1} onChange={setDemand} />
        <Slider label="Capacity ×" value={capacity} min={0.2} max={3} step={0.1} onChange={setCapacity} />
        <Slider label="Incident (min)" value={incident} min={0} max={120} step={5} onChange={setIncident} />
        <button
          type="submit"
          className="rounded-md bg-[#3ecf8e] px-3 py-2 text-sm font-medium text-[#0b1220]"
          disabled={create.isPending}
        >
          {create.isPending ? 'Running…' : 'Simulate scenario'}
        </button>
      </form>

      <div className="grid gap-3">
        <h3 className="text-lg font-medium">Saved runs</h3>
        {(list.data ?? []).length === 0 && (
          <p className="text-sm text-[#93a4c3]">No scenarios yet — create one on the left.</p>
        )}
        {(list.data ?? []).map((s) => (
          <article key={s.id} className="rounded-xl border border-white/10 bg-[#121a2b] p-4">
            <div className="flex flex-wrap items-start justify-between gap-2">
              <h4 className="font-medium">{s.name}</h4>
              <span className="font-mono text-xs text-[#93a4c3]">
                {new Date(s.createdAt).toLocaleString()}
              </span>
            </div>
            <p className="mt-2 text-sm text-[#93a4c3]">{s.assumptions}</p>
            <dl className="mt-3 grid grid-cols-2 gap-2 text-sm md:grid-cols-4">
              <Metric label="Demand ×" value={s.demandMultiplier.toFixed(1)} />
              <Metric label="Capacity ×" value={s.capacityMultiplier.toFixed(1)} />
              <Metric label="Pred. speed" value={`${s.predictedAvgSpeedKmh} km/h`} />
              <Metric label="Congestion idx" value={String(s.predictedCongestionIndex)} />
            </dl>
            <p className="mt-3 text-xs text-[#93a4c3]">{s.evaluationNotes}</p>
          </article>
        ))}
      </div>
    </div>
  )
}

function Slider({
  label,
  value,
  min,
  max,
  step,
  onChange,
}: {
  label: string
  value: number
  min: number
  max: number
  step: number
  onChange: (v: number) => void
}) {
  return (
    <label className="grid gap-1 text-sm">
      <span className="flex justify-between">
        <span>{label}</span>
        <span className="font-mono text-[#3ecf8e]">{value}</span>
      </span>
      <input
        type="range"
        min={min}
        max={max}
        step={step}
        value={value}
        onChange={(e) => onChange(Number(e.target.value))}
      />
    </label>
  )
}

function Metric({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <dt className="text-[#93a4c3]">{label}</dt>
      <dd className="font-semibold">{value}</dd>
    </div>
  )
}
