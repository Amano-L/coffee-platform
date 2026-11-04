<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

import UserCenterNav from '@/components/user/UserCenterNav.vue'
import { useUserStore } from '@/stores/user'

interface ProfileForm {
  nickname: string
  phone: string
  avatarUrl: string
}

const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)
const saving = ref(false)

const form = reactive<ProfileForm>({
  nickname: '',
  phone: '',
  avatarUrl: '',
})

const rules: FormRules<ProfileForm> = {
  nickname: [{ max: 64, message: '昵称不能超过 64 个字符', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  avatarUrl: [{ max: 255, message: '头像地址不能超过 255 个字符', trigger: 'blur' }],
}

const initial = computed(() => (form.nickname || userStore.displayName).slice(0, 1))
const statusText = computed(() => (userStore.profile?.status === 0 ? '已禁用' : '正常'))
const roleText = computed(() => (userStore.isAdmin ? '管理员' : '普通用户'))

function fillForm(): void {
  const profile = userStore.profile
  if (!profile) return
  form.nickname = profile.nickname ?? ''
  form.phone = profile.phone ?? ''
  form.avatarUrl = profile.avatarUrl ?? ''
}

async function loadProfile(): Promise<void> {
  loading.value = true
  try {
    const profile = await userStore.fetchUserInfo(true)
    if (profile) fillForm()
  } catch {
    // 错误提示由 Axios 拦截器统一处理
  } finally {
    loading.value = false
  }
}

async function handleSubmit(): Promise<void> {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  saving.value = true
  try {
    await userStore.updateUserInfo({
      nickname: form.nickname,
      phone: form.phone,
      avatarUrl: form.avatarUrl,
    })
    ElMessage.success('个人信息已更新')
  } catch {
    // 错误提示已由 Axios 拦截器统一处理
  } finally {
    saving.value = false
  }
}

function handleReset(): void {
  fillForm()
}

onMounted(() => {
  void loadProfile()
})
</script>

<template>
  <div class="page">
    <div class="page-container">
      <header class="page-head">
        <div>
          <span class="eyebrow">MY ACCOUNT</span>
          <h1 class="page-title">个人中心</h1>
          <p class="page-subtitle">管理你的昵称、头像与联系方式。</p>
        </div>
      </header>

      <UserCenterNav />

      <div v-if="loading" class="profile-grid">
        <div class="surface card-skeleton"><el-skeleton :rows="5" animated /></div>
        <div class="surface card-skeleton"><el-skeleton :rows="6" animated /></div>
      </div>

      <div v-else class="profile-grid">
        <aside class="surface profile-card">
          <el-avatar :size="84" :src="form.avatarUrl || undefined" class="avatar">
            {{ initial }}
          </el-avatar>
          <h2>{{ form.nickname || userStore.displayName }}</h2>
          <p class="username">@{{ userStore.userInfo?.username || 'coffee_lover' }}</p>
          <div class="badges">
            <el-tag type="success" effect="light" round>{{ statusText }}</el-tag>
            <el-tag type="info" effect="plain" round>{{ roleText }}</el-tag>
          </div>
          <div class="profile-meta">
            <div>
              <span>用户编号</span>
              <strong>#{{ userStore.userInfo?.id ?? '-' }}</strong>
            </div>
            <div>
              <span>注册邮箱</span>
              <strong>{{ userStore.profile?.email || '未绑定' }}</strong>
            </div>
          </div>
        </aside>

        <section class="surface profile-form">
          <div class="form-head">
            <h2>基本资料</h2>
            <p>用户名与邮箱暂不支持修改；其余信息可随时更新。</p>
          </div>

          <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
            <el-form-item label="头像地址" prop="avatarUrl">
              <div class="avatar-row">
                <el-avatar :size="54" :src="form.avatarUrl || undefined">
                  {{ initial }}
                </el-avatar>
                <el-input
                  v-model="form.avatarUrl"
                  placeholder="https://example.com/avatar.jpg"
                  class="avatar-input"
                />
              </div>
            </el-form-item>

            <div class="two-col">
              <el-form-item label="昵称" prop="nickname">
                <el-input v-model="form.nickname" placeholder="给自己取个咖啡昵称" maxlength="64" />
              </el-form-item>
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="form.phone" placeholder="11 位手机号" maxlength="11" />
              </el-form-item>
            </div>

            <div class="two-col">
              <el-form-item label="用户名">
                <el-input :model-value="userStore.userInfo?.username" disabled />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input :model-value="userStore.profile?.email || ''" disabled placeholder="未绑定" />
              </el-form-item>
            </div>

            <div class="form-actions">
              <el-button type="primary" :loading="saving" @click="handleSubmit">保存修改</el-button>
              <el-button @click="handleReset">重置</el-button>
            </div>
          </el-form>
        </section>
      </div>
    </div>
  </div>
</template>

<style scoped>
.profile-grid {
  display: grid;
  grid-template-columns: 300px minmax(0, 1fr);
  gap: 22px;
  align-items: start;
}

.card-skeleton {
  padding: 24px;
}

.profile-card {
  padding: 30px 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  position: sticky;
  top: 96px;
}

.avatar {
  background: var(--coffee-900);
  color: #fffaf4;
  font-size: 30px;
  border: 4px solid #f3e9dd;
}

.profile-card h2 {
  margin-top: 16px;
  font-size: 22px;
  color: var(--coffee-900);
}

.username {
  margin-top: 4px;
  font-size: 13px;
  color: var(--muted);
}

.badges {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.profile-meta {
  width: 100%;
  margin-top: 22px;
  padding-top: 18px;
  border-top: 1px dashed var(--line);
  display: flex;
  flex-direction: column;
  gap: 12px;
  text-align: left;
}

.profile-meta div {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.profile-meta span {
  font-size: 11px;
  color: var(--muted);
  letter-spacing: 0.06em;
}

.profile-meta strong {
  font-size: 13px;
  color: var(--coffee-800);
  font-weight: 500;
  word-break: break-all;
}

.profile-form {
  padding: 28px;
}

.form-head {
  padding-bottom: 18px;
  margin-bottom: 20px;
  border-bottom: 1px solid var(--line-soft);
}

.form-head h2 {
  font-size: 20px;
  color: var(--coffee-900);
}

.form-head p {
  margin-top: 6px;
  font-size: 12px;
  color: var(--muted);
}

.avatar-row {
  display: flex;
  align-items: center;
  gap: 14px;
  width: 100%;
}

.avatar-input {
  flex: 1;
}

.two-col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-actions {
  display: flex;
  gap: 10px;
  margin-top: 6px;
}

@media (max-width: 900px) {
  .profile-grid {
    grid-template-columns: 1fr;
  }

  .profile-card {
    position: static;
  }
}

@media (max-width: 560px) {
  .two-col {
    grid-template-columns: 1fr;
    gap: 0;
  }

  .profile-form {
    padding: 20px;
  }

  .avatar-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .avatar-input {
    width: 100%;
  }
}
</style>
