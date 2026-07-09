import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { AuthApi } from '../api'

const TOKEN_KEY = 'ignai_access_token'
const USER_KEY = 'ignai_user'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem(TOKEN_KEY) || '')
  const user = ref(JSON.parse(localStorage.getItem(USER_KEY) || 'null'))

  const isLoggedIn = computed(() => !!token.value)

  function saveSession(accessToken, userData) {
    token.value = accessToken
    user.value = userData
    localStorage.setItem(TOKEN_KEY, accessToken)
    localStorage.setItem(USER_KEY, JSON.stringify(userData))
  }

  function clearSession() {
    token.value = ''
    user.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  async function login(data) {
    const res = await AuthApi.login(data)
    saveSession(res.accessToken, res.user)
    return res
  }

  async function register(data) {
    return await AuthApi.register(data)
  }

  function logout() {
    clearSession()
  }

  return { token, user, isLoggedIn, login, register, logout, saveSession, clearSession }
})
