package com.zzsong.iam.server.domain.model.auth

/**
 * @author 宋志宗 on 2022/2/23
 */
interface AuthClientRepository {

  suspend fun save(authClientDo: AuthClientDo): AuthClientDo

  suspend fun delete(authClientDo: AuthClientDo)

  suspend fun findByClientId(clientId: String): AuthClientDo?

  suspend fun findById(id: Long): AuthClientDo?
}
