<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { EventApi, PublicApi } from '../api'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const event = ref(null)
const projects = ref([])
const errorMessage = ref('')
const likeMessage = ref('')
const likeLoadingId = ref('')

const tracks = computed(() => [...new Set(projects.value.map(p => p.track))])

const selectedTrack = ref('')
const sortBy = ref('votes')

const filteredProjects = computed(() => {
  let list = projects.value
  if (selectedTrack.value) list = list.filter(p => p.track === selectedTrack.value)
  if (sortBy.value === 'votes') list = [...list].sort((a, b) => b.votes - a.votes)
  else if (sortBy.value === 'latest') list = [...list].sort((a, b) => new Date(b.submittedAt) - new Date(a.submittedAt))
  else if (sortBy.value === 'featured') list = [...list].sort((a, b) => (b.featured ? 1 : 0) - (a.featured ? 1 : 0))
  return list
})

const likedProjects = ref(new Set(JSON.parse(localStorage.getItem('ignai_project_likes') || '[]')))

async function loadProjects() {
  errorMessage.value = ''
  try {
    const routeEventId = route.query.event ? String(route.query.event) : ''
    const currentEvent = /^\d+$/.test(routeEventId) ? await EventApi.detail(routeEventId) : await EventApi.current()
    event.value = currentEvent
    const list = await PublicApi.projects({ status: 'published' })
    projects.value = (Array.isArray(list) ? list : [])
      .filter(p => !currentEvent?.id || Number(p.eventId) === Number(currentEvent.id))
      .map(p => normalizeProject(p, currentEvent))
  } catch (e) {
    errorMessage.value = e.message
    projects.value = []
  }
}

function normalizeProject(project, currentEvent) {
  const track = (currentEvent?.tracks || []).find(t => Number(t.id) === Number(project.trackId))
  return {
    ...project,
    id: String(project.id),
    track: track?.name || project.trackName || '未分赛道',
    image: project.coverUrl || '/img/ignai-business-card-mockup.webp',
    files: project.files || [],
    votes: project.votes || 0,
    featured: project.featured || false,
    submittedAt: project.submittedAt || project.createdAt || '',
  }
}

async function toggleLike(id) {
  const projectId = String(id)
  errorMessage.value = ''
  likeMessage.value = ''

  if (likeLoadingId.value === projectId) {
    return
  }

  if (!auth.isLoggedIn) {
    likeMessage.value = '请先登录后点赞'
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }

  likeLoadingId.value = projectId
  try {
    const res = await PublicApi.vote(projectId)
    likedProjects.value = new Set([...likedProjects.value, projectId])

    const project = projects.value.find(p => p.id === projectId)
    if (project) {
      if (typeof res?.votes === 'number') project.votes = res.votes
      else project.votes += 1
    }

    likeMessage.value = res?.duplicated ? '你已经点赞过这个作品' : '点赞成功'
    localStorage.setItem('ignai_project_likes', JSON.stringify([...likedProjects.value]))
  } catch (e) {
    errorMessage.value = e.message || '点赞失败，请稍后再试'
  } finally {
    likeLoadingId.value = ''
  }
}

onMounted(loadProjects)
watch(() => route.query.event, loadProjects)
</script>

<template>
  <main class="projects-page">
    <section class="section">
      <div class="section-copy">
        <p class="eyebrow">PROJECTS</p>
        <h2>{{ event?.title || '作品展示' }}</h2>
        <p>浏览参赛作品，为你喜欢的项目点赞。</p>
        <p v-if="errorMessage" class="api-error">{{ errorMessage }}</p>
        <p v-if="likeMessage" class="like-note">{{ likeMessage }}</p>
      </div>

      <!-- Filters -->
      <div class="project-filters">
        <div class="track-filters">
          <button :class="{ active: !selectedTrack }" @click="selectedTrack = ''">全部</button>
          <button v-for="track in tracks" :key="track" :class="{ active: selectedTrack === track }" @click="selectedTrack = track">{{ track }}</button>
        </div>
        <div class="sort-filters">
          <button :class="{ active: sortBy === 'votes' }" @click="sortBy = 'votes'">票数</button>
          <button :class="{ active: sortBy === 'featured' }" @click="sortBy = 'featured'">精选</button>
          <button :class="{ active: sortBy === 'latest' }" @click="sortBy = 'latest'">最新</button>
        </div>
      </div>

      <!-- Project Cards -->
      <div v-if="filteredProjects.length" class="project-grid">
        <article v-for="project in filteredProjects" :key="project.id" class="project-card">
          <div class="project-cover">
            <img :src="project.image" :alt="project.title" />
            <span v-if="project.featured" class="featured-badge">精选</span>
          </div>
          <div class="project-info">
            <div class="project-track">{{ project.track }}</div>
            <h3>{{ project.title }}</h3>
            <p>{{ project.tagline }}</p>
            <div class="project-meta">
              <div class="project-files">
                <span v-for="file in project.files" :key="file" class="file-tag">{{ file }}</span>
              </div>
              <button
                class="like-button"
                :class="{ liked: likedProjects.has(project.id) }"
                :disabled="likeLoadingId === project.id"
                @click="toggleLike(project.id)"
              >
                {{ likeLoadingId === project.id ? '点赞中' : likedProjects.has(project.id) ? '已赞' : auth.isLoggedIn ? '点赞' : '登录点赞' }} {{ project.votes }}
              </button>
            </div>
          </div>
        </article>
      </div>
      <p v-else class="empty-state">当前活动还没有公开作品。</p>
    </section>
  </main>
</template>

<style scoped>
.project-filters { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 12px; }
.track-filters, .sort-filters { display: flex; gap: 8px; }
.track-filters button, .sort-filters button { padding: 6px 14px; border: 1px solid var(--line); border-radius: 20px; background: transparent; color: var(--muted); cursor: pointer; font: inherit; font-size: .82rem; }
.track-filters button.active, .sort-filters button.active { border-color: var(--heat); color: var(--heat); }
.project-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 24px; }
.project-card { border: 1px solid var(--line); border-radius: var(--radius); overflow: hidden; }
.project-cover { position: relative; }
.project-cover img { width: 100%; height: 180px; object-fit: cover; }
.featured-badge { position: absolute; top: 10px; right: 10px; background: var(--heat); color: #fff; padding: 2px 10px; border-radius: 12px; font-size: .75rem; font-weight: 700; }
.project-info { padding: 16px; }
.project-track { color: var(--signal); font-size: .78rem; font-weight: 700; text-transform: uppercase; letter-spacing: .06em; }
.project-info h3 { margin: 6px 0; }
.project-info p { color: var(--muted); font-size: .9rem; }
.project-meta { display: flex; justify-content: space-between; align-items: center; margin-top: 12px; }
.project-files { display: flex; gap: 6px; }
.file-tag { padding: 2px 8px; border: 1px solid var(--line); border-radius: 4px; font-size: .72rem; color: var(--muted); }
.like-button { padding: 6px 14px; border: 1px solid var(--line); border-radius: 20px; background: transparent; cursor: pointer; font: inherit; font-size: .82rem; }
.like-button.liked { border-color: var(--heat); color: var(--heat); }
.like-button:disabled { cursor: not-allowed; opacity: .7; }
.api-error, .empty-state { color: var(--muted); }
.like-note { color: var(--heat); font-weight: 700; }
.empty-state { text-align: center; padding: 40px 20px; }
</style>
