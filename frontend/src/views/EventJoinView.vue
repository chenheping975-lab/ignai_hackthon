<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { hackathonEvents } from '../data/mock'
import { useAuthStore } from '../stores/auth'
import { EventApi, RegistrationApi } from '../api'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const event = ref(normalizeMockEvent(hackathonEvents.find(e => e.id === route.params.id) || hackathonEvents[0]))
const form = ref({})
const formMessage = ref('')
const formMessageType = ref('muted')
const apiLoading = ref(false)
const submitting = ref(false)

const loginRedirect = computed(() => `/event/${route.params.id}/join`)

function normalizeMockEvent(mockEvent) {
  return {
    ...mockEvent,
    backendId: Number.isFinite(Number(mockEvent.id)) ? Number(mockEvent.id) : null,
    formFields: mockEvent.formFields || [],
    tracks: mockEvent.tracks || [],
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
    registrationOpen: data.registrationOpen,
    statusText: statusText(data),
    date: buildDateText(data),
    location: data.location || '地点待公布',
    summary: data.description || data.subtitle || '活动介绍待补充。',
    tracks: (data.tracks || []).map(track => ({
      id: track.id,
      name: track.name,
      description: track.description,
    })),
    formFields: (data.registrationFields || [])
      .filter(field => field.enabled !== false)
      .map(normalizeApiField),
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

async function handleSubmit() {
  formMessage.value = ''
  if (!auth.isLoggedIn) {
    formMessage.value = '请先登录再提交报名'
    formMessageType.value = 'error'
    return
  }
  if (!event.value.backendId) {
    formMessage.value = '当前活动还没有绑定后端活动 ID，暂不能提交报名'
    formMessageType.value = 'error'
    return
  }
  submitting.value = true
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
    formMessage.value = '报名提交成功！即将跳转到「我的报名」...'
    formMessageType.value = 'success'
    setTimeout(() => router.push('/my/registrations'), 900)
  } catch (e) {
    formMessage.value = e.message
    formMessageType.value = 'error'
  } finally {
    submitting.value = false
  }
}

onMounted(loadEvent)
watch(() => route.params.id, loadEvent)
</script>

<template>
  <main class="join-page">
    <!-- 面包屑 -->
    <nav class="breadcrumb">
      <router-link to="/events">活动列表</router-link>
      <span aria-hidden="true">/</span>
      <router-link :to="`/event/${event.id}`">{{ event.title }}</router-link>
      <span aria-hidden="true">/</span>
      <span class="current">加入比赛</span>
    </nav>

    <!-- 未登录提示 -->
    <div v-if="!auth.isLoggedIn" class="login-prompt">
      <p>加入比赛需要先登录。</p>
      <div class="login-prompt-actions">
        <router-link class="primary-button" :to="{ name: 'login', query: { redirect: loginRedirect } }">去登录</router-link>
        <router-link class="outline-button" to="/register">没有账号？立即注册</router-link>
      </div>
    </div>

    <div class="join-layout">
      <!-- 左栏：活动摘要 -->
      <aside class="join-aside">
        <p class="eyebrow">{{ event.statusText }}</p>
        <h2>{{ event.title }}</h2>
        <p class="aside-summary">{{ event.summary }}</p>
        <div class="aside-meta">
          <div><span>时间</span><strong>{{ event.date }}</strong></div>
          <div><span>地点</span><strong>{{ event.location }}</strong></div>
        </div>
        <div class="aside-tracks" v-if="event.tracks?.length">
          <h3>赛道</h3>
          <ul>
            <li v-for="track in event.tracks" :key="track.id || track.name">
              <strong>{{ track.name }}</strong>
              <p>{{ track.description }}</p>
            </li>
          </ul>
        </div>
      </aside>

      <!-- 右栏：报名表单 -->
      <section class="join-main">
        <header class="join-header">
          <p class="eyebrow">REGISTRATION</p>
          <h1>填写报名信息</h1>
          <p class="join-sub">提交后可在「我的报名」中查看审核状态。</p>
        </header>

        <p v-if="apiLoading" class="loading-note">正在读取活动配置...</p>

        <form class="registration-form" @submit.prevent="handleSubmit" v-if="event.formFields?.length">
          <div v-for="field in event.formFields" :key="field.key" class="form-field">
            <label>
              {{ field.label }}
              <span v-if="field.required" class="required">*</span>
            </label>
            <input v-if="field.type === 'text' || field.type === 'url'" v-model="form[field.key]" :type="field.type" :placeholder="field.placeholder" :required="field.required" :disabled="!auth.isLoggedIn" />
            <input v-else-if="field.type === 'date'" v-model="form[field.key]" type="date" :required="field.required" :disabled="!auth.isLoggedIn" />
            <input v-else-if="field.type === 'file'" type="file" :required="field.required" :disabled="!auth.isLoggedIn" />
            <textarea v-else-if="field.type === 'textarea'" v-model="form[field.key]" :placeholder="field.placeholder" :required="field.required" rows="3" :disabled="!auth.isLoggedIn"></textarea>
            <select v-else-if="field.type === 'select'" v-model="form[field.key]" :required="field.required" :disabled="!auth.isLoggedIn">
              <option value="">请选择</option>
              <option v-for="opt in field.options" :key="opt" :value="opt">{{ opt }}</option>
            </select>
            <div v-else-if="field.type === 'radio'" class="radio-group">
              <label v-for="opt in field.options" :key="opt">
                <input type="radio" v-model="form[field.key]" :value="opt" :disabled="!auth.isLoggedIn" /> {{ opt }}
              </label>
            </div>
            <div v-else-if="field.type === 'checkbox'" class="checkbox-group">
              <label v-for="opt in field.options" :key="opt">
                <input type="checkbox" v-model="form[field.key]" :value="opt" :disabled="!auth.isLoggedIn" /> {{ opt }}
              </label>
            </div>
          </div>
          <button class="primary-button cta-submit" type="submit" :disabled="submitting || !auth.isLoggedIn">
            {{ submitting ? '提交中...' : '提交报名' }}
          </button>
          <p class="form-message" :class="formMessageType">{{ formMessage }}</p>
        </form>

        <div v-else-if="!apiLoading" class="no-form">
          <p>当前活动没有配置报名字段。</p>
          <router-link class="outline-button" :to="`/event/${event.id}`">← 返回活动详情</router-link>
        </div>
      </section>
    </div>
  </main>
</template>

<style scoped>
.join-page { max-width: 1200px; margin: 0 auto; padding: 40px 20px 80px; }

.breadcrumb {
  display: flex; gap: 8px; flex-wrap: wrap;
  font-size: .82rem; color: var(--muted);
  margin-bottom: 28px;
}
.breadcrumb a { color: var(--muted); text-decoration: none; }
.breadcrumb a:hover { color: var(--heat); }
.breadcrumb .current { color: var(--faint); font-weight: 700; }

.login-prompt {
  border: 1px dashed var(--heat);
  background: rgba(255, 122, 0, .06);
  border-radius: var(--radius);
  padding: 24px 28px;
  margin-bottom: 32px;
}
.login-prompt p { margin: 0 0 14px; color: var(--heat); font-weight: 700; }
.login-prompt-actions { display: flex; gap: 12px; flex-wrap: wrap; }

.join-layout {
  display: grid;
  grid-template-columns: 1fr 1.4fr;
  gap: 40px;
  align-items: start;
}
@media (max-width: 860px) {
  .join-layout { grid-template-columns: 1fr; gap: 24px; }
}

.join-aside {
  border: 1px solid var(--line);
  border-radius: var(--radius);
  padding: 28px;
  background: var(--paper);
  position: sticky;
  top: 20px;
}
@media (max-width: 860px) {
  .join-aside { position: static; }
}
.join-aside h2 { margin: 4px 0 12px; font-size: 1.5rem; line-height: 1.2; }
.aside-summary { color: var(--muted); line-height: 1.7; margin: 0 0 20px; }
.aside-meta {
  display: grid; gap: 12px;
  padding: 16px 0;
  border-top: 1px solid var(--line);
  border-bottom: 1px solid var(--line);
  margin-bottom: 20px;
}
.aside-meta div { display: flex; justify-content: space-between; gap: 12px; }
.aside-meta span { color: var(--faint); font-size: .78rem; text-transform: uppercase; letter-spacing: .08em; }
.aside-meta strong { color: var(--text); font-weight: 700; }
.aside-tracks h3 { margin: 0 0 12px; font-size: 1rem; }
.aside-tracks ul { list-style: none; padding: 0; margin: 0; display: grid; gap: 12px; }
.aside-tracks li { padding: 12px 0; border-bottom: 1px dashed var(--line); }
.aside-tracks li:last-child { border-bottom: none; }
.aside-tracks strong { display: block; margin-bottom: 4px; }
.aside-tracks p { margin: 0; color: var(--muted); font-size: .85rem; line-height: 1.6; }

.join-main { min-width: 0; }
.join-header h1 { margin: 6px 0 8px; font-size: clamp(1.8rem, 4vw, 2.6rem); line-height: 1.1; }
.join-sub { color: var(--muted); margin: 0 0 24px; }
.loading-note { color: var(--heat); font-weight: 700; }

.registration-form { display: grid; gap: 18px; }
.form-field label {
  display: block;
  font-weight: 700; font-size: .85rem;
  margin-bottom: 6px;
}
.form-field input,
.form-field textarea,
.form-field select {
  width: 100%;
  min-height: 44px;
  border: 1px solid var(--line);
  border-radius: var(--radius);
  padding: 0 12px;
  background: var(--paper);
  font: inherit;
}
.form-field textarea { padding: 10px 12px; resize: vertical; }
.form-field input:disabled,
.form-field textarea:disabled,
.form-field select:disabled { opacity: .55; cursor: not-allowed; }
.radio-group, .checkbox-group { display: flex; gap: 16px; flex-wrap: wrap; }
.radio-group label, .checkbox-group label {
  display: flex; align-items: center; gap: 6px;
  font-weight: 400; font-size: .95rem;
}
.radio-group input, .checkbox-group input { width: auto; min-height: auto; }
.required { color: var(--red); }

.cta-submit {
  padding: 14px 28px;
  font-size: 1rem; font-weight: 900;
  letter-spacing: .04em;
  margin-top: 8px;
}
.cta-submit:disabled { opacity: .5; cursor: not-allowed; }

.form-message { margin: 4px 0 0; }
.form-message.muted { color: var(--muted); }
.form-message.error { color: var(--red); }
.form-message.success { color: var(--green); font-weight: 700; }

.no-form {
  border: 1px dashed var(--line);
  padding: 40px 20px;
  text-align: center;
  border-radius: var(--radius);
}
.no-form p { color: var(--muted); margin: 0 0 16px; }
</style>
