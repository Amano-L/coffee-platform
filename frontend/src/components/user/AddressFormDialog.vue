<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

import { addAddress, updateAddress } from '@/api/user'
import type { Address, AddressPayload } from '@/types/user'

const props = withDefaults(
  defineProps<{
    modelValue: boolean
    address?: Address | null
  }>(),
  {
    address: null,
  },
)

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  saved: []
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value),
})

const formRef = ref<FormInstance>()
const saving = ref(false)

const createEmptyForm = (): AddressPayload => ({
  receiverName: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: 0,
})

const form = reactive<AddressPayload>(createEmptyForm())

const rules: FormRules<AddressPayload> = {
  receiverName: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' },
    { max: 64, message: '收货人姓名不能超过 64 个字符', trigger: 'blur' },
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' },
  ],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  detailAddress: [
    { required: true, message: '请输入详细地址', trigger: 'blur' },
    { max: 255, message: '详细地址不能超过 255 个字符', trigger: 'blur' },
  ],
}

const isEdit = computed(() => Boolean(props.address?.id))

function syncForm(): void {
  Object.assign(form, createEmptyForm())
  if (props.address) {
    form.receiverName = props.address.receiverName
    form.phone = props.address.phone
    form.province = props.address.province
    form.city = props.address.city
    form.district = props.address.district
    form.detailAddress = props.address.detailAddress
    form.isDefault = props.address.isDefault
  }
}

watch(
  () => props.modelValue,
  (open) => {
    if (open) syncForm()
  },
)

watch(
  () => props.address,
  () => {
    if (props.modelValue) syncForm()
  },
)

async function handleSubmit(): Promise<void> {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  saving.value = true
  try {
    if (props.address?.id) {
      await updateAddress(props.address.id, { ...form })
      ElMessage.success('收货地址已更新')
    } else {
      await addAddress({ ...form })
      ElMessage.success('收货地址已添加')
    }
    visible.value = false
    emit('saved')
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <el-dialog
    v-model="visible"
    :title="isEdit ? '编辑收货地址' : '新增收货地址'"
    width="560px"
    :close-on-click-modal="false"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="88px">
      <el-form-item label="收货人" prop="receiverName">
        <el-input v-model="form.receiverName" placeholder="请输入收货人姓名" maxlength="64" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
      </el-form-item>
      <el-form-item label="所在地区" required>
        <div class="region-grid">
          <el-form-item prop="province" class="region-item">
            <el-input v-model="form.province" placeholder="省份" />
          </el-form-item>
          <el-form-item prop="city" class="region-item">
            <el-input v-model="form.city" placeholder="城市" />
          </el-form-item>
          <el-form-item prop="district" class="region-item">
            <el-input v-model="form.district" placeholder="区县" />
          </el-form-item>
        </div>
      </el-form-item>
      <el-form-item label="详细地址" prop="detailAddress">
        <el-input
          v-model="form.detailAddress"
          type="textarea"
          :rows="2"
          maxlength="255"
          show-word-limit
          placeholder="街道、门牌号等"
        />
      </el-form-item>
      <el-form-item label="默认地址">
        <el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" />
        <span class="switch-tip">开启后下单时优先使用该地址</span>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="saving" @click="handleSubmit">
        {{ isEdit ? '保存修改' : '添加地址' }}
      </el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.region-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
  width: 100%;
}

.region-item {
  margin-bottom: 0;
}

.region-item :deep(.el-form-item__content) {
  margin-left: 0 !important;
}

.switch-tip {
  margin-left: 12px;
  font-size: 12px;
  color: var(--muted);
}

@media (max-width: 560px) {
  .region-grid {
    grid-template-columns: 1fr;
  }
}
</style>
