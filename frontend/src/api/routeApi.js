import request from './request'

export function getRoutes(params) {
  return request.get('/routes', { params })
}

export function getRouteDetail(id) {
  return request.get(`/routes/${id}`)
}
