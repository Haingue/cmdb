import './configuration.ts'
import { createRoot } from 'react-dom/client'
import Root from './Root.tsx'
import { BrowserRouter, createBrowserRouter, RouterProvider, type BrowserRouterProps } from 'react-router'
import store from './store/index.ts'
import { Provider } from 'react-redux'

import Dashboard from './pages/home/Dashboard'
import NotFound from './pages/NotFound'
import Map from './pages/map/Map'
import InventoryService from './pages/InventoryService'
import About from './pages/about'
import ItemTypeExplorer from './pages/InventoryService/item-type'
import ItemTypeForm from './pages/InventoryService/item-type-form'
import LinkTypeExplorer from './pages/InventoryService/link-type'
import LinkTypeForm from './pages/InventoryService/link-type-form'
import ItemExplorer from './pages/InventoryService/items'
import ItemFormPage from './pages/InventoryService/item-form'
import TrafficPage from './pages/traffic'
import ItemDetailsPage from './pages/InventoryService/items/item-details'
import ProjectIndexPage from './pages/project'
import BusinessServiceIndexPage from './pages/business-service'
import EnvironmentIndexPage from './pages/environment'
import ComponentIndexPage from './pages/component'
import HostIndexPage from './pages/component/host'
import SoftwareIndexPage from './pages/component/software'
import ProjectDetailsPage from './pages/project/ProjectDetails'
import EnvironmentDetaillsPage from './pages/environment/EnvironmentDetaills'

const router = createBrowserRouter([
  {
    Component: Root,
    children: [
      { index: true, Component: Dashboard },
      { path: "/map", Component: Map },
      {
        path: "/project",
        children: [
          { index: true, Component: ProjectIndexPage },
          { path: "details", Component: ProjectDetailsPage },
        ]
      },
      { path: "/business-service", Component: BusinessServiceIndexPage },
      {
        path: "/environment",
        children: [
          { index: true, Component: EnvironmentIndexPage },
          { path: "details", Component: EnvironmentDetaillsPage },
        ]
      },
      {
        path: "/component",
        children: [
          { index: true, Component: ComponentIndexPage },
          { path: "host", Component: HostIndexPage },
          { path: "software", Component: SoftwareIndexPage },
        ]
      },
      
      { 
        path: "/standards",
        children: [
          { path: "host", Component: ComponentIndexPage },
          { path: "development", Component: ComponentIndexPage },
        ]
      },
    
      {
        path: "/traffic",
        children: [
          { index: true, Component: TrafficPage },
        ]
      },

      {
        path: "/inventory-service",
        children: [
          { index: true, Component: InventoryService },
          { path: "item-types", Component: ItemTypeExplorer },
          { path: "item-type-form", Component: ItemTypeForm },
          { path: "link-types", Component: LinkTypeExplorer },
          { path: "link-type-form", Component: LinkTypeForm },
          { path: "items", Component: ItemExplorer },
          { path: "items/:itemUuid", Component: ItemDetailsPage },
          { path: "item-form", Component: ItemFormPage },
        ]
      },
      { path: "/about", Component: About },
      { path: "*", Component: NotFound },
    ]
  },
])

createRoot(document.getElementById('root')!).render(
  //<StrictMode>
    <Provider store={store}>
      <RouterProvider router={router}>
        <Root />
      </RouterProvider>
    </Provider>
  //</StrictMode>
)
