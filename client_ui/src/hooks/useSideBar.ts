import { type EventHandler, type MouseEvent } from 'react'
import useWindowDimensions from './useWindowDimensions'
import { useDispatch, useSelector } from 'react-redux'
import { collapseSidebar, expandSidebar, toggleSidebar } from '../store/ux.slice'

type useSideBarReturn = {
  sidebarCollapsed: boolean
  toggleSideBarVisibility: EventHandler<MouseEvent>
  collapseSidebar: EventHandler<MouseEvent>
  expandSidebar: EventHandler<MouseEvent>
}

export default function useSideBar(): useSideBarReturn {
  const dispatch = useDispatch()
  const sidebarCollapsed = useSelector((state) => state.ux.sidebarCollapsed)
  const { width } = useWindowDimensions()

  const toggleSideBarVisibility: EventHandler<MouseEvent> = () => {
    if (width < 1024) {
      dispatch(toggleSidebar())
    }
  }

  return {
    sidebarCollapsed,
    toggleSideBarVisibility,
    collapseSidebar: () => dispatch(collapseSidebar()),
    expandSidebar: () => dispatch(expandSidebar()),
  }
}
