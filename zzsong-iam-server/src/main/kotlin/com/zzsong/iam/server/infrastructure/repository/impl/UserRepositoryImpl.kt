package com.zzsong.iam.server.infrastructure.repository.impl

import cn.idealframework.kotlin.toPageable
import com.zzsong.iam.server.domain.model.user.UserDo
import com.zzsong.iam.server.domain.model.user.UserRepository
import com.zzsong.iam.server.domain.model.user.args.QueryUserArgs
import com.zzsong.iam.server.infrastructure.repository.DatabaseIDGenerator
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

/**
 * @author 宋志宗 on 2022/6/1
 */
@Repository
class UserRepositoryImpl(
  private val idGenerator: DatabaseIDGenerator,
  private val mongoTemplate: ReactiveMongoTemplate,
) : UserRepository {

  override suspend fun save(userDo: UserDo): UserDo {
    val id = userDo.id
    if (id < 1) {
      userDo.id = idGenerator.generate()
      return mongoTemplate.insert(userDo).awaitSingle()
    }
    return mongoTemplate.save(userDo).awaitSingle()
  }

  override suspend fun delete(userDo: UserDo) {
    mongoTemplate.remove(userDo).awaitSingle()
  }

  override suspend fun findById(id: Long): UserDo? {
    val criteria = Criteria("id").`is`(id)
    return mongoTemplate.findOne(Query(criteria), UserDo::class.java).awaitSingleOrNull()
  }

  override suspend fun findAllById(ids: Iterable<Long>): List<UserDo> {
    val criteria = Criteria("id").`in`(ids)
    return mongoTemplate.find(Query(criteria), UserDo::class.java).collectList().awaitSingle()
  }

  override suspend fun findByPlatformAndPhone(platform: String, phone: String): UserDo? {
    val criteria = Criteria("platform").`is`(platform)
      .and("phone").`is`(UserDo.encryptPhone(phone))
    return mongoTemplate.findOne(Query(criteria), UserDo::class.java).awaitSingleOrNull()
  }

  override suspend fun findByPlatformAndAccount(platform: String, account: String): UserDo? {
    val criteria = Criteria("platform").`is`(platform)
      .and("account").`is`(UserDo.encryptAccount(account))
    return mongoTemplate.findOne(Query(criteria), UserDo::class.java).awaitSingleOrNull()
  }

  override suspend fun findByPlatformAndEmail(platform: String, email: String): UserDo? {
    val criteria = Criteria("platform").`is`(platform)
      .and("email").`is`(UserDo.encryptEmail(email))
    return mongoTemplate.findOne(Query(criteria), UserDo::class.java).awaitSingleOrNull()
  }

  override suspend fun findByUniqueIdentification(
    platform: String,
    uniqueIdentification: String
  ): UserDo? {
    val criteria = Criteria("platform").`is`(platform)
      .orOperator(
        Criteria("account").`is`(UserDo.encryptAccount(uniqueIdentification)),
        Criteria("phone").`is`(UserDo.encryptPhone(uniqueIdentification)),
        Criteria("email").`is`(UserDo.encryptEmail(uniqueIdentification)),
      )
    return mongoTemplate.findOne(Query(criteria), UserDo::class.java).awaitSingleOrNull()
  }

  override suspend fun query(platform: String, args: QueryUserArgs): Page<UserDo> {
    val paging = args.paging.descBy("id")
    val name = args.name
    val account = args.account
    val email = args.email
    val phone = args.phone
    var criteria = Criteria("platform").`is`(platform)
    val or = ArrayList<Criteria>()
    if (name != null && name.isNotBlank()) {
      or.add(Criteria("name").regex("^.*$name.*$"))
    }
    if (account != null && account.isNotBlank()) {
      or.add(Criteria("account").`is`(account))
    }
    if (email != null && email.isNotBlank()) {
      or.add(Criteria("email").`is`(email))
    }
    if (phone != null && phone.isNotBlank()) {
      or.add(Criteria("phone").`is`(phone))
    }
    if (or.isNotEmpty()) {
      criteria = criteria.orOperator(*or.toTypedArray())
    }
    val pageable = paging.toPageable()
    val query = Query(criteria).with(pageable)
    return coroutineScope {
      val contentAsync = async {
        mongoTemplate.find(query, UserDo::class.java).collectList().awaitSingle()
      }
      val countAsync = async {
        mongoTemplate.count(Query(criteria), UserDo::class.java).awaitSingle()
      }
      val content = contentAsync.await()
      val count = countAsync.await()
      PageImpl(content, pageable, count)
    }
  }
}
