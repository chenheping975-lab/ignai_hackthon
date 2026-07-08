<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { hackathonEvents } from '../data/mock'
import { useAuthStore } from '../stores/auth'
import { EventApi, RegistrationApi } from '../api'

const route = useRoute()
const auth = useAuthStore()

const event = ref(normalizeMockEvent(hackathonEvents.find(e => e.id === route.params.id) || hackathonEvents[0]))
const form = ref({})
const formMessage = ref('')
const formMessageType = ref('muted')
const apiLoading = ref(false)

const activeStage = computed(() => {
  const stages = event.value.stages || []
  return stages.find((stage, index) => stage.done && !stages[index + 1]?.done) || stages[0]
})

function normalizeMockEvent(mockEvent) {
  return {
    ...mockEvent,
    backendId: Number.isFinite(Number(mockEvent.id)) ? Number(mockEvent.id) : null,
    formFields: mockEvent.formFields || [],
    tracks: mockEvent.tracks || [],
    submissionRequirements: mockEvent.submissionRequirements || [],
  }
}

function formatDate(value) {
  if (!value) return ''
  return String(value).slice(0, 10).replaceAll('-', '.')
}

function buildDateText(data) {
  const registration = formatDate(data.registrationDeadline)
  const submission = formatDate(data.submissionDeadline)
  if (registration && submission) return `报名截止 ${registration} · 提交截止 ${submission}`
  if (registration) return `报名截止 ${registration}`
  if (submission) return `提交截止 ${submission}`
  return '活动时间待公布'
}

function statusText(data) {
  if (data.status === 'draft') return '筹备中'
  if (data.status === 'running') return '作品提交中'
  if (data.status === 'archived') return '公开展示中'
  return data.registrationOpen ? '报名开放' : '报名暂未开放'
}

function buildStages(data) {
  const status = data.status || 'published'
  return [
    { key: 'registration', label: data.registrationOpen ? '报名开放' : '报名收集', done: status !== 'draft' },
    { key: 'review', label: '审核组队', done: status === 'running' || status === 'archived' },
    { key: 'submission', label: '作品提交', done: status === 'running' || status === 'archived' },
    { key: 'showcase', label: '公开展示', done: status === 'archived' },
  ]
}

function normalizeApiField(field) {
  return {
    key: field.fieldKey,
    label: field.label,
    type: field.fieldType || 'text',
    required: field.required ?? false,
    placeholder: field.placeholder || '',
    options: field.options || [],
  }
}

function normalizeApiEvent(data) {
  return {
    id: String(data.id),
    backendId: Number(data.id),
    title: data.title || 'IGN AI Skillathon',
    status: data.status || 'published',
    statusText: statusText(data),
    date: buildDateText(data),
    location: data.location || '地点待公布',
    summary: data.description || data.subtitle || '活动介绍待补充。',
    tracks: (data.tracks || []).map(track => ({
      id: track.id,
      name: track.name,
      description: track.description,
    })),
    stages: buildStages(data),
    formFields: (data.registrationFields || [])
      .filter(field => field.enabled !== false)
      .map(normalizeApiField),
    submissionRequirements: [
      '项目标题与一句话简介',
      'Demo 链接或可运行 HTML',
      '代码仓库或说明文档',
      'PPT、HTML、MP3、图片等轻量附件',
      'MVP 阶段不接收视频上传',
    ],
  }
}

function initForm() {
  const nextForm = {}
  event.value.formFields.forEach(field => {
    nextForm[field.key] = field.type === 'checkbox' ? [] : ''
  })
  form.value = nextForm
}

async function loadEvent() {
  const routeId = String(route.params.id)
  const fallback = hackathonEvents.find(e => e.id === routeId) || hackathonEvents[0]
  event.value = normalizeMockEvent(fallback)
  initForm()

  if (!/^\d+$/.test(routeId)) {
    return
  }

  apiLoading.value = true
  try {
    const data = await EventApi.detail(routeId)
    event.value = normalizeApiEvent(data)
    initForm()
  } catch (error) {
    formMessage.value = error.message
    formMessageType.value = 'error'
  } finally {
    apiLoading.value = false
  }
}

function selectedTrackId() {
  const selectedName = form.value.track
  const selectedTrack = event.value.tracks.find(track => track.name === selectedName)
  return selectedTrack?.id || null
}

async function handleRegister() {
  if (!auth.isLoggedIn) {
    formMessage.value = '请先登录再报名'
    formMessageType.value = 'error'
    return
  }
  if (!event.value.backendId) {
    formMessage.value = '当前活动还没有绑定后端活动 ID，暂不能提交报名'
    formMessageType.value = 'error'
    return
  }
  try {
    await RegistrationApi.submit({
      eventId: event.value.backendId,
      registrationType: form.value.registrationType?.includes('团队') ? 'team' : 'individual',
      teamName: form.value.teamName || null,
      contactName: form.value.name || auth.user?.name,
      contactPhone: form.value.phone || auth.user?.phone,
      contactEmail: form.value.email || auth.user?.email,
      trackId: selectedTrackId(),
    })
    formMessage.value = '报名成功！'
    formMessageType.value = 'success'
  } catch (e) {
    formMessage.value = e.message
    formMessageType.value = 'error'
  }
}

onMounted(loadEvent)
watch(() => route.params.id, loadEvent)
</script>

