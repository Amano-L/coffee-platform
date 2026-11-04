import { ref } from 'vue'

/** 管理单个异步操作的 loading 状态 */
export function useLoading(initial = false) {
  const loading = ref(initial)

  async function withLoading<T>(task: () => Promise<T>): Promise<T> {
    loading.value = true
    try {
      return await task()
    } finally {
      loading.value = false
    }
  }

  return { loading, withLoading }
}
