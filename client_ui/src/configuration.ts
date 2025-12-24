export const BACKEND_BASE_URL = process.env.BACKEND_BASE_URL || import.meta.env.VITE_BACKEND_BASE_URL || 'http://localhost:8090'

console.log('BACKEND_BASE_URL:', BACKEND_BASE_URL);
