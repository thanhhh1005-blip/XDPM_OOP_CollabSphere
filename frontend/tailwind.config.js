/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      // 👇 BẮT ĐẦU PHẦN BỔ SUNG
      keyframes: {
        // Định nghĩa hiệu ứng mờ dần hiện ra
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        // Định nghĩa hiệu ứng nảy nhẹ
        bounceShort: {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-5px)' },
        }
      },
      animation: {
        // Đăng ký tên class để sử dụng (VD: animate-fade-in)
        'fade-in': 'fadeIn 0.3s ease-out',
        'bounce-short': 'bounceShort 1s ease-in-out infinite',
      }
      // 👆 KẾT THÚC PHẦN BỔ SUNG
    },
  },
  plugins: [],
}