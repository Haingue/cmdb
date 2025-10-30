import { createSlice } from '@reduxjs/toolkit'

export type UxStateType = {
  sidebarCollapsed: boolean,
}
const initialState: UxStateType = {
  sidebarCollapsed: false,
}
export const uxSlice = createSlice({
  name: 'ux',
  initialState,
  reducers: {
    toggleSidebar: (state) => {
      console.debug('Toggle sidebar')
      state.sidebarCollapsed = !state.sidebarCollapsed
    },
    collapseSidebar: (state) => {
      console.debug('Collapse sidebar')
      state.sidebarCollapsed = true
    },
    expandSidebar: (state) => {
      console.debug('Expand sidebar')
      state.sidebarCollapsed = false
    },
  },
})

export const { toggleSidebar, collapseSidebar, expandSidebar } = uxSlice.actions

export default uxSlice.reducer
