import { configureStore } from '@reduxjs/toolkit'
import uxReducer from './ux.slice.ts'
import itemTypeReducer from './itemType.slice.ts'
import itemReducer from './item.slice.ts'
import notificationReducer from './notification.slice.ts'

const store = configureStore({
  reducer: {
    ux: uxReducer,
    notifications: notificationReducer,
    itemTypes: itemTypeReducer,
    items: itemReducer,
  },
})
export default store;
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;