package com.zzsong.iam.server.infrastructure.repository.impl

import com.zzsong.iam.server.domain.model.role.RoleDo
import com.zzsong.iam.server.domain.model.role.RoleRepository
import com.zzsong.iam.server.infrastructure.repository.DatabaseIDGenerator
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository

/**
 * @author 宋志宗 on 2022/2/26
 */
@Repository
class RoleRepositoryImpl(
  private val template: R2dbcEntityTemplate,
  private val databaseIDGenerator: DatabaseIDGenerator,
) : RoleRepository {

  override suspend fun save(roleDo: RoleDo): RoleDo {
    if (roleDo.id < 1) {
      roleDo.id = databaseIDGenerator.generate()
      return template.insert(roleDo).awaitSingle()
    }
    return template.update(roleDo).awaitSingle()
  }
}
