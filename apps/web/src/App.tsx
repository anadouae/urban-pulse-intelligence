import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { Shell } from './components/Shell'
import { EnvironmentPage } from './pages/EnvironmentPage'
import { ForecastPage } from './pages/ForecastPage'
import { OverviewPage } from './pages/OverviewPage'
import { PipelinePage } from './pages/PipelinePage'
import { ScenariosPage } from './pages/ScenariosPage'
import { TrafficPage } from './pages/TrafficPage'
import { TwinPage } from './pages/TwinPage'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Shell />}>
          <Route index element={<OverviewPage />} />
          <Route path="twin" element={<TwinPage />} />
          <Route path="traffic" element={<TrafficPage />} />
          <Route path="environment" element={<EnvironmentPage />} />
          <Route path="forecast" element={<ForecastPage />} />
          <Route path="scenarios" element={<ScenariosPage />} />
          <Route path="pipeline" element={<PipelinePage />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}
