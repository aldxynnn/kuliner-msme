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
          50: '#FFF3EC',
          100: '#FDE4D6',
          200: '#F2D5C6',
          300: '#E8B297',
          400: '#DE8257',
          500: '#C94F1D', // Aroma Primary Terracotta (preserved from main)
          600: '#B04115',
          700: '#983610',
          800: '#7C2B0D',
          900: '#62220A',
        },
        cream: {
          50: '#FAF8F5',  // Warm Ivory canvas
          100: '#F4EFE8', // Soft linen surface
          200: '#E8E0D6', // Hairline border & divider
          300: '#DBD0C3', // Stronger divider
        },
        charcoal: {
          DEFAULT: '#2B211D', // Warm Espresso Charcoal (preserved from main)
          secondary: '#635852',
          muted: '#8C8078',
          light: '#ABA098',
        },
        semantic: {
          success: '#1F7A52',
          'success-bg': '#EAF6F0',
          'success-border': '#C3E6D5',
          warning: '#B87314',
          'warning-bg': '#FEF6E6',
          'warning-border': '#F3DBA6',
          error: '#C43A2F',
          'error-bg': '#FDEEEC',
          'error-border': '#F4C4BF',
          info: '#25638A',
          'info-bg': '#EDF5FA',
          'info-border': '#C5DDEC',
        },
      },
      fontFamily: {
        display: ['"Fraunces"', 'Georgia', 'serif'],
        sans: ['"Plus Jakarta Sans"', 'Inter', 'system-ui', '-apple-system', 'sans-serif'],
        body: ['Inter', '"Plus Jakarta Sans"', 'system-ui', '-apple-system', 'sans-serif'],
      },
      boxShadow: {
        'card': '0 1px 2px rgba(43, 33, 29, 0.04), 0 4px 12px rgba(43, 33, 29, 0.03)',
        'card-hover': '0 6px 20px rgba(43, 33, 29, 0.08), 0 2px 6px rgba(201, 79, 29, 0.08)',
        'dock': '0 10px 28px -6px rgba(201, 79, 29, 0.35), 0 4px 12px -2px rgba(43, 33, 29, 0.12)',
        'sheet': '0 -8px 28px rgba(43, 33, 29, 0.12)',
      },
      borderRadius: {
        '2xl': '1rem',
        '3xl': '1.25rem',
      },
    },
  },
  plugins: [],
};
