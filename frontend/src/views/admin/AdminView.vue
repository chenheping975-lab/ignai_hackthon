<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { AdminApi, RootApi } from '../../api'
import { useAuthStore } from '../../stores/auth'

const auth = useAuthStore()
const activeView = ref('dashboard')
const adminReady = ref(false)
const rootInitialized = ref(true)
const authMode = computed(() => rootInitialized.value ? 'login' : 'bootstrap')
const authForm = reactive({ account: '', ownerEmail: '', name: '', password: '' })

const views = [
  { key: 'dashboard', label: '概览', icon: '📊' },
  { key: 'events', label: '活动管理', icon: '🎯' },
  { key: 'registrations', label: '报名管理', icon: '📋' },
  { key: 'projects', label: '项目看板', icon: '📁' },
  { key: 'mail', label: '邮件管理', icon: '✉️' },
]

const loading = ref(false)
const message = ref('')

async function checkRootStatus() {
  try {
    const status = await RootApi.setupStatus()
    rootInitialized.value = status?.initialized !== false
    if (auth.token && rootInitialized.value) {
      try {
        await RootApi.me()
        adminReady.value = true
        await loadAdminData()
      } catch {
        auth.clearSession()
      }
    }
  } catch (e) {
    message.value = e.message
  }
}

async function handleRootAuth() {
  message.value = ''
  loading.value = true
  try {
    let res
    if (authMode.value === 'bootstrap') {
      res = await RootApi.bootstrap({
        ownerEmail: authForm.ownerEmail,
        name: authForm.name,
        password: authForm.password,
      })
      message.value = 'root 初始化成功，请登录后台'
      rootInitialized.value = true
      authForm.account = authForm.ownerEmail
      authForm.password = ''
      return
    }
    res = await RootApi.login({ account: authForm.account, password: authForm.password })
    auth.saveSession(res.accessToken, res.user)
    adminReady.value = true
    await loadAdminData()
  } catch (e) {
    message.value = e.message
  } finally {
    loading.value = false
  }
}

// ========== Dashboard ==========
const metrics = reactive({ totalUsers: 0, totalRegistrations: 0, pendingRegistrations: 0, approvedRegistrations: 0, totalProjects: 0 })

async function loadDashboard() {
  try { Object.assign(metrics, await AdminApi.dashboard()) } catch (e) { message.value = e.message }
}

// ========== Events ==========
const events = ref([])
const eventForm = reactive({
  title: '',
  subtitle: '',
  location: '',
  description: '',
  status: 'published',
  registrationOpen: true,
  registrationDeadline: '',
  submissionDeadline: '',
})
const editingEventId = ref(null)
const tracks = ref([])
const newTrackName = ref('')
const formFields = ref([])
const newField = reactive({
  fieldKey: '',
  label: '',
  fieldType: 'text',
  required: false,
  placeholder: '',
  optionsText: '',
})

async function loadEvents() {
  try { events.value = await AdminApi.events() } catch (e) { message.value = e.message }
}

function openCreateEvent() {
  editingEventId.value = 0
  eventForm.title = ''
  eventForm.subtitle = ''
  eventForm.location = ''
  eventForm.description = ''
  eventForm.status = 'published'
  eventForm.registrationOpen = true
  eventForm.registrationDeadline = ''
  eventForm.submissionDeadline = ''
  tracks.value = []
  formFields.value = []
  message.value = ''
}

function openEditEvent(evt) {
  editingEventId.value = evt.id
  Object.assign(eventForm, {
    title: evt.title || '',
    subtitle: evt.subtitle || '',
    location: evt.location || '',
    description: evt.description || '',
    status: evt.status || 'published',
    registrationOpen: evt.registrationOpen === true || evt.registrationOpen === 1,
    registrationDeadline: toDateTimeInput(evt.registrationDeadline),
    submissionDeadline: toDateTimeInput(evt.submissionDeadline),
  })
  tracks.value = []
  formFields.value = []
  loadTracks(evt.id)
  loadFormFields(evt.id)
  message.value = ''
}

function toDateTimeInput(value) {
  if (!value) return ''
  return String(value).slice(0, 16)
}

function toApiDateTime(value) {
  if (!value) return null
  return value.length === 10 ? `${value}T23:59:00` : value
}

