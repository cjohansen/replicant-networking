/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.clj", "./src/**/*.cljc", "./src/**/*.cljs", "./portfolio/**/*.cljs"],
  plugins: [
    require("daisyui")
  ],
  daisyui: {
    themes: [
      "cupcake"
    ]
  }
}
