import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'


// https://vite.dev/config/
export default defineConfig({
  define: {
    'process.env': {
      NODE_ENV: process.env.NODE_ENV,
      BACKEND_BASE_URL: process.env.BACKEND_BASE_URL
    }
  },
  plugins: [react(), tailwindcss()],
  server: {
    allowedHosts: true,
  },
})
