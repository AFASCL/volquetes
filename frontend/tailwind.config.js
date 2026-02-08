/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        // Sidebar (Slate 900)
        sidebar: '#1E293B',
        // Primario — botones de acción (Blue 600)
        primary: '#2563EB',
        // Badges estados volquetes
        badgeObra: '#f59e0b',      // Amber 500 — En Obra
        badgeDisponible: '#22c55e', // Green 500 — Disponible
        badgeMantenimiento: '#ef4444', // Red 500 — Mantenimiento
      },
    },
  },
  plugins: [],
}
