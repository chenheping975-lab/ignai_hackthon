<script setup>
import { ref, onMounted } from 'vue'
import { hackathonEvents } from '../data/mock'
import { EventApi } from '../api'

const events = ref(hackathonEvents)
const page = ref(1)

onMounted(async () => {
  try {
    const res = await EventApi.list(page.value, 6)
    if (res?.list?.length) events.value = res.list
  } catch { /* fallback to mock */ }
})
</script>

<template>
  <main class="events-page">
    <section class="section">
      <div class="section-copy">
        <p class="eyebrow">ALL EVENTS</p>
        <h2>活动列表</h2>
        <p>浏览所有黑客松活动，选择感兴趣的报名参加。</p>
      </div>
      <div class="event-card-list">
        <router-link v-for="evt in events" :key="evt.id" :to="`/event/${evt.id}`" class="event-card">
          <div class="event-card-status" :data-status="evt.status">{{ evt.statusText }}</div>
          <h3>{{ evt.title }}</h3>
          <p>{{ evt.summary }}</p>
          <div class="event-card-meta">
            <span>{{ evt.date }}</span>
            <span>{{ evt.location }}</span>
          </div>
        </router-link>
      </div>
    </section>
  </main>
</template>
