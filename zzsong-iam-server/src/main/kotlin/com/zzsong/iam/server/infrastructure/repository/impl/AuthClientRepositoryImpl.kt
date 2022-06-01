package com.zzsong.iam.server.infrastructure.repository.impl

import com.zzsong.iam.server.domain.model.auth.AuthClientDo
import com.zzsong.iam.server.domain.model.auth.AuthClientRepository
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
class AuthClientRepositoryImpl(
  private val idGenerator: DatabaseIDGenerator,
  private val mongoTemplate: ReactiveMongoTemplate,
) : AuthClientRepository {

  override suspend fun save(authClientDo: AuthClientDo): AuthClientDo {
    val id = authClientDo.id
    if (id < 1) {
      authClientDo.id = idGenerator.generate()
      return mongoTemplate.insert(authClientDo).awaitSingle()
    }
    return mongoTemplate.save(authClientDo).awaitSingle()
  }

  override suspend fun delete(authClientDo: AuthClientDo) {
    mongoTemplate.remove(authClientDo).awaitSingle()
  }

  override suspend fun findByClientId(clientId: String): AuthClientDo? {
    val query = Query(Criteria("clientId").`is`(clientId))
    return mongoTemplate.findOne(query, AuthClientDo::class.java).awaitSingleOrNull()
  }

  override suspend fun findById(id: Long): AuthClientDo? {
    val query = Query(Criteria("id").`is`(id))
    return mongoTemplate.findOne(query, AuthClientDo::class.java).awaitSingleOrNull()
  }
}
