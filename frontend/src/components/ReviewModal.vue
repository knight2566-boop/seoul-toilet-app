<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-panel">
      <div class="modal-header">
        <span class="modal-title">{{ toiletName }}</span>
        <button class="modal-close" @click="$emit('close')">✕</button>
      </div>

      <div class="modal-body">
        <div v-if="reviews.length" class="section">
          <div class="section-title">리뷰 ({{ reviews.length }})</div>
          <div v-for="r in reviews" :key="r.id" class="review-card">
            <div class="stars-row">
              <span v-for="s in 5" :key="s" :class="s <= r.rating ? 'star-filled' : 'star-empty'">★</span>
            </div>
            <p v-if="r.comment" class="review-comment">{{ r.comment }}</p>
            <img v-if="r.photoUrl" :src="r.photoUrl" class="review-photo" />
            <div class="review-date">{{ new Date(r.createdAt).toLocaleDateString() }}</div>
          </div>
        </div>

        <div class="section">
          <div class="section-title">리뷰 작성</div>
          <div class="stars-row">
            <button
              v-for="s in 5" :key="s"
              :class="['star-btn', s <= newRating && 'star-btn-active']"
              @click="newRating = s"
            >★</button>
          </div>
          <textarea
            v-model="newComment"
            class="comment-input"
            placeholder="화장실 상태에 대한 의견을 남겨주세요..."
            rows="3"
          />
          <div class="photo-row">
            <button class="photo-btn" @click="pickPhoto">🖼 앨범</button>
            <button class="photo-btn" @click="takePhoto">📷 촬영</button>
            <button v-if="photoPreview" class="remove-photo" @click="removePhoto">✕ 제거</button>
          </div>
          <img v-if="photoPreview" :src="photoPreview" class="preview-photo" />
          <button
            :class="['submit-btn', { disabled: newRating === 0 || submitting }]"
            :disabled="newRating === 0 || submitting"
            @click="handleSubmit"
          >
            {{ submitting ? '저장 중...' : '리뷰 등록' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { fetchReviews, addReview } from '../api/toiletApi.js'

const props = defineProps({
  toiletId: { type: Number, required: true },
  toiletName: { type: String, default: '' },
})

const emit = defineEmits(['close', 'updated'])

const reviews = ref([])
const newRating = ref(0)
const newComment = ref('')
const photoPreview = ref(null)
const photoDataUrl = ref(null)
const submitting = ref(false)

onMounted(async () => {
  if (props.toiletId) {
    reviews.value = await fetchReviews(props.toiletId)
  }
})

function pickPhoto() {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = (e) => {
    const file = e.target.files[0]
    if (file) {
      const reader = new FileReader()
      reader.onload = (ev) => {
        photoPreview.value = ev.target.result
        photoDataUrl.value = ev.target.result
      }
      reader.readAsDataURL(file)
    }
  }
  input.click()
}

function takePhoto() {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.capture = 'environment'
  input.onchange = (e) => {
    const file = e.target.files[0]
    if (file) {
      const reader = new FileReader()
      reader.onload = (ev) => {
        photoPreview.value = ev.target.result
        photoDataUrl.value = ev.target.result
      }
      reader.readAsDataURL(file)
    }
  }
  input.click()
}

function removePhoto() {
  photoPreview.value = null
  photoDataUrl.value = null
}

async function handleSubmit() {
  if (newRating.value === 0) return
  submitting.value = true
  await addReview(props.toiletId, {
    rating: newRating.value,
    comment: newComment.value,
    photoUrl: photoDataUrl.value,
  })
  newRating.value = 0
  newComment.value = ''
  photoPreview.value = null
  photoDataUrl.value = null
  submitting.value = false
  reviews.value = await fetchReviews(props.toiletId)
  emit('updated')
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5);
  z-index: 300;
  display: flex;
  align-items: flex-end;
}
.modal-panel {
  width: 100%;
  max-height: 85%;
  min-height: 50%;
  background: #fff;
  border-top-left-radius: 20px;
  border-top-right-radius: 20px;
  overflow-y: auto;
}
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #eee;
}
.modal-title {
  font-size: 16px;
  font-weight: 700;
  color: #333;
  flex: 1;
  margin-right: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.modal-close {
  background: none;
  border: none;
  font-size: 20px;
  color: #999;
  cursor: pointer;
  padding: 4px;
}
.modal-body {
  padding: 16px;
}
.section {
  margin-bottom: 16px;
}
.section-title {
  font-size: 14px;
  font-weight: 700;
  color: #555;
  margin-bottom: 8px;
}
.review-card {
  background: #f8f8f8;
  border-radius: 10px;
  padding: 12px;
  margin-bottom: 8px;
}
.stars-row {
  display: flex;
  gap: 2px;
  margin-bottom: 6px;
}
.star-filled {
  font-size: 16px;
  color: #FFB800;
}
.star-empty {
  font-size: 16px;
  color: #ddd;
}
.review-comment {
  font-size: 13px;
  color: #555;
  line-height: 1.4;
  margin-bottom: 4px;
}
.review-photo {
  width: 120px;
  height: 90px;
  border-radius: 8px;
  object-fit: cover;
  margin-top: 4px;
}
.review-date {
  font-size: 11px;
  color: #aaa;
  margin-top: 4px;
}
.star-btn {
  font-size: 28px;
  color: #ddd;
  background: none;
  border: none;
  cursor: pointer;
  padding: 2px;
  transition: color 0.1s;
}
.star-btn-active {
  color: #FFB800;
}
.comment-input {
  width: 100%;
  border: 1px solid #ddd;
  border-radius: 10px;
  padding: 12px;
  font-size: 14px;
  font-family: inherit;
  color: #333;
  resize: none;
  margin-bottom: 12px;
}
.comment-input::placeholder {
  color: #999;
}
.photo-row {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 8px;
}
.photo-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 8px;
  background: #f0f0f0;
  font-size: 13px;
  font-weight: 600;
  color: #555;
  cursor: pointer;
}
.remove-photo {
  background: none;
  border: none;
  font-size: 12px;
  color: #FF3B30;
  font-weight: 600;
  cursor: pointer;
}
.preview-photo {
  width: 160px;
  height: 120px;
  border-radius: 10px;
  object-fit: cover;
  margin-bottom: 12px;
}
.submit-btn {
  width: 100%;
  padding: 14px;
  border: none;
  border-radius: 10px;
  background: #007AFF;
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  margin-bottom: 20px;
}
.submit-btn.disabled {
  background: #ccc;
  cursor: not-allowed;
}
</style>
