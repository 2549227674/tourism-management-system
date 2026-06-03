import request from './request'

export function getAdminRoutes(params) {
  return request.get('/admin/routes', { params })
}

export function getAdminRouteDetail(id) {
  return request.get(`/admin/routes/${id}`)
}

export function createAdminRoute(data) {
  return request.post('/admin/routes', data)
}

export function updateAdminRoute(id, data) {
  return request.put(`/admin/routes/${id}`, data)
}

export function deleteAdminRoute(id) {
  return request.delete(`/admin/routes/${id}`)
}

export function updateRouteStatus(id, status) {
  return request.put(`/admin/routes/${id}/status`, null, { params: { status } })
}
