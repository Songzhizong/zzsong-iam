package com.zzsong.iam.server.infrastructure.repository.impl

import cn.idealframework.id.IDGenerator
import cn.idealframework.id.IDGeneratorFactory
import cn.idealframework.kotlin.toPageable
import com.zzsong.iam.server.domain.model.user.UserDo
import com.zzsong.iam.server.domain.model.user.UserRepository
import com.zzsong.iam.server.domain.model.user.args.QueryUserArgs
import com.zzsong.iam.server.infrastructure.repository.impl.r2dbc.R2dbcUserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.isEqual
import org.springframework.stereotype.Repository

/**
 * @author 宋志宗 on 2022/2/22
 */
@Repository
class UserRepositoryImpl(
  idGeneratorFactory: IDGeneratorFactory,
  private val template: R2dbcEntityTemplate,
  private val r2dbcUserRepository: R2dbcUserRepository
) : UserRepository {
  private val idGenerator: IDGenerator = idGeneratorFactory.getGenerator("database")

  override suspend fun save(userDo: UserDo): UserDo {
    if (userDo.id < 1) {
      userDo.id = idGenerator.generate()
      return template.insert(userDo).awaitSingle()
    }
    return template.update(userDo).awaitSingle()
  }

  override suspend fun delete(userDo: UserDo) {
    r2dbcUserRepository.deleteById(userDo.id).awaitSingleOrNull()
  }

  override suspend fun findById(id: Long): UserDo? {
    return r2dbcUserRepository.findById(id).awaitSingleOrNull()
  }

  override suspend fun findByPhone(phone: String): UserDo? {
    val encrypt = UserDo.encrypt(phone)
    return r2dbcUserRepository.findByPhone(encrypt).awaitSingleOrNull()
  }

  override suspend fun findByAccount(account: String): UserDo? {
    val encrypt = UserDo.encrypt(account)
    return r2dbcUserRepository.findByAccount(encrypt).awaitSingleOrNull()
  }

  override suspend fun findByEmail(email: String): UserDo? {
    val encrypt = UserDo.encrypt(email)
    return r2dbcUserRepository.findByEmail(encrypt).awaitSingleOrNull()
  }

  override suspend fun findByUniqueIdentification(uniqueIdentification: String): UserDo? {
    val encrypt = UserDo.encrypt(uniqueIdentification)
    return r2dbcUserRepository.findByAccountOrEmailOrPhone(encrypt, encrypt, encrypt)
      .awaitSingleOrNull()
  }

  override suspend fun query(args: QueryUserArgs): Page<UserDo> {
    val paging = args.paging.descBy("id")
    // 动态查询条件
    val name = args.name
    val account = args.account
    val email = args.email
    val phone = args.phone
    var criteria = Criteria.empty()
    if (name != null && name.isNotBlank()) {
      criteria = criteria.and("name").like("$name%")
    }
    if (account != null && account.isNotBlank()) {
      val encrypt = UserDo.encrypt(account)
      criteria = criteria.and("account").isEqual(encrypt)
    }
    if (email != null && email.isNotBlank()) {
      val encrypt = UserDo.encrypt(email)
      criteria = criteria.and("email").isEqual(encrypt)
    }
    if (phone != null && phone.isNotBlank()) {
      val encrypt = UserDo.encrypt(phone)
      criteria = criteria.and("phone").isEqual(encrypt)
    }

    val countQuery = Query.query(criteria)
    val pageable = paging.toPageable()
    val selectQuery = countQuery.with(pageable)
    return coroutineScope {
      val count = async {
        template.count(countQuery, UserDo::class.java).awaitSingleOrNull() ?: 0
      }
      val list = async {
        template.select(selectQuery, UserDo::class.java)
          .collectList().awaitSingleOrNull() ?: listOf()
      }
      PageImpl(list.await(), pageable, count.await())
    }
  }
}