async function loadTracks(eventId) {
  try { tracks.value = await AdminApi.tracks(eventId) } catch (e) { tracks.value = [] }
}

async function loadFormFields(eventId) {
  try { formFields.value = await AdminApi.formFields(eventId, 'registration') } catch (e) { formFields.value = [] }
}

async function saveEvent() {
  if (!eventForm.title.trim()) { message.value = '请输入活动标题'; return }
  loading.value = true
  try {
    const payload = {
      ...eventForm,
      registrationOpen: eventForm.registrationOpen ? 1 : 0,
      registrationDeadline: toApiDateTime(eventForm.registrationDeadline),
      submissionDeadline: toApiDateTime(eventForm.submissionDeadline),
    }
    if (editingEventId.value) {
      await AdminApi.updateEvent(editingEventId.value, payload)
    } else {
      const created = await AdminApi.createEvent(payload)
      editingEventId.value = created.id
      await loadTracks(created.id)
      await loadFormFields(created.id)
    }
    message.value = '保存成功'
    await loadEvents()
  } catch (e) { message.value = e.message }
  finally { loading.value = false }
}

async function addTrack() {
  if (!editingEventId.value || !newTrackName.value.trim()) return
  try {
    await AdminApi.createTrack(editingEventId.value, { name: newTrackName.value.trim(), sortOrder: tracks.value.length + 1 })
    newTrackName.value = ''
    await loadTracks(editingEventId.value)
  } catch (e) { message.value = e.message }
}

function resetNewField() {
  Object.assign(newField, {
    fieldKey: '',
    label: '',
    fieldType: 'text',
    required: false,
    placeholder: '',
    optionsText: '',
  })
}

function fieldPayload(field, index = 0) {
  return {
    targetType: 'registration',
    fieldKey: field.fieldKey.trim(),
    label: field.label.trim(),
    fieldType: field.fieldType,
    required: field.required,
    placeholder: field.placeholder,
    options: (field.optionsText || '').split('\n').map(item => item.trim()).filter(Boolean),
    sortOrder: index + 1,
    enabled: true,
  }
}

async function addFormField() {
  if (!editingEventId.value) {
    message.value = '请先保存活动，再添加报名字段'
    return
  }
  if (!newField.fieldKey.trim() || !newField.label.trim()) {
    message.value = '字段标识和字段标题不能为空'
    return
  }
  try {
    await AdminApi.createFormField(editingEventId.value, fieldPayload(newField, formFields.value.length))
    resetNewField()
    await loadFormFields(editingEventId.value)
    message.value = '报名字段已添加'
  } catch (e) { message.value = e.message }
}

// ========== Registrations ==========
const registrations = ref([])
const regFilter = ref('all')
const selectedReg = ref(null)

const filteredRegs = computed(() => {
  if (regFilter.value === 'all') return registrations.value
  return registrations.value.filter(r => r.status === regFilter.value)
})

async function loadRegistrations() {
  try {
    const data = await AdminApi.registrations({})
    registrations.value = Array.isArray(data) ? data : []
  } catch (e) { registrations.value = [] }
}

async function reviewReg(id, status) {
  try {
    await AdminApi.reviewRegistration(id, { status, note: '' })
    const reg = registrations.value.find(r => r.id === id)
    if (reg) reg.status = status
    selectedReg.value = null
    await loadDashboard()
  } catch (e) { message.value = e.message }
}

function statusLabel(s) {
  const map = { pending: '待审核', approved: '已通过', rejected: '已拒绝', waitlist: '候补', cancelled: '已取消' }
  return map[s] || s
}

// ========== Projects ==========
const projects = ref([])

async function loadProjects() {
  try {
    const data = await AdminApi.projects({})
    projects.value = Array.isArray(data) ? data : []
  } catch (e) { projects.value = [] }
}

async function updateProjectStatus(id, status) {
  try { await AdminApi.updateProjectStatus(id, status); await loadProjects() } catch (e) { message.value = e.message }
}

async function loadAdminData() {
  await loadDashboard()
  await loadEvents()
  await loadRegistrations()
  await loadProjects()
}

// ========== Lifecycle ==========
onMounted(checkRootStatus)
</script>

