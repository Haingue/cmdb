import { createBrowserRouter, type DataRouter } from 'react-router'

import Root from '../Root'
import InventoryService from '../pages/InventoryService'
import ItemFormPage from '../pages/InventoryService/item-form'
import ItemTypeExplorer from '../pages/InventoryService/item-type'
import ItemTypeForm from '../pages/InventoryService/item-type-form'
import ItemExplorer from '../pages/InventoryService/items'
import ItemDetailsPage from '../pages/InventoryService/items/item-details'
import LinkTypeExplorer from '../pages/InventoryService/link-type'
import LinkTypeForm from '../pages/InventoryService/link-type-form'
import NotFound from '../pages/NotFound'
import About from '../pages/about'
import BusinessServiceIndexPage from '../pages/business-service'
import ComponentIndexPage from '../pages/component'
import HostIndexPage from '../pages/component/host'
import SoftwareIndexPage from '../pages/component/software'
import EnvironmentIndexPage from '../pages/environment'
import EnvironmentDetaillsPage from '../pages/environment/EnvironmentDetaills'
import Dashboard from '../pages/home/Dashboard'
import MapPage from '../pages/map'
import ProjectIndexPage from '../pages/project'
import ProjectDetailsPage from '../pages/project/ProjectDetails'
import TrafficPage from '../pages/traffic'
import NotAvailable from '../pages/NotAvailable'
import LinkForm from '../pages/InventoryService/link-form'
import HostDetailsPage from '../pages/component/host/HostDetails'
import SoftwareDetailsPage from '../pages/component/software/SoftwareDetails'

const router: DataRouter = createBrowserRouter([
  {
    Component: Root,
    children: [
      { index: true, Component: Dashboard },
      { path: "/map", Component: MapPage },
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
          {
            path: "host",
            children: [
              { index: true, Component: HostIndexPage },
              { path: "details", Component: HostDetailsPage },
            ]
          },
          {
            path: "software",
            children: [
              { index: true, Component: SoftwareIndexPage },
              { path: "details", Component: SoftwareDetailsPage },
            ]
          }
        ]
      },
      
      { 
        path: "/standard",
        children: [
          { path: "host", Component: NotAvailable },
          { path: "development", Component: NotAvailable },
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
          { path: "link-form", Component: LinkForm },
        ]
      },
      { path: "/about", Component: About },
      { path: "*", Component: NotFound },
    ]
  },
])
export default router