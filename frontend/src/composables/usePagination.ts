import { ref } from 'vue'

import { DEFAULT_PAGE_SIZE } from '@/utils/constants'

interface PageLike {
  total: number
  current?: number
  size?: number
}

/** 列表页统一分页状态，与后端 PageResult（records/total/current/size）对齐 */
export function usePagination(defaultSize = DEFAULT_PAGE_SIZE) {
  const page = ref(1)
  const size = ref(defaultSize)
  const total = ref(0)

  function applyPageResult(result: PageLike): void {
    total.value = result.total ?? 0
    if (result.current) page.value = result.current
    if (result.size) size.value = result.size
  }

  function handlePageChange(value: number): void {
    page.value = value
  }

  function handleSizeChange(value: number): void {
    size.value = value
    page.value = 1
  }

  function reset(): void {
    page.value = 1
    total.value = 0
  }

  return {
    page,
    size,
    total,
    applyPageResult,
    handlePageChange,
    handleSizeChange,
    reset,
  }
}
