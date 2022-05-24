package com.zzsong.iam.server.infrastructure.repository.impl.r2dbc

import com.zzsong.iam.server.domain.model.platform.PlatformDo
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

/**
 * @author 宋志宗 on 2022/4/1
 */
@Repository
interface R2dbcPlatformRepository : R2dbcRepository<PlatformDo, Long> {

  fun findByCode(code: String): Mono<PlatformDo>
}
