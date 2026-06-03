import request from './request'

export function getAdminCategories() {
  return request.get('/admin/categories')
}

export function createAdminCategory(data) {
  return request.post('/admin/categories', data)
}

export function updateAdminCategory(id, data) {
  return request.put(`/admin/categories/${id}`, data)
}

export function deleteAdminCategory(id) {
  return request.delete(`/admin/categories/${id}`)
}
