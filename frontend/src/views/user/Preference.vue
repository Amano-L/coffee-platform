<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

import UserCenterNav from '@/components/user/UserCenterNav.vue'
import { getPreference, updatePreference } from '@/api/user'
import type { Preference, PreferencePayload } from '@/types/user'
import {
  FLAVOR_TAG_SUGGESTIONS,
  PROCESS_METHOD_LABELS,
  ROAST_LEVEL_LABELS,
} from '@/utils/constants'

interface OptionDetail {
  value: string
  title: string
  desc: string
}

const roastDetails: OptionDetail[] = [
  { value: 'LIGHT', title: '浅烘', desc: '花香、柑橘，酸质明亮' },
  { value: 'MEDIUM', title: '中烘', desc: '坚果、焦糖，平衡圆润' },
  { value: 'DARK', title: '深烘', desc: '巧克力、醇厚，低酸浓郁' },
]

const processDetails: OptionDetail[] = [
  { value: 'WASHED', title: '水洗', desc: '干净通透，风味清晰' },
  { value: 'NATURAL', title: '日晒', desc: '果香浓郁，甜感突出' },
  { value: 'HONEY', title: '蜜处理', desc: '甜感与酸质兼顾' },
]

const loading = ref(false)
const saving = ref(false)

const form = reactive<Required<PreferencePayload>>({
  roastLevel: 'LIGHT',
  processMethod: 'WASHED',
  flavorTags: [],
})

const summary = computed(() => {
  const roast = ROAST_LEVEL_LABELS[form.roastLevel] ?? '未选择'
  const process = PROCESS_METHOD_LABELS[form.processMethod] ?? '未选择'
  return `${roast} · ${process}${form.flavorTags.length > 0 ? ` · ${form.flavorTags.join('、')}` : ''}`
})

function applyPreference(data: Preference): void {
  form.roastLevel = data.roastLevel || 'LIGHT'
  form.processMethod = data.processMethod || 'WASHED'
  form.flavorTags = data.flavorTags ?? []
}

async function loadPreference(): Promise<void> {
  loading.value = true
  try {
    const data = await getPreference()
    if (data) applyPreference(data)
  } catch {
    // 错误提示由 Axios 拦截器统一处理
  } finally {
    loading.value = false
  }
}

async function handleSave(): Promise<void> {
  saving.value = true
  try {
    const data = await updatePreference({
      roastLevel: form.roastLevel,
      flavorTags: form.flavorTags,
      processMethod: form.processMethod,
    })
    if (data) applyPreference(data)
    ElMessage.success('咖啡偏好已保存')
  } catch {
    // 错误提示已由 Axios 拦截器统一处理
  } finally {
    saving.value = false
  }
}

function handleReset(): void {
  void loadPreference()
}

onMounted(() => {
  void loadPreference()
})
</script>

