import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// VITE_BASE: caminho de publicacao (ex.: /lab02-sistema-aluguel-de-carros/ no GitHub Pages)
export default defineConfig({
  base: process.env.VITE_BASE ?? '/',
  plugins: [react()],
})
