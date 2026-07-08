<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const name = ref('')
const phone = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const message = ref('')
const messageType = ref('muted')

async function handleRegister() {
  message.value = ''
  if (!name.value || !phone.value || !password.value) {
    message.value = '请填写必填项'; messageType.value = 'error'; return
  }
  if (!/^1\d{10}$/.test(phone.value)) {
    message.value = '手机号格式不正确'; messageType.value = 'error'; return
  }
  if (password.value.length < 6) {
    message.value = '密码至少 6 位'; messageType.value = 'error'; return
  }
  if (password.value !== confirmPassword.value) {
    message.value = '两次密码不一致'; messageType.value = 'error'; return
  }
  try {
    await auth.register({ name: name.value, phone: phone.value, email: email.value, password: password.value })
    message.value = '注册成功，跳转登录...'
    messageType.value = 'success'
    setTimeout(() => router.push('/login'), 900)
  } catch (e) {
    message.value = e.message; messageType.value = 'error'
  }
}
</script>

<template>
  <main class="auth-main">
    <section class="auth-card">
      <p class="eyebrow">SIGN UP</p>
      <h1>注册参赛账号。</h1>
      <p>注册后即可报名参加黑客松活动。</p>
      <form class="auth-form" @submit.prevent="handleRegister">
        <label>姓名<input v-model="name" type="text" placeholder="真实姓名" /></label>
        <label>手机号<input v-model="phone" type="tel" placeholder="11 位手机号" /></label>
        <label>邮箱<input v-model="email" type="email" placeholder="选填" /></label>
        <label>密码<input v-model="password" type="password" placeholder="至少 6 位" /></label>
        <label>确认密码<input v-model="confirmPassword" type="password" /></label>
        <button class="primary-button" type="submit">注册</button>
        <p :style="{ color: messageType === 'error' ? 'var(--red)' : messageType === 'success' ? 'var(--green)' : 'var(--muted)' }">{{ message }}</p>
      </form>
      <div class="auth-links">
        <router-link to="/login">已有账号？去登录</router-link>
        <router-link to="/">先看看首页</router-link>
      </div>
    </section>
  </main>
</template>

<style scoped>
.auth-main { max-width: 520px; margin: 0 auto; padding: 72px 20px; }
.auth-card { border: 1px solid var(--line); border-radius: var(--radius); padding: 32px; background: rgba(255,250,242,.82); }
.auth-card h1 { margin: 0; font-size: clamp(2rem, 6vw, 3.6rem); line-height: 1.06; }
.auth-card p { color: var(--muted); line-height: 1.7; }
.auth-form { display: grid; gap: 14px; margin-top: 24px; }
.auth-form label { display: grid; gap: 8px; color: var(--faint); font-size: .78rem; font-weight: 900; letter-spacing: .08em; text-transform: uppercase; }
.auth-form input { min-height: 46px; border: 1px solid var(--line); border-radius: var(--radius); padding: 0 12px; background: rgba(255,255,255,.72); font: inherit; }
.auth-form button { cursor: pointer; }
.auth-links { display: flex; justify-content: space-between; gap: 12px; margin-top: 18px; color: var(--muted); }
</style>
