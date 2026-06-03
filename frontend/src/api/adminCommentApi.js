import request from './request'

export function getAdminComments(params) {
  return request.get('/admin/comments', { params })
}

export function auditAdminComment(id, data) {
  return request.put(`/admin/comments/${id}/audit`, data)
}
