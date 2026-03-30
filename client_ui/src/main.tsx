import './configuration.ts'
import { createRoot } from 'react-dom/client'
import Root from './Root.tsx'
import { RouterProvider } from 'react-router'
import store from './store/index.ts'
import { Provider } from 'react-redux'
import router from './router/index.ts'


createRoot(document.getElementById('root')!).render(
  //<StrictMode>
    <Provider store={store}>
      <RouterProvider router={router}>
        <Root />
      </RouterProvider>
    </Provider>
  //</StrictMode>
)
