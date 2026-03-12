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
            {/* <Routes>
              <Route path="/" Component={Dashboard} />
              <Route path="/map" Component={Map} />
              <Route path="/project" Component={ProjectIndexPage} />
              <Route path="/project-details" Component={ProjectDetailsPage} />
              <Route path="/business-service" Component={BusinessServiceIndexPage} />
              <Route path="/environment" Component={EnvironmentIndexPage} />
              <Route path="/environment-details" Component={EnvironmentDetaillsPage} />
              <Route path="/component" Component={ComponentIndexPage} />
              <Route path="/component/host" Component={HostIndexPage} />
              <Route path="/component/software" Component={SoftwareIndexPage} />
              
              <Route path="/standards/host" Component={ComponentIndexPage} />
              <Route path="/standards/development" Component={ComponentIndexPage} />

              <Route path="/traffic" Component={TrafficPage} />
              <Route path="/inventory-service" Component={InventoryService} />
              <Route path="/inventory-service/item-types" Component={ItemTypeExplorer} />
              <Route path="/inventory-service/item-type-form" Component={ItemTypeForm} />
              <Route path="/inventory-service/link-types" Component={LinkTypeExplorer} />
              <Route path="/inventory-service/link-type-form" Component={LinkTypeForm} />
              <Route path="/inventory-service/items" Component={ItemExplorer} />
              <Route path="/inventory-service/items/:itemUuid" Component={ItemDetailsPage} />
              <Route path="/inventory-service/item-form" Component={ItemFormPage} />
              <Route path="/about" Component={About} />

              <Route path="*" Component={NotFound} />
            </Routes> */}
          </main>
        </div>
      </div>
    </>
  )
}

export default Root
