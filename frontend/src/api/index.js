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
}

export const RootApi = {
  setupStatus: () => request('/root/setup-status'),
  bootstrap: (data) => request('/root/bootstrap', { method: 'POST', body: data }),
  login: (data) => request('/root/login', { method: 'POST', body: data }),
  me: () => request('/root/me'),
}

export const EventApi = {
  current: () => request('/public/events/current'),
  detail: (eventId) => request(`/public/events/${eventId}`),
  tracks: (eventId) => request(`/public/events/${eventId}/tracks`),
  formFields: (eventId, target = 'registration') => request(`/public/events/${eventId}/form-fields?target=${target}`),
  list: (page = 1, pageSize = 6) => request(`/public/events?page=${page}&pageSize=${pageSize}`),
}

export const RegistrationApi = {
  submit: (data) => request('/registrations', { method: 'POST', body: data }),
  mine: () => request('/registrations/my'),
}

export const PublicApi = {
  projects: (params) => request(`/public/projects?${new URLSearchParams(params)}`),
  projectDetail: (id) => request(`/public/projects/${id}`),
  vote: (projectId) => request(`/public/projects/${projectId}/votes`, { method: 'POST' }),
  rate: (projectId, data) => request(`/public/projects/${projectId}/ratings`, { method: 'POST', body: data }),
}

export const ProjectApi = {
  mine: () => request('/projects/my'),
  create: (data) => request('/projects', { method: 'POST', body: data }),
  update: (id, data) => request(`/projects/${id}`, { method: 'PUT', body: data }),
  submit: (id) => request(`/projects/${id}/submit`, { method: 'POST' }),
  files: (id) => request(`/projects/${id}/files`),
  uploadFile: (id, file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request(`/projects/${id}/files`, { method: 'POST', body: formData })
  },
}

export const AdminApi = {
  dashboard: () => request('/admin/dashboard'),
  users: (params) => request(`/admin/users?${new URLSearchParams(params)}`),
  createAdmin: (data) => request('/admin/users/admins', { method: 'POST', body: data }),
  updateUserStatus: (userId, status) => request(`/admin/users/${userId}/status`, { method: 'PATCH', body: { status } }),
  events: () => request('/admin/events'),
  createEvent: (data) => request('/admin/events', { method: 'POST', body: data }),
  updateEvent: (id, data) => request(`/admin/events/${id}`, { method: 'PUT', body: data }),
  formFields: (eventId, target = 'registration') => request(`/admin/events/${eventId}/form-fields?target=${target}`),
  createFormField: (eventId, data) => request(`/admin/events/${eventId}/form-fields`, { method: 'POST', body: data }),
  updateFormField: (eventId, fieldId, data) => request(`/admin/events/${eventId}/form-fields/${fieldId}`, { method: 'PUT', body: data }),
  registrations: (params) => request(`/admin/registrations?${new URLSearchParams(params)}`),
  registrationDetail: (id) => request(`/admin/registrations/${id}`),
  reviewRegistration: (id, data) => request(`/admin/registrations/${id}/review`, { method: 'PATCH', body: data }),
  batchReview: (data) => request('/admin/registrations/batch-review', { method: 'POST', body: data }),
  tracks: (eventId) => request(`/admin/events/${eventId}/tracks`),
  createTrack: (eventId, data) => request(`/admin/events/${eventId}/tracks`, { method: 'POST', body: data }),
  projects: (params) => request(`/admin/projects?${new URLSearchParams(params)}`),
  projectDetail: (id) => request(`/admin/projects/${id}`),
  updateProjectStatus: (id, status) => request(`/admin/projects/${id}/status`, { method: 'PATCH', body: { status } }),
}
