import request from '@/utils/request'

export function tableInfoForImageUrl(data) {
  return request({
    url: '/table/tableInfoForImageUrl',
    method: 'get',
    params: {
      url: data.url
    }
  })
}