<template>
  <main v-if="!adminReady" class="admin-auth-page">
    <section class="admin-auth-card">
      <router-link to="/" class="back-link">← 返回前台</router-link>
      <p class="eyebrow">ROOT ADMIN</p>
      <h1>{{ authMode === 'bootstrap' ? '初始化后台账号' : '登录后台' }}</h1>
      <p>{{ authMode === 'bootstrap' ? '第一次使用后台，需要先创建 root 超级管理员。' : '请输入 root 邮箱和密码进入后台。' }}</p>

      <form class="auth-form" @submit.prevent="handleRootAuth">
        <template v-if="authMode === 'bootstrap'">
          <label>创始人邮箱 <input v-model="authForm.ownerEmail" type="email" required /></label>
          <label>显示名称 <input v-model="authForm.name" type="text" placeholder="IGNAI Root" /></label>
          <label>密码 <input v-model="authForm.password" type="password" required minlength="8" /></label>
        </template>
        <template v-else>
          <label>邮箱 <input v-model="authForm.account" type="text" required /></label>
          <label>密码 <input v-model="authForm.password" type="password" required /></label>
        </template>
        <button class="primary-button" type="submit" :disabled="loading">
          {{ loading ? '处理中...' : authMode === 'bootstrap' ? '初始化 root' : '登录后台' }}
        </button>
        <p v-if="message" class="top-msg">{{ message }}</p>
      </form>
    </section>
  </main>

  <div v-else class="admin-shell">
    <aside class="admin-sidebar">
      <div class="sidebar-brand">
        <router-link to="/">← 返回前台</router-link>
        <h2>后台管理</h2>
      </div>
      <nav class="sidebar-nav">
        <button v-for="v in views" :key="v.key" :class="{ active: activeView === v.key }" @click="activeView = v.key">
          <span>{{ v.icon }}</span> {{ v.label }}
        </button>
      </nav>
    </aside>

    <main class="admin-main">
      <p v-if="message" class="top-msg">{{ message }}</p>

      <!-- Dashboard -->
      <div v-if="activeView === 'dashboard'" class="admin-view">
        <h1>概览</h1>
        <div class="metric-grid">
          <div class="metric-card"><div class="metric-value">{{ metrics.totalRegistrations }}</div><div class="metric-label">报名总数</div></div>
          <div class="metric-card"><div class="metric-value">{{ metrics.pendingRegistrations }}</div><div class="metric-label">待审核</div></div>
          <div class="metric-card"><div class="metric-value">{{ metrics.approvedRegistrations }}</div><div class="metric-label">已通过</div></div>
          <div class="metric-card"><div class="metric-value">{{ metrics.totalProjects }}</div><div class="metric-label">项目数</div></div>
          <div class="metric-card"><div class="metric-value">{{ metrics.totalUsers }}</div><div class="metric-label">用户数</div></div>
        </div>
      </div>

      <!-- Events -->
      <div v-if="activeView === 'events'" class="admin-view">
        <h1>活动管理</h1>
        <div class="toolbar">
          <button class="primary-button" @click="openCreateEvent">创建活动</button>
        </div>

        <!-- Event List -->
        <table v-if="events.length" class="data-table">
          <thead><tr><th>ID</th><th>标题</th><th>状态</th><th>报名</th><th>操作</th></tr></thead>
          <tbody>
            <tr v-for="evt in events" :key="evt.id">
              <td>{{ evt.id }}</td>
              <td>{{ evt.title }}</td>
              <td>{{ evt.status }}</td>
              <td>{{ evt.registrationOpen ? '开放' : '关闭' }}</td>
              <td><button class="outline-button" @click="openEditEvent(evt)">编辑</button></td>
            </tr>
          </tbody>
        </table>
        <p v-else class="muted">暂无活动</p>

        <!-- Event Editor -->
        <div v-if="editingEventId !== null || editingEventId === 0" class="editor-panel">
          <h2>{{ editingEventId ? '编辑活动 #' + editingEventId : '创建活动' }}</h2>
          <div class="form-grid">
            <label>标题 <input v-model="eventForm.title" /></label>
            <label>副标题 <input v-model="eventForm.subtitle" /></label>
            <label>地点 <input v-model="eventForm.location" /></label>
            <label>状态
              <select v-model="eventForm.status">
                <option value="draft">草稿</option>
                <option value="published">已发布</option>
                <option value="running">进行中</option>
                <option value="archived">已归档</option>
              </select>
            </label>
            <label>报名状态
              <select v-model="eventForm.registrationOpen">
                <option :value="true">开放报名</option>
                <option :value="false">关闭报名</option>
              </select>
            </label>
            <label>报名截止
              <input v-model="eventForm.registrationDeadline" type="datetime-local" />
            </label>
            <label>提交截止
              <input v-model="eventForm.submissionDeadline" type="datetime-local" />
            </label>
            <label class="wide">描述 <textarea v-model="eventForm.description" rows="3"></textarea></label>
          </div>
          <button class="primary-button" @click="saveEvent" :disabled="loading">保存活动</button>

          <!-- Tracks (only when editing) -->
          <div v-if="editingEventId" style="margin-top:24px">
            <h3>赛道</h3>
            <ul v-if="tracks.length">
              <li v-for="t in tracks" :key="t.id">{{ t.name }} — {{ t.description }}</li>
            </ul>
            <div class="inline-form">
              <input v-model="newTrackName" placeholder="新赛道名称" @keyup.enter="addTrack" />
              <button class="outline-button" @click="addTrack">添加赛道</button>
            </div>
          </div>

          <div v-if="editingEventId" class="field-editor-block">
            <h3>报名字段</h3>
            <ul v-if="formFields.length" class="field-list">
              <li v-for="field in formFields" :key="field.id">
                <strong>{{ field.label }}</strong>
                <span>{{ field.fieldKey }} · {{ field.fieldType }} · {{ field.required ? '必填' : '选填' }}</span>
              </li>
            </ul>
            <p v-else class="muted">还没有报名字段。至少添加姓名、手机号、赛道等字段，前台报名页才会出现表单。</p>

            <div class="field-form">
              <label>字段标识 <input v-model="newField.fieldKey" placeholder="name / phone / track" /></label>
              <label>字段标题 <input v-model="newField.label" placeholder="姓名 / 手机号 / 选择赛道" /></label>
              <label>字段类型
                <select v-model="newField.fieldType">
                  <option value="text">单行文本</option>
                  <option value="textarea">多行文本</option>
                  <option value="select">下拉选择</option>
                  <option value="radio">单选</option>
                  <option value="checkbox">多选</option>
                  <option value="url">链接</option>
                  <option value="date">日期</option>
                  <option value="file">文件</option>
                </select>
              </label>
              <label>占位提示 <input v-model="newField.placeholder" placeholder="请输入..." /></label>
              <label class="check-label"><input v-model="newField.required" type="checkbox" /> 必填</label>
              <label class="wide">选项
                <textarea v-model="newField.optionsText" rows="3" placeholder="下拉/单选/多选时使用，每行一个选项"></textarea>
              </label>
              <button class="outline-button" @click="addFormField">添加报名字段</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Registrations -->
      <div v-if="activeView === 'registrations'" class="admin-view">
        <h1>报名管理</h1>
        <div class="reg-filters">
          <button v-for="f in [{key:'all',label:'全部'},{key:'pending',label:'待审核'},{key:'approved',label:'已通过'},{key:'rejected',label:'已拒绝'}]" :key="f.key" :class="{ active: regFilter === f.key }" @click="regFilter = f.key">{{ f.label }}</button>
          <button class="outline-button" @click="loadRegistrations" style="margin-left:auto">刷新</button>
        </div>
        <table v-if="filteredRegs.length" class="data-table">
          <thead><tr><th>姓名</th><th>类型</th><th>状态</th><th>操作</th></tr></thead>
          <tbody>
            <tr v-for="reg in filteredRegs" :key="reg.id" @click="selectedReg = reg" style="cursor:pointer">
              <td>{{ reg.contactName }}</td>
              <td>{{ reg.registrationType === 'team' ? '团队' : '个人' }}</td>
              <td><span class="status-badge" :data-status="reg.status">{{ statusLabel(reg.status) }}</span></td>
              <td><button class="outline-button" @click.stop="selectedReg = reg">详情</button></td>
            </tr>
          </tbody>
        </table>
        <p v-else class="muted">暂无报名数据</p>

        <!-- Detail Drawer -->
        <div v-if="selectedReg" class="detail-drawer">
          <div class="drawer-header">
            <h3>{{ selectedReg.contactName }}</h3>
            <button @click="selectedReg = null">✕</button>
          </div>
          <div class="drawer-body">
            <p><strong>类型：</strong>{{ selectedReg.registrationType === 'team' ? '团队报名' : '个人报名' }}</p>
            <p><strong>队伍：</strong>{{ selectedReg.teamName || '无' }}</p>
            <p><strong>手机：</strong>{{ selectedReg.contactPhone }}</p>
            <p><strong>邮箱：</strong>{{ selectedReg.contactEmail }}</p>
            <p><strong>赛道ID：</strong>{{ selectedReg.trackId }}</p>
            <p><strong>状态：</strong>{{ statusLabel(selectedReg.status) }}</p>
            <div class="drawer-actions" v-if="selectedReg.status === 'pending'">
              <button class="primary-button" @click="reviewReg(selectedReg.id, 'approved')">通过</button>
              <button class="danger-button" @click="reviewReg(selectedReg.id, 'rejected')">拒绝</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Projects -->
      <div v-if="activeView === 'projects'" class="admin-view">
        <h1>项目看板</h1>
        <button class="outline-button" @click="loadProjects" style="margin-bottom:16px">刷新</button>
        <table v-if="projects.length" class="data-table">
          <thead><tr><th>标题</th><th>状态</th><th>创建时间</th><th>操作</th></tr></thead>
          <tbody>
            <tr v-for="p in projects" :key="p.id">
              <td>{{ p.title }}</td>
              <td>{{ p.status }}</td>
              <td>{{ p.createdAt?.slice(0,10) }}</td>
              <td>
                <button v-if="p.status === 'submitted'" class="outline-button" @click="updateProjectStatus(p.id, 'published')">发布</button>
                <button v-if="p.status === 'published'" class="ghost-button" @click="updateProjectStatus(p.id, 'archived')">归档</button>
              </td>
            </tr>
          </tbody>
        </table>
        <p v-else class="muted">暂无项目</p>
      </div>

      <!-- Mail -->
      <div v-if="activeView === 'mail'" class="admin-view">
        <h1>邮件管理</h1>
        <p class="muted">邮件功能暂未实现。</p>
      </div>
    </main>
  </div>
