package com.zzsong.iam.server.infrastructure.repository.impl.r2dbc

import com.zzsong.iam.server.domain.model.user.UserDo
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

/**
 * @author 宋志宗 on 2022/2/22
 */
@Repository
interface R2dbcUserRepository : R2dbcRepository<UserDo, Long> {

  fun findByPlatformAndPhone(platform: String, phone: String): Mono<UserDo>

  fun findByPlatformAndAccount(platform: String, account: String): Mono<UserDo>

  fun findByPlatformAndEmail(platform: String, email: String): Mono<UserDo>

  fun findByAccountOrEmailOrPhone(account: String, email: String, phone: String): Mono<UserDo>
}
