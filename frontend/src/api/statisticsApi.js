import request from './request'

export function getStatisticsSummary() {
  return request.get('/admin/statistics/summary')
}
