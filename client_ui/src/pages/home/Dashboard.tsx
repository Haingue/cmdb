import { useEffect, useState } from 'react'
import PageTitle from '../../components/PageTitle'
import './Dashboard.css'

import { DashboardMetrics } from '../../service/backend/types';
import BackendSync from '../../service/backend/BackendSync';

export const MeasureCard = ({ title, value, change, chartId }: { title: string; value: string; change?: string; chartId?: string }) => {
  return (
    <div className="min-w-0 p-4 bg-white rounded-lg shadow dark:bg-gray-800">
      <h5 className="mb-4 text-base font-semibold text-gray-900 dark:text-white">{title}</h5>
      <span className="text-2xl font-bold text-gray-900 dark:text-white">{value}</span>
      {change && (
        <span className={`text-sm font-medium ${change.startsWith('+') ? 'text-green-500 dark:text-green-400' : 'text-red-500 dark:text-red-400'}`}>
          {change}
        </span>
      )}
      <div className="mt-4 overflow-clip">
        <canvas id={chartId} width="400" height="100">
        </canvas>
      </div>
    </div>
  )
}

const TrafficRateChart = () => {
  return (
    <div className="min-w-0 p-4 bg-white rounded-lg shadow dark:bg-gray-800">
    </div>
  )
}

function Dashboard() {
  const title = 'dashboard'
  const [metrics, setMetrics] = useState<DashboardMetrics | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    BackendSync.getDashboardMetrics()
      .then((data) => {
        setMetrics(data)
        setLoading(false)
      })
      .catch((err) => {
        console.error('Error fetching dashboard metrics:', err)
        setLoading(false)
      })
  }, [])

  if (loading) {
    return (
      <>
        <PageTitle title={title} />
        <div className="p-4 text-center text-gray-500">Chargement des métriques...</div>
      </>
    )
  }
      <section className="p-4 rounded-lg dark:bg-gray-800 mt-4">
        <div className="grid grid-cols-1 gap-4 mb-4 lg:grid-cols-3">
          <MeasureCard title="Servers" value={metrics?.serverCount.toString() ?? '0'} />
          <MeasureCard title="Applications" value={metrics?.applicationCount.toString() ?? '0'} />
          <MeasureCard title="Projects" value={metrics?.projectCount.toString() ?? '0'} />
          <MeasureCard title="Users actifs" value={metrics?.activeUserCount.toString() ?? '0'} />
        </div>
      </section>
      <section className="p-4 rounded-lg dark:bg-gray-800 mt-4">
        <TrafficRateChart />
      </section>
    </>
  )
}

export default Dashboard
