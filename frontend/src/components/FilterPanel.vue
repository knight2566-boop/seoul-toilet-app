<template>
  <div class="filter-panel" :class="{ visible: true }">
    <div class="filter-header">
      <span class="filter-title">필터</span>
      <button class="filter-close" @click="$emit('close')">✕</button>
    </div>

    <div class="filter-section">
      <label class="section-label">검색 반경</label>
      <div class="radius-row">
        <button
          v-for="r in [0.5, 1, 2]"
          :key="r"
          :class="['radius-btn', { active: radius === r }]"
          @click="$emit('update:radius', r)"
        >
          {{ r }}km
        </button>
      </div>
    </div>

    <div class="filter-section">
      <label class="section-label">옵션</label>
      <label class="filter-item">
        <input type="checkbox" :checked="disabled" @change="$emit('update:disabled', $event.target.checked)" />
        <span>♿ 장애인 화장실만</span>
      </label>
      <label class="filter-item">
        <input type="checkbox" :checked="diaper" @change="$emit('update:diaper', $event.target.checked)" />
        <span>🍼 기저귀교환대 있는 곳만</span>
      </label>
      <label class="filter-item">
        <input type="checkbox" :checked="h24" @change="$emit('update:h24', $event.target.checked)" />
        <span>🌙 24시간 운영만</span>
      </label>
      <label class="filter-item">
        <input type="checkbox" :checked="open" @change="$emit('update:open', $event.target.checked)" />
        <span>🏪 민간 개방화장실만</span>
      </label>
    </div>
  </div>
</template>

<script setup>
defineProps({
  radius: Number,
  disabled: Boolean,
  diaper: Boolean,
  h24: Boolean,
  open: Boolean,
})

defineEmits(['update:radius', 'update:disabled', 'update:diaper', 'update:h24', 'update:open', 'close'])
</script>

<style scoped>
.filter-panel {
  position: fixed;
  top: 76px;
  right: 16px;
  width: 240px;
  background: rgba(255,255,255,0.97);
  border-radius: 12px;
  padding: 16px;
  z-index: 150;
  box-shadow: 0 4px 16px rgba(0,0,0,0.15);
}
.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.filter-title {
  font-size: 12px;
  font-weight: 700;
  color: #888;
  text-transform: uppercase;
}
.filter-close {
  background: none;
  border: none;
  font-size: 18px;
  color: #999;
  cursor: pointer;
  padding: 4px;
}
.filter-section {
  margin-bottom: 12px;
}
.section-label {
  display: block;
  font-size: 12px;
  font-weight: 700;
  color: #888;
  text-transform: uppercase;
  margin-bottom: 8px;
}
.radius-row {
  display: flex;
  gap: 8px;
}
.radius-btn {
  flex: 1;
  padding: 8px 0;
  border: none;
  border-radius: 8px;
  background: #f0f0f0;
  font-size: 13px;
  font-weight: 600;
  color: #555;
  cursor: pointer;
}
.radius-btn.active {
  background: #007AFF;
  color: #fff;
}
.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
  font-size: 14px;
  color: #333;
  cursor: pointer;
}
.filter-item input[type="checkbox"] {
  width: 18px;
  height: 18px;
  accent-color: #007AFF;
}
</style>
