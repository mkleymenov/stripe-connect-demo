/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./public/index.html', './src/**/*.{js,tsx}'],
  theme: {
    extend: {
      colors: {
        'green-dark': '#2F7532',
        'green-light': '#DEF5E1',
        'green-primary': '#4CAF50',
        accent: '#FFD35A',
        'grey-primary': '#212121',
        'grey-secondary': '#757575',
      },
    },
  },
  plugins: [],
};
