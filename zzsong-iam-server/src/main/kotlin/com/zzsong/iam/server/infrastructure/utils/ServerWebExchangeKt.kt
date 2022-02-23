package com.zzsong.iam.server.infrastructure.utils

import cn.idealframework.lang.StringUtils
import org.springframework.web.server.ServerWebExchange

fun ServerWebExchange.getOriginalIp(): String? {
  val request = this.request
  val headers = request.headers
  var ip = headers.getFirst("X-Real-IP")
  if (StringUtils.isNotBlank(ip) && "UNKNOWN".equals(ip, true)) {
    return ip!!
  }
  ip = headers.getFirst("X-Forwarded-For")
  if (StringUtils.isNotBlank(ip) && "UNKNOWN".equals(ip, true)) {
    val index = ip!!.indexOf(",")
    return if (index > -1) {
      ip.substring(0, index)
    } else {
      ip
    }
  }
  return request.remoteAddress?.address?.hostAddress
}
