<script setup>
import { useAuthStore } from '../../stores/auth'
import { useThemeStore } from '../../stores/theme'
import { useRouter } from 'vue-router'
import { computed } from 'vue'

const auth = useAuthStore()
const theme = useThemeStore()
const router = useRouter()

const logoSrc = computed(() => theme.theme === 'dark' ? '/img/ignai-logo-nav-dark.png' : '/img/ignai-logo-nav-light.png')

function handleLogout() {
  auth.logout()
  router.push('/')
}
</script>

<template>
  <header class="site-header">
    <!-- 左上角：logo + 官网（紧邻） -->
    <div class="brand-group">
      <router-link class="brand" to="/" aria-label="洋客松 Yang Ke Song">
        <img :src="logoSrc" alt="IGNAI" />
      </router-link>
      <a class="brand-site" href="https://www.ignai.cn/" target="_blank" rel="noreferrer">官网 ↗</a>
    </div>

    <!-- 中部导航：极简（登录后才有「我的报名」） -->
    <nav class="nav-links" aria-label="主导航">
      <router-link v-if="auth.isLoggedIn" to="/my/registrations">我的报名</router-link>
    </nav>

    <div class="header-actions">
      <div class="auth-widget" :data-state="auth.isLoggedIn ? 'authed' : 'guest'">
        <template v-if="!auth.isLoggedIn">
          <router-link class="header-cta" to="/login">登录</router-link>
          <router-link class="outline-button" to="/register">注册</router-link>
        </template>
        <template v-else>
          <router-link class="header-cta" to="/my/registrations">
            <div class="auth-profile">
              <span>参赛账号</span>
              <strong>{{ auth.user?.name || '已登录' }}</strong>
            </div>
          </router-link>
          <button class="outline-button" @click="auth.logout(); router.push('/')">退出</button>
        </template>
      </div>
      <button class="theme-toggle" @click="theme.toggle()" aria-label="切换明暗模式">
        <svg v-if="theme.theme === 'dark'" class="icon-moon" viewBox="0 0 24 24" fill="currentColor">
          <path d="M13.3125 1.50001C12.675 1.31251 12.0375 1.16251 11.3625 1.05001C10.875 0.975006 10.35 1.23751 10.1625 1.68751C9.93751 2.13751 10.05 2.70001 10.425 3.00001C13.0875 5.47501 14.0625 9.11251 12.975 12.525C11.775 16.3125 8.25001 18.975 4.16251 19.0875C3.63751 19.0875 3.22501 19.425 3.07501 19.9125C2.92501 20.4 3.15001 20.925 3.56251 21.1875C4.50001 21.75 5.43751 22.2 6.37501 22.5C7.46251 22.8375 8.58751 22.9875 9.71251 22.9875C11.625 22.9875 13.5 22.5 15.1875 21.5625C17.85 20.1 19.725 17.7375 20.55 14.8875C22.1625 9.26251 18.975 3.37501 13.3125 1.50001Z" />
        </svg>
        <svg v-else class="icon-sun" viewBox="0 0 24 24" fill="currentColor">
          <circle cx="12" cy="12" r="5" />
          <path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42" />
        </svg>
      </button>
    </div>
  </header>
</template>

<style scoped>
.brand-group {
  display: flex;
  align-items: center;
  gap: 18px;
  min-width: 180px;
}
.brand {
  display: inline-flex;
  align-items: center;
}
.brand img {
  height: 40px;
  width: auto;
  border-radius: 4px;
  transition: filter .3s;
}
.brand-site {
  font-size: .82rem;
  font-weight: 800;
  letter-spacing: .08em;
  color: var(--heat);
  text-decoration: none;
  padding: 6px 10px;
  border: 1px solid var(--line);
  border-radius: var(--radius);
  transition: border-color .2s, color .2s;
}
.brand-site:hover {
  border-color: var(--heat);
  color: var(--ink);
}
.nav-links {
  display: flex;
  align-items: center;
  gap: 26px;
  color: var(--muted);
  font-size: .94rem;
  font-weight: 650;
  min-height: 44px;
}
.nav-links a {
  position: relative;
  transition: color .3s;
}
.nav-links a::after {
  content: "";
  position: absolute;
  left: 0; right: 0; bottom: -8px;
  height: 2px;
  background: var(--heat);
  transform: scaleX(0);
  transform-origin: left center;
  transition: transform .4s;
  border-radius: 2px;
}
.nav-links a:hover { color: var(--ink); }
.nav-links a:hover::after { transform: scaleX(1); }
.nav-links:empty { display: none; }
</style>
