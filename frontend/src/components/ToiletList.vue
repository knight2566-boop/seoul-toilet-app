<template>
  <div class="list-overlay" @click.self="$emit('close')">
    <div class="list-panel">
      <div class="list-header">
        <span>주변 화장실 ({{ toilets.length }})</span>
        <button class="list-close" @click="$emit('close')">✕</button>
      </div>
      <div class="list-body">
        <div v-if="!toilets.length" class="list-empty">조건에 맞는 화장실이 없습니다</div>
        <div
          v-for="(t, i) in toilets"
          :key="t.id"
          class="list-item"
          @click="$emit('select', t)"
        >
          <div class="item-left">
            <div :class="['rank', i === 0 && 'rank-first']">{{ i + 1 }}</div>
            <div class="item-info">
              <div class="item-name-row">
                <span class="item-name">{{ t.name }}</span>
                <span v-if="t.type === '공중화장실'" class="type-icon">🏛</span>
                <span v-else-if="t.type === '개방화장실'" class="type-icon">🏪</span>
                <span v-else class="type-icon">🚻</span>
              </div>
              <div class="item-addr">{{ (t.address || '-').replace('서울특별시', '서울시') }}</div>
              <div class="item-badges">
                <span class="badge">{{ distText(distKm(userLat, userLng, t.lat, t.lng)) }}</span>
                <span v-if="parseInt(t.disabledMale) > 0 || parseInt(t.disabledFemale) > 0" class="badge">♿</span>
                <span v-if="t.diaperTable" class="badge">🍼</span>
                <span v-if="is24h(t.hours)" class="badge">🌙</span>
                <span v-if="t.emergencyBell" class="badge">🔔</span>
              </div>
            </div>
          </div>
          <span class="item-arrow">›</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  toilets: { type: Array, required: true },
  userLat: { type: Number, required: true },
  userLng: { type: Number, required: true },
})

defineEmits(['select', 'close'])

function distKm(lat1, lng1, lat2, lng2) {
  const R = 6371
  const dLat = (lat2 - lat1) * Math.PI / 180
  const dLng = (lng2 - lng1) * Math.PI / 180
  const a = Math.sin(dLat / 2) ** 2 + Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLng / 2) ** 2
  return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
}

function distText(km) {
  return km < 1 ? Math.round(km * 1000) + 'm' : km.toFixed(1) + 'km'
}

function is24h(hours) {
  if (!hours) return false
  return hours.includes('24') || hours.includes('상시') || hours.includes('연중')
}
</script>

<style scoped>
.list-overlay {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  top: 0;
  z-index: 130;
  display: flex;
  align-items: flex-end;
}
.list-panel {
  width: 100%;
  max-height: 55%;
  background: #fff;
  border-top-left-radius: 20px;
  border-top-right-radius: 20px;
  box-shadow: 0 -4px 16px rgba(0,0,0,0.15);
  overflow: hidden;
}
.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 18px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 15px;
  font-weight: 700;
  color: #333;
}
.list-close {
  background: none;
  border: none;
  font-size: 18px;
  color: #999;
  cursor: pointer;
  padding: 4px;
}
.list-body {
  padding: 0 18px 20px;
  overflow-y: auto;
  max-height: 400px;
}
.list-empty {
  padding: 40px 0;
  text-align: center;
  color: #999;
  font-size: 14px;
}
.list-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
}
.list-item:hover {
  background: #fafafa;
}
.item-left {
  display: flex;
  align-items: center;
  flex: 1;
}
.rank {
  width: 26px;
  height: 26px;
  border-radius: 13px;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  color: #888;
  margin-right: 12px;
  flex-shrink: 0;
}
.rank-first {
  background: #FF3B30;
  color: #fff;
}
.item-info {
  flex: 1;
}
.item-name-row {
  display: flex;
  align-items: center;
  gap: 4px;
}
.item-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}
.type-icon {
  font-size: 12px;
}
.item-addr {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}
.item-badges {
  display: flex;
  gap: 4px;
  margin-top: 4px;
}
.badge {
  font-size: 11px;
  background: #f0f0f0;
  color: #666;
  padding: 2px 6px;
  border-radius: 8px;
  font-weight: 600;
}
.item-arrow {
  font-size: 22px;
  color: #ccc;
  margin-left: 8px;
}
</style>
