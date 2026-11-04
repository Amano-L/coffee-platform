<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Iphone, Lock, Message, User } from '@element-plus/icons-vue'

import { register } from '@/api/auth'

interface RegisterForm {
  username: string
  password: string
  confirmPassword: string
  phone: string
  email: string
}

const router = useRouter()
const formRef = ref<FormInstance>()
const submitting = ref(false)

const form = reactive<RegisterForm>({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
  email: '',
})

function validateConfirm(_rule: unknown, value: string, callback: (error?: Error) => void): void {
  if (!value) {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules: FormRules<RegisterForm> = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 32, message: '用户名长度必须在 4-32 位之间', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度必须在 6-32 位之间', trigger: 'blur' },
  ],
  confirmPassword: [{ validator: validateConfirm, trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' },
  ],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }],
}

async function handleSubmit(): Promise<void> {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  submitting.value = true
  try {
    await register({
      username: form.username,
      password: form.password,
      phone: form.phone,
      email: form.email || undefined,
    })
    ElMessage.success('注册成功，请登录')
    await router.push({ path: '/login', query: { username: form.username } })
  } catch {
    // 错误提示已由 Axios 拦截器统一处理
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="auth-card rise-in">
    <span class="eyebrow">JOIN THE CLUB</span>
    <h1 class="auth-title">创建账号</h1>
    <p class="auth-subtitle">注册后即可收藏风味、管理订阅，让好咖啡按时抵达。</p>

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
          placeholder="4-32 位用户名"
          autocomplete="username"
          :prefix-icon="User"
        />
      </el-form-item>
      <div class="two-col">
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            size="large"
            show-password
            placeholder="6-32 位密码"
            autocomplete="new-password"
            :prefix-icon="Lock"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            size="large"
            show-password
            placeholder="再次输入密码"
            autocomplete="new-password"
            :prefix-icon="Lock"
          />
        </el-form-item>
      </div>
      <el-form-item label="手机号" prop="phone">
        <el-input
          v-model="form.phone"
          size="large"
          placeholder="用于接收订单通知"
          maxlength="11"
          :prefix-icon="Iphone"
        />
      </el-form-item>
      <el-form-item label="邮箱（选填）" prop="email">
        <el-input
          v-model="form.email"
          size="large"
          placeholder="coffee@example.com"
          :prefix-icon="Message"
        />
      </el-form-item>

      <el-button type="primary" size="large" class="submit-btn" :loading="submitting" @click="handleSubmit">
        注册
      </el-button>
    </el-form>

    <p class="auth-switch">
      已有账号？
      <router-link to="/login" class="switch-link">返回登录</router-link>
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
  margin-top: 26px;
}

.auth-form :deep(.el-form-item__label) {
  font-size: 13px;
  padding-bottom: 4px;
}

.two-col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
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

  .two-col {
    grid-template-columns: 1fr;
    gap: 0;
  }
}
</style>
