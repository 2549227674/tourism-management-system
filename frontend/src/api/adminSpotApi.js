import request from './request'

export function getAdminSpots(params) {
  return request.get('/admin/spots', { params })
}

export function createAdminSpot(data) {
  return request.post('/admin/spots', data)
}

export function updateAdminSpot(id, data) {
  return request.put(`/admin/spots/${id}`, data)
}

export function deleteAdminSpot(id) {
  return request.delete(`/admin/spots/${id}`)
}

export function updateSpotStatus(id, status) {
  return request.put(`/admin/spots/${id}/status`, null, { params: { status } })
}
