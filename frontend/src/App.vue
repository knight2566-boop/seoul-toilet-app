<template>
  <div class="app">
    <KakaoMap
      ref="mapRef"
      :lat="position.lat"
      :lng="position.lng"
      :heading="heading"
      :toilets="displayList"
      @select-toilet="handleSelectToilet"
      @map-moved="handleMapMoved"
    />

    <FilterPanel
      v-if="showFilters"
      :radius="filters.radius"
      :disabled="filters.disabled"
      :diaper="filters.diaper"
      :h24="filters.h24"
      :open="filters.open"
      @update:radius="filters.radius = $event; loadToilets()"
      @update:disabled="filters.disabled = $event"
      @update:diaper="filters.diaper = $event"
      @update:h24="filters.h24 = $event"
      @update:open="filters.open = $event"
      @close="showFilters = false"
    />

    <ToiletList
      v-if="showList"
      :toilets="displayList"
      :user-lat="position.lat"
      :user-lng="position.lng"
      @select="moveToToilet"
      @close="showList = false"
    />

    <ReviewModal
      v-if="selectedToilet"
      :toilet-id="selectedToilet.id"
      :toilet-name="selectedToilet.name"
      @close="selectedToilet = null"
      @updated="loadToilets()"
    />

    <div class="ui-overlay">
      <div class="ui-top">
        <button class="ui-btn" @click="showFilters = !showFilters">
          {{ showFilters ? '닫기' : '⚙️ 필터' }}
        </button>
        <button class="ui-btn" @click="toggleList">
          {{ showList ? '닫기' : '📋 리스트' }}
        </button>
      </div>
    </div>

    <div class="bottom-right">
      <button class="my-location-btn" @click="moveToMyLocation" title="내 위치">
        <span class="loc-icon">⊙</span>
        <span class="loc-text">내 위치</span>
      </button>
      <button class="emergency-btn" @click="handleEmergency" title="긴급 찾기">
        <span class="em-icon">🚽</span>
        <span class="em-text">긴급찾기</span>
      </button>
    </div>

    <div class="error-banner" v-if="error">
      {{ error }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import KakaoMap from './components/KakaoMap.vue'
import FilterPanel from './components/FilterPanel.vue'
import ToiletList from './components/ToiletList.vue'
import ReviewModal from './components/ReviewModal.vue'
import { fetchNearbyToilets } from './api/toiletApi.js'

const mapRef = ref(null)
const position = ref({ lat: 37.5665, lng: 126.9780 })
const heading = ref(0)
const error = ref('')
const showFilters = ref(false)
const showList = ref(false)
const selectedToilet = ref(null)
const toilets = ref([])
const filters = ref({
  radius: 1.5,
  disabled: false,
  diaper: false,
  h24: false,
  open: false,
})

function is24h(hours) {
  if (!hours) return false
  return hours.includes('24') || hours.includes('상시') || hours.includes('연중')
}

function filterToilet(t) {
  if (filters.value.disabled && !(parseInt(t.disabledMale) > 0 || parseInt(t.disabledFemale) > 0)) return false
  if (filters.value.diaper && !t.diaperTable) return false
  if (filters.value.h24 && !is24h(t.hours)) return false
  if (filters.value.open && t.type !== '개방화장실') return false
  return true
}

const displayList = computed(() => {
  return toilets.value.filter(filterToilet)
})

async function loadToilets() {
  try {
    toilets.value = await fetchNearbyToilets(position.value.lat, position.value.lng, filters.value.radius)
  } catch (e) {
    error.value = '데이터를 불러올 수 없습니다'
  }
}

function handleMapMoved({ lat, lng }) {
  position.value = { lat, lng }
  loadToilets()
}

function handleSelectToilet(toilet) {
  selectedToilet.value = toilet
}

function moveToToilet(toilet) {
  position.value = { lat: toilet.lat, lng: toilet.lng }
  loadToilets()
  showList.value = false
}

function moveToMyLocation() {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition((pos) => {
      position.value = { lat: pos.coords.latitude, lng: pos.coords.longitude }
      loadToilets()
    })
  }
}

function toggleList() {
  showList.value = !showList.value
  showFilters.value = false
}

function handleEmergency() {
  if (toilets.value.length > 0) {
    const nearest = toilets.value.reduce((a, b) => {
      const dA = distKm(position.value.lat, position.value.lng, a.lat, a.lng)
      const dB = distKm(position.value.lat, position.value.lng, b.lat, b.lng)
      return dA < dB ? a : b
    })
    openNavigation(nearest)
  }
}

function openNavigation(t) {
  const kakaoUrl = `https://map.kakao.com/link/to/${encodeURIComponent(t.name)},${t.lat},${t.lng}`
  window.open(kakaoUrl, '_blank')
}

function distKm(lat1, lng1, lat2, lng2) {
  const R = 6371
  const dLat = (lat2 - lat1) * Math.PI / 180
  const dLng = (lng2 - lng1) * Math.PI / 180
  const a = Math.sin(dLat / 2) ** 2 + Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLng / 2) ** 2
  return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
}

onMounted(() => {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        position.value = { lat: pos.coords.latitude, lng: pos.coords.longitude }
        loadToilets()
      },
      () => {
        error.value = '위치 권한이 거부되었습니다. 서울시청 기준으로 표시합니다.'
        loadToilets()
      },
      { enableHighAccuracy: true, timeout: 10000 }
    )
  } else {
    loadToilets()
  }
})
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; }

.app {
  width: 100vw;
  height: 100vh;
  position: relative;
  overflow: hidden;
}
.ui-overlay {
  position: fixed;
  top: 16px;
  right: 16px;
  z-index: 100;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.ui-top {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.ui-btn {
  background: rgba(150, 150, 150, 0.92);
  color: #fff;
  border: none;
  padding: 10px 18px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}
.ui-btn:hover { opacity: 0.9; }
.bottom-right {
  position: fixed;
  bottom: 100px;
  right: 16px;
  z-index: 100;
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: center;
}
.my-location-btn {
  width: 56px;
  height: 56px;
  border-radius: 28px;
  background: rgba(150, 150, 150, 0.92);
  border: none;
  color: #fff;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1px;
}
.loc-icon { font-size: 20px; font-weight: 700; }
.loc-text { font-size: 9px; font-weight: 700; }
.emergency-btn {
  width: 64px;
  height: 64px;
  border-radius: 32px;
  background: #FF3B30;
  border: none;
  color: #fff;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(255,59,48,0.4);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1px;
}
.em-icon { font-size: 22px; }
.em-text { font-size: 10px; font-weight: 700; }
.error-banner {
  position: fixed;
  top: 80px;
  left: 16px;
  right: 16px;
  background: rgba(255, 149, 0, 0.95);
  color: #fff;
  padding: 10px 14px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 600;
  text-align: center;
  z-index: 200;
}
</style>
