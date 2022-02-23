package com.zzsong.iam.server.infrastructure.repository.impl

import com.zzsong.iam.server.configure.IamServerProperties
import com.zzsong.iam.server.domain.model.auth.AccessTokenDo
import com.zzsong.iam.server.domain.model.auth.RefreshTokenDo
import com.zzsong.iam.server.domain.model.auth.TokenRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Repository
import javax.annotation.Nonnull

/**
 * @author 宋志宗 on 2022/2/23
 */
@Repository
class RedisTokenRepository(
  iamServerProperties: IamServerProperties,
  private val redisTemplate: ReactiveStringRedisTemplate
) : TokenRepository {
  private val keyPrefix: String

  init {
    var tokenPrefix = iamServerProperties.tokenPrefix
    while (tokenPrefix.endsWith(":")) {
      tokenPrefix = tokenPrefix.substring(0, tokenPrefix.length - 1)
    }
    keyPrefix = tokenPrefix
  }

  override suspend fun save(token: AccessTokenDo) {
    val authentication = token.authentication
    val clientId = authentication.clientId
    val userId = authentication.userId

    coroutineScope {
      // reset user to access
      val userIdToAccessKey = generateUserIdToAccessKey(clientId, userId)
      val async1 = async {
        val members = redisTemplate.opsForSet()
          .members(userIdToAccessKey).collectList().awaitSingleOrNull() ?: emptyList()
        val array = members.map { member ->
          async {
            val accessKey = generateAccessKey(member)
            val hasKey = redisTemplate.hasKey(accessKey).awaitSingleOrNull() ?: false
            if (hasKey) member else null
          }
        }.mapNotNull { it.await() }.toTypedArray()
        if (array.isNotEmpty()) {
          @Suppress("UNCHECKED_CAST")
          redisTemplate.opsForSet()
            .remove(userIdToAccessKey, array as Array<Any>).awaitSingleOrNull()
        }
      }
      // reset user to refresh
      val userIdToRefreshKey = generateUserIdToRefreshKey(clientId, userId)
      val async2 = async {
        val members = redisTemplate.opsForSet()
          .members(userIdToRefreshKey).collectList().awaitSingleOrNull() ?: emptyList()
        val array = members.map { member ->
          async {
            val refreshKey = generateRefreshKey(member)
            val hasKey = redisTemplate.hasKey(refreshKey).awaitSingleOrNull() ?: false
            if (hasKey) member else null
          }
        }.mapNotNull { it.await() }.toTypedArray()
        if (array.isNotEmpty()) {
          @Suppress("UNCHECKED_CAST")
          redisTemplate.opsForSet()
            .remove(userIdToRefreshKey, array as Array<Any>).awaitSingleOrNull()
        }
      }
      // access token
      val async3 = async {

      }
      // refresh token
      val async4 = async {

      }
      async1.await()
      async2.await()
      async3.await()
      async4.await()
    }

    TODO("Not yet implemented")
  }

  override suspend fun readAccessToken(value: String): AccessTokenDo? {
    TODO("Not yet implemented")
  }

  override suspend fun delete(token: AccessTokenDo) {
    TODO("Not yet implemented")
  }

  override suspend fun readRefreshToken(value: String): RefreshTokenDo? {
    TODO("Not yet implemented")
  }

  override suspend fun cleanAllToken(clientId: String, userId: Long) {
    TODO("Not yet implemented")
  }

  @Nonnull
  private fun generateAccessKey(@Nonnull accessToken: String): String {
    return "$keyPrefix:access:$accessToken"
  }

  @Nonnull
  private fun generateRefreshKey(@Nonnull refreshToken: String): String {
    return "$keyPrefix:refresh:$refreshToken"
  }

  @Nonnull
  private fun generateUserIdToAccessKey(@Nonnull clientId: String, userId: Long): String {
    return "$keyPrefix:user_id_to_access:$clientId:$userId"
  }

  @Nonnull
  private fun generateUserIdToRefreshKey(@Nonnull clientId: String, userId: Long): String {
    return "$keyPrefix:user_id_to_refresh:$clientId:$userId"
  }
}
