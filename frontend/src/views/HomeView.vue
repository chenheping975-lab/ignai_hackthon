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

const featured = computed(() => {
  const open = events.value.find(e => e.registrationOpen || e.status === 'registration' || e.status === 'running')
  return open || events.value[0]
})

const schedule = [
  { phase: '01', label: '报名截止', desc: '提交报名问卷，等待审核组队通知。', deadline: '2026.07.18 23:00' },
  { phase: '02', label: '现场开场', desc: '赛题公布、规则说明、资源介绍、破冰组队。', deadline: '2026.07.20 09:00' },
  { phase: '03', label: '创作中段', desc: '导师巡场、Workshop、交流夜谈穿插在创作之间。', deadline: '2026.07.20 — 07.21' },
  { phase: '04', label: '提交截止', desc: '⚠️ 截止时间绝对，系统关闭后不再接受补交。', deadline: '2026.07.21 21:00' },
  { phase: '05', label: '路演评审', desc: '每个项目 5 分钟路演 + 3 分钟问答，评委打分。', deadline: '2026.07.21 21:30' },
]

const personas = [
  {
    no: '01',
    title: '学生与探索者',
    desc: '清醒地意识到自己正站在新技术浪潮边缘的学生，一边周旋于课业，一边在深夜里编译另一种人生版本的可能。',
    tags: ['高校', '课业之外', '深夜编译'],
  },
  {
    no: '02',
    title: '独立开发者与创业者',
    desc: '在 Figma、Notion 或备忘录里压着尚未成形的产品草图，却相信有些东西值得先被做出来。',
    tags: ['独立开发', '产品草图', '先做出来'],
  },
  {
    no: '03',
    title: '跨界创作者',
    desc: '工作也许更接近写作、音乐、影像或空间设计，在「和模型一起做点东西」的可能面前生出久违的兴奋。',
    tags: ['写作', '音乐', '影像', '设计'],
  },
]

const stats = [
  { value: '200+', label: '社区成员', sub: '持续增长中' },
  { value: '20+', label: '线下活动', sub: '分享会 × 黑客松' },
  { value: '长沙', label: '城市', sub: 'Local roots' },
  { value: '2025', label: '成立', sub: 'Ignite before AGI' },
]
</script>

