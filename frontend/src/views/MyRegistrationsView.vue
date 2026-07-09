<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { ProjectApi, RegistrationApi } from '../api'

const router = useRouter()
const auth = useAuthStore()

const registrations = ref([])
const project = ref(null)
const loading = ref(false)
const savingProject = ref(false)
const submittingProject = ref(false)
const errorMessage = ref('')
const projectMessage = ref('')
const projectMessageType = ref('muted')
const projectForm = ref({
  title: '',
  tagline: '',
  description: '',
  demoUrl: '',
  codeUrl: '',
  trackId: '',
})

const statusMeta = {
  pending: { label: '审核中', tone: 'pending' },
  approved: { label: '已通过', tone: 'approved' },
  rejected: { label: '未通过', tone: 'rejected' },
  draft: { label: '草稿', tone: 'pending' },
}

function resolveStatus(item) {
  const raw = String(item.status || item.reviewStatus || 'pending').toLowerCase()
  return statusMeta[raw] || { label: item.status || '审核中', tone: 'pending' }
}

function projectStatusText(status) {
  const map = {
    draft: '草稿',
    submitted: '已提交',
    locked: '已锁定',
    published: '已公开',
    archived: '已归档',
  }
  return map[status] || status || '未创建'
}

function fillProjectForm(data) {
  projectForm.value = {
    title: data?.title || '',
    tagline: data?.tagline || '',
    description: data?.description || '',
    demoUrl: data?.demoUrl || '',
    codeUrl: data?.codeUrl || '',
    trackId: data?.trackId ? String(data.trackId) : '',
  }
}

function applyRegistrationDefaults() {
  if (projectForm.value.trackId) return
  const firstWithTrack = registrations.value.find(item => item.trackId)
  if (firstWithTrack?.trackId) {
    projectForm.value.trackId = String(firstWithTrack.trackId)
  }
}

async function loadMyData() {
  loading.value = true
  errorMessage.value = ''
  try {
    const regData = await RegistrationApi.mine()
    registrations.value = Array.isArray(regData) ? regData : (regData?.list || [])

    const projectData = await ProjectApi.mine()
    project.value = projectData || null
    fillProjectForm(project.value)
    applyRegistrationDefaults()
  } catch (e) {
    errorMessage.value = e.message
  } finally {
    loading.value = false
  }
}

function buildProjectPayload() {
  const trackId = projectForm.value.trackId ? Number(projectForm.value.trackId) : null
  return {
    title: projectForm.value.title.trim(),
    tagline: projectForm.value.tagline.trim(),
    description: projectForm.value.description.trim(),
    demoUrl: projectForm.value.demoUrl.trim(),
    codeUrl: projectForm.value.codeUrl.trim(),
    trackId,
  }
}

async function saveProject() {
  projectMessage.value = ''
  if (!projectForm.value.title.trim()) {
    projectMessage.value = '请先填写作品标题'
    projectMessageType.value = 'error'
    return
  }

  savingProject.value = true
  try {
    const payload = buildProjectPayload()
    if (project.value?.id) {
      await ProjectApi.update(project.value.id, payload)
      project.value = { ...project.value, ...payload }
    } else {
      project.value = await ProjectApi.create(payload)
      fillProjectForm(project.value)
    }
    projectMessage.value = '作品信息已保存'
    projectMessageType.value = 'success'
  } catch (e) {
    projectMessage.value = e.message
    projectMessageType.value = 'error'
  } finally {
    savingProject.value = false
  }
}

async function submitProject() {
  projectMessage.value = ''
  if (!project.value?.id) {
    projectMessage.value = '请先保存作品，再正式提交'
    projectMessageType.value = 'error'
    return
  }

  submittingProject.value = true
  try {
    await ProjectApi.submit(project.value.id)
    project.value = { ...project.value, status: 'submitted' }
    projectMessage.value = '作品已正式提交'
    projectMessageType.value = 'success'
  } catch (e) {
    projectMessage.value = e.message
    projectMessageType.value = 'error'
  } finally {
    submittingProject.value = false
  }
}

