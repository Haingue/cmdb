import NotificationBell from '../../components/NotificationBell'
import PageTitle from '../../components/PageTitle'
import './Dashboard.css'

function Dashboard() {
  const title = 'dashboard'
  return (
    <>
      <PageTitle title={title} />
      <div>
        <NotificationBell />
      </div>
    </>
  )
}

export default Dashboard
