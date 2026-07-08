<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { AdminApi } from '../../api'

const activeView = ref('dashboard')

const views = [
  { key: 'dashboard', label: '概览', icon: '📊' },
  { key: 'events', label: '活动管理', icon: '🎯' },
  { key: 'registrations', label: '报名管理', icon: '📋' },
  { key: 'projects', label: '项目看板', icon: '📁' },
  { key: 'mail', label: '邮件管理', icon: '✉️' },
]

const fieldTypes = [
  { value: 'text', label: '短文本' },
  { value: 'textarea', label: '长文本' },
  { value: 'select', label: '下拉选择' },
  { value: 'radio', label: '单选' },
  { value: 'checkbox', label: '多选框' },
  { value: 'date', label: '日期' },
  { value: 'url', label: '链接' },
  { value: 'file', label: '文件' },
]

const eventStatuses = [
  { value: 'draft', label: '草稿' },
  { value: 'published', label: '已发布' },
  { value: 'running', label: '进行中' },
  { value: 'archived', label: '已归档' },
]

const metrics = reactive({
  registrations: 6,
  pending: 3,
  approved: 2,
  projects: 5,
  likes: 128,
})

const registrations = ref([
  { id: 1, name: '陈晨', team: 'Torch Lab', type: '团队报名', track: 'Agent 工具', status: 'pending', tags: ['Agent', '教育实训', '全栈'], summary: '团队希望做一个面向软件实训课程的 Agent 工具。' },
  { id: 2, name: '李想', team: '个人报名', type: '个人报名', track: '内容生产', status: 'pending', tags: ['内容', '播客'], summary: '想做一个播客内容自动整理工具。' },
  { id: 3, name: '王磊', team: 'Signal Group', type: '团队报名', track: '商业应用', status: 'approved', tags: ['商业', '本地'], summary: '帮本地小团队发现 AI 应用机会。' },
  { id: 4, name: '赵雪', team: '个人报名', type: '个人报名', track: 'Agent 工具', status: 'approved', tags: ['Agent', '效率'], summary: '做一个面向学生的任务管理 Agent。' },
  { id: 5, name: '孙浩', team: 'Voice Lab', type: '团队报名', track: '内容生产', status: 'pending', tags: ['音频', 'AI'], summary: '把现场录音自动转成文字和摘要。' },
  { id: 6, name: '周文', team: '个人报名', type: '个人报名', track: '教育实训', status: 'rejected', tags: ['教育'], summary: '做一个课程表管理工具。' },
])

const selectedReg = ref(null)
const regFilter = ref('all')
const eventLoading = ref(false)
const eventSaving = ref(false)
const eventMessage = ref('')
const eventMessageType = ref('muted')

const eventConfig = reactive(defaultEventConfig())

const filteredRegs = computed(() => {
  if (regFilter.value === 'all') return registrations.value
  return registrations.value.filter(r => r.status === regFilter.value)
})

const previewFields = computed(() => eventConfig.registrationFields.filter(field => field.enabled !== false))

function selectReg(reg) {
  selectedReg.value = reg
}

function reviewReg(id, status) {
  const reg = registrations.value.find(r => r.id === id)
  if (reg) reg.status = status
  selectedReg.value = null
}

function defaultTracks() {
  return [
    { name: 'Agent 工具', description: '围绕自动化工作流、个人效率和开发协作的 AI Agent 应用。', sortOrder: 1 },
    { name: '教育实训', description: '服务课堂、实训、知识管理和学习反馈的 AI 原型。', sortOrder: 2 },
    { name: '内容生产', description: '面向图文、音频、演示材料和运营内容的 AI 创作工具。', sortOrder: 3 },
  ]
}

function defaultFields() {
  return [
    { fieldKey: 'name', label: '姓名', fieldType: 'text', required: true, placeholder: '请填写真实姓名', optionsText: '', sortOrder: 1, enabled: true },
    { fieldKey: 'phone', label: '手机号', fieldType: 'text', required: true, placeholder: '用于活动通知', optionsText: '', sortOrder: 2, enabled: true },
    { fieldKey: 'track', label: '关注赛道', fieldType: 'select', required: true, placeholder: '', optionsText: defaultTracks().map(t => t.name).join('\n'), sortOrder: 3, enabled: true },
    { fieldKey: 'idea', label: '你想解决什么问题？', fieldType: 'textarea', required: true, placeholder: '简单描述你的项目方向或灵感', optionsText: '', sortOrder: 4, enabled: true },
  ]
}

