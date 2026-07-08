<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeView = ref('dashboard')

const views = [
  { key: 'dashboard', label: '概览', icon: '📊' },
  { key: 'events', label: '活动管理', icon: '🎯' },
  { key: 'registrations', label: '报名管理', icon: '📋' },
  { key: 'projects', label: '项目看板', icon: '📁' },
  { key: 'mail', label: '邮件管理', icon: '✉️' },
]

// Mock data
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

const filteredRegs = computed(() => {
  if (regFilter.value === 'all') return registrations.value
  return registrations.value.filter(r => r.status === regFilter.value)
})

function selectReg(reg) { selectedReg.value = reg }
function reviewReg(id, status) {
  const reg = registrations.value.find(r => r.id === id)
  if (reg) reg.status = status
  selectedReg.value = null
}
</script>

<template>
  <div class="admin-shell">
    <!-- Sidebar -->
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

    <!-- Main Content -->
    <main class="admin-main">
      <!-- Dashboard -->
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

      <!-- Events -->
      <div v-if="activeView === 'events'" class="admin-view">
        <h1>活动管理</h1>
        <p class="muted">活动 CRUD 接口待后端实现后接入。</p>
      </div>

      <!-- Registrations -->
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

        <!-- Detail Drawer -->
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

      <!-- Projects -->
      <div v-if="activeView === 'projects'" class="admin-view">
        <h1>项目看板</h1>
        <p class="muted">项目提交接口待后端实现后接入。</p>
      </div>

      <!-- Mail -->
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
.metric-card { background: var(--panel, #121318); border: 1px solid var(--line, #2a2b30); border-radius: 12px; padding: 20px; text-align: center; }
.metric-value { font-size: 2rem; font-weight: 800; color: var(--heat, #ff7a18); }
.metric-label { color: var(--muted, #888); font-size: .82rem; margin-top: 4px; }
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
.danger-button { padding: 8px 20px; border: 1px solid #f87171; border-radius: 8px; background: transparent; color: #f87171; cursor: pointer; font: inherit; }
.muted { color: var(--muted, #888); }
@media (max-width: 768px) {
  .admin-shell { grid-template-columns: 1fr; }
  .admin-sidebar { position: static; height: auto; }
  .detail-drawer { width: 100%; }
}
</style>
