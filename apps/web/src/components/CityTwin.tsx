import { Canvas } from '@react-three/fiber'
import { OrbitControls, Grid, Text } from '@react-three/drei'
import type { TrafficSegment } from '../api'

function congestionColor(level: string): string {
  switch (level) {
    case 'free':
      return '#3ecf8e'
    case 'moderate':
      return '#f0b429'
    case 'heavy':
      return '#f78c6c'
    case 'severe':
      return '#f07178'
    default:
      return '#5b6b88'
  }
}

function RoadSegment({ segment }: { segment: TrafficSegment }) {
  const color = congestionColor(segment.congestionLevel)
  return (
    <group position={[segment.x, 0.05, segment.z]} rotation={[0, (segment.headingDeg * Math.PI) / 180, 0]}>
      <mesh>
        <boxGeometry args={[segment.length, 0.08, 1.4]} />
        <meshStandardMaterial color={color} emissive={color} emissiveIntensity={0.18} />
      </mesh>
      <Text
        position={[0, 0.35, 0]}
        fontSize={0.55}
        color="#e8eefc"
        anchorX="center"
        anchorY="middle"
        outlineWidth={0.02}
        outlineColor="#0b1220"
      >
        {segment.name}
      </Text>
    </group>
  )
}

function Building({ x, z, h }: { x: number; z: number; h: number }) {
  return (
    <mesh position={[x, h / 2, z]}>
      <boxGeometry args={[2.2, h, 2.2]} />
      <meshStandardMaterial color="#243352" />
    </mesh>
  )
}

export function CityTwin({ segments }: { segments: TrafficSegment[] }) {
  return (
    <div className="h-[420px] w-full overflow-hidden rounded-xl border border-white/10 bg-[#0a1020]">
      <Canvas camera={{ position: [28, 24, 28], fov: 42 }}>
        <color attach="background" args={['#0a1020']} />
        <ambientLight intensity={0.55} />
        <directionalLight position={[20, 30, 10]} intensity={1.1} />
        <Grid
          args={[60, 60]}
          cellSize={2}
          cellThickness={0.5}
          sectionSize={10}
          sectionThickness={1}
          fadeDistance={55}
          cellColor="#1d2a44"
          sectionColor="#2c4068"
        />
        <mesh rotation={[-Math.PI / 2, 0, 0]} position={[0, -0.02, 0]}>
          <planeGeometry args={[60, 60]} />
          <meshStandardMaterial color="#10182a" />
        </mesh>

        {/* Procedural filler buildings — not a real city footprint */}
        <Building x={-18} z={-18} h={4} />
        <Building x={18} z={-16} h={6} />
        <Building x={-16} z={16} h={5} />
        <Building x={16} z={18} h={7} />
        <Building x={-6} z={-18} h={3.5} />
        <Building x={6} z={18} h={4.5} />

        {segments.map((segment) => (
          <RoadSegment key={segment.spatialEntityId} segment={segment} />
        ))}

        <OrbitControls makeDefault maxPolarAngle={Math.PI / 2.15} minDistance={12} maxDistance={60} />
      </Canvas>
    </div>
  )
}
