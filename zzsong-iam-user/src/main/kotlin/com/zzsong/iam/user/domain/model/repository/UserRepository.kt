package com.zzsong.iam.user.domain.model.repository

import com.zzsong.iam.user.domain.model.UserDo
import com.zzsong.iam.user.dto.args.QueryUserArgs
import org.springframework.data.domain.Page

/**
 * @author 宋志宗 on 2022/6/7
 */
interface UserRepository {

  suspend fun save(userDo: UserDo): UserDo

  suspend fun findById(id: Long): UserDo?

  suspend fun findAllById(ids: Collection<Long>): List<UserDo>

  suspend fun findByAccount(account: String): UserDo?

  suspend fun findByPhone(phone: String): UserDo?

  suspend fun findByEmail(email: String): UserDo?

  suspend fun findByIdent(ident: String): UserDo?

  suspend fun count(args: QueryUserArgs): Long

  suspend fun query(args: QueryUserArgs): List<UserDo>

  suspend fun queryPage(args: QueryUserArgs): Page<UserDo>
}
