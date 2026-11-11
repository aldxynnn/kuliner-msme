/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#FFF6F2',
          100: '#FFEADF',
          200: '#FFCBB2',
          300: '#FFA07A',
          400: '#F77245',
          500: '#D9531E', // Terracotta primary
          600: '#C84B19',
          700: '#A6380F',
          800: '#852D0E',
          900: '#6C270E',
        },
        cream: {
          50: '#FAF6F0',
          100: '#F4EFE6',
          200: '#E8E1D5',
        },
        charcoal: {
          DEFAULT: '#1A1A1A',
          muted: '#666666',
          light: '#999999',
        }
      },
      fontFamily: {
        sans: ['Plus Jakarta Sans', 'Inter', 'sans-serif'],
      }
    },
  },
  plugins: [],
}
