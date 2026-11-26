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
          50: '#F8F9FA',  // Clean Airy Neutral-White canvas
          100: '#F3F4F6', // Subtle inset / track
          200: '#E5E7EB', // Crisp hairline border
          300: '#D1D5DB', // Dashed divider
        },
        ink: {
          DEFAULT: '#1F2937', // Crisp Slate Primary Text (text only, no heavy blocks)
          soft: '#374151',
          secondary: '#4B5563',
          muted: '#6B7280',
          light: '#9CA3AF',
        },
        spice: {
          50: '#FFF7ED',
          100: '#FFEDD5',
          200: '#FED7AA',
          500: '#EA580C', // Clean Brand Orange
          600: '#C2410C',
          700: '#9A3412',
        },
        pandan: {
          50: '#F0FDF4',
          200: '#BBF7D0',
          500: '#16A34A',
          600: '#15803D',
          700: '#166534',
        },
        semantic: {
          success: '#15803D',
          'success-bg': '#F0FDF4',
          'success-border': '#BBF7D0',
          warning: '#B45309',
          'warning-bg': '#FFFBEB',
          'warning-border': '#FDE68A',
          error: '#DC2626',
          'error-bg': '#FEF2F2',
          'error-border': '#FECACA',
          info: '#1D4ED8',
          'info-bg': '#EFF6FF',
          'info-border': '#BFDBFE',
        },
      },
      fontFamily: {
        display: ['"DM Sans"', 'system-ui', '-apple-system', 'sans-serif'],
        sans: ['"DM Sans"', 'system-ui', '-apple-system', 'sans-serif'],
        mono: ['"JetBrains Mono"', 'ui-monospace', 'SFMono-Regular', 'monospace'],
      },
      boxShadow: {
        ticket: '0 6px 24px -10px rgba(15, 23, 42, 0.08)',
        dock: '0 12px 30px -8px rgba(234, 88, 12, 0.35)',
        'card-hover': '0 10px 24px -12px rgba(234, 88, 12, 0.16)',
      },
      borderRadius: {
        '2xl': '1rem',
        '3xl': '1.25rem',
      },
    },
  },
  plugins: [],
};
