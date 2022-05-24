package com.zzsong.iam.server.infrastructure.repository.impl

import com.zzsong.iam.server.domain.model.platform.PlatformDo
import com.zzsong.iam.server.domain.model.platform.PlatformRepository
import com.zzsong.iam.server.infrastructure.repository.DatabaseIDGenerator
import com.zzsong.iam.server.infrastructure.repository.impl.r2dbc.R2dbcPlatformRepository
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository

/**
 * @author 宋志宗 on 2022/4/1
 */
@Repository
class PlatformRepositoryImpl(
  private val template: R2dbcEntityTemplate,
  private val databaseIDGenerator: DatabaseIDGenerator,
  private val r2dbcPlatformRepository: R2dbcPlatformRepository
) : PlatformRepository {

  override suspend fun save(platformDo: PlatformDo): PlatformDo {
    if (platformDo.id < 1) {
      platformDo.id = databaseIDGenerator.generate()
      return template.insert(platformDo).awaitSingle()
    }
    return template.update(platformDo).awaitSingle()
  }

  override suspend fun findByCode(code: String): PlatformDo? {
    return r2dbcPlatformRepository.findByCode(code).awaitSingleOrNull()
  }
}
