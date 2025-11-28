import { useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import type { AppDispatch } from '../store'
import { subscribe, type NotificationState } from '../store/notification.slice'

const NotificationBell = () => {
  const dispatch = useDispatch<AppDispatch>()
  const  { isOpen, notifications } = useSelector<NotificationState>((state) => state.notifications) as NotificationState

  useEffect(() => {
    dispatch(subscribe())
  }, [dispatch])

  return (
    <>
      <div>NotificationBell</div>
      { isOpen && (
        <div>
          <h3>Notifications</h3>
          <ul>
            {notifications.map((notification, index) => (
              <li key={index}>{JSON.stringify(notification)}</li>
            ))}
          </ul>
        </div>
      )}
    </>
  )
}

export default NotificationBell