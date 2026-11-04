<script setup lang="ts">
import heroImage from '@/assets/images/hero-coffee.svg'
</script>

<template>
  <div class="blank-shell">
    <aside class="brand-panel">
      <router-link to="/" class="brand">
        <span class="brand-mark">
          <el-icon :size="20"><Coffee /></el-icon>
        </span>
        <span class="brand-text">
          <strong>拾豆咖啡</strong>
          <small>BEAN PICK</small>
        </span>
      </router-link>

      <div class="brand-copy">
        <span class="eyebrow light">SINCE 2026</span>
        <h1>从一颗豆开始，<br />喝到世界的风味。</h1>
        <p>精选单一产区咖啡豆，按你的节奏烘焙、配送。每一杯，都值得慢慢喝。</p>
      </div>

      <img :src="heroImage" alt="咖啡豆与咖啡杯插画" class="brand-visual" />

      <div class="brand-points">
        <div>
          <strong>12+</strong>
          <span>全球产区</span>
        </div>
        <div>
          <strong>48h</strong>
          <span>新鲜烘焙</span>
        </div>
        <div>
          <strong>随时</strong>
          <span>暂停订阅</span>
        </div>
      </div>
    </aside>

    <main class="form-panel">
      <router-link to="/" class="mobile-brand">
        <el-icon><Coffee /></el-icon>
        <span>拾豆咖啡</span>
      </router-link>
      <div class="form-wrap">
        <router-view />
      </div>
      <footer class="form-footer">
        <span>© {{ new Date().getFullYear() }} Bean Pick Coffee</span>
        <router-link to="/">返回首页</router-link>
      </footer>
    </main>
  </div>
</template>

<style scoped>
.blank-shell {
  display: flex;
  min-height: 100vh;
  background: var(--cream);
}

.brand-panel {
  position: relative;
  display: flex;
  flex-direction: column;
  width: 46%;
  max-width: 620px;
  padding: 40px 52px;
  color: #f7f1ea;
  background:
    radial-gradient(circle at 15% 10%, rgba(192, 133, 82, 0.35), transparent 42%),
    radial-gradient(circle at 85% 80%, rgba(192, 133, 82, 0.2), transparent 40%),
    linear-gradient(155deg, #35201a 0%, #24150f 100%);
  overflow: hidden;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  z-index: 1;
}

.brand-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: rgba(255, 250, 244, 0.12);
  color: #fffaf4;
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.15;
}

.brand-text strong {
  font-family: 'Noto Serif SC', 'Songti SC', Georgia, serif;
  font-size: 18px;
  letter-spacing: 0.06em;
}

.brand-text small {
  font-size: 9px;
  letter-spacing: 0.3em;
  color: #c9a98e;
}

.brand-copy {
  position: relative;
  z-index: 1;
  margin-top: 12vh;
  max-width: 380px;
}

.eyebrow.light {
  color: #d9b58f;
}

.brand-copy h1 {
  margin-top: 14px;
  font-size: clamp(30px, 3.4vw, 42px);
  line-height: 1.3;
  color: #fffaf4;
}

.brand-copy p {
  margin-top: 16px;
  font-size: 14px;
  line-height: 1.8;
  color: #c9b6a9;
}

.brand-visual {
  position: absolute;
  right: -70px;
  bottom: -10px;
  width: 58%;
  max-width: 360px;
  opacity: 0.9;
  filter: drop-shadow(0 20px 40px rgba(0, 0, 0, 0.3));
}

.brand-points {
  position: relative;
  z-index: 1;
  display: flex;
  gap: 34px;
  margin-top: auto;
  padding-top: 30px;
}

.brand-points div {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.brand-points strong {
  font-size: 20px;
  color: #fffaf4;
}

.brand-points span {
  font-size: 11px;
  letter-spacing: 0.08em;
  color: #a89284;
}

.form-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 24px 24px;
}

.form-wrap {
  width: 100%;
  max-width: 430px;
}

.mobile-brand {
  display: none;
}

.form-footer {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 36px;
  font-size: 12px;
  color: var(--muted);
}

.form-footer a:hover {
  color: var(--coffee-900);
}

@media (max-width: 900px) {
  .brand-panel {
    display: none;
  }

  .form-panel {
    justify-content: flex-start;
    padding-top: 28px;
  }

  .mobile-brand {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 26px;
    color: var(--coffee-900);
    font-family: 'Noto Serif SC', 'Songti SC', Georgia, serif;
    font-size: 18px;
    letter-spacing: 0.05em;
  }
}
</style>
