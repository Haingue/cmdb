import { createSlice } from '@reduxjs/toolkit';

export type Alert = {
    uuid: string,
    type: 'info' | 'warning' | 'error' | 'success',
    message: string,
    details?: object,
}

export type AlertState = {
  alerts: Alert[],
};

const initialState: AlertState = {
  alerts: [],
};

const alertSlice = createSlice({
  name: 'alert',
  initialState,
  reducers: {
    addAlert: (state, action) => {
      state.alerts.push(action.payload);
      console.log("Alert added:", action.payload);
    },
    removeAlert: (state, action) => {
      state.alerts = state.alerts.filter(alert => alert.uuid !== action.payload);
    },
  },
});

export const { addAlert, removeAlert } = alertSlice.actions;
export default alertSlice.reducer;
