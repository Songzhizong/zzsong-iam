package com.zzsong.iam.server.domain.model.tenant

/**
 * @author 宋志宗 on 2022/2/26
 */
interface TenantUserRepository {

  suspend fun save(tenantUserDo: TenantUserDo): TenantUserDo

  suspend fun delete(tenantUserDo: TenantUserDo)
}
