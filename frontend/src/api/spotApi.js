import request from './request'

export function getSpots(params) {
  return request.get('/spots', { params })
}

export function getSpotDetail(id) {
  return request.get(`/spots/${id}`)
}

export function getCategories() {
  return request.get('/categories')
}
