<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const account = ref('')
const password = ref('')
const message = ref('')
const messageType = ref('muted')

async function handleLogin() {
  message.value = ''
  if (!account.value || !password.value) {
    message.value = '请填写账号和密码'
    messageType.value = 'error'
    return
  }
  try {
    await auth.login({ account: account.value, password: password.value })
    message.value = '登录成功'
    messageType.value = 'success'
    setTimeout(() => router.push('/'), 800)
  } catch (e) {
    message.value = e.message
    messageType.value = 'error'
  }
}
</script>

<template>
  <main class="auth-main">
    <section class="auth-card">
      <p class="eyebrow">SIGN IN</p>
      <h1>登录后继续报名。</h1>
      <p>平台信息可以直接预览；提交报名和查看状态需要登录。</p>
      <form class="auth-form" @submit.prevent="handleLogin">
        <label>账号<input v-model="account" type="text" autocomplete="username" placeholder="手机号或邮箱" /></label>
        <label>密码<input v-model="password" type="password" autocomplete="current-password" /></label>
        <button class="primary-button" type="submit">登录</button>
        <p :style="{ color: messageType === 'error' ? 'var(--red)' : messageType === 'success' ? 'var(--green)' : 'var(--muted)' }">{{ message }}</p>
      </form>
      <div class="auth-links">
        <router-link to="/register">没有账号？立即注册</router-link>
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
