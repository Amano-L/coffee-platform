import { ref } from 'vue'
import { defineStore } from 'pinia'

const SIDEBAR_KEY = 'coffee_sidebar_collapsed'

export const useAppStore = defineStore('app', () => {
  const sidebarCollapsed = ref(localStorage.getItem(SIDEBAR_KEY) === '1')
  const globalLoading = ref(false)

  function setSidebarCollapsed(value: boolean): void {
    sidebarCollapsed.value = value
    localStorage.setItem(SIDEBAR_KEY, value ? '1' : '0')
  }

  function toggleSidebar(): void {
    setSidebarCollapsed(!sidebarCollapsed.value)
  }

  function setGlobalLoading(flag: boolean): void {
    globalLoading.value = flag
  }

  return {
    sidebarCollapsed,
    globalLoading,
    toggleSidebar,
    setSidebarCollapsed,
    setGlobalLoading,
  }
})