<template>
  <main>
    <section class="hero event-hero">
      <div class="hero-content">
        <p class="eyebrow">{{ event.statusText }}</p>
        <h1>{{ event.title }}</h1>
        <p class="hero-sub">{{ event.summary }}</p>
        <div class="event-meta">
          <span>{{ event.date }}</span>
          <span>{{ event.location }}</span>
        </div>
        <p v-if="apiLoading" class="loading-note">正在读取活动配置...</p>
      </div>
    </section>

    <div class="event-status-rail" id="event-status-rail" v-if="event.stages?.length">
      <div v-for="stage in event.stages" :key="stage.key" class="status-step" :class="{ done: stage.done, active: stage.key === activeStage?.key }">
        <div class="status-dot"></div>
        <span>{{ stage.label }}</span>
      </div>
    </div>

    <section class="section" v-if="event.tracks?.length">
      <div class="section-copy">
        <p class="eyebrow">TRACKS</p>
        <h2>赛道</h2>
      </div>
      <div class="card-grid">
        <article v-for="track in event.tracks" :key="track.id || track.name" class="track-card">
          <h3>{{ track.name }}</h3>
          <p>{{ track.description }}</p>
        </article>
      </div>
    </section>

    <section class="section" id="registration" v-if="event.formFields?.length">
      <div class="section-copy">
        <p class="eyebrow">REGISTER</p>
        <h2>报名</h2>
        <p>填写以下信息完成报名。</p>
      </div>
      <form class="registration-form" @submit.prevent="handleRegister">
        <div v-for="field in event.formFields" :key="field.key" class="form-field">
          <label>
            {{ field.label }}
            <span v-if="field.required" class="required">*</span>
          </label>
          <input v-if="field.type === 'text' || field.type === 'url'" v-model="form[field.key]" :type="field.type" :placeholder="field.placeholder" :required="field.required" />
          <input v-else-if="field.type === 'date'" v-model="form[field.key]" type="date" :required="field.required" />
          <input v-else-if="field.type === 'file'" type="file" :required="field.required" />
          <textarea v-else-if="field.type === 'textarea'" v-model="form[field.key]" :placeholder="field.placeholder" :required="field.required" rows="3"></textarea>
          <select v-else-if="field.type === 'select'" v-model="form[field.key]" :required="field.required">
            <option value="">请选择</option>
            <option v-for="opt in field.options" :key="opt" :value="opt">{{ opt }}</option>
          </select>
          <div v-else-if="field.type === 'radio'" class="radio-group">
            <label v-for="opt in field.options" :key="opt">
              <input type="radio" v-model="form[field.key]" :value="opt" /> {{ opt }}
            </label>
          </div>
          <div v-else-if="field.type === 'checkbox'" class="checkbox-group">
            <label v-for="opt in field.options" :key="opt">
              <input type="checkbox" v-model="form[field.key]" :value="opt" /> {{ opt }}
            </label>
          </div>
        </div>
        <button class="primary-button" type="submit">提交报名</button>
        <p :style="{ color: formMessageType === 'error' ? 'var(--red)' : formMessageType === 'success' ? 'var(--green)' : 'var(--muted)' }">{{ formMessage }}</p>
      </form>
    </section>

    <section class="section" id="requirements" v-if="event.submissionRequirements?.length">
      <div class="section-copy">
        <p class="eyebrow">REQUIREMENTS</p>
        <h2>提交要求</h2>
      </div>
      <ul class="requirements-list">
        <li v-for="req in event.submissionRequirements" :key="req">{{ req }}</li>
      </ul>
    </section>
  </main>
</template>

<style scoped>
.event-hero { padding: 120px 20px 60px; text-align: center; }
.event-meta { display: flex; gap: 24px; justify-content: center; color: var(--muted); margin-top: 16px; flex-wrap: wrap; }
.loading-note { margin-top: 14px; color: var(--heat); font-weight: 700; }
.event-status-rail { display: flex; justify-content: center; gap: 32px; padding: 24px 20px; border-bottom: 1px solid var(--line); flex-wrap: wrap; }
.status-step { display: flex; align-items: center; gap: 8px; color: var(--muted); }
.status-step.done { color: var(--green); }
.status-step.active { color: var(--heat); font-weight: 700; }
.status-dot { width: 10px; height: 10px; border-radius: 50%; background: var(--line); }
.status-step.done .status-dot { background: var(--green); }
.status-step.active .status-dot { background: var(--heat); }
.track-card { border: 1px solid var(--line); border-radius: var(--radius); padding: 24px; }
.track-card h3 { margin: 0 0 8px; }
.registration-form { max-width: 560px; display: grid; gap: 16px; }
.form-field label { display: block; font-weight: 700; margin-bottom: 6px; font-size: .85rem; }
.form-field input, .form-field textarea, .form-field select { width: 100%; min-height: 44px; border: 1px solid var(--line); border-radius: var(--radius); padding: 0 12px; background: var(--paper); font: inherit; }
.form-field textarea { padding: 10px 12px; resize: vertical; }
.radio-group, .checkbox-group { display: flex; gap: 16px; flex-wrap: wrap; }
.radio-group label, .checkbox-group label { display: flex; align-items: center; gap: 6px; font-weight: 400; }
.radio-group input, .checkbox-group input { width: auto; min-height: auto; }
.requirements-list { list-style: none; padding: 0; display: grid; gap: 12px; }
.requirements-list li { padding: 16px 20px; border: 1px solid var(--line); border-radius: var(--radius); }
.required { color: var(--red); }
</style>
