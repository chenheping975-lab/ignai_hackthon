<script setup>
import { ref, onMounted } from 'vue'
import { hackathonEvents } from '../data/mock'
import { EventApi } from '../api'

const events = ref(hackathonEvents)

onMounted(async () => {
  try {
    const res = await EventApi.list(1, 6)
    if (res?.list?.length) events.value = res.list
  } catch { /* fallback to mock */ }
})
</script>

<template>
  <main>
    <!-- Hero -->
    <section class="hero" id="top">
      <div class="hero-content">
        <p class="eyebrow">IGNITE BEFORE AGI</p>
        <h1>连接创造者，<br />点燃可能性。</h1>
        <p class="hero-sub">IGNAI AI Skillathon 是面向高校、开发者和 AI 行动者的线下黑客松。</p>
        <div class="hero-actions">
          <a class="primary-button" href="#events">查看活动</a>
          <a class="outline-button" href="https://www.ignai.cn/" target="_blank" rel="noreferrer">访问官网</a>
        </div>
      </div>
    </section>

    <!-- Signal Strip -->
    <div class="signal-strip" aria-hidden="true">
      <div class="signal-track">
        <span>LOCAL ROOTS</span><span>GLOBAL SIGNAL</span><span>AGENT TOOLS</span>
        <span>EDUCATION</span><span>CONTENT WORKFLOW</span><span>NO VIDEO</span>
        <span>LOCAL ROOTS</span><span>GLOBAL SIGNAL</span><span>AGENT TOOLS</span>
        <span>EDUCATION</span><span>CONTENT WORKFLOW</span><span>NO VIDEO</span>
      </div>
    </div>

    <!-- Events -->
    <section class="section" id="events">
      <div class="section-copy">
        <p class="eyebrow">EVENTS</p>
        <h2>黑客松活动</h2>
        <p>选择一场活动，查看详情并报名。</p>
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

    <!-- Spirit -->
    <section class="section" id="spirit">
      <div class="section-copy">
        <p class="eyebrow">SPIRIT</p>
        <h2>社区精神</h2>
        <p>行动创造价值，人链接人。</p>
      </div>
      <div class="card-grid">
        <article class="spirit-card">
          <h3>真实问题</h3>
          <p>从身边的真实需求出发，不做空中楼阁。</p>
        </article>
        <article class="spirit-card">
          <h3>现场共创</h3>
          <p>两天一夜，和队友一起把想法推进到可展示。</p>
        </article>
        <article class="spirit-card">
          <h3>持续曝光</h3>
          <p>作品在平台上长期展示，获得持续关注和反馈。</p>
        </article>
      </div>
    </section>
  </main>
</template>
