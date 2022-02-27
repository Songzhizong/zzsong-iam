package com.zzsong.iam.server.infrastructure.repository.impl

import com.zzsong.iam.server.domain.model.tenant.TenantUserDo
import com.zzsong.iam.server.domain.model.tenant.TenantUserRepository
import com.zzsong.iam.server.infrastructure.repository.DatabaseIDGenerator
import com.zzsong.iam.server.infrastructure.repository.impl.r2dbc.R2dbcTenantUserRepository
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository

/**
 * @author 宋志宗 on 2022/2/26
 */
@Repository
class TenantUserRepositoryImpl(
  private val template: R2dbcEntityTemplate,
  private val databaseIDGenerator: DatabaseIDGenerator,
  private val r2dbcTenantUserRepository: R2dbcTenantUserRepository
) : TenantUserRepository {

  override suspend fun save(tenantUserDo: TenantUserDo): TenantUserDo {
    if (tenantUserDo.id < 1) {
      tenantUserDo.id = databaseIDGenerator.generate()
      return template.insert(tenantUserDo).awaitSingle()
    }
    return template.update(tenantUserDo).awaitSingle()
  }

  override suspend fun delete(tenantUserDo: TenantUserDo) {
    r2dbcTenantUserRepository.deleteById(tenantUserDo.id).awaitSingleOrNull()
  }
}
