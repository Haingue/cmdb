import { Route, Routes } from 'react-router'
import Dashboard from './pages/home/Dashboard'
import NotFound from './pages/NotFound'
import Map from './pages/map/Map'
import MapItems from './pages/map/MapItem'
import SideBar from './components/navigation/SideBar'
import NavBar from './components/navigation/NavBar'
import InventoryService from './pages/InventoryService'
import About from './pages/About'
import ItemTypeExplorer from './pages/InventoryService/item-type'
import ItemTypeForm from './pages/InventoryService/item-type-form'
import LinkTypeExplorer from './pages/InventoryService/link-type'
import LinkTypeForm from './pages/InventoryService/link-type-form'
import ItemExplorer from './pages/InventoryService/items'
import ItemFormPage from './pages/InventoryService/item-form'
import TrafficPage from './pages/traffic'

const Root = () => {
  return (
    <>
      <header>
        <NavBar />
      </header>
      <div className="flex pt-16 overflow-hidden">
        <div className={`relative grow h-full overflow-y-auto transition duration-75 lg:ml-64 ml-4`}>
          <SideBar backdrop />
          <main>
            <Routes>
              <Route path="/" Component={Dashboard} />
              <Route path="/map" Component={Map} />
              <Route path="/map-items" Component={MapItems} />
              <Route path="/traffic" Component={TrafficPage} />
              <Route path="/inventory-service" Component={InventoryService} />
              <Route path="/inventory-service/item-types" Component={ItemTypeExplorer} />
              <Route path="/inventory-service/item-type-form" Component={ItemTypeForm} />
              <Route path="/inventory-service/link-types" Component={LinkTypeExplorer} />
              <Route path="/inventory-service/link-type-form" Component={LinkTypeForm} />
              <Route path="/inventory-service/items" Component={ItemExplorer} />
              <Route path="/inventory-service/item-form" Component={ItemFormPage} />
              <Route path="/about" Component={About} />
              <Route path="*" Component={NotFound} />
            </Routes>
          </main>
        </div>
      </div>
    </>
  )
}

export default Root
