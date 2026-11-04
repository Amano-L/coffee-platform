import { fileURLToPath, URL } from 'node:url'

import vue from '@vitejs/plugin-vue'
import { defineConfig } from 'vite'

// 本地可用 VITE_PROXY_TARGET 指向其他端口（例如 8081）的后端
const proxyTarget = process.env.VITE_PROXY_TARGET || 'http://localhost:8080'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    host: '0.0.0.0',
    port: 5173,
    proxy: {
      // 开发环境将 /api 代理到后端，避免跨域
      '/api': {
        target: proxyTarget,
        changeOrigin: true,
      },
    },
  },
})
