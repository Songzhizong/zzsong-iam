package com.zzsong.iam.server.infrastructure.repository.impl

import com.zzsong.iam.server.domain.model.platform.PlatformDo
import com.zzsong.iam.server.domain.model.platform.PlatformRepository
import com.zzsong.iam.server.infrastructure.repository.DatabaseIDGenerator
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

/**
 * @author 宋志宗 on 2022/6/1
 */
@Repository
class PlatformRepositoryImpl(
  private val idGenerator: DatabaseIDGenerator,
  private val mongoTemplate: ReactiveMongoTemplate,
) : PlatformRepository {

  override suspend fun save(platformDo: PlatformDo): PlatformDo {
    val id = platformDo.id
    if (id < 1) {
      platformDo.id = idGenerator.generate()
      return mongoTemplate.insert(platformDo).awaitSingle()
    }
    return mongoTemplate.save(platformDo).awaitSingle()
  }

  override suspend fun findByCode(code: String): PlatformDo? {
    val query = Query(Criteria("code").`is`(code))
    return mongoTemplate.findOne(query, PlatformDo::class.java).awaitSingleOrNull()
  }
}
