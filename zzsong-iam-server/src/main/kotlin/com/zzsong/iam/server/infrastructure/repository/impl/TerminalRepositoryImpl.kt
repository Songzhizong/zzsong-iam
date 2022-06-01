package com.zzsong.iam.server.infrastructure.repository.impl

import com.zzsong.iam.server.domain.model.menu.TerminalRepository
import com.zzsong.iam.server.infrastructure.repository.DatabaseIDGenerator
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Repository

/**
 * @author 宋志宗 on 2022/6/1
 */
@Repository
class TerminalRepositoryImpl(
  private val idGenerator: DatabaseIDGenerator,
  private val mongoTemplate: ReactiveMongoTemplate,
) : TerminalRepository {

}
