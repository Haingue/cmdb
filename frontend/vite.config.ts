import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

console.log(JSON.stringify(process.env.BACKEND_BASE_URL, null, 2))

export default defineConfig({
  define: {
    __BACKEND_BASE_URL__: process.env.BACKEND_BASE_URL,
  },
  plugins: [react(), tailwindcss()],
  server: {
    allowedHosts: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
