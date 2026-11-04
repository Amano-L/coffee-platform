<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Lock, User } from '@element-plus/icons-vue'

import { useUserStore } from '@/stores/user'

interface LoginForm {
  username: string
  password: string
}

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const form = reactive<LoginForm>({
  username: '',
  password: '',
})

const rules: FormRules<LoginForm> = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

onMounted(() => {
  if (typeof route.query.username === 'string') {
    form.username = route.query.username
  }
})

async function handleSubmit(): Promise<void> {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  try {
    const result = await userStore.login({ ...form })
    ElMessage.success(`欢迎回来，${result.userInfo.nickname || result.userInfo.username}`)
    const redirectQuery = typeof route.query.redirect === 'string' ? route.query.redirect : ''
    // 只允许站内相对路径，避免开放重定向
    const redirect =
      redirectQuery.startsWith('/') && !redirectQuery.startsWith('//') ? redirectQuery : '/'
    await router.replace(redirect)
  } catch {
    // 错误提示已由 Axios 拦截器统一处理
  }
}
</script>

<template>
  <div class="auth-card rise-in">
    <span class="eyebrow">WELCOME BACK</span>
    <h1 class="auth-title">欢迎回来</h1>
    <p class="auth-subtitle">登录后查看订单、管理订阅，继续你的咖啡旅程。</p>

    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-position="top"
      class="auth-form"
      @submit.prevent="handleSubmit"
    >
      <el-form-item label="用户名" prop="username">
        <el-input
          v-model="form.username"
          size="large"
          placeholder="请输入用户名"
          autocomplete="username"
          :prefix-icon="User"
        />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input
          v-model="form.password"
          type="password"
          size="large"
          show-password
          placeholder="请输入密码"
          autocomplete="current-password"
          :prefix-icon="Lock"
          @keyup.enter="handleSubmit"
        />
      </el-form-item>

      <el-button
        type="primary"
        size="large"
        class="submit-btn"
        :loading="userStore.loading"
        @click="handleSubmit"
      >
        登录
      </el-button>
    </el-form>

    <p class="auth-switch">
      还没有账号？
      <router-link to="/register" class="switch-link">立即注册</router-link>
    </p>
  </div>
</template>

<style scoped>
.auth-card {
  padding: 34px 32px;
  background: var(--paper);
  border: 1px solid var(--line-soft);
  border-radius: 22px;
  box-shadow: 0 24px 60px rgba(62, 39, 35, 0.1);
}

.auth-title {
  margin-top: 12px;
  font-size: 32px;
  color: var(--coffee-900);
}

.auth-subtitle {
  margin-top: 10px;
  font-size: 14px;
  color: var(--muted);
}

.auth-form {
  margin-top: 30px;
}

.auth-form :deep(.el-form-item__label) {
  font-size: 13px;
  padding-bottom: 4px;
}

.submit-btn {
  width: 100%;
  margin-top: 8px;
  letter-spacing: 0.1em;
}

.auth-switch {
  margin-top: 22px;
  text-align: center;
  font-size: 13px;
  color: var(--muted);
}

.switch-link {
  color: var(--caramel-dark);
  font-weight: 600;
}

.switch-link:hover {
  text-decoration: underline;
}

@media (max-width: 520px) {
  .auth-card {
    padding: 26px 20px;
  }

  .auth-title {
    font-size: 28px;
  }
}
</style>
