import { configureStore } from '@reduxjs/toolkit'
import uxReducer from './ux.slice.ts'
import itemTypeReducer from './itemType.slice.ts'

export default configureStore({
  reducer: {
    ux: uxReducer,
    itemTypes: itemTypeReducer,
  },
})
