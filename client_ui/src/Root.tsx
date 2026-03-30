import SideBar from './components/navigation/SideBar'
import NavBar from './components/navigation/NavBar'
import AlertSection from './components/alert/AlertSection'
import Breadcrumbs from './components/Breadcrumbs'
import { Outlet } from 'react-router'

const Root = () => {
  return (
    <>
      <header>
        <NavBar />
      </header>
      <div className="flex pt-16 overflow-hidden">
        <div className={`relative grow h-full overflow-y-auto transition duration-75 lg:ml-64 ml-4`}>
          <SideBar backdrop />
          <main className="p-4">
            <Breadcrumbs />
            <AlertSection />
            <Outlet />
          </main>
        </div>
      </div>
    </>
  )
}

export default Root
