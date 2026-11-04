<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<template>
  <div class="not-found">
    <div class="panel">
      <router-link to="/" class="brand">
        <span class="brand-mark"><el-icon :size="20"><Coffee /></el-icon></span>
        <span>拾豆咖啡</span>
      </router-link>

      <div class="code">404</div>
      <h1>这杯咖啡还没煮好</h1>
      <p>你访问的页面不存在，可能已经被移动或删除。</p>

      <div class="actions">
        <el-button type="primary" size="large" @click="router.push('/')">回到首页</el-button>
        <el-button size="large" plain @click="router.push('/products')">去逛逛咖啡豆</el-button>
      </div>

      <router-link to="/products" class="hint">
        或者看看<el-icon><ArrowRight /></el-icon>当季推荐
      </router-link>
    </div>
  </div>
</template>

<style scoped>
.not-found {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 24px;
  background:
    radial-gradient(circle at 75% 20%, rgba(192, 133, 82, 0.22), transparent 42%),
    radial-gradient(circle at 10% 85%, rgba(192, 133, 82, 0.12), transparent 36%),
    var(--cream);
}

.panel {
  width: 100%;
  max-width: 520px;
  text-align: center;
  padding: 40px 32px;
  background: var(--paper);
  border: 1px solid var(--line-soft);
  border-radius: 24px;
  box-shadow: 0 30px 70px rgba(62, 39, 35, 0.1);
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  color: var(--coffee-900);
  font-family: 'Noto Serif SC', 'Songti SC', Georgia, serif;
  font-size: 17px;
  letter-spacing: 0.05em;
}

.brand-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border-radius: 11px;
  background: var(--coffee-900);
  color: #fffaf4;
}

.code {
  margin-top: 26px;
  font-family: Georgia, 'Times New Roman', serif;
  font-size: 96px;
  line-height: 1;
  font-weight: 700;
  color: #e7d6c3;
  letter-spacing: 0.04em;
}

.panel h1 {
  margin-top: 4px;
  font-size: 28px;
  color: var(--coffee-900);
}

.panel p {
  margin-top: 10px;
  font-size: 14px;
  color: var(--muted);
}

.actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 26px;
  flex-wrap: wrap;
}

.hint {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  margin-top: 22px;
  font-size: 12px;
  color: var(--coffee-500);
}

.hint:hover {
  color: var(--coffee-900);
}

@media (max-width: 520px) {
  .panel {
    padding: 30px 20px;
  }

  .code {
    font-size: 76px;
  }
}
</style>
