import request from './request'

export function createOrder(data) {
  return request.post('/orders', data)
}

export function getMyOrders(params) {
  return request.get('/orders/my', { params })
}

export function getOrderDetail(id) {
  return request.get(`/orders/${id}`)
}

export function cancelOrder(id) {
  return request.put(`/orders/${id}/cancel`)
}
