import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

const THEME_KEY = 'ignai-theme'

export const useThemeStore = defineStore('theme', () => {
  const systemPrefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
  const theme = ref(localStorage.getItem(THEME_KEY) || (systemPrefersDark ? 'dark' : 'light'))

  function applyTheme() {
    document.documentElement.classList.remove('dark', 'light')
    document.documentElement.classList.add(theme.value)
    localStorage.setItem(THEME_KEY, theme.value)
  }

  function toggle() {
    theme.value = theme.value === 'dark' ? 'light' : 'dark'
    applyTheme()
  }

  // Apply on init
  applyTheme()

  // Listen for system theme changes
  window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
    if (!localStorage.getItem(THEME_KEY)) {
      theme.value = e.matches ? 'dark' : 'light'
      applyTheme()
    }
  })

  return { theme, toggle }
})
