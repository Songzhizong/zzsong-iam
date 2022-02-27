package com.zzsong.iam.server.infrastructure.repository.impl

import com.zzsong.iam.server.domain.model.user.UserRoleDo
import com.zzsong.iam.server.domain.model.user.UserRoleRepository
import com.zzsong.iam.server.infrastructure.repository.DatabaseIDGenerator
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository

/**
 * @author 宋志宗 on 2022/2/26
 */
@Repository
class UserRoleRepositoryImpl(
  private val template: R2dbcEntityTemplate,
  private val databaseIDGenerator: DatabaseIDGenerator,
) : UserRoleRepository {
  override suspend fun save(userRoleDo: UserRoleDo): UserRoleDo {
    if (userRoleDo.id < 1) {
      userRoleDo.id = databaseIDGenerator.generate()
      return template.insert(userRoleDo).awaitSingle()
    }
    return template.update(userRoleDo).awaitSingle()
  }
}
