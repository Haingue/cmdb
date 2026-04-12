import PageTitle from '../../components/PageTitle'
import './Dashboard.css'
import { Doughnut } from 'react-chartjs-2';

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
  return (
    <>
      <PageTitle title={title} />
      <div className="hidden">
      </div>
      <section className="p-4 rounded-lg dark:bg-gray-800 mt-4">
        <div className="grid grid-cols-1 gap-4 mb-4 lg:grid-cols-3">
          <MeasureCard title="Total clients" value="32" change="+2" />
          <MeasureCard title="Server uptime" value="99.9%" />
          <MeasureCard title="Active sessions" value="5" />
        </div>
      </section>
      <section className="p-4 rounded-lg dark:bg-gray-800 mt-4">
        <div className="grid grid-cols-1 gap-4 mb-4 lg:grid-cols-3">
          <MeasureCard title="Projects" value="84" />
          <MeasureCard title="Software" value="34" />
          <MeasureCard title="Servers" value="72" />
          <MeasureCard title="End user device" value="605" change="-1.2%" />
        </div>
      </section>
      <section className="p-4 rounded-lg dark:bg-gray-800 mt-4">
        <TrafficRateChart />
      </section>
    </>
  )
}

export default Dashboard