<template>
  <main class="home">
    <!-- 1. Hero：纯字体，无图片（参考 aihacktour） -->
    <section class="home-hero">
      <p class="hero-eyebrow">Yang · Ke · Song &nbsp;·&nbsp; 2026 &nbsp;·&nbsp; Hackathon</p>

      <h1 class="hero-mega">
        <span class="mega-zh">洋客松</span>
        <span class="mega-en">YANG KE SONG</span>
      </h1>

      <p class="hero-sub">
        洋 = 跨文化、全球信号。客 = 创客、极客、黑客。松 = 黑客松。<br />
        一场面向高校、开发者和 AI 行动者的两天一夜线下黑客松。
      </p>

      <div class="hero-actions">
        <router-link class="primary-button" to="/events">查看全部活动</router-link>
        <a class="outline-button" href="https://www.ignai.cn/" target="_blank" rel="noreferrer">访问 IGNAI 官网</a>
      </div>

      <!-- 底部信息条（参考 aihacktour 的 meta strip） -->
      <div class="hero-meta-strip">
        <div class="meta-cell">
          <span class="meta-label">Organizer</span>
          <strong>IGNAI · 长沙 AI 社区</strong>
        </div>
        <div class="meta-cell">
          <span class="meta-label">Stop</span>
          <strong>长沙站 · 线下</strong>
        </div>
        <div class="meta-cell">
          <span class="meta-label">Current Phase</span>
          <strong>报名开放中</strong>
        </div>
        <div class="meta-cell">
          <span class="meta-label">Year</span>
          <strong>2026</strong>
        </div>
      </div>
    </section>

    <!-- 2. 跑马灯 -->
    <div class="signal-strip" aria-hidden="true">
      <div>
        <span>◆ 洋客松</span><span>◆ Yang Ke Song</span><span>◆ Local roots · Global signal</span>
        <span>◆ 真实行动</span><span>◆ AI · Agent · Product</span><span>◆ 长沙 AI 社区</span>
        <span>◆ Ignite before AGI</span><span>◆ 做项目 · 分享观点</span>
        <span>◆ 洋客松</span><span>◆ Yang Ke Song</span><span>◆ Local roots · Global signal</span>
        <span>◆ 真实行动</span><span>◆ AI · Agent · Product</span><span>◆ 长沙 AI 社区</span>
        <span>◆ Ignite before AGI</span><span>◆ 做项目 · 分享观点</span>
      </div>
    </div>

    <!-- 3. 定调引言（独立、大留白） -->
    <section class="home-section home-opening">
      <p class="section-eyebrow">Opening Note</p>
      <h2 class="section-quote">Ignite before AGI.</h2>
      <p class="section-lead">
        你无需先证明自己是「足够资深的工程师」才能开口。
        只要带着一个你真心在意的想法走进会场，把 AI 当作一个可靠但仍有个性的合作者。
      </p>
    </section>

    <!-- 4. 这是洋客松（功能解释，独立章节） -->
    <section class="home-section home-about">
      <p class="section-eyebrow">About · 这是洋客松</p>
      <h2 class="section-title">两天一夜，<br />把真实问题推进到可以被展示。</h2>
      <p class="section-lead">
        从赛题公布到作品提交，导师巡场、Workshop 和交流夜谈会穿插在创作之间。
        我们关心的是：你能不能在这里，把一个想法推进到可以被展示、被讨论、被继续迭代的样子。
      </p>
    </section>

    <!-- 5. 关键时间点 -->
    <section class="home-section home-schedule">
      <p class="section-eyebrow">Schedule · 关键时间点</p>
      <h2 class="section-title">从开场到路演，<br />每一节点都不容错过。</h2>
      <p class="section-intro">黑客松从正式开场开始计算有效开发时间，到最终作品提交截止为止。截止时间是绝对的。</p>

      <ol class="timeline-list">
        <li v-for="item in schedule" :key="item.phase">
          <span class="timeline-no">{{ item.phase }}</span>
          <div class="timeline-body">
            <div class="timeline-head">
              <strong>{{ item.label }}</strong>
              <span class="timeline-deadline">{{ item.deadline }}</span>
            </div>
            <p>{{ item.desc }}</p>
          </div>
        </li>
      </ol>
    </section>

    <!-- 6. 谁会在这里相遇 -->
    <section class="home-section home-people">
      <p class="section-eyebrow">Who You'll Meet · 谁会在这里相遇</p>
      <h2 class="section-title">这里聚集了<br />三类不一样的行动者。</h2>
      <p class="section-intro">不是按 title 划分，而是按「你现在在做什么」划分。</p>

      <div class="persona-grid">
        <article v-for="p in personas" :key="p.no" class="persona-card">
          <span class="persona-no">{{ p.no }}</span>
          <h3>{{ p.title }}</h3>
          <p>{{ p.desc }}</p>
          <div class="persona-tags">
            <span v-for="t in p.tags" :key="t">{{ t }}</span>
          </div>
        </article>
      </div>
    </section>

    <!-- 7. 主办方 -->
    <section class="home-section home-organizer">
      <p class="section-eyebrow">Organizer · 主办方</p>
      <h2 class="section-title">IGNAI · 长沙 AI 社区</h2>
      <p class="section-lead">
        IGNAI 是长沙本地的 AI 社区。本地连接、全球信号，让真实行动者聚在一起。
      </p>
      <p class="section-sub">
        我们做分享会、做黑客松、做现场记录。社区里有人在做产品、有人在写文章、有人在上课、有人在跑 Demo——
        洋客松是把这群人放在同一个会场里两天一夜。
      </p>

      <div class="stats-grid">
        <div v-for="s in stats" :key="s.label" class="stat-card">
          <div class="stat-value">{{ s.value }}</div>
          <div class="stat-label">{{ s.label }}</div>
          <div class="stat-sub">{{ s.sub }}</div>
        </div>
      </div>
    </section>

    <!-- 8. Featured 精选活动 -->
    <section class="home-section home-featured" v-if="featured">
      <p class="section-eyebrow">Featured · 当前精选</p>
      <h2 class="section-title">正在报名中。</h2>
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

    <!-- 9. 收尾 CTA -->
    <section class="home-section home-finale">
      <p class="section-eyebrow">Ignite before AGI</p>
      <h2 class="finale-title">那些疯狂到以为<br />自己能够改变世界的人，<br />才是真正改变世界的人。</h2>
      <p class="finale-attr">— 洋客松 · 长沙</p>
      <div class="cta-finale-actions">
        <router-link class="primary-button" to="/events">查看全部活动</router-link>
        <router-link v-if="featured" class="outline-button" :to="`/event/${featured.id}/join`">加入当前比赛</router-link>
      </div>
    </section>
  </main>
</template>

<style scoped>
/* ============================================================
   Hero：纯字体，无图片
   ============================================================ */
