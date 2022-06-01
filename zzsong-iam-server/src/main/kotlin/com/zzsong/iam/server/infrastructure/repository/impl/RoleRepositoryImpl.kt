package com.zzsong.iam.server.infrastructure.repository.impl

import com.zzsong.iam.server.domain.model.role.RoleDo
import com.zzsong.iam.server.domain.model.role.RoleRepository
import com.zzsong.iam.server.infrastructure.repository.DatabaseIDGenerator
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Repository

/**
 * @author 宋志宗 on 2022/6/1
 */
@Repository
class RoleRepositoryImpl(
  private val idGenerator: DatabaseIDGenerator,
  private val mongoTemplate: ReactiveMongoTemplate,
) : RoleRepository {

  override suspend fun save(roleDo: RoleDo): RoleDo {
    TODO("Not yet implemented")
  }
}
