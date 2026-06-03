import request from './request'

export function getAdminAnnouncements(params) {
  return request.get('/admin/announcements', { params })
}

export function getAdminAnnouncementDetail(id) {
  return request.get(`/admin/announcements/${id}`)
}

export function createAdminAnnouncement(data) {
  return request.post('/admin/announcements', data)
}

export function updateAdminAnnouncement(id, data) {
  return request.put(`/admin/announcements/${id}`, data)
}

export function publishAnnouncement(id) {
  return request.put(`/admin/announcements/${id}/publish`)
}

export function offlineAnnouncement(id) {
  return request.put(`/admin/announcements/${id}/offline`)
}

export function deleteAdminAnnouncement(id) {
  return request.delete(`/admin/announcements/${id}`)
}
