package com.zzsong.iam.server.infrastructure.repository.impl

import com.zzsong.iam.server.domain.model.tenant.TenantDo
import com.zzsong.iam.server.domain.model.tenant.TenantRepository
import com.zzsong.iam.server.infrastructure.repository.DatabaseIDGenerator
import com.zzsong.iam.server.infrastructure.repository.impl.r2dbc.R2dbcTenantRepository
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository

/**
 * @author 宋志宗 on 2022/2/26
 */
@Repository
class TenantRepositoryImpl(
  private val template: R2dbcEntityTemplate,
  private val databaseIDGenerator: DatabaseIDGenerator,
  private val r2dbcTenantRepository: R2dbcTenantRepository
) : TenantRepository {

  override suspend fun save(tenantDo: TenantDo): TenantDo {
    if (tenantDo.id < 1) {
      tenantDo.id = databaseIDGenerator.generate()
      return template.insert(tenantDo).awaitSingle()
    }
    return template.update(tenantDo).awaitSingle()
  }

  override suspend fun delete(tenantDo: TenantDo) {
    r2dbcTenantRepository.deleteById(tenantDo.id).awaitSingleOrNull()
  }

  override suspend fun findById(id: Long): TenantDo? {
    return r2dbcTenantRepository.findById(id).awaitSingleOrNull()
  }

  override suspend fun findAllById(ids: Iterable<Long>): List<TenantDo> {
    return r2dbcTenantRepository.findAllById(ids)
      .collectList().awaitSingleOrNull() ?: emptyList()
  }

  override suspend fun findAllByRouterLike(routerLike: String): List<TenantDo> {
    return r2dbcTenantRepository.findAllByRouterLike(routerLike)
      .collectList().awaitSingleOrNull() ?: emptyList()
  }
}
