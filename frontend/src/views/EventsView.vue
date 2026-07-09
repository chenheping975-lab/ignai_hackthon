<script setup>
import { ref, computed, onMounted } from 'vue'
import { hackathonEvents } from '../data/mock'
import { EventApi } from '../api'

const events = ref(hackathonEvents)
const page = ref(1)
const activeFilter = ref('all')

const filters = [
  { key: 'all', label: '全部' },
  { key: 'registration', label: '报名中' },
  { key: 'running', label: '进行中' },
  { key: 'archived', label: '已归档' },
]

const filteredEvents = computed(() => {
  if (activeFilter.value === 'all') return events.value
  return events.value.filter(e => (e.status || '').includes(activeFilter.value) || e.status === activeFilter.value)
})

onMounted(async () => {
  try {
    const res = await EventApi.list(page.value, 12)
    if (res?.list?.length) events.value = res.list
  } catch { /* fallback to mock */ }
})
</script>

<template>
  <main class="events-page">
    <section class="events-hero">
      <p class="eyebrow">ALL EVENTS</p>
      <h1>黑客松活动</h1>
      <p>浏览所有活动，挑一场感兴趣的查看详情或报名参加。</p>
    </section>

    <div class="events-filter">
      <button
        v-for="f in filters"
        :key="f.key"
        class="filter-pill"
        :class="{ active: activeFilter === f.key }"
        @click="activeFilter = f.key"
      >{{ f.label }}</button>
    </div>

    <section class="section">
      <div class="event-card-list" v-if="filteredEvents.length">
        <article v-for="evt in filteredEvents" :key="evt.id" class="event-card">
          <div class="event-card-status" :data-status="evt.status">{{ evt.statusText }}</div>
          <h3>{{ evt.title }}</h3>
          <p>{{ evt.summary }}</p>
          <div class="event-card-meta">
            <span>{{ evt.date }}</span>
            <span>{{ evt.location }}</span>
          </div>
          <div class="event-card-actions">
            <router-link class="outline-button" :to="`/event/${evt.id}`">查看活动</router-link>
            <router-link
              v-if="evt.status === 'registration' || evt.registrationOpen"
              class="primary-button"
              :to="`/event/${evt.id}/join`"
            >加入比赛</router-link>
          </div>
        </article>
      </div>
      <p v-else class="empty-state">这个分类下暂时没有活动。</p>
    </section>
  </main>
</template>

<style scoped>
.events-hero { padding: 100px 20px 32px; text-align: center; }
.events-hero h1 { margin: 8px 0 12px; font-size: clamp(2.4rem, 6vw, 4rem); line-height: 1.06; }
.events-hero p { color: var(--muted); max-width: 560px; margin: 0 auto; line-height: 1.7; }

.events-filter {
  display: flex; gap: 10px; flex-wrap: wrap;
  justify-content: center;
  padding: 0 20px 24px;
}
.filter-pill {
  padding: 8px 18px;
  border: 1px solid var(--line);
  border-radius: 999px;
  background: var(--paper);
  color: var(--muted);
  font: inherit;
  cursor: pointer;
  transition: all .2s;
}
.filter-pill:hover { border-color: var(--heat); color: var(--heat); }
.filter-pill.active {
  background: var(--heat);
  border-color: var(--heat);
  color: var(--paper);
}

.event-card-actions {
  display: flex; gap: 10px; flex-wrap: wrap;
  margin-top: 16px;
}
.event-card-actions .outline-button,
.event-card-actions .primary-button {
  padding: 8px 16px; font-size: .85rem;
}

.empty-state {
  text-align: center;
  color: var(--muted);
  padding: 60px 20px;
}
</style>
