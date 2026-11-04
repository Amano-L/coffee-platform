import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'

import 'element-plus/dist/index.css'
import './assets/styles/main.css'

const app = createApp(App)

// 全局注册 Element Plus 图标
for (const [name, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(name, component)
}

// Pinia 必须先于 Router 安装，路由守卫中会使用 Store
app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })

app.mount('#app')
