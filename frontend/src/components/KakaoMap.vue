<template>
  <div ref="mapContainer" class="map-container"></div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'

const props = defineProps({
  lat: { type: Number, required: true },
  lng: { type: Number, required: true },
  heading: { type: Number, default: 0 },
  toilets: { type: Array, default: () => [] },
})

const emit = defineEmits(['select-toilet', 'map-moved'])

const KAKAO_API_KEY = import.meta.env.VITE_KAKAO_MAP_API_KEY || ''

const mapContainer = ref(null)
let map = null
let markers = []
let infowindows = []
let myMarker = null
let kakaoReady = false

function loadKakaoMap() {
  return new Promise((resolve, reject) => {
    if (window.kakao && window.kakao.maps) {
      resolve(window.kakao.maps)
      return
    }
    const script = document.createElement('script')
    script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=${KAKAO_API_KEY}&autoload=false&libraries=services`
    script.onload = () => window.kakao.maps.load(() => resolve(window.kakao.maps))
    script.onerror = reject
    document.head.appendChild(script)
  })
}

function distKm(lat1, lng1, lat2, lng2) {
  const R = 6371
  const dLat = (lat2 - lat1) * Math.PI / 180
  const dLng = (lng2 - lng1) * Math.PI / 180
  const a = Math.sin(dLat / 2) ** 2
    + Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLng / 2) ** 2
  return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
}

function distText(km) {
  return km < 1 ? Math.round(km * 1000) + 'm' : km.toFixed(1) + 'km'
}

function is24h(hours) {
  if (!hours) return false
  return hours.includes('24') || hours.includes('상시') || hours.includes('연중')
}

function clearMarkers() {
  markers.forEach(m => m.setMap(null))
  infowindows.forEach(iw => iw.close())
  markers = []
  infowindows = []
}

window.__openReview = (id, name) => {
  emit('select-toilet', { id, name })
}

function placeToilets() {
  if (!map || !kakaoReady || !props.toilets.length) return
  clearMarkers()

  const kakao = window.kakao.maps

  props.toilets.forEach((t, i) => {
    const pos = new kakao.LatLng(t.lat, t.lng)
    const isFirst = i === 0

    let color = '#007AFF'
    if (t.type === '개방화장실') color = isFirst ? '#FF9500' : '#34C759'
    else if (t.type === '이동화장실' || t.type === '간이화장실') color = isFirst ? '#FF3B30' : '#8E8E93'
    else if (t.type === '공중화장실') color = isFirst ? '#FF9500' : '#007AFF'

    const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 32 32">
      <circle cx="16" cy="16" r="14" fill="${color}" stroke="#fff" stroke-width="2"/>
      <path d="M10 13 C10 10, 22 10, 22 13 L21 20 C21 22, 11 22, 11 20 Z" fill="#fff"/>
      <rect x="13" y="7" width="6" height="4" rx="1.5" fill="#fff"/>
    </svg>`

    const markerImg = new kakao.MarkerImage(
      'data:image/svg+xml,' + encodeURIComponent(svg),
      new kakao.Size(32, 32)
    )

    const marker = new kakao.Marker({ position: pos, map, image: markerImg })

    const dist = distKm(props.lat, props.lng, t.lat, t.lng)
    const dt = distText(dist)

    let badges = ''
    if (parseInt(t.disabledMale) > 0 || parseInt(t.disabledFemale) > 0) badges += '♿ '
    if (t.diaperTable) badges += '🍼 '
    if (t.emergencyBell) badges += '🔔 '
    if (is24h(t.hours)) badges += '🌙 '

    const kakaoUrl = `https://map.kakao.com/link/to/${encodeURIComponent(t.name)},${t.lat},${t.lng}`

    const typeLabel = t.type === '공중화장실' ? '🏛 공중'
      : t.type === '개방화장실' ? '🏪 개방'
      : '🚻 ' + t.type

    const safeName = t.name.replace(/'/g, "\\'")

    const iwContent = `
      <div style="padding:12px;font-size:13px;max-width:260px;line-height:1.6">
        <div style="font-size:15px;font-weight:700;margin-bottom:4px">${isFirst ? '⭐ ' : ''}${dt} ${typeLabel}</div>
        <div style="font-weight:600">${t.name}</div>
        <div style="color:#888">${(t.address || '-').replace('서울특별시', '서울시')}</div>
        <div style="color:#888">${t.hours || '-'}</div>
        ${badges ? '<div style="margin-top:4px">' + badges + '</div>' : ''}
        ${t.averageRating ? '<div style="margin-top:2px;color:#FFB800">★ ' + t.averageRating.toFixed(1) + ' (' + t.reviewCount + ')</div>' : ''}
        <div style="margin-top:8px;display:flex;gap:6px">
          <a href="${kakaoUrl}" target="_blank" style="flex:1;display:block;text-align:center;padding:8px 0;background:#007AFF;color:#fff;border-radius:8px;text-decoration:none;font-size:13px;font-weight:600">📍 길찾기</a>
          <a href="#" style="flex:1;display:block;text-align:center;padding:8px 0;background:#34C759;color:#fff;border-radius:8px;text-decoration:none;font-size:13px;font-weight:600" onclick="window.__openReview(${t.id}, '${safeName}');return false;">💬 리뷰</a>
        </div>
      </div>`

    const iw = new kakao.InfoWindow({ content: iwContent, removable: true })

    kakao.event.addListener(marker, 'click', () => {
      infowindows.forEach(w => w.close())
      iw.open(map, marker)
    })

    markers.push(marker)
    infowindows.push(iw)
  })
}

function initMap() {
  const kakao = window.kakao.maps
  kakaoReady = true

  map = new kakao.Map(mapContainer.value, {
    center: new kakao.LatLng(props.lat, props.lng),
    level: 3,
  })

  const myContent = `<div style="width:60px;height:60px;display:flex;align-items:center;justify-content:center;filter:drop-shadow(0 1px 3px rgba(0,0,0,0.2))">
    <svg viewBox="0 0 60 90">
      <path d="M12.5 22.5 L30 10 L47.5 22.5" fill="none" stroke="#000" stroke-width="8.75" stroke-linecap="round" stroke-linejoin="round"/>
      <path d="M12.5 22.5 L30 10 L47.5 22.5" fill="none" stroke="#FFE600" stroke-width="3.75" stroke-linecap="round" stroke-linejoin="round"/>
      <path d="M16.25 33.75 L30 25 L43.75 33.75" fill="none" stroke="#000" stroke-width="7.5" stroke-linecap="round" stroke-linejoin="round"/>
      <path d="M16.25 33.75 L30 25 L43.75 33.75" fill="none" stroke="#FFE600" stroke-width="3.125" stroke-linecap="round" stroke-linejoin="round"/>
      <path d="M20 43.75 L30 37.5 L40 43.75" fill="none" stroke="#000" stroke-width="6.25" stroke-linecap="round" stroke-linejoin="round"/>
      <path d="M20 43.75 L30 37.5 L40 43.75" fill="none" stroke="#FFE600" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
      <circle cx="30" cy="61.25" r="16.25" fill="#007AFF" stroke="#fff" stroke-width="3.125"/>
    </svg>
  </div>`

  myMarker = new kakao.CustomOverlay({
    position: new kakao.LatLng(props.lat, props.lng),
    map,
    content: myContent,
    yAnchor: 0.5,
    xAnchor: 0.5,
  })

  kakao.event.addListener(map, 'dragend', () => {
    const c = map.getCenter()
    emit('map-moved', { lat: c.getLat(), lng: c.getLng() })
  })

  placeToilets()
}

onMounted(async () => {
  try {
    await loadKakaoMap()
    initMap()
  } catch (e) {
    console.error('Kakao Map load failed', e)
  }
})

watch(() => [props.lat, props.lng], ([newLat, newLng]) => {
  if (!map) return
  const kakao = window.kakao.maps
  const pos = new kakao.LatLng(newLat, newLng)
  map.setCenter(pos)
  if (myMarker) myMarker.setPosition(pos)
})

watch(() => props.toilets, () => {
  placeToilets()
}, { deep: true })
</script>

<style scoped>
.map-container {
  width: 100%;
  height: 100%;
}
</style>