</template>

<style scoped>
.admin-auth-page { min-height: 100vh; display: grid; place-items: center; padding: 32px 20px; background: var(--paper); }
.admin-auth-card { width: min(460px, 100%); border: 1px solid var(--line); border-radius: 8px; padding: 28px; background: var(--card-bg); }
.admin-auth-card h1 { margin: 8px 0 10px; font-size: clamp(1.8rem, 5vw, 2.6rem); }
.admin-auth-card p { color: var(--muted); line-height: 1.7; }
.back-link { color: var(--muted); text-decoration: none; font-size: .86rem; }
.auth-form { display: grid; gap: 14px; margin-top: 22px; }
.admin-shell { display: grid; grid-template-columns: 248px 1fr; min-height: 100vh; background: var(--paper); color: var(--ink); }
.admin-sidebar { background: var(--paper-strong); border-right: 1px solid var(--line); padding: 20px; position: sticky; top: 0; height: 100vh; }
.sidebar-brand a { color: var(--muted); text-decoration: none; font-size: .82rem; }
.sidebar-brand h2 { margin: 12px 0 24px; font-size: 1.1rem; }
.sidebar-nav { display: grid; gap: 4px; }
.sidebar-nav button { display: flex; align-items: center; gap: 10px; padding: 10px 14px; border: none; border-radius: 8px; background: transparent; color: var(--muted); cursor: pointer; font: inherit; font-size: .9rem; text-align: left; }
.sidebar-nav button.active { background: rgba(255,122,24,.14); color: var(--heat); }
.admin-main { padding: 32px; background: var(--paper); }
.admin-view h1 { margin: 0 0 24px; }
.top-msg { color: var(--heat); font-weight: 700; margin-bottom: 12px; }
.metric-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(160px, 1fr)); gap: 16px; }
.metric-card { background: var(--card-bg); border: 1px solid var(--line); border-radius: 8px; padding: 20px; text-align: center; }
.metric-value { font-size: 2rem; font-weight: 800; color: var(--heat); }
.metric-label { color: var(--muted); font-size: .82rem; margin-top: 4px; }
.toolbar { margin-bottom: 16px; display: flex; gap: 12px; }
.data-table { width: 100%; border-collapse: collapse; margin-bottom: 24px; }
.data-table th, .data-table td { padding: 10px 12px; text-align: left; border-bottom: 1px solid var(--line); }
.data-table th { color: var(--muted); font-size: .78rem; text-transform: uppercase; }
.editor-panel { background: var(--card-bg); border: 1px solid var(--line); border-radius: 8px; padding: 24px; margin-top: 20px; }
.editor-panel h2 { margin: 0 0 16px; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; }
.form-grid .wide { grid-column: 1 / -1; }
label { color: var(--muted); font-size: .78rem; font-weight: 700; display: grid; gap: 6px; }
input, textarea, select { min-height: 42px; border: 1px solid var(--line); border-radius: 8px; padding: 0 12px; background: var(--paper-strong); color: var(--ink); font: inherit; font-size: .92rem; }
textarea { padding: 10px 12px; resize: vertical; }
.inline-form { display: flex; gap: 10px; margin-top: 12px; }
.inline-form input { flex: 1; }
.field-editor-block { margin-top: 24px; }
.field-list {
  list-style: none;
  display: grid;
  gap: 8px;
  padding: 0;
  margin: 0 0 14px;
}
.field-list li {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 12px;
  border: 1px solid var(--line);
  border-radius: 8px;
}
.field-list span { color: var(--muted); font-size: .82rem; }
.field-form {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-top: 12px;
}
.field-form .wide { grid-column: 1 / -1; }
.check-label {
  display: flex;
  align-items: center;
  gap: 8px;
  min-height: 42px;
  margin-top: 18px;
}
.check-label input { width: auto; min-height: auto; }
.reg-filters { display: flex; gap: 8px; margin-bottom: 16px; align-items: center; }
.reg-filters button { padding: 6px 14px; border: 1px solid var(--line); border-radius: 20px; background: transparent; color: var(--muted); cursor: pointer; font: inherit; font-size: .82rem; }
.reg-filters button.active { border-color: var(--heat); color: var(--heat); }
.status-badge { padding: 2px 10px; border-radius: 12px; font-size: .78rem; font-weight: 700; }
.status-badge[data-status="pending"] { background: rgba(255,200,0,.15); color: #ffc800; }
.status-badge[data-status="approved"] { background: rgba(49,196,141,.15); color: #31c48d; }
.status-badge[data-status="rejected"] { background: rgba(248,113,113,.15); color: #f87171; }
.detail-drawer { position: fixed; right: 0; top: 0; width: 400px; height: 100vh; background: var(--card-bg); border-left: 1px solid var(--line); padding: 24px; z-index: 100; overflow-y: auto; }
.drawer-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.drawer-header button { background: none; border: none; color: var(--muted); cursor: pointer; font-size: 1.2rem; }
.drawer-body p { margin: 8px 0; }
.drawer-actions { display: flex; gap: 12px; margin-top: 20px; }
.primary-button, .outline-button, .ghost-button, .danger-button { min-height: 38px; padding: 8px 16px; border-radius: 8px; cursor: pointer; font: inherit; font-weight: 800; display: inline-flex; align-items: center; }
.primary-button { border: 1px solid var(--heat); background: var(--heat); color: #111; }
.outline-button { border: 1px solid var(--line); background: transparent; color: var(--ink); }
.ghost-button { border: none; background: transparent; color: var(--muted); }
.danger-button { border: 1px solid #f87171; background: transparent; color: #f87171; }
.primary-button:disabled, .outline-button:disabled { opacity: .55; cursor: not-allowed; }
.muted { color: var(--muted); }
@media (max-width: 768px) {
  .admin-shell { grid-template-columns: 1fr; }
  .admin-sidebar { position: static; height: auto; }
  .admin-main { padding: 20px; }
  .detail-drawer { width: 100%; }
  .form-grid { grid-template-columns: 1fr; }
}
</style>