<template>
  <div class="page">
    <div class="page-container">
      <header class="page-head">
        <div>
          <span class="eyebrow">TASTE PROFILE</span>
          <h1 class="page-title">咖啡偏好</h1>
          <p class="page-subtitle">告诉我们你喜欢的风味，让每一期推荐更合口味。</p>
        </div>
        <el-button plain @click="$router.push('/subscriptions/plans')">去订阅</el-button>
      </header>

      <UserCenterNav />

      <div v-if="loading" class="surface card-skeleton">
        <el-skeleton :rows="8" animated />
      </div>

      <div v-else class="pref-layout">
        <div class="stack">
          <section class="surface pref-card">
            <div class="card-head">
              <div>
                <h2>烘焙偏好</h2>
                <p>选择你更偏爱的烘焙程度。</p>
              </div>
              <span class="current">{{ ROAST_LEVEL_LABELS[form.roastLevel] || '未选择' }}</span>
            </div>
            <div class="option-grid">
              <button
                v-for="option in roastDetails"
                :key="option.value"
                type="button"
                class="option-card"
                :class="{ active: form.roastLevel === option.value }"
                @click="form.roastLevel = option.value"
              >
                <span class="option-title">{{ option.title }}</span>
                <span class="option-desc">{{ option.desc }}</span>
                <el-icon v-if="form.roastLevel === option.value" class="option-check">
                  <CircleCheckFilled />
                </el-icon>
              </button>
            </div>
          </section>

          <section class="surface pref-card">
            <div class="card-head">
              <div>
                <h2>处理法偏好</h2>
                <p>不同处理法会带来不同的香气与甜感。</p>
              </div>
              <span class="current">{{ PROCESS_METHOD_LABELS[form.processMethod] || '未选择' }}</span>
            </div>
            <div class="option-grid three">
              <button
                v-for="option in processDetails"
                :key="option.value"
                type="button"
                class="option-card"
                :class="{ active: form.processMethod === option.value }"
                @click="form.processMethod = option.value"
              >
                <span class="option-title">{{ option.title }}</span>
                <span class="option-desc">{{ option.desc }}</span>
                <el-icon v-if="form.processMethod === option.value" class="option-check">
                  <CircleCheckFilled />
                </el-icon>
              </button>
            </div>
          </section>

          <section class="surface pref-card">
            <div class="card-head">
              <div>
                <h2>风味标签</h2>
                <p>可以多选，也可以输入自定义风味。</p>
              </div>
            </div>
            <el-select
              v-model="form.flavorTags"
              multiple
              filterable
              allow-create
              default-first-option
              placeholder="选择或输入你喜欢的风味"
              class="tag-select"
            >
              <el-option
                v-for="tag in FLAVOR_TAG_SUGGESTIONS"
                :key="tag"
                :label="tag"
                :value="tag"
              />
            </el-select>
            <div class="quick-tags">
              <button
                v-for="tag in FLAVOR_TAG_SUGGESTIONS.slice(0, 8)"
                :key="tag"
                type="button"
                class="quick-tag"
                :class="{ active: form.flavorTags.includes(tag) }"
                @click="
                  form.flavorTags.includes(tag)
                    ? (form.flavorTags = form.flavorTags.filter((item) => item !== tag))
                    : form.flavorTags.push(tag)
                "
              >
                {{ tag }}
              </button>
            </div>
          </section>
        </div>

        <aside class="summary-card pref-summary">
          <span class="eyebrow">YOUR TASTE</span>
          <h2>偏好预览</h2>
          <p class="summary-text">{{ summary }}</p>
          <div class="summary-chips">
            <el-tag v-if="form.roastLevel" type="warning" effect="plain" round>
              {{ ROAST_LEVEL_LABELS[form.roastLevel] }}
            </el-tag>
            <el-tag v-if="form.processMethod" type="success" effect="plain" round>
              {{ PROCESS_METHOD_LABELS[form.processMethod] }}
            </el-tag>
            <el-tag
              v-for="tag in form.flavorTags"
              :key="tag"
              type="info"
              effect="plain"
              round
            >
              {{ tag }}
            </el-tag>
          </div>
          <p class="summary-tip">保存后，偏好会用于商品推荐与订阅配置参考。</p>
          <el-button type="primary" class="save-btn" :loading="saving" @click="handleSave">
            保存偏好
          </el-button>
          <el-button text class="reset-btn" @click="handleReset">重置为已保存的偏好</el-button>
        </aside>
      </div>
    </div>
  </div>
</template>

<style scoped>
.card-skeleton {
  padding: 26px;
}

.pref-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 22px;
  align-items: start;
}

.pref-card {
  padding: 24px;
}

.card-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.card-head h2 {
  font-size: 19px;
  color: var(--coffee-900);
}

.card-head p {
  margin-top: 4px;
  font-size: 12px;
  color: var(--muted);
}

.current {
  flex: none;
  padding: 4px 12px;
  border-radius: 999px;
  background: #f4ebe1;
  color: var(--coffee-700);
  font-size: 12px;
}

.option-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.option-grid.three {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.option-card {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 16px;
  text-align: left;
  background: #fdfaf5;
  border: 1px solid var(--line);
  border-radius: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.option-card:hover {
  border-color: var(--caramel);
}

.option-card.active {
  border-color: var(--coffee-800);
  background: #fbf3e9;
  box-shadow: inset 0 0 0 1px var(--coffee-800);
}

.option-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--coffee-900);
}

.option-desc {
  font-size: 11px;
  line-height: 1.6;
  color: var(--muted);
}

.option-check {
  position: absolute;
  right: 10px;
  top: 10px;
  color: var(--coffee-800);
  font-size: 15px;
}

.tag-select {
  width: 100%;
}

.quick-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 14px;
}

.quick-tag {
  padding: 5px 12px;
  border-radius: 999px;
  border: 1px solid var(--line);
  background: var(--paper);
  color: var(--coffee-600);
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.quick-tag:hover {
  border-color: var(--caramel);
  color: var(--caramel-dark);
}

.quick-tag.active {
  background: var(--coffee-900);
  border-color: var(--coffee-900);
  color: #fffaf4;
}

.pref-summary {
  position: sticky;
  top: 96px;
}

.pref-summary h2 {
  margin-top: 8px;
  font-size: 22px;
  color: var(--coffee-900);
}

.summary-text {
  margin-top: 10px;
  font-size: 14px;
  line-height: 1.8;
  color: var(--coffee-600);
}

.summary-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;
}

.summary-tip {
  margin-top: 18px;
  font-size: 12px;
  line-height: 1.7;
  color: var(--muted);
}

.save-btn {
  width: 100%;
  margin-top: 18px;
}

.reset-btn {
  width: 100%;
  margin: 8px 0 0;
}

@media (max-width: 960px) {
  .pref-layout {
    grid-template-columns: 1fr;
  }

  .pref-summary {
    position: static;
  }
}

@media (max-width: 620px) {
  .option-grid,
  .option-grid.three {
    grid-template-columns: 1fr;
  }

  .pref-card {
    padding: 18px;
  }
}
</style>
