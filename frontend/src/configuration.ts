export const BACKEND_BASE_URL = __BACKEND_BASE_URL__ || import.meta.env.VITE_BACKEND_BASE_URL || 'http://localhost:8080'

console.log('BACKEND_BASE_URL:', __BACKEND_BASE_URL__)
console.log('BACKEND_BASE_URL:', import.meta.env.VITE_BACKEND_BASE_URL)
console.log('BACKEND_BASE_URL:', BACKEND_BASE_URL)
console.log('BACKEND_BASE_URL:', process.env.BACKEND_BASE_URL)
