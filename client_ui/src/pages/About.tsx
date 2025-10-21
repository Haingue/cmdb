import { useEffect } from 'react'
import PageTitle from '../components/PageTitle'
import BackendService from '../service/backend/BackendSync'
import InventoryService from '../service/inventory/InventorySync'

type ServerInfo = {
  name: string
  version: string
  description: string
}
type ComponentStatusProps = {
  name: string
  version: string
  isUp: boolean
}
const ComponentStatus = ({component} : {component: ComponentStatusProps}) => (
    <div
      key={component.name}
      className="flex items-center justify-between px-3 py-2 rounded-md border bg-white shadow-sm"
      role="group"
      aria-label={`${component.name} status`}
    >
      <div className="flex-1 text-sm font-medium text-gray-700 truncate">
        {component.name}
      </div>

      <div className="mx-4 text-sm text-gray-500">{component.version}</div>

      <div
        className={`ml-2 px-2 py-0.5 text-xs font-semibold rounded-full ${
          component.isUp ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
        }`}
        aria-live="polite"
      >
        {component.isUp ? 'UP' : 'Down'}
      </div>
    </div>
  )

const About = () => {
  const components: ComponentStatusProps[] = [
    { name: 'Backend', version: '0.0.0', isUp: false },
    { name: 'InventoryService', version: '0.0.0', isUp: false },
  ]

  useEffect(() => {
    BackendService.getServerInfo()
      .then((appInfo: ServerInfo) => {
        components[0].isUp = true
        components[0].version = appInfo.version
      })
      .catch(() => {
        components[0].isUp = false
      })
    InventoryService.getServerInfo()
      .then((status: ServerInfo) => {
        components[1].isUp = true
        components[1].version = status.version
      })
      .catch(() => {
        components[1].isUp = false
      })
  }, [])

  return (
    <>
      <PageTitle title="About" />
      <section className="mt-4">
        <h2 className="text-2xl font-semibold mb-2">Components</h2>
        <div className="mb-4 space-y-2">
            {components.map((component) => (
              <ComponentStatus key={component.name} component={component} />
            ))}
        </div>
      </section>
    </>
  )
}

export default About