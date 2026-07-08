import axios from 'axios'

const TOKEN_KEY = 'ignai_access_token'
const USER_KEY = 'ignai_user'

const apiClient = axios.create({
  baseURL: '/api',
  headers: { Accept: 'application/json' },
})

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  if (config.data && !(config.data instanceof FormData)) {
    config.headers['Content-Type'] = 'application/json'
  }
  return config
})

async function request(path, options = {}) {
  let response
  try {
    response = await apiClient({
      url: path,
      method: options.method || 'GET',
      data: options.body,
      ...options,
      body: undefined,
      headers: undefined,
    })
  } catch (error) {
    if (error.response) {
      if (error.response.status === 401) {
        localStorage.removeItem(TOKEN_KEY)
        localStorage.removeItem(USER_KEY)
        throw new Error('登录已失效，请重新登录。')
      }
      const payload = error.response.data
      throw new Error(payload.message || `请求失败：${error.response.status}`)
    }
    throw new Error('无法连接后端服务，请确认 Spring Boot 已启动。')
  }

  const payload = response.data
  if (payload.success === false) {
    throw new Error(payload.message || '请求失败')
  }
  return payload.data
}

export const AuthApi = {
  register: (data) => request('/auth/register', { method: 'POST', body: data }),
  login: (data) => request('/auth/login', { method: 'POST', body: data }),
  me: () => request('/auth/me'),
  logout: () => request('/auth/logout', { method: 'POST' }),
}

export const EventApi = {
  current: () => request('/public/events/current'),
  tracks: (eventId) => request(`/public/events/${eventId}/tracks`),
  list: (page = 1, pageSize = 6) => request(`/public/events?page=${page}&pageSize=${pageSize}`),
}

export const RegistrationApi = {
  submit: (data) => request('/registrations', { method: 'POST', body: data }),
  mine: () => request('/registrations/my'),
}

export const PublicApi = {
  projects: (params) => request(`/public/projects?${new URLSearchParams(params)}`),
}
