package com.zzsong.iam.server.port.controller

import cn.idealframework.transmission.Result
import com.zzsong.iam.server.application.TenantService
import com.zzsong.iam.server.application.dto.args.CreateTenantArgs
import com.zzsong.iam.server.domain.model.tenant.Tenant
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 租户管理
 *
 * @author 宋志宗 on 2022/2/26
 */
@RestController
@RequestMapping("/iam/tenant")
class TenantController(private val tenantService: TenantService) {

  /**
   * 创建租户
   *
   * @author 宋志宗 on 2022/2/26
   */
  @PostMapping("/create")
  suspend fun create(@RequestBody args: CreateTenantArgs): Result<Tenant> {
    val tenantDo = tenantService.create(args)
    val tenant = tenantDo.toTenant()
    return Result.data(tenant)
  }

  
}