.home-hero {
  position: relative;
  min-height: calc(100vh - 84px);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  padding: 80px clamp(20px, 5vw, 60px) 40px;
  background: var(--paper);
  overflow: hidden;
}

/* 巨型品牌水印（替代原全局 .hero::before） */
.home-hero::before {
  content: "YKS";
  position: absolute;
  right: -2vw;
  bottom: -8%;
  font-size: clamp(12rem, 32vw, 32rem);
  font-weight: 950;
  line-height: .85;
  color: var(--hero-watermark, rgba(255, 95, 31, 0.08));
  pointer-events: none;
  letter-spacing: -.04em;
  z-index: 0;
}

.hero-eyebrow {
  position: relative;
  z-index: 1;
  margin: 0 0 32px;
  color: var(--heat);
  font-size: clamp(.72rem, 1vw, .86rem);
  font-weight: 900;
  letter-spacing: .24em;
  text-transform: uppercase;
}

.hero-mega {
  position: relative;
  z-index: 1;
  margin: 0;
  display: grid;
  gap: 12px;
  justify-items: center;
}
.mega-zh {
  font-size: clamp(5rem, 16vw, 14rem);
  font-weight: 950;
  line-height: .95;
  letter-spacing: .04em;
  color: var(--ink);
  text-shadow: 0 4px 24px rgba(255, 95, 31, 0.16);
}
.mega-en {
  font-size: clamp(.78rem, 1.2vw, 1.1rem);
  font-weight: 900;
  letter-spacing: .8em;
  text-indent: .8em;
  color: var(--heat);
}

.hero-sub {
  position: relative;
  z-index: 1;
  margin: 36px auto 0;
  max-width: 640px;
  color: var(--muted);
  font-size: clamp(.95rem, 1.3vw, 1.1rem);
  line-height: 1.85;
}

.hero-actions {
  position: relative;
  z-index: 1;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: center;
  margin-top: 36px;
}

/* Hero 底部信息条 */
.hero-meta-strip {
  position: relative;
  z-index: 1;
  margin-top: 80px;
  width: 100%;
  max-width: 920px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 0;
  border-top: 1px solid var(--line);
  border-bottom: 1px solid var(--line);
}
.meta-cell {
  padding: 20px 18px;
  text-align: left;
  border-right: 1px solid var(--line);
}
.meta-cell:last-child { border-right: none; }
.meta-label {
  display: block;
  font-size: .68rem;
  font-weight: 900;
  letter-spacing: .14em;
  text-transform: uppercase;
  color: var(--faint);
  margin-bottom: 8px;
}
.meta-cell strong {
  display: block;
  color: var(--ink);
  font-size: .98rem;
  font-weight: 750;
  line-height: 1.35;
}

/* ============================================================
   通用叙事 section
   ============================================================ */
.home-section {
  max-width: 1080px;
  margin: 0 auto;
  padding: clamp(72px, 10vw, 140px) clamp(20px, 5vw, 60px);
  position: relative;
}

.section-eyebrow {
  margin: 0 0 24px;
  color: var(--heat);
  font-size: .76rem;
  font-weight: 900;
  letter-spacing: .18em;
  text-transform: uppercase;
}

/* 3. 开场引言：大留白 + 大引文 */
.home-opening { text-align: center; }
.section-quote {
  margin: 0 0 36px;
  font-size: clamp(2.4rem, 6vw, 5rem);
  font-weight: 920;
  line-height: 1.05;
  letter-spacing: -.01em;
  color: var(--ink);
}
.home-opening .section-lead {
  max-width: 680px;
  margin: 0 auto;
  font-size: clamp(1.05rem, 1.5vw, 1.25rem);
  line-height: 1.85;
  color: var(--muted);
  font-weight: 480;
}

/* 4. About：左标题右内容（参考 aihacktour 章节排版） */
.home-about .section-title,
.home-organizer .section-title,
.home-featured .section-title,
.home-finale .finale-title {
  margin: 0 0 28px;
  font-size: clamp(1.9rem, 4.4vw, 3.4rem);
  font-weight: 900;
  line-height: 1.1;
  color: var(--ink);
}

.home-about .section-lead,
.home-organizer .section-lead {
  max-width: 720px;
  margin: 0 0 16px;
  font-size: clamp(1rem, 1.4vw, 1.15rem);
  line-height: 1.85;
  color: var(--ink);
  font-weight: 540;
}
.home-organizer .section-sub {
  max-width: 720px;
  margin: 0 0 40px;
  color: var(--muted);
  line-height: 1.85;
  font-size: .98rem;
}

