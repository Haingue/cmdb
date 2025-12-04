import './configuration.ts'
import { createRoot } from 'react-dom/client'
import Root from './Root.tsx'
import { BrowserRouter } from 'react-router'
import store from './store/index.ts'
import { Provider } from 'react-redux'


createRoot(document.getElementById('root')!).render(
  //<StrictMode>
    <Provider store={store}>
      <BrowserRouter>
        <Root />
      </BrowserRouter>
    </Provider>
  //</StrictMode>
)
