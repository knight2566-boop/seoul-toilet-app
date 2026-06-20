import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
})

export async function fetchNearbyToilets(lat, lng, radius = 1.5) {
  const res = await api.get('/toilets/nearby', { params: { lat, lng, radius } })
  return res.data
}

export async function fetchToiletDetail(id) {
  const res = await api.get(`/toilets/${id}`)
  return res.data
}

export async function searchToilets(query) {
  const res = await api.get('/toilets/search', { params: { q: query } })
  return res.data
}

export async function fetchReviews(toiletId) {
  const res = await api.get(`/toilets/${toiletId}/reviews`)
  return res.data
}

export async function addReview(toiletId, { rating, comment, photoUrl }) {
  const res = await api.post(`/toilets/${toiletId}/reviews`, { rating, comment, photoUrl })
  return res.data
}

export async function fetchAverageRating(toiletId) {
  const res = await api.get(`/toilets/${toiletId}/reviews/average`)
  return res.data.averageRating
}