onMounted(async () => {
  if (!auth.isLoggedIn) {
    router.push({ name: 'login', query: { redirect: '/my/registrations' } })
    return
  }
  await loadMyData()
})
</script>

<template>
  <main class="my-page">
    <header class="my-header">
      <p class="eyebrow">MY REGISTRATIONS</p>
      <h1>我的报名</h1>
      <p>查看报名审核状态，并在这里保存和提交你的参赛作品。</p>
    </header>

    <p v-if="loading" class="loading-note">正在读取报名记录...</p>

    <div v-else-if="errorMessage" class="api-error-card">
      <h3>暂未拿到后端报名数据</h3>
      <p>接口提示：{{ errorMessage }}</p>
      <p class="hint">接口返回：{{ errorMessage }}</p>
      <router-link class="outline-button" to="/events">先去看看活动 →</router-link>
    </div>

    <div v-else-if="!registrations.length" class="empty-state">
      <h3>还没有报名记录</h3>
      <p>挑一场活动加入，提交后会在这里看到审核状态。</p>
      <router-link class="primary-button" to="/events">浏览活动</router-link>
    </div>

    <template v-else>
      <ul class="reg-list">
        <li v-for="item in registrations" :key="item.id" class="reg-card">
          <div class="reg-card-head">
            <span class="reg-status" :data-tone="resolveStatus(item).tone">{{ resolveStatus(item).label }}</span>
            <span class="reg-type">{{ item.registrationType === 'team' ? '团队' : '个人' }}</span>
          </div>
          <h3>{{ item.eventTitle || `活动 #${item.eventId}` }}</h3>
          <div class="reg-card-meta">
            <span v-if="item.contactName">联系人：{{ item.contactName }}</span>
            <span v-if="item.teamName">队伍：{{ item.teamName }}</span>
            <span v-if="item.trackId">赛道 ID：{{ item.trackId }}</span>
          </div>
          <p class="reg-submitted" v-if="item.createdAt">报名时间：{{ String(item.createdAt).slice(0, 16).replace('T', ' ') }}</p>
        </li>
      </ul>

      <section class="project-panel">
        <div class="project-panel-head">
          <div>
            <p class="eyebrow">PROJECT SUBMISSION</p>
            <h2>作品提交</h2>
            <p>先保存草稿，确认无误后再正式提交。</p>
          </div>
          <span class="project-status">{{ projectStatusText(project?.status) }}</span>
        </div>

        <form class="project-form" @submit.prevent="saveProject">
          <label>作品标题
            <input v-model="projectForm.title" type="text" placeholder="例如：Campus Agent Hub" required />
          </label>
          <label>一句话简介
            <input v-model="projectForm.tagline" type="text" placeholder="一句话说明你的作品价值" />
          </label>
          <label>赛道 ID
            <input v-model="projectForm.trackId" type="number" min="1" placeholder="可从报名记录里查看" />
          </label>
          <label>Demo 链接
            <input v-model="projectForm.demoUrl" type="url" placeholder="https://..." />
          </label>
          <label>代码仓库
            <input v-model="projectForm.codeUrl" type="url" placeholder="https://github.com/..." />
          </label>
          <label class="wide">作品描述
            <textarea v-model="projectForm.description" rows="5" placeholder="说明你解决的问题、核心功能和当前完成度"></textarea>
          </label>

          <div class="project-actions">
            <button class="primary-button" type="submit" :disabled="savingProject">
              {{ savingProject ? '保存中...' : project?.id ? '保存修改' : '创建作品' }}
            </button>
            <button
              class="outline-button"
              type="button"
              :disabled="!project?.id || submittingProject || project?.status === 'submitted'"
              @click="submitProject"
            >
              {{ submittingProject ? '提交中...' : project?.status === 'submitted' ? '已正式提交' : '正式提交' }}
            </button>
            <router-link v-if="project?.id" class="outline-button" to="/projects">查看作品展示</router-link>
            <p class="project-message" :class="projectMessageType">{{ projectMessage }}</p>
          </div>
        </form>
      </section>
    </template>
  </main>
