/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        paper: {
          50: '#FBF9F5',  // Warm Alabaster Paper canvas
          100: '#F3EFE8', // Warm Stone tint
          200: '#E6DFD5', // Warm Sand hairline border
          300: '#D5CBBF', // Dashed ticket divider
        },
        ink: {
          DEFAULT: '#171311', // Deep Roast Ink
          soft: '#26201D',
          secondary: '#4E4640',
          muted: '#7C726B',
          light: '#A0968E',
        },
        spice: {
          50: '#FDF1EC',
          100: '#FADFD4',
          200: '#F2C9B8',
          500: '#C84319', // Burnt Lengkuas Vermilion
          600: '#A93612',
          700: '#8C2A0C',
        },
        pandan: {
          50: '#EAF5F0',
          200: '#B9DEC9',
          500: '#1D6B4F',
          600: '#14533C', // Pandan Leaf Deep Green
          700: '#0F402E',
        },
        semantic: {
          success: '#14533C',
          'success-bg': '#EAF5F0',
          'success-border': '#B9DEC9',
          warning: '#B4690E',
          'warning-bg': '#FEF5E7',
          'warning-border': '#F2D49B',
          error: '#B92D26',
          'error-bg': '#FDEEEC',
          'error-border': '#F3C1BE',
          info: '#1D5A7E',
          'info-bg': '#ECF5FA',
          'info-border': '#BCD9EA',
        },
      },
      fontFamily: {
        display: ['"Playfair Display"', 'Georgia', 'serif'],
        sans: ['"DM Sans"', 'system-ui', '-apple-system', 'sans-serif'],
        mono: ['"JetBrains Mono"', 'ui-monospace', 'SFMono-Regular', 'monospace'],
      },
      boxShadow: {
        ticket: '0 8px 30px -12px rgba(23, 19, 17, 0.08)',
        dock: '0 14px 36px -8px rgba(23, 19, 17, 0.34)',
        'card-hover': '0 12px 28px -14px rgba(23, 19, 17, 0.14)',
      },
      borderRadius: {
        '2xl': '1rem',
        '3xl': '1.25rem',
      },
    },
  },
  plugins: [],
};