function defaultEventConfig() {
  return {
    id: null,
    title: 'IGN AI Skillathon',
    subtitle: '用 AI 原型解决真实问题',
    location: '长沙 · 线下',
    description: '围绕 AI Agent、教育实训和内容生产，用一个周末完成可展示的项目原型。',
    status: 'published',
    registrationOpen: true,
    ratingEnabled: false,
    voteEnabled: true,
    registrationDeadline: '',
    submissionDeadline: '',
    ratingStartAt: '',
    ratingEndAt: '',
    officialSiteUrl: 'https://www.ignai.cn/',
    benchmarkSiteUrl: 'https://aihacktour.com/',
    tracks: defaultTracks(),
    registrationFields: defaultFields(),
  }
}

function normalizeDateForInput(value) {
  if (!value) return ''
  return String(value).slice(0, 16)
}

function toApiDate(value) {
  if (!value) return null
  return value.length === 16 ? `${value}:00` : value
}

function fieldOptionsText(field) {
  if (field.optionsText !== undefined) return field.optionsText
  return Array.isArray(field.options) ? field.options.join('\n') : ''
}

function parseOptions(text) {
  if (!text) return []
  return text.split('\n').map(item => item.trim()).filter(Boolean)
}

function applyEventConfig(data) {
  const tracks = data?.tracks?.length ? data.tracks : defaultTracks()
  const fields = data?.registrationFields?.length ? data.registrationFields : defaultFields()
  Object.assign(eventConfig, {
    id: data?.id ?? null,
    title: data?.title ?? 'IGN AI Skillathon',
    subtitle: data?.subtitle ?? '',
    location: data?.location ?? '',
    description: data?.description ?? '',
    status: data?.status ?? 'published',
    registrationOpen: data?.registrationOpen ?? true,
    ratingEnabled: data?.ratingEnabled ?? false,
    voteEnabled: data?.voteEnabled ?? true,
    registrationDeadline: normalizeDateForInput(data?.registrationDeadline),
    submissionDeadline: normalizeDateForInput(data?.submissionDeadline),
    ratingStartAt: normalizeDateForInput(data?.ratingStartAt),
    ratingEndAt: normalizeDateForInput(data?.ratingEndAt),
    officialSiteUrl: data?.officialSiteUrl ?? 'https://www.ignai.cn/',
    benchmarkSiteUrl: data?.benchmarkSiteUrl ?? 'https://aihacktour.com/',
    tracks: tracks.map((track, index) => ({
      id: track.id,
      name: track.name || '',
      description: track.description || '',
      sortOrder: track.sortOrder ?? index + 1,
    })),
    registrationFields: fields.map((field, index) => ({
      id: field.id,
      fieldKey: field.fieldKey || `field_${index + 1}`,
      label: field.label || '',
      fieldType: field.fieldType || 'text',
      required: field.required ?? false,
      placeholder: field.placeholder || '',
      optionsText: fieldOptionsText(field),
      sortOrder: field.sortOrder ?? index + 1,
      enabled: field.enabled ?? true,
    })),
  })
}

async function loadEventConfig() {
  eventLoading.value = true
  eventMessage.value = ''
  try {
    const data = await AdminApi.currentEventConfig()
    applyEventConfig(data)
    eventMessage.value = '已读取后端活动配置'
    eventMessageType.value = 'success'
  } catch (error) {
    applyEventConfig(defaultEventConfig())
    eventMessage.value = error.message
    eventMessageType.value = 'error'
  } finally {
    eventLoading.value = false
  }
}

function addTrack() {
  eventConfig.tracks.push({ name: '', description: '', sortOrder: eventConfig.tracks.length + 1 })
}

function removeTrack(index) {
  eventConfig.tracks.splice(index, 1)
}