</template>

<style scoped>
.my-page { max-width: 880px; margin: 0 auto; padding: 60px 20px 80px; }
.my-header { margin-bottom: 32px; }
.my-header h1 { margin: 6px 0 8px; font-size: clamp(2rem, 5vw, 3rem); line-height: 1.1; }
.my-header p { color: var(--muted); margin: 0; }

.loading-note { color: var(--heat); font-weight: 700; }

.api-error-card, .empty-state {
  border: 1px dashed var(--line);
  border-radius: var(--radius);
  padding: 36px 28px;
  text-align: center;
  background: var(--paper);
}
.api-error-card h3, .empty-state h3 { margin: 0 0 10px; }
.api-error-card p, .empty-state p { color: var(--muted); margin: 0 0 16px; line-height: 1.7; }
.api-error-card .hint { font-size: .9rem; }
.api-error-card code {
  background: var(--line);
  padding: 2px 6px;
  border-radius: 4px;
  font-size: .85em;
}

.reg-list { list-style: none; padding: 0; margin: 0; display: grid; gap: 16px; }
.reg-card {
  border: 1px solid var(--line);
  border-radius: var(--radius);
  padding: 20px 24px;
  background: var(--paper);
}
.reg-card-head {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 10px;
}
.reg-status {
  padding: 4px 12px;
  border-radius: 999px;
  font-size: .75rem;
  font-weight: 900;
  letter-spacing: .08em;
  text-transform: uppercase;
}
.reg-status[data-tone="pending"] { background: rgba(255, 122, 0, .14); color: var(--heat); }
.reg-status[data-tone="approved"] { background: rgba(34, 197, 94, .14); color: var(--green); }
.reg-status[data-tone="rejected"] { background: rgba(239, 68, 68, .14); color: var(--red); }
.reg-type { color: var(--muted); font-size: .82rem; }

.reg-card h3 { margin: 0 0 12px; font-size: 1.15rem; }
.reg-card-meta {
  display: flex; gap: 16px; flex-wrap: wrap;
  color: var(--faint); font-size: .85rem;
}
.reg-submitted { margin: 10px 0 0; color: var(--muted); font-size: .8rem; }

.project-panel {
  margin-top: 28px;
  border: 1px solid var(--line);
  border-radius: var(--radius);
  padding: 24px;
  background: var(--paper);
}
.project-panel-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 20px;
}
.project-panel-head h2 { margin: 6px 0 8px; font-size: 1.5rem; }
.project-panel-head p { margin: 0; color: var(--muted); }
.project-status {
  flex: 0 0 auto;
  padding: 5px 12px;
  border-radius: 999px;
  background: rgba(255, 122, 0, .14);
  color: var(--heat);
  font-size: .78rem;
  font-weight: 900;
}
.project-form {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}
.project-form label {
  display: grid;
  gap: 8px;
  color: var(--faint);
  font-size: .78rem;
  font-weight: 900;
  letter-spacing: .08em;
  text-transform: uppercase;
}
.project-form .wide { grid-column: 1 / -1; }
.project-form input,
.project-form textarea {
  width: 100%;
  min-height: 44px;
  border: 1px solid var(--line);
  border-radius: var(--radius);
  padding: 0 12px;
  background: rgba(255,255,255,.72);
  color: var(--ink);
  font: inherit;
}
.project-form textarea { padding: 11px 12px; resize: vertical; }
.project-actions {
  grid-column: 1 / -1;
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 4px;
}
.project-actions button:disabled { opacity: .55; cursor: not-allowed; }
.project-message { margin: 0; color: var(--muted); }
.project-message.error { color: var(--red); }
.project-message.success { color: var(--green); font-weight: 700; }
@media (max-width: 720px) {
  .project-panel-head { display: grid; }
  .project-form { grid-template-columns: 1fr; }
}
</style>
