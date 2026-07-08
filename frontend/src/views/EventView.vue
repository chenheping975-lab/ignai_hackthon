<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { hackathonEvents } from '../data/mock'
import { useAuthStore } from '../stores/auth'
import { EventApi, RegistrationApi } from '../api'

const route = useRoute()
const auth = useAuthStore()

const event = computed(() => hackathonEvents.find(e => e.id === route.params.id) || hackathonEvents[0])
const activeStage = computed(() => event.value.stages.find(s => s.done && !event.value.stages[event.value.stages.indexOf(s) + 1]?.done) || event.value.stages[0])

const form = ref({})
const formMessage = ref('')
const formMessageType = ref('muted')

function initForm() {
  const f = {}
  event.value.formFields.forEach(field => {
    f[field.key] = field.type === 'checkbox' ? [] : ''
  })
  form.value = f
}

async function handleRegister() {
  if (!auth.isLoggedIn) {
    formMessage.value = '请先登录再报名'
    formMessageType.value = 'error'
    return
  }
  try {
    await RegistrationApi.submit({
      eventId: event.value.id === 'ignai-ai-skillathon' ? 1 : 2,
      registrationType: 'individual',
      contactName: form.value.name || auth.user?.name,
      contactPhone: form.value.phone || auth.user?.phone,
      contactEmail: auth.user?.email,
    })
    formMessage.value = '报名成功！'
    formMessageType.value = 'success'
  } catch (e) {
    formMessage.value = e.message
    formMessageType.value = 'error'
  }
}

onMounted(initForm)
</script>

<template>
  <main>
    <!-- Hero -->
    <section class="hero event-hero">
      <div class="hero-content">
        <p class="eyebrow">{{ event.statusText }}</p>
        <h1>{{ event.title }}</h1>
        <p class="hero-sub">{{ event.summary }}</p>
        <div class="event-meta">
          <span>{{ event.date }}</span>
          <span>{{ event.location }}</span>
        </div>
      </div>
    </section>

    <!-- Status Rail -->
    <div class="event-status-rail" id="event-status-rail">
      <div v-for="stage in event.stages" :key="stage.key" class="status-step" :class="{ done: stage.done, active: stage.key === activeStage.key }">
        <div class="status-dot"></div>
        <span>{{ stage.label }}</span>
      </div>
    </div>

    <!-- Tracks -->
    <section class="section" v-if="event.tracks?.length">
      <div class="section-copy">
        <p class="eyebrow">TRACKS</p>
        <h2>赛道</h2>
      </div>
      <div class="card-grid">
        <article v-for="track in event.tracks" :key="track.name" class="track-card">
          <h3>{{ track.name }}</h3>
          <p>{{ track.description }}</p>
        </article>
      </div>
    </section>

    <!-- Registration Form -->
    <section class="section" id="registration" v-if="event.formFields?.length">
      <div class="section-copy">
        <p class="eyebrow">REGISTER</p>
        <h2>报名</h2>
        <p>填写以下信息完成报名。</p>
      </div>
      <form class="registration-form" @submit.prevent="handleRegister">
        <div v-for="field in event.formFields" :key="field.key" class="form-field">
          <label>
            {{ field.label }}
            <span v-if="field.required" class="required">*</span>
          </label>
          <input v-if="field.type === 'text' || field.type === 'url'" v-model="form[field.key]" :type="field.type" :placeholder="field.placeholder" :required="field.required" />
          <textarea v-else-if="field.type === 'textarea'" v-model="form[field.key]" :placeholder="field.placeholder" :required="field.required" rows="3"></textarea>
          <select v-else-if="field.type === 'select'" v-model="form[field.key]" :required="field.required">
            <option value="">请选择</option>
            <option v-for="opt in field.options" :key="opt" :value="opt">{{ opt }}</option>
          </select>
          <div v-else-if="field.type === 'radio'" class="radio-group">
            <label v-for="opt in field.options" :key="opt">
              <input type="radio" v-model="form[field.key]" :value="opt" /> {{ opt }}
            </label>
          </div>
          <div v-else-if="field.type === 'checkbox'" class="checkbox-group">
            <label v-for="opt in field.options" :key="opt">
              <input type="checkbox" v-model="form[field.key]" :value="opt" /> {{ opt }}
            </label>
          </div>
        </div>
        <button class="primary-button" type="submit">提交报名</button>
        <p :style="{ color: formMessageType === 'error' ? 'var(--red)' : formMessageType === 'success' ? 'var(--green)' : 'var(--muted)' }">{{ formMessage }}</p>
      </form>
    </section>

    <!-- Submission Requirements -->
    <section class="section" id="requirements" v-if="event.submissionRequirements?.length">
      <div class="section-copy">
        <p class="eyebrow">REQUIREMENTS</p>
        <h2>提交要求</h2>
      </div>
      <ul class="requirements-list">
        <li v-for="req in event.submissionRequirements" :key="req">{{ req }}</li>
      </ul>
    </section>
  </main>
</template>

<style scoped>
.event-hero { padding: 120px 20px 60px; text-align: center; }
.event-meta { display: flex; gap: 24px; justify-content: center; color: var(--muted); margin-top: 16px; }
.event-status-rail { display: flex; justify-content: center; gap: 32px; padding: 24px 20px; border-bottom: 1px solid var(--line); }
.status-step { display: flex; align-items: center; gap: 8px; color: var(--muted); }
.status-step.done { color: var(--green); }
.status-step.active { color: var(--heat); font-weight: 700; }
.status-dot { width: 10px; height: 10px; border-radius: 50%; background: var(--line); }
.status-step.done .status-dot { background: var(--green); }
.status-step.active .status-dot { background: var(--heat); }
.track-card { border: 1px solid var(--line); border-radius: var(--radius); padding: 24px; }
.track-card h3 { margin: 0 0 8px; }
.registration-form { max-width: 560px; display: grid; gap: 16px; }
.form-field label { display: block; font-weight: 700; margin-bottom: 6px; font-size: .85rem; }
.form-field input, .form-field textarea, .form-field select { width: 100%; min-height: 44px; border: 1px solid var(--line); border-radius: var(--radius); padding: 0 12px; background: var(--paper); font: inherit; }
.form-field textarea { padding: 10px 12px; resize: vertical; }
.radio-group, .checkbox-group { display: flex; gap: 16px; flex-wrap: wrap; }
.radio-group label, .checkbox-group label { display: flex; align-items: center; gap: 6px; font-weight: 400; }
.requirements-list { list-style: none; padding: 0; display: grid; gap: 12px; }
.requirements-list li { padding: 16px 20px; border: 1px solid var(--line); border-radius: var(--radius); }
.required { color: var(--red); }
</style>