function addField() {
  const next = eventConfig.registrationFields.length + 1
  eventConfig.registrationFields.push({
    fieldKey: `field_${next}`,
    label: `新增问题 ${next}`,
    fieldType: 'text',
    required: false,
    placeholder: '',
    optionsText: '',
    sortOrder: next,
    enabled: true,
  })
}

function removeField(index) {
  eventConfig.registrationFields.splice(index, 1)
}

function buildPayload() {
  return {
    title: eventConfig.title,
    subtitle: eventConfig.subtitle,
    location: eventConfig.location,
    description: eventConfig.description,
    status: eventConfig.status,
    registrationOpen: eventConfig.registrationOpen,
    ratingEnabled: eventConfig.ratingEnabled,
    voteEnabled: eventConfig.voteEnabled,
    registrationDeadline: toApiDate(eventConfig.registrationDeadline),
    submissionDeadline: toApiDate(eventConfig.submissionDeadline),
    ratingStartAt: toApiDate(eventConfig.ratingStartAt),
    ratingEndAt: toApiDate(eventConfig.ratingEndAt),
    officialSiteUrl: eventConfig.officialSiteUrl,
    benchmarkSiteUrl: eventConfig.benchmarkSiteUrl,
    tracks: eventConfig.tracks.map((track, index) => ({
      name: track.name,
      description: track.description,
      sortOrder: index + 1,
    })),
    registrationFields: eventConfig.registrationFields.map((field, index) => ({
      fieldKey: field.fieldKey,
      label: field.label,
      fieldType: field.fieldType,
      required: field.required,
      placeholder: field.placeholder,
      options: parseOptions(field.optionsText),
      sortOrder: index + 1,
      enabled: field.enabled,
    })),
  }
}

async function saveEventConfig() {
  if (!eventConfig.id) {
    eventMessage.value = '当前没有可保存的活动 ID'
    eventMessageType.value = 'error'
    return
  }
  eventSaving.value = true
  eventMessage.value = ''
  try {
    const data = await AdminApi.saveEventConfig(eventConfig.id, buildPayload())
    applyEventConfig(data)
    eventMessage.value = '已保存，前台活动页会读取这份配置'
    eventMessageType.value = 'success'
  } catch (error) {
    eventMessage.value = error.message
    eventMessageType.value = 'error'
  } finally {
    eventSaving.value = false
  }
}

onMounted(loadEventConfig)

watch(activeView, (view) => {
  if (view === 'events' && !eventConfig.id && !eventLoading.value) {
    loadEventConfig()
  }
})
</script>

