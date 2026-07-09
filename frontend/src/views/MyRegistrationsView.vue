<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { RegistrationApi } from '../api'

const router = useRouter()
const auth = useAuthStore()

const registrations = ref([])
const loading = ref(false)
const errorMessage = ref('')

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

onMounted(async () => {
  if (!auth.isLoggedIn) {
    router.push({ name: 'login', query: { redirect: '/my/registrations' } })
    return
  }
  loading.value = true
  try {
    const data = await RegistrationApi.mine()
    registrations.value = Array.isArray(data) ? data : (data?.list || [])
  } catch (e) {
    // 后端 /registrations/my 尚未实现时的友好提示
    errorMessage.value = e.message
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <main class="my-page">
    <header class="my-header">
      <p class="eyebrow">MY REGISTRATIONS</p>
      <h1>我的报名</h1>
      <p>查看你提交过的所有活动报名记录和当前审核状态。</p>
    </header>

    <p v-if="loading" class="loading-note">正在读取报名记录...</p>

    <div v-else-if="errorMessage" class="api-error-card">
      <h3>暂未拿到后端报名数据</h3>
      <p>接口提示：{{ errorMessage }}</p>
      <p class="hint">后端 <code>GET /api/registrations/my</code> 还在建设中。一旦接口就绪，这里会自动列出你的报名记录。</p>
      <router-link class="outline-button" to="/events">先去看看活动 →</router-link>
    </div>

    <div v-else-if="!registrations.length" class="empty-state">
      <h3>还没有报名记录</h3>
      <p>挑一场活动加入，提交后会在这里看到审核状态。</p>
      <router-link class="primary-button" to="/events">浏览活动</router-link>
    </div>

    <ul v-else class="reg-list">
      <li v-for="item in registrations" :key="item.id" class="reg-card">
        <div class="reg-card-head">
          <span class="reg-status" :data-tone="resolveStatus(item).tone">{{ resolveStatus(item).label }}</span>
          <span class="reg-type">{{ item.registrationType === 'team' ? '团队' : '个人' }}</span>
        </div>
        <h3>{{ item.eventTitle || `活动 #${item.eventId}` }}</h3>
        <div class="reg-card-meta">
          <span v-if="item.contactName">联系人：{{ item.contactName }}</span>
          <span v-if="item.teamName">队伍：{{ item.teamName }}</span>
          <span v-if="item.trackName">赛道：{{ item.trackName }}</span>
        </div>
        <p class="reg-submitted" v-if="item.submittedAt">提交时间：{{ String(item.submittedAt).slice(0, 16).replace('T', ' ') }}</p>
      </li>
    </ul>
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
</style>
