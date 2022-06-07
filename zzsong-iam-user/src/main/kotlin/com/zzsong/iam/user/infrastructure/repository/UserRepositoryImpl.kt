package com.zzsong.iam.user.infrastructure.repository

import cn.idealframework.transmission.SpringPageConverter
import com.zzsong.iam.infrastructure.IamIDGenerator
import com.zzsong.iam.user.domain.model.UserDo
import com.zzsong.iam.user.domain.model.repository.UserRepository
import com.zzsong.iam.user.dto.args.QueryUserArgs
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

/**
 * @author 宋志宗 on 2022/6/7
 */
@Repository
class UserRepositoryImpl(
  private val idGenerator: IamIDGenerator,
  private val mongoTemplate: ReactiveMongoTemplate,
) : UserRepository {

  override suspend fun save(userDo: UserDo): UserDo {
    if (userDo.id < 0) {
      userDo.id = idGenerator.generate()
      return mongoTemplate.insert(userDo).awaitSingle()
    }
    return mongoTemplate.save(userDo).awaitSingle()
  }

  override suspend fun findById(id: Long): UserDo? {
    val criteria = Criteria("id").`is`(id)
    return mongoTemplate.findOne(Query.query(criteria), UserDo::class.java).awaitSingleOrNull()
  }

  override suspend fun findAllById(ids: Collection<Long>): List<UserDo> {
    val criteria = Criteria("id").`in`(ids)
    return mongoTemplate.find(Query.query(criteria), UserDo::class.java).collectList().awaitSingle()
  }

  override suspend fun findByAccount(account: String): UserDo? {
    val criteria = Criteria("account").`is`(UserDo.encryptAccount(account))
    return mongoTemplate.findOne(Query.query(criteria), UserDo::class.java).awaitSingleOrNull()
  }

  override suspend fun findByPhone(phone: String): UserDo? {
    val criteria = Criteria("phone").`is`(UserDo.encryptPhone(phone))
    return mongoTemplate.findOne(Query.query(criteria), UserDo::class.java).awaitSingleOrNull()
  }

  override suspend fun findByEmail(email: String): UserDo? {
    val criteria = Criteria("email").`is`(UserDo.encryptEmail(email))
    return mongoTemplate.findOne(Query.query(criteria), UserDo::class.java).awaitSingleOrNull()
  }

  override suspend fun findByIdent(ident: String): UserDo? {
    val criteria = Criteria().orOperator(
      Criteria("account").`is`(UserDo.encryptAccount(ident)),
      Criteria("phone").`is`(UserDo.encryptPhone(ident)),
      Criteria("email").`is`(UserDo.encryptEmail(ident)),
    )
    return mongoTemplate.findOne(Query.query(criteria), UserDo::class.java).awaitSingleOrNull()
  }

  override suspend fun count(args: QueryUserArgs): Long {
    val criteria = generateCriteria(args)
    return mongoTemplate.count(Query(criteria), UserDo::class.java).awaitSingle()
  }

  override suspend fun query(args: QueryUserArgs): List<UserDo> {
    val paging = args.paging
    val criteria = generateCriteria(args)
    val pageable = SpringPageConverter.pageable(paging)
    val query = Query(criteria).with(pageable)
    return mongoTemplate.find(query, UserDo::class.java)
      .collectList().awaitSingle()
  }

  override suspend fun queryPage(args: QueryUserArgs): Page<UserDo> {
    val paging = args.paging
    val criteria = generateCriteria(args)
    val pageable = SpringPageConverter.pageable(paging)
    val query = Query(criteria).with(pageable)
    val content = mongoTemplate.find(query, UserDo::class.java)
      .collectList().awaitSingle()
    if (content.size < paging.pageSize) {
      return PageImpl(content, pageable, 0)
    }
    val total = mongoTemplate.count(Query(criteria), UserDo::class.java).awaitSingle()
    return PageImpl(content, pageable, total)
  }

  private fun generateCriteria(args: QueryUserArgs): Criteria {
    val name = args.name
    val account = args.account
    val phone = args.phone
    val email = args.email
    var criteria = Criteria()
    if (name != null && name.isNotBlank()) {
      criteria = criteria.and("name").`is`(name)
    }
    val orCriteriaList = ArrayList<Criteria>()
    if (account != null && account.isNotBlank()) {
      orCriteriaList.add(Criteria("account").`is`(UserDo.encryptAccount(account)))
    }
    if (phone != null && phone.isNotBlank()) {
      orCriteriaList.add(Criteria("phone").`is`(UserDo.encryptPhone(phone)))
    }
    if (email != null && email.isNotBlank()) {
      orCriteriaList.add(Criteria("email").`is`(UserDo.encryptEmail(email)))
    }
    if (orCriteriaList.isNotEmpty()) {
      criteria.orOperator(*orCriteriaList.toTypedArray())
    }
    return criteria
  }
}
