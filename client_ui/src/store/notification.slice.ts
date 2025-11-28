import { createSlice } from '@reduxjs/toolkit'
import { subscribeToNotifications } from '../service/inventory/InventorySync'
import type { ServerSentEventNotificationDto } from '../service/inventory/types'

export type NotificationState = {
  stream?: EventSource
  notifications: Array<ServerSentEventNotificationDto>,
  isOpen: boolean,
  error?: string,
  lastFetched?: number,
}
const initialState: NotificationState = {
  notifications: [],
  isOpen: false,
}
export const notification = createSlice({
  name: 'notification',
  initialState,
  reducers: {
    subscribe: (state) => {
      if (!state.isOpen) {
        try {
          console.log('Subscribing to notifications...')
          const eventSource = subscribeToNotifications(notification.actions.notifyionReceived)
          state.isOpen = true
          eventSource.addEventListener('message', (notification: ServerSentEventNotificationDto) => {
            console.debug('Received notification:', notification)
            state.notifications.push(notification)
            state.lastFetched = Date.now()
            state.isOpen = true
          })
          /*
          subscribeToNotifications((notification: ServerSentEventNotificationDto) => {
            console.debug('Received notification:', notification)
            state.notifications.push(notification)
            state.lastFetched = Date.now()
            state.isOpen = true
          })*/
        } catch (error) {
          console.error('An unknown error occurred while subscribing to notifications', error)
            state.isOpen = false
            state.error = (error as Error).message
        }
      }
      return state
    },
    notifyionReceived: (state, action) => {
      state.notifications.push(action.payload)
      state.lastFetched = Date.now()
      state.isOpen = true
    },
    getNotifications: (state) => {
      return state
    }
  }
})

export const { getNotifications, subscribe } = notification.actions

export default notification.reducer
