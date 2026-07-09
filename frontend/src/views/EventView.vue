<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { hackathonEvents } from '../data/mock'
import { EventApi } from '../api'

const route = useRoute()

const event = ref(normalizeMockEvent(hackathonEvents.find(e => e.id === route.params.id) || hackathonEvents[0]))
const apiLoading = ref(false)
const loadError = ref('')

const activeStage = computed(() => {
  const stages = event.value.stages || []
  return stages.find((stage, index) => stage.done && !stages[index + 1]?.done) || stages[0]
})

// 是否处于可报名状态
const canRegister = computed(() => {
  const status = event.value.status
  return status === 'registration' || status === 'published' || event.value.registrationOpen
})

function normalizeMockEvent(mockEvent) {
  return {
    ...mockEvent,
    backendId: Number.isFinite(Number(mockEvent.id)) ? Number(mockEvent.id) : null,
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
    stages: buildStages(data),
    submissionRequirements: [
      '项目标题与一句话简介',
      'Demo 链接或可运行 HTML',
      '代码仓库或说明文档',
      'PPT、HTML、MP3、图片等轻量附件',
      'MVP 阶段不接收视频上传',
    ],
  }
}

async function loadEvent() {
  const routeId = String(route.params.id)
  const fallback = hackathonEvents.find(e => e.id === routeId) || hackathonEvents[0]
  event.value = normalizeMockEvent(fallback)
  loadError.value = ''

  if (!/^\d+$/.test(routeId)) {
    return
  }

  apiLoading.value = true
  try {
    const data = await EventApi.detail(routeId)
    event.value = normalizeApiEvent(data)
  } catch (error) {
    loadError.value = error.message
  } finally {
    apiLoading.value = false
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

        <!-- 加入比赛主 CTA：决策点 -->
        <div class="event-primary-cta">
          <router-link
            v-if="canRegister"
            class="primary-button cta-large"
            :to="`/event/${event.id}/join`"
          >加入比赛 →</router-link>
          <p v-else class="cta-disabled">该活动当前不在报名期</p>
          <router-link class="outline-button" to="/events">返回活动列表</router-link>
        </div>
        <p v-if="loadError" class="api-error">⚠ {{ loadError }}（已使用本地预览数据）</p>
      </div>
    </section>

    <div class="event-status-rail" v-if="event.stages?.length">
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

    <section class="section" id="requirements" v-if="event.submissionRequirements?.length">
      <div class="section-copy">
        <p class="eyebrow">REQUIREMENTS</p>
        <h2>提交要求</h2>
        <p>决定参赛前先了解一下作品要做成什么样。</p>
      </div>
      <ul class="requirements-list">
        <li v-for="req in event.submissionRequirements" :key="req">{{ req }}</li>
      </ul>

      <!-- 文末次级 CTA：再次给出报名入口 -->
      <div class="section-cta" v-if="canRegister">
        <router-link class="primary-button cta-large" :to="`/event/${event.id}/join`">准备就绪，加入比赛</router-link>
      </div>
    </section>
  </main>
</template>

<style scoped>
.event-hero { padding: 120px 20px 60px; text-align: center; }
.event-meta { display: flex; gap: 24px; justify-content: center; color: var(--muted); margin-top: 16px; flex-wrap: wrap; }
.loading-note { margin-top: 14px; color: var(--heat); font-weight: 700; }
.api-error { margin-top: 14px; color: var(--red); font-size: .85rem; }

.event-primary-cta {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
  margin-top: 28px;
}
.cta-large { padding: 14px 28px; font-size: 1rem; font-weight: 900; letter-spacing: .04em; }
.cta-disabled { color: var(--muted); font-style: italic; }

.event-status-rail { display: flex; justify-content: center; gap: 32px; padding: 24px 20px; border-bottom: 1px solid var(--line); flex-wrap: wrap; }
.status-step { display: flex; align-items: center; gap: 8px; color: var(--muted); }
.status-step.done { color: var(--green); }
.status-step.active { color: var(--heat); font-weight: 700; }
.status-dot { width: 10px; height: 10px; border-radius: 50%; background: var(--line); }
.status-step.done .status-dot { background: var(--green); }
.status-step.active .status-dot { background: var(--heat); }
.track-card { border: 1px solid var(--line); border-radius: var(--radius); padding: 24px; }
.track-card h3 { margin: 0 0 8px; }
.requirements-list { list-style: none; padding: 0; display: grid; gap: 12px; }
.requirements-list li { padding: 16px 20px; border: 1px solid var(--line); border-radius: var(--radius); }
.section-cta { margin-top: 36px; text-align: center; }
</style>
