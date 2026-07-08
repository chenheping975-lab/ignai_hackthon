<script setup>
import { useRoute } from 'vue-router'
import { computed } from 'vue'
import AppHeader from './components/layout/AppHeader.vue'
import AppFooter from './components/layout/AppFooter.vue'

const route = useRoute()
const isAdmin = computed(() => route.path.startsWith('/root'))
</script>

<template>
  <div class="app-shell">
    <AppHeader v-if="!isAdmin" />
    <router-view />
    <AppFooter v-if="!isAdmin" />
  </div>
</template>
