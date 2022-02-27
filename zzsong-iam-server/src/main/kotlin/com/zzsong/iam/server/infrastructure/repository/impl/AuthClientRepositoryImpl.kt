package com.zzsong.iam.server.infrastructure.repository.impl

import com.zzsong.iam.server.domain.model.auth.AuthClientDo
import com.zzsong.iam.server.domain.model.auth.AuthClientRepository
import com.zzsong.iam.server.infrastructure.repository.DatabaseIDGenerator
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Repository

/**
 * @author 宋志宗 on 2022/2/23
 */
@Repository
class AuthClientRepositoryImpl(
  private val template: R2dbcEntityTemplate,
  private val databaseIDGenerator: DatabaseIDGenerator,
) : AuthClientRepository {

  override suspend fun save(authClientDo: AuthClientDo): AuthClientDo {
    if (authClientDo.id < 1) {
      authClientDo.id = databaseIDGenerator.generate()
      return template.insert(authClientDo).awaitSingle()
    }
    return template.update(authClientDo).awaitSingle()
  }

  override suspend fun delete(authClientDo: AuthClientDo) {
    template.delete(authClientDo).awaitSingleOrNull()
  }

  override suspend fun findByClientId(clientId: String): AuthClientDo? {
    val query = Query.query(Criteria.where("clientId").`is`(clientId))
    return template.selectOne(query, AuthClientDo::class.java).awaitSingleOrNull()
  }

  override suspend fun findById(id: Long): AuthClientDo? {
    val query = Query.query(Criteria.where("id").`is`(id))
    return template.selectOne(query, AuthClientDo::class.java).awaitSingleOrNull()
  }
}
