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
          50: '#FBF2EE',
          100: '#F6E1D8',
          200: '#EAC0AF',
          300: '#DC9A81',
          400: '#C56E4E',
          500: '#A9472D', // Aroma POS Primary Terracotta
          600: '#923B24',
          700: '#79301D',
          800: '#612617',
          900: '#4C1E12',
        },
        cream: {
          50: '#FAF7F2',  // Subtle inset surface
          100: '#F6F0E6', // Aroma POS Warm Cream canvas
          200: '#E8DFD1', // Hairline border & muted track
          300: '#D8CBB8', // Stronger divider
        },
        charcoal: {
          DEFAULT: '#292722', // Aroma POS Charcoal
          secondary: '#5C574F',
          muted: '#7B746A',
          light: '#A1998E',
        },
        semantic: {
          success: '#1F7A4C',
          'success-bg': '#E8F6EE',
          'success-border': '#BDE5CE',
          warning: '#B76E14',
          'warning-bg': '#FDF4E3',
          'warning-border': '#F2D6A2',
          error: '#C4382B',
          'error-bg': '#FCECEB',
          'error-border': '#F3BEB9',
          info: '#25638A',
          'info-bg': '#EAF3F9',
          'info-border': '#BBD8EA',
        },
      },
      fontFamily: {
        sans: ['"Plus Jakarta Sans"', 'Inter', 'system-ui', '-apple-system', 'sans-serif'],
        body: ['Inter', '"Plus Jakarta Sans"', 'system-ui', '-apple-system', 'sans-serif'],
      },
      boxShadow: {
        'card': '0 1px 2px rgba(41, 39, 34, 0.04), 0 4px 12px rgba(41, 39, 34, 0.04)',
        'card-hover': '0 4px 16px rgba(41, 39, 34, 0.08), 0 2px 6px rgba(169, 71, 45, 0.08)',
        'dock': '0 10px 30px -6px rgba(41, 39, 34, 0.24), 0 4px 10px -2px rgba(41, 39, 34, 0.12)',
        'sheet': '0 -8px 32px rgba(41, 39, 34, 0.16)',
      },
      borderRadius: {
        '2xl': '1rem',
        '3xl': '1.25rem',
      },
    },
  },
  plugins: [],
};
