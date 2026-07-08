<script setup>
import { ref, computed, onMounted } from 'vue'
import { hackathonEvents } from '../data/mock'
import { EventApi } from '../api'

const events = ref(hackathonEvents)

onMounted(async () => {
  try {
    const res = await EventApi.list(1, 6)
    if (res?.list?.length) events.value = res.list
  } catch { /* fallback to mock */ }
})

// 首页只精选「报名开放中」的一场作为 Featured
const featured = computed(() => {
  const open = events.value.find(e => e.registrationOpen || e.status === 'registration' || e.status === 'running')
  return open || events.value[0]
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
          <router-link class="primary-button" to="/events">查看全部活动</router-link>
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

    <!-- Featured Event -->
    <section class="section" id="featured" v-if="featured">
      <div class="section-copy">
        <p class="eyebrow">FEATURED</p>
        <h2>当前精选活动</h2>
        <p>正在报名中，点进去查看赛道与提交要求。</p>
      </div>
      <router-link :to="`/event/${featured.id}`" class="featured-card">
        <div class="featured-card-status">{{ featured.statusText || '报名开放' }}</div>
        <h3>{{ featured.title }}</h3>
        <p>{{ featured.summary }}</p>
        <div class="featured-card-meta">
          <span>{{ featured.date }}</span>
          <span>{{ featured.location }}</span>
        </div>
        <span class="featured-card-cta">查看活动详情 →</span>
      </router-link>
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
      <div class="spirit-cta">
        <router-link class="outline-button" to="/events">查看全部活动</router-link>
      </div>
    </section>
  </main>
</template>

<style scoped>
.featured-card {
  display: grid;
  gap: 12px;
  padding: 28px;
  border: 1px solid var(--line);
  border-radius: var(--radius);
  background: var(--paper);
  text-decoration: none;
  color: inherit;
  transition: border-color .2s, transform .2s;
}
.featured-card:hover { border-color: var(--heat); transform: translateY(-2px); }
.featured-card-status {
  display: inline-block;
  width: max-content;
  padding: 4px 12px;
  border-radius: 999px;
  background: var(--heat);
  color: var(--paper);
  font-size: .72rem;
  font-weight: 900;
  letter-spacing: .12em;
  text-transform: uppercase;
}
.featured-card h3 { margin: 4px 0 0; font-size: 1.6rem; }
.featured-card p { margin: 0; color: var(--muted); line-height: 1.7; }
.featured-card-meta {
  display: flex; gap: 18px; flex-wrap: wrap;
  color: var(--faint); font-size: .85rem;
}
.featured-card-cta {
  margin-top: 8px;
  color: var(--heat);
  font-weight: 700;
  letter-spacing: .04em;
}
.spirit-cta { margin-top: 32px; text-align: center; }
</style>
