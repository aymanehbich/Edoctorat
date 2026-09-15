import path from 'node:path'
import tailwindcss from '@tailwindcss/vite'
import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],
  resolve: {
    alias: {
      '@': path.resolve(import.meta.dirname, './src'),
    },
  },
  server: {
    proxy: {
      // The backend's CORS allowlist only contains http://localhost:4200 and
      // rejects any other Origin outright (Spring's CorsFilter treats a
      // present-but-mismatched Origin header as an invalid CORS request,
      // even though the browser considers this same-origin traffic to Vite).
      // Rewriting the proxied request's Origin to match the backend's own
      // origin makes Spring's CorsUtils.isCorsRequest() see it as same-origin
      // and skip CORS evaluation entirely — no backend change needed for dev.
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        configure: (proxy) => {
          proxy.on('proxyReq', (proxyReq) => {
            proxyReq.setHeader('origin', 'http://localhost:8080')
          })
        },
      },
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
