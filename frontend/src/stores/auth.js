import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, register as registerApi, getCurrentUser } from '../api/authApi'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  async function login(username, password) {
    const data = await loginApi({ username, password })
    token.value = data.token
    user.value = data.user
    localStorage.setItem('token', data.token)
    localStorage.setItem('user', JSON.stringify(data.user))
    return data.user
  }

  async function register(payload) {
    return await registerApi(payload)
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  function isLoggedIn() {
    return !!token.value
  }

  function isAdmin() {
    return user.value?.role === 'ADMIN'
  }

  async function refreshCurrentUser() {
    const data = await getCurrentUser()
    user.value = data
    localStorage.setItem('user', JSON.stringify(data))
    return data
  }

  return { token, user, login, register, logout, isLoggedIn, isAdmin, refreshCurrentUser }
})
