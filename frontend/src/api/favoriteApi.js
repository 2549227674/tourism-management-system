import request from './request'

export function addFavorite(data) {
  return request.post('/favorites', data)
}

export function getMyFavorites(params) {
  return request.get('/favorites/my', { params })
}

export function removeFavorite(id) {
  return request.delete(`/favorites/${id}`)
}
