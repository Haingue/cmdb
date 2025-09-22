import { configureStore } from '@reduxjs/toolkit'
import uxReducer from './ux.slice.ts'

export default configureStore({
  reducer: {
    ux: uxReducer,
  },
})
