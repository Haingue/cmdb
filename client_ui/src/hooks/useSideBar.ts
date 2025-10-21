import { type EventHandler, type MouseEvent } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import type { AppDispatch, RootState } from '../store'
import { collapseSidebar, expandSidebar, toggleSidebar, type UxStateType } from '../store/ux.slice'
import useWindowDimensions from './useWindowDimensions'

type useSideBarReturn = {
  sidebarCollapsed: boolean
  toggleSideBarVisibility: EventHandler<MouseEvent>
  collapseSidebar: EventHandler<MouseEvent>
  expandSidebar: EventHandler<MouseEvent>
}

export default function useSideBar(): useSideBarReturn {
  const dispatch = useDispatch<AppDispatch>()
  const { sidebarCollapsed } = useSelector<RootState>((state) => state.ux.sidebarCollapsed) as UxStateType
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
