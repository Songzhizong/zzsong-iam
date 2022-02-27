package com.zzsong.iam.server.domain.model.user

/**
 * @author 宋志宗 on 2022/2/26
 */
interface UserRoleRepository {

  suspend fun save(userRoleDo: UserRoleDo): UserRoleDo
}