<template>
  <div class="admin-shell">
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
      <div v-if="activeView === 'dashboard'" class="admin-view">
        <h1>概览</h1>
        <div class="metric-grid">
          <div class="metric-card"><div class="metric-value">{{ metrics.registrations }}</div><div class="metric-label">报名总数</div></div>
          <div class="metric-card"><div class="metric-value">{{ metrics.pending }}</div><div class="metric-label">待审核</div></div>
          <div class="metric-card"><div class="metric-value">{{ metrics.approved }}</div><div class="metric-label">已通过</div></div>
          <div class="metric-card"><div class="metric-value">{{ metrics.projects }}</div><div class="metric-label">项目数</div></div>
          <div class="metric-card"><div class="metric-value">{{ metrics.likes }}</div><div class="metric-label">总点赞</div></div>
        </div>
      </div>

      <div v-if="activeView === 'events'" class="admin-view">
        <div class="event-editor-header">
          <div>
            <p class="eyebrow">EVENT CMS</p>
            <h1>活动管理与报名问卷</h1>
            <p class="muted">后台保存后，前台活动详情页会读取同一份云端数据库配置。</p>
          </div>
          <div class="event-actions">
            <router-link v-if="eventConfig.id" class="outline-button" :to="`/event/${eventConfig.id}`">打开前台活动页</router-link>
            <button class="outline-button" type="button" @click="loadEventConfig" :disabled="eventLoading">刷新</button>
            <button class="primary-button" type="button" @click="saveEventConfig" :disabled="eventSaving || eventLoading">
              {{ eventSaving ? '保存中' : '保存配置' }}
            </button>
          </div>
        </div>

        <p v-if="eventMessage" class="event-message" :data-type="eventMessageType">{{ eventMessage }}</p>

        <div class="event-editor-grid">
          <section class="config-panel">
            <h2>基础信息</h2>
            <div class="form-grid">
              <label>
                活动标题
                <input v-model="eventConfig.title" type="text" />
              </label>
              <label>
                副标题
                <input v-model="eventConfig.subtitle" type="text" />
              </label>
              <label>
                城市与场地
                <input v-model="eventConfig.location" type="text" />
              </label>
              <label>
                状态
                <select v-model="eventConfig.status">
                  <option v-for="status in eventStatuses" :key="status.value" :value="status.value">{{ status.label }}</option>
                </select>
              </label>
              <label class="wide-field">
                活动描述
                <textarea v-model="eventConfig.description" rows="4"></textarea>
              </label>
              <label>
                报名截止
                <input v-model="eventConfig.registrationDeadline" type="datetime-local" />
              </label>
              <label>
                提交截止
                <input v-model="eventConfig.submissionDeadline" type="datetime-local" />
              </label>
              <label>
                官网链接
                <input v-model="eventConfig.officialSiteUrl" type="url" />
              </label>
              <label>
                对标网站
                <input v-model="eventConfig.benchmarkSiteUrl" type="url" />
              </label>
            </div>
            <div class="switch-row">
              <label><input v-model="eventConfig.registrationOpen" type="checkbox" /> 开放报名</label>
              <label><input v-model="eventConfig.voteEnabled" type="checkbox" /> 开放点赞</label>
              <label><input v-model="eventConfig.ratingEnabled" type="checkbox" /> 开放评分</label>
            </div>
          </section>

          <section class="config-panel">
            <div class="panel-title-row">
              <h2>赛道</h2>
              <button class="outline-button" type="button" @click="addTrack">新增赛道</button>
            </div>
            <div class="stack-list">
              <div v-for="(track, index) in eventConfig.tracks" :key="index" class="editor-row">
                <div class="row-number">{{ String(index + 1).padStart(2, '0') }}</div>
                <label>
                  赛道名称
                  <input v-model="track.name" type="text" />
                </label>
                <label>
                  描述
                  <input v-model="track.description" type="text" />
                </label>
                <button class="ghost-button" type="button" @click="removeTrack(index)">删除</button>
              </div>
            </div>
          </section>

          <section class="config-panel">
            <div class="panel-title-row">
              <h2>报名问题</h2>
              <button class="outline-button" type="button" @click="addField">新增问题</button>
            </div>
            <div class="field-list">
              <div v-for="(field, index) in eventConfig.registrationFields" :key="index" class="field-editor">
                <div class="field-editor-head">
                  <div class="row-number">{{ String(index + 1).padStart(2, '0') }}</div>
                  <label class="toggle-label"><input v-model="field.enabled" type="checkbox" /> 启用</label>
                  <label class="toggle-label"><input v-model="field.required" type="checkbox" /> 必填</label>
                  <button class="ghost-button" type="button" @click="removeField(index)">删除</button>
                </div>
                <div class="form-grid compact">
                  <label>
                    问题标题
                    <input v-model="field.label" type="text" />
                  </label>
                  <label>
                    字段 Key
                    <input v-model="field.fieldKey" type="text" />
                  </label>
                  <label>
                    类型
                    <select v-model="field.fieldType">
                      <option v-for="type in fieldTypes" :key="type.value" :value="type.value">{{ type.label }}</option>
                    </select>
                  </label>
                  <label>
                    占位提示
                    <input v-model="field.placeholder" type="text" />
                  </label>
                  <label v-if="['select', 'radio', 'checkbox'].includes(field.fieldType)" class="wide-field">
                    选项，每行一个
                    <textarea v-model="field.optionsText" rows="3"></textarea>
                  </label>
                </div>
              </div>
            </div>
          </section>

          <aside class="config-preview">
            <p class="eyebrow">PARTICIPANT PREVIEW</p>
            <h2>{{ eventConfig.title || '活动标题' }}</h2>
            <p class="muted">{{ eventConfig.description || '活动描述将在这里展示。' }}</p>
            <div class="preview-tracks" v-if="eventConfig.tracks.length">
              <span v-for="track in eventConfig.tracks" :key="track.name">{{ track.name || '未命名赛道' }}</span>
            </div>
            <div class="preview-form">
              <div v-for="field in previewFields" :key="field.fieldKey" class="preview-field">
                <label>{{ field.label }} <span v-if="field.required" class="required">*</span></label>
                <select v-if="field.fieldType === 'select'">
                  <option>{{ field.placeholder || '请选择' }}</option>
                  <option v-for="opt in parseOptions(field.optionsText)" :key="opt">{{ opt }}</option>
                </select>
                <textarea v-else-if="field.fieldType === 'textarea'" :placeholder="field.placeholder || '参赛者填写内容'" rows="3"></textarea>
                <div v-else-if="['radio', 'checkbox'].includes(field.fieldType)" class="preview-options">
                  <label v-for="opt in parseOptions(field.optionsText)" :key="opt"><input :type="field.fieldType" disabled /> {{ opt }}</label>
                </div>
                <input v-else :type="field.fieldType === 'date' ? 'date' : field.fieldType === 'url' ? 'url' : 'text'" :placeholder="field.placeholder || '参赛者填写内容'" />
              </div>
            </div>
          </aside>
        </div>
      </div>

      <div v-if="activeView === 'registrations'" class="admin-view">
        <h1>报名管理</h1>
        <div class="reg-filters">
          <button v-for="f in [{key:'all',label:'全部'},{key:'pending',label:'待审核'},{key:'approved',label:'已通过'},{key:'rejected',label:'已拒绝'}]" :key="f.key" :class="{ active: regFilter === f.key }" @click="regFilter = f.key">{{ f.label }}</button>
        </div>
        <table class="reg-table">
          <thead><tr><th>姓名</th><th>类型</th><th>赛道</th><th>状态</th><th>操作</th></tr></thead>
          <tbody>
            <tr v-for="reg in filteredRegs" :key="reg.id">
              <td>{{ reg.name }}</td>
              <td>{{ reg.type }}</td>
              <td>{{ reg.track }}</td>
              <td><span class="status-badge" :data-status="reg.status">{{ reg.status }}</span></td>
              <td><button class="outline-button" @click="selectReg(reg)">详情</button></td>
            </tr>
          </tbody>
        </table>

        <div v-if="selectedReg" class="detail-drawer">
          <div class="drawer-header">
            <h3>{{ selectedReg.name }}</h3>
            <button @click="selectedReg = null">✕</button>
          </div>
          <div class="drawer-body">
            <p><strong>类型：</strong>{{ selectedReg.type }}</p>
            <p><strong>赛道：</strong>{{ selectedReg.track }}</p>
            <p><strong>摘要：</strong>{{ selectedReg.summary }}</p>
            <div class="tag-list">
              <span v-for="tag in selectedReg.tags" :key="tag" class="tag">{{ tag }}</span>
            </div>
            <div class="drawer-actions" v-if="selectedReg.status === 'pending'">
              <button class="primary-button" @click="reviewReg(selectedReg.id, 'approved')">通过</button>
              <button class="danger-button" @click="reviewReg(selectedReg.id, 'rejected')">拒绝</button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="activeView === 'projects'" class="admin-view">
        <h1>项目看板</h1>
        <p class="muted">项目提交接口待后端实现后接入。</p>
      </div>

      <div v-if="activeView === 'mail'" class="admin-view">
        <h1>邮件管理</h1>
        <p class="muted">邮件模板和发送接口待后端实现后接入。</p>
      </div>
    </main>
  </div>
