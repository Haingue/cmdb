import React from 'react'
import { useDispatch, useSelector } from 'react-redux';
import type { AppDispatch, RootState } from '../../store';
import Alert from './Alert';
import { removeAlert } from '../../store/alert.slice';

const AlertSection = () => {
  const dispatch = useDispatch<AppDispatch>()
  const alerts = useSelector((state: RootState) => state.alert.alerts);
  return (
    <>
        { alerts && alerts.map((alert) => (
            <Alert
            key={alert.uuid}
            type={alert.type}
            message={alert.message}
            details={alert.details}
            onClose={() => { dispatch(removeAlert(alert.uuid)) }}
            />
        ))
        }
    </>
  )
}

export default AlertSection