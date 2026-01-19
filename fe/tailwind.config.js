/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: ["class"],
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
    "./public/index.html",
  ],
  prefix: "",
  theme: {
    container: {
      center: true,
      padding: "2rem",
      screens: {
        sm: "640px",
        md: "768px",
        lg: "1024px",
        xl: "1280px",
        "2xl": "1536px",
      },
    },
    screens: {
      'sm': '640px',
      'md': '768px',
      'lg': '1024px',
      'xl': '1280px',
      '2xl': '1536px',
    },
    extend: {
      fontFamily: {
        sans: [
          "Pretendard",
          "-apple-system",
          "BlinkMacSystemFont",
          "Apple SD Gothic Neo",
          "Roboto",
          "Noto Sans KR",
          "Segoe UI",
          "Malgun Gothic",
          "sans-serif",
        ],
      },
      spacing: {
        // 8px grid system (CSS 변수 참조)
        "1": "var(--spacing-1)",
        "2": "var(--spacing-2)",
        "3": "var(--spacing-3)",
        "4": "var(--spacing-4)",
        "5": "var(--spacing-5)",
        "6": "var(--spacing-6)",
        "7": "var(--spacing-7)",
        "8": "var(--spacing-8)",
        "9": "var(--spacing-9)",
        "10": "var(--spacing-10)",
        "11": "var(--spacing-11)",
        "12": "var(--spacing-12)",
      },
      fontSize: {
        // Extra small sizes for compact UI
        "2xs": ["var(--font-size-2xs)", { lineHeight: "var(--line-height-2xs)", fontWeight: "var(--font-weight-regular)" }],
        "xs-sm": ["var(--font-size-xs-sm)", { lineHeight: "var(--line-height-xs-sm)", fontWeight: "var(--font-weight-regular)" }],
        "xs-md": ["var(--font-size-xs-md)", { lineHeight: "var(--line-height-xs-md)", fontWeight: "var(--font-weight-regular)" }],
        // Mobile-optimized typography scale
        caption: ["var(--font-size-xs)", { lineHeight: "var(--line-height-caption)", fontWeight: "var(--font-weight-regular)" }],
        body2: ["var(--font-size-sm)", { lineHeight: "var(--line-height-body2)", fontWeight: "var(--font-weight-regular)" }],
        body1: ["var(--font-size-base)", { lineHeight: "var(--line-height-body1)", fontWeight: "var(--font-weight-regular)" }],
        subtitle: ["var(--font-size-lg)", { lineHeight: "var(--line-height-subtitle)", fontWeight: "var(--font-weight-medium)" }],
        h6: ["var(--font-size-xl)", { lineHeight: "var(--line-height-h6)", fontWeight: "var(--font-weight-semibold)" }],
        h5: ["var(--font-size-h5)", { lineHeight: "var(--line-height-h5)", fontWeight: "var(--font-weight-semibold)" }],
        h4: ["var(--font-size-2xl)", { lineHeight: "var(--line-height-h4)", fontWeight: "var(--font-weight-semibold)" }],
        h3: ["var(--font-size-h3)", { lineHeight: "var(--line-height-h3)", fontWeight: "var(--font-weight-bold)" }],
        h2: ["var(--font-size-h2)", { lineHeight: "var(--line-height-h2)", fontWeight: "var(--font-weight-bold)" }],
        h1: ["var(--font-size-3xl)", { lineHeight: "var(--line-height-h1)", fontWeight: "var(--font-weight-bold)" }],
      },
      colors: {
        // Family Tree Brand Colors
        primary: {
          DEFAULT: "hsl(var(--primary))",
          foreground: "hsl(var(--primary-foreground))",
          50: "#fff7ed",
          100: "#ffedd5",
          200: "#fed7aa",
          300: "#fdba74",
          400: "#fb923c",
          500: "#f97316", // Main brand color (Orange-500)
          600: "#ea580c",
          700: "#c2410c",
          800: "#9a3412",
          900: "#7c2d12",
          950: "#431407",
        },
        secondary: {
          DEFAULT: "hsl(var(--secondary))",
          foreground: "hsl(var(--secondary-foreground))",
          50: "#fffbeb",
          100: "#fef3c7",
          200: "#fde68a",
          300: "#fcd34d",
          400: "#fbbf24",
          500: "#f59e0b", // Secondary brand color (Amber-500)
          600: "#d97706",
          700: "#b45309",
          800: "#92400e",
          900: "#78350f",
          950: "#451a03",
        },
        // Semantic colors
        success: {
          50: "#f0fdf4",
          500: "#22c55e",
          600: "#16a34a",
        },
        warning: {
          50: "#fffbeb",
          500: "#f59e0b",
          600: "#d97706",
        },
        error: {
          50: "#fef2f2",
          500: "#ef4444",
          600: "#dc2626",
        },
        info: {
          50: "#eff6ff",
          500: "#3b82f6",
          600: "#2563eb",
        },
        // External Brand Colors
        kakao: {
          DEFAULT: "hsl(var(--kakao))",
          hover: "hsl(var(--kakao-hover))",
          text: "hsl(var(--kakao-text))",
        },
        // Updated neutral grays for better readability
        border: "hsl(var(--border))",
        input: "hsl(var(--input))",
        ring: "hsl(var(--ring))",
        background: "hsl(var(--background))",
        foreground: "hsl(var(--foreground))",
        muted: {
          DEFAULT: "hsl(var(--muted))",
          foreground: "hsl(var(--muted-foreground))",
        },
        accent: {
          DEFAULT: "hsl(var(--accent))",
          foreground: "hsl(var(--accent-foreground))",
        },
        destructive: {
          DEFAULT: "hsl(var(--destructive))",
          foreground: "hsl(var(--destructive-foreground))",
        },
        popover: {
          DEFAULT: "hsl(var(--popover))",
          foreground: "hsl(var(--popover-foreground))",
        },
        card: {
          DEFAULT: "hsl(var(--card))",
          foreground: "hsl(var(--card-foreground))",
        },
        sidebar: {
          DEFAULT: "hsl(var(--sidebar-background))",
          foreground: "hsl(var(--sidebar-foreground))",
          primary: "hsl(var(--sidebar-primary))",
          "primary-foreground": "hsl(var(--sidebar-primary-foreground))",
          accent: "hsl(var(--sidebar-accent))",
          "accent-foreground": "hsl(var(--sidebar-accent-foreground))",
          border: "hsl(var(--sidebar-border))",
          ring: "hsl(var(--sidebar-ring))",
        },
      },
      borderRadius: {
        lg: "var(--radius)",
        md: "calc(var(--radius) - 2px)",
        sm: "calc(var(--radius) - 4px)",
      },
      boxShadow: {
        // Decorative shadows for Family Tree
        "family-sm": "0 1px 2px 0 rgba(249, 115, 22, 0.05)",
        family:
          "0 4px 6px -1px rgba(249, 115, 22, 0.1), 0 2px 4px -1px rgba(249, 115, 22, 0.06)",
        "family-md":
          "0 10px 15px -3px rgba(249, 115, 22, 0.1), 0 4px 6px -2px rgba(249, 115, 22, 0.05)",
        "family-lg":
          "0 20px 25px -5px rgba(249, 115, 22, 0.1), 0 10px 10px -5px rgba(249, 115, 22, 0.04)",
        "family-xl": "0 25px 50px -12px rgba(249, 115, 22, 0.25)",
      },
      backgroundImage: {
        // Gradient backgrounds
        "family-gradient": "linear-gradient(135deg, #f97316 0%, #f59e0b 100%)",
        "family-gradient-soft":
          "linear-gradient(135deg, #fff7ed 0%, #fffbeb 100%)",
      },
      keyframes: {
        "accordion-down": {
          from: {
            height: "0",
          },
          to: {
            height: "var(--radix-accordion-content-height)",
          },
        },
        "accordion-up": {
          from: {
            height: "var(--radix-accordion-content-height)",
          },
          to: {
            height: "0",
          },
        },
        "family-bounce": {
          "0%, 100%": {
            transform: "translateY(0)",
            animationTimingFunction: "cubic-bezier(0.8, 0, 1, 1)",
          },
          "50%": {
            transform: "translateY(-5px)",
            animationTimingFunction: "cubic-bezier(0, 0, 0.2, 1)",
          },
        },
      },
      animation: {
        "accordion-down": "accordion-down 0.2s ease-out",
        "accordion-up": "accordion-up 0.2s ease-out",
        "family-bounce": "family-bounce 1s infinite",
      },
      zIndex: {
        base: "var(--z-base)",
        dropdown: "var(--z-dropdown)",
        sticky: "var(--z-sticky)",
        fixed: "var(--z-fixed)",
        "modal-backdrop": "var(--z-modal-backdrop)",
        modal: "var(--z-modal)",
        popover: "var(--z-popover)",
        tooltip: "var(--z-tooltip)",
        notification: "var(--z-notification)",
      },
      transitionDuration: {
        fast: "var(--duration-fast)",
        normal: "var(--duration-normal)",
        slow: "var(--duration-slow)",
      },
      transitionTimingFunction: {
        "ease-in": "var(--ease-in)",
        "ease-out": "var(--ease-out)",
        "ease-in-out": "var(--ease-in-out)",
      },
    },
  },
  plugins: [require("tailwindcss-animate"), require("@tailwindcss/typography")],
};