import request from './request'

export function getAdminOrders(params) {
  return request.get('/admin/orders', { params })
}

export function getAdminOrderDetail(id) {
  return request.get(`/admin/orders/${id}`)
}

export function confirmAdminOrder(id) {
  return request.put(`/admin/orders/${id}/confirm`)
}

export function rejectAdminOrder(id, data) {
  return request.put(`/admin/orders/${id}/reject`, data)
}

export function completeAdminOrder(id) {
  return request.put(`/admin/orders/${id}/complete`)
}

export function cancelAdminOrder(id, data) {
  return request.put(`/admin/orders/${id}/cancel`, data)
}
