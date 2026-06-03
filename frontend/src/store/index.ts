import { configureStore } from '@reduxjs/toolkit'
import alertReducer from './alert.slice.ts'
import uxReducer from './ux.slice.ts'
import itemTypeReducer from './itemType.slice.ts'
import linkTypeReducer from './linkType.slice.ts'
import itemReducer from './item.slice.ts'
import notificationReducer from './notification.slice.ts'

const store = configureStore({
  reducer: {
    alert: alertReducer,
    ux: uxReducer,
    notifications: notificationReducer,
    itemTypes: itemTypeReducer,
    linkTypes: linkTypeReducer,
    items: itemReducer,
  },
})
export default store;
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;