</template>

<style scoped>
.admin-shell { display: grid; grid-template-columns: 248px 1fr; min-height: 100vh; background: var(--bg, #08090d); color: var(--ink, #e8e6e3); }
.admin-sidebar { background: var(--panel, #121318); border-right: 1px solid var(--line, #2a2b30); padding: 20px; position: sticky; top: 0; height: 100vh; }
.sidebar-brand a { color: var(--muted, #888); text-decoration: none; font-size: .82rem; }
.sidebar-brand h2 { margin: 12px 0 24px; font-size: 1.1rem; }
.sidebar-nav { display: grid; gap: 4px; }
.sidebar-nav button { display: flex; align-items: center; gap: 10px; padding: 10px 14px; border: none; border-radius: 8px; background: transparent; color: var(--muted, #888); cursor: pointer; font: inherit; font-size: .9rem; text-align: left; }
.sidebar-nav button.active { background: rgba(255,122,24,.12); color: var(--heat, #ff7a18); }
.admin-main { padding: 32px; }
.admin-view h1 { margin: 0 0 24px; }
.metric-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(160px, 1fr)); gap: 16px; }
.metric-card { background: var(--panel, #121318); border: 1px solid var(--line, #2a2b30); border-radius: 8px; padding: 20px; text-align: center; }
.metric-value { font-size: 2rem; font-weight: 800; color: var(--heat, #ff7a18); }
.metric-label { color: var(--muted, #888); font-size: .82rem; margin-top: 4px; }
.event-editor-header { display: flex; align-items: flex-end; justify-content: space-between; gap: 24px; margin-bottom: 24px; }
.event-editor-header h1 { margin-bottom: 8px; }
.event-actions { display: flex; gap: 10px; flex-wrap: wrap; justify-content: flex-end; }
.eyebrow { margin: 0 0 8px; color: var(--heat, #ff7a18); font-size: .78rem; font-weight: 800; letter-spacing: 0; text-transform: uppercase; }
.event-editor-grid { display: grid; grid-template-columns: minmax(0, 1fr) 420px; gap: 20px; align-items: start; }
.config-panel, .config-preview { background: var(--panel, #121318); border: 1px solid var(--line, #2a2b30); border-radius: 8px; padding: 22px; }
.config-panel { grid-column: 1; }
.config-panel h2, .config-preview h2 { margin: 0 0 16px; font-size: 1.15rem; }
.config-preview { grid-column: 2; grid-row: 1 / span 3; position: sticky; top: 24px; }
.panel-title-row { display: flex; align-items: center; justify-content: space-between; gap: 16px; margin-bottom: 14px; }
.panel-title-row h2 { margin: 0; }
.form-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 14px; }
.form-grid.compact { grid-template-columns: repeat(4, minmax(0, 1fr)); }
.wide-field { grid-column: 1 / -1; }
label { color: var(--muted, #888); font-size: .78rem; font-weight: 700; }
input, textarea, select { width: 100%; margin-top: 6px; min-height: 42px; border: 1px solid var(--line, #2a2b30); border-radius: 8px; padding: 0 12px; background: #1b1c22; color: var(--ink, #e8e6e3); font: inherit; font-size: .92rem; }
textarea { padding: 10px 12px; resize: vertical; }
input[type="checkbox"], input[type="radio"] { width: auto; min-height: auto; margin: 0; }
.switch-row { display: flex; flex-wrap: wrap; gap: 18px; margin-top: 16px; }
.switch-row label, .toggle-label { display: inline-flex; align-items: center; gap: 8px; color: var(--ink, #e8e6e3); }
.stack-list, .field-list { display: grid; gap: 12px; }
.editor-row { display: grid; grid-template-columns: 48px minmax(160px, 220px) 1fr auto; gap: 12px; align-items: end; padding: 14px; border: 1px solid var(--line, #2a2b30); border-radius: 8px; background: rgba(255,255,255,.02); }
.row-number { display: grid; place-items: center; width: 44px; height: 44px; border-radius: 8px; background: var(--heat, #ff7a18); color: #111; font-weight: 900; }
.field-editor { padding: 14px; border: 1px solid var(--line, #2a2b30); border-radius: 8px; background: rgba(255,255,255,.02); }
.field-editor-head { display: flex; align-items: center; gap: 14px; margin-bottom: 12px; }
.field-editor-head .ghost-button { margin-left: auto; }
.preview-tracks { display: flex; flex-wrap: wrap; gap: 8px; margin: 16px 0; }
.preview-tracks span { padding: 5px 10px; border: 1px solid rgba(255,122,24,.45); border-radius: 999px; color: var(--heat, #ff7a18); font-size: .78rem; font-weight: 800; }
.preview-form { display: grid; gap: 12px; margin-top: 18px; }
.preview-field { padding: 12px; border: 1px solid var(--line, #2a2b30); border-radius: 8px; }
.preview-options { display: flex; gap: 12px; flex-wrap: wrap; margin-top: 10px; }
.preview-options label { display: inline-flex; align-items: center; gap: 6px; color: var(--ink, #e8e6e3); }
.event-message { margin: -8px 0 18px; font-weight: 700; }
.event-message[data-type="success"] { color: #31c48d; }
.event-message[data-type="error"] { color: #f87171; }
.primary-button, .outline-button, .ghost-button, .danger-button { min-height: 38px; padding: 8px 16px; border-radius: 8px; cursor: pointer; font: inherit; font-weight: 800; text-decoration: none; display: inline-flex; align-items: center; justify-content: center; }
.primary-button { border: 1px solid var(--heat, #ff7a18); background: var(--heat, #ff7a18); color: #111; }
.outline-button { border: 1px solid var(--line, #2a2b30); background: transparent; color: var(--ink, #e8e6e3); }
.ghost-button { border: none; background: transparent; color: var(--muted, #888); }
.danger-button { border: 1px solid #f87171; background: transparent; color: #f87171; }
.primary-button:disabled, .outline-button:disabled { opacity: .55; cursor: not-allowed; }
.reg-filters { display: flex; gap: 8px; margin-bottom: 16px; }
.reg-filters button { padding: 6px 14px; border: 1px solid var(--line, #2a2b30); border-radius: 20px; background: transparent; color: var(--muted, #888); cursor: pointer; font: inherit; font-size: .82rem; }
.reg-filters button.active { border-color: var(--heat, #ff7a18); color: var(--heat, #ff7a18); }
.reg-table { width: 100%; border-collapse: collapse; }
.reg-table th, .reg-table td { padding: 10px 12px; text-align: left; border-bottom: 1px solid var(--line, #2a2b30); }
.reg-table th { color: var(--muted, #888); font-size: .78rem; text-transform: uppercase; }
.status-badge { padding: 2px 10px; border-radius: 12px; font-size: .78rem; font-weight: 700; }
.status-badge[data-status="pending"] { background: rgba(255,200,0,.15); color: #ffc800; }
.status-badge[data-status="approved"] { background: rgba(49,196,141,.15); color: #31c48d; }
.status-badge[data-status="rejected"] { background: rgba(248,113,113,.15); color: #f87171; }
.detail-drawer { position: fixed; right: 0; top: 0; width: 400px; height: 100vh; background: var(--panel, #121318); border-left: 1px solid var(--line, #2a2b30); padding: 24px; z-index: 100; overflow-y: auto; }
.drawer-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.drawer-header button { background: none; border: none; color: var(--muted, #888); cursor: pointer; font-size: 1.2rem; }
.drawer-body p { margin: 8px 0; }
.tag-list { display: flex; gap: 6px; flex-wrap: wrap; margin: 12px 0; }
.tag { padding: 2px 10px; border: 1px solid var(--line, #2a2b30); border-radius: 12px; font-size: .78rem; color: var(--muted, #888); }
.drawer-actions { display: flex; gap: 12px; margin-top: 20px; }
.muted { color: var(--muted, #888); }
.required { color: #f87171; }
@media (max-width: 1120px) {
  .event-editor-grid { grid-template-columns: 1fr; }
  .config-panel, .config-preview { grid-column: 1; }
  .config-preview { grid-row: auto; position: static; }
  .form-grid.compact { grid-template-columns: repeat(2, minmax(0, 1fr)); }
}
@media (max-width: 768px) {
  .admin-shell { grid-template-columns: 1fr; }
  .admin-sidebar { position: static; height: auto; }
  .admin-main { padding: 20px; }
  .event-editor-header { align-items: flex-start; flex-direction: column; }
  .form-grid, .form-grid.compact, .editor-row { grid-template-columns: 1fr; }
  .detail-drawer { width: 100%; }
}
</style>
