package com.zzsong.iam.server.infrastructure.repository.impl.r2dbc

import com.zzsong.iam.server.domain.model.tenant.TenantUserDo
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository

/**
 * @author 宋志宗 on 2022/2/26
 */
@Repository
interface R2dbcTenantUserRepository : R2dbcRepository<TenantUserDo, Long> {
}
