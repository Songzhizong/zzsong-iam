package com.zzsong.iam.person.infrastructure.repository

import com.zzsong.iam.infrastructure.IamIDGenerator
import com.zzsong.iam.person.domian.model.PersonDo
import com.zzsong.iam.person.domian.model.PersonRepository
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

/**
 * @author 宋志宗 on 2022/6/7
 */
@Repository
class PersonRepositoryImpl(
  private val idGenerator: IamIDGenerator,
  private val mongoTemplate: ReactiveMongoTemplate,
) : PersonRepository {

  override suspend fun save(person: PersonDo): PersonDo {
    if (person.id < 0) {
      person.id = idGenerator.generate()
      return mongoTemplate.insert(person).awaitSingle()
    }
    return mongoTemplate.save(person).awaitSingle()
  }

  override suspend fun delete(person: PersonDo) {
    mongoTemplate.remove(person).awaitSingleOrNull()
  }

  override suspend fun findById(id: Long): PersonDo? {
    val criteria = Criteria("id").`is`(id)
    return mongoTemplate.findOne(Query(criteria), PersonDo::class.java).awaitSingleOrNull()
  }

}
