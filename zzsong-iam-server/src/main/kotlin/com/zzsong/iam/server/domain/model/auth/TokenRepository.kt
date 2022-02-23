package com.zzsong.iam.server.domain.model.auth

/**
 * @author 宋志宗 on 2022/2/23
 */
interface TokenRepository {

  suspend fun save(token: AccessTokenDo)

  suspend fun readAccessToken(value: String): AccessTokenDo?

  suspend fun delete(token: AccessTokenDo)

  suspend fun readRefreshToken(value: String): RefreshTokenDo?

  suspend fun cleanAllToken(clientId: String, userId: Long)
}