/* 5. 时间线 */
.home-schedule .section-title,
.home-people .section-title {
  margin: 0 0 20px;
  font-size: clamp(1.9rem, 4.4vw, 3.4rem);
  font-weight: 900;
  line-height: 1.1;
  color: var(--ink);
}
.section-intro {
  max-width: 680px;
  margin: 0 0 44px;
  color: var(--muted);
  line-height: 1.85;
}
.timeline-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 14px;
}
.timeline-list li {
  display: grid;
  grid-template-columns: 64px 1fr;
  gap: 24px;
  align-items: start;
  padding: 22px 24px;
  border: 1px solid var(--line);
  border-radius: var(--radius);
  background: var(--card-bg);
  transition: border-color .2s, transform .2s;
}
.timeline-list li:hover { border-color: var(--heat); transform: translateY(-1px); }
.timeline-no {
  font-size: 1.6rem;
  font-weight: 950;
  color: var(--heat);
  letter-spacing: .04em;
  line-height: 1;
  padding-top: 4px;
}
.timeline-head {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 6px;
}
.timeline-head strong { font-size: 1.1rem; }
.timeline-deadline {
  color: var(--heat);
  font-weight: 700;
  font-size: .9rem;
  letter-spacing: .04em;
}
.timeline-body p {
  margin: 0;
  color: var(--muted);
  line-height: 1.7;
  font-size: .95rem;
}

/* 6. 人物 */
.persona-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 18px;
}
.persona-card {
  padding: 28px 24px;
  border: 1px solid var(--line);
  border-radius: var(--radius);
  background: var(--card-bg);
  transition: border-color .2s, transform .2s;
}
.persona-card:hover { border-color: var(--heat); transform: translateY(-2px); }
.persona-no {
  display: inline-block;
  color: var(--heat);
  font-weight: 950;
  font-size: 1rem;
  letter-spacing: .08em;
  margin-bottom: 14px;
}
.persona-card h3 { margin: 0 0 12px; font-size: 1.25rem; }
.persona-card p {
  margin: 0 0 18px;
  color: var(--muted);
  line-height: 1.75;
  font-size: .95rem;
}
.persona-tags { display: flex; flex-wrap: wrap; gap: 6px; }
.persona-tags span {
  padding: 4px 10px;
  border: 1px solid var(--line);
  border-radius: 999px;
  font-size: .72rem;
  color: var(--faint);
  font-weight: 700;
  letter-spacing: .04em;
}

/* 7. 主办方 stats */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 18px;
}
.stat-card {
  padding: 24px 22px;
  border: 1px solid var(--line);
  border-radius: var(--radius);
  background: var(--card-bg);
}
.stat-value {
  font-size: clamp(2rem, 3.5vw, 2.8rem);
  font-weight: 950;
  line-height: 1;
  color: var(--heat);
  margin-bottom: 10px;
}
.stat-label { font-size: 1rem; font-weight: 700; margin-bottom: 4px; }
.stat-sub { color: var(--faint); font-size: .82rem; letter-spacing: .04em; }

/* 8. Featured */
.featured-card {
  display: grid;
  gap: 10px;
  padding: 28px;
  border: 1px solid var(--line);
  border-radius: var(--radius);
  background: var(--card-bg);
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
.featured-card h3 { margin: 4px 0 0; font-size: 1.5rem; }
.featured-card p { margin: 0; color: var(--muted); line-height: 1.7; }
.featured-card-meta {
  display: flex; gap: 18px; flex-wrap: wrap;
  color: var(--faint); font-size: .85rem;
}
.featured-card-cta { margin-top: 8px; color: var(--heat); font-weight: 700; }

/* 9. Finale */
.home-finale { text-align: center; }
.finale-title {
  margin: 8px 0 16px !important;
  font-size: clamp(1.6rem, 3.2vw, 2.6rem) !important;
  line-height: 1.3 !important;
  font-weight: 800 !important;
  color: var(--ink);
}
.finale-attr {
  color: var(--heat);
  font-weight: 700;
  letter-spacing: .04em;
  margin: 0 0 36px;
}
.cta-finale-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}

/* ============================================================
   响应式
   ============================================================ */
@media (max-width: 720px) {
  .home-hero { padding: 60px 20px 30px; }
  .hero-meta-strip { margin-top: 50px; }
  .meta-cell { padding: 14px 12px; }
  .meta-cell:nth-child(even) { border-right: none; }
  .timeline-list li { grid-template-columns: 48px 1fr; gap: 16px; padding: 18px; }
  .home-hero::before { font-size: clamp(8rem, 28vw, 18rem); }
}
</style>
