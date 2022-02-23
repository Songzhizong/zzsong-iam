package com.zzsong.iam.server.infrastructure.repository.impl

import cn.idealframework.date.DateTimes
import cn.idealframework.json.JsonUtils
import com.zzsong.iam.server.configure.IamServerProperties
import com.zzsong.iam.server.domain.model.auth.AccessTokenDo
import com.zzsong.iam.server.domain.model.auth.RefreshTokenDo
import com.zzsong.iam.server.domain.model.auth.TokenRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import javax.annotation.Nonnull

/**
 * @author 宋志宗 on 2022/2/23
 */
@Repository
class RedisTokenRepository(
  iamServerProperties: IamServerProperties,
  private val redisTemplate: ReactiveStringRedisTemplate
) : TokenRepository {
  companion object {
    val log: Logger = LoggerFactory.getLogger(RedisTokenRepository::class.java)
  }

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
            if (hasKey) null else member
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
            if (hasKey) null else member
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
        val accessTokenValue = token.value
        val expiresIn = token.expiresIn
        val accessTokenKey = generateAccessKey(accessTokenValue)
        val accessTokenJsonString = JsonUtils.toJsonString(token)
        val duration = Duration.ofSeconds(expiresIn.toLong())
        redisTemplate.opsForValue()
          .set(accessTokenKey, accessTokenJsonString, duration)
          .awaitSingleOrNull()
        // userId to access
        redisTemplate.opsForSet().add(userIdToAccessKey, accessTokenValue).awaitSingleOrNull()
        redisTemplate.expire(userIdToAccessKey, duration).awaitSingleOrNull()
      }
      // refresh token
      val async4 = async {
        val refreshToken = token.refreshToken
        if (refreshToken != null) {
          val refreshTokenValue = refreshToken.value
          val refreshTokenExpiresIn = refreshToken.expiresIn
          val refreshKey = generateRefreshKey(refreshTokenValue)
          val refreshTokenJsonString = JsonUtils.toJsonString(refreshToken)

          val duration = Duration.ofSeconds(refreshTokenExpiresIn.toLong())
          redisTemplate.opsForValue()
            .set(refreshKey, refreshTokenJsonString, duration)
            .awaitSingleOrNull()
          // userId to refresh
          redisTemplate.opsForSet().add(userIdToRefreshKey, refreshTokenValue).awaitSingleOrNull()
          redisTemplate.expire(userIdToRefreshKey, duration).awaitSingleOrNull()
        }
      }
      async1.await()
      async2.await()
      async3.await()
      async4.await()
    }
  }

  override suspend fun readAccessToken(value: String): AccessTokenDo? {
    val accessKey = generateAccessKey(value)
    val accessTokenJsonString = redisTemplate.opsForValue().get(accessKey).awaitSingleOrNull()
    if (accessTokenJsonString == null || accessTokenJsonString.isBlank()) {
      return null
    }
    val accessTokenDo = JsonUtils.parse(accessTokenJsonString, AccessTokenDo::class.java)
    accessTokenAutoRenewal(accessTokenDo)
    return accessTokenDo
  }

  private suspend fun accessTokenAutoRenewal(accessToken: AccessTokenDo) {
    if (!accessToken.autoRenewal) {
      return
    }
    val validity: Long = accessToken.validity
    val expiresIn = accessToken.expiresIn
    // 过期时间不到四分之一才刷新过期时间
    if (expiresIn > validity shr 2) {
      return
    }
    val expiration = DateTimes.now().plusSeconds(validity)
    accessToken.expiration = expiration
    val accessTokenValue = accessToken.value
    val accessTokenKey = generateAccessKey(accessTokenValue)
    val accessTokenJsonString = JsonUtils.toJsonString(accessToken)
    val authentication = accessToken.authentication
    val clientId = authentication.clientId
    val userId = authentication.userId
    val userIdToAccessKey = generateUserIdToAccessKey(clientId, userId)
    val duration = Duration.ofSeconds(expiresIn.toLong())
    coroutineScope {
      val as1 = async {
        redisTemplate.opsForValue()
          .set(accessTokenKey, accessTokenJsonString, duration)
          .awaitSingleOrNull()
      }
      val as2 = async {
        redisTemplate.expire(userIdToAccessKey, duration).awaitSingleOrNull()
      }
      as1.await()
      as2.await()
    }
    log.info("access token auto renewal {}", accessTokenValue)
  }

  override suspend fun delete(token: AccessTokenDo) {
    val accessTokenValue = token.value
    coroutineScope {
      val as1 = async {
        val accessKey = generateAccessKey(accessTokenValue)
        redisTemplate.delete(accessKey).awaitSingleOrNull()
      }
      val as2 = async {
        val refreshToken = token.refreshToken
        if (refreshToken != null) {
          val refreshTokenValue = refreshToken.value
          val refreshKey = generateRefreshKey(refreshTokenValue)
          redisTemplate.delete(refreshKey).awaitSingleOrNull()
        }
      }
      val as3 = async {
        val authentication = token.authentication
        val clientId = authentication.clientId
        val userId = authentication.userId
        val userIdToAccessKey = generateUserIdToAccessKey(clientId, userId)
        redisTemplate.opsForSet().remove(userIdToAccessKey, accessTokenValue).awaitSingleOrNull()
      }
      as1.await()
      as2.await()
      as3.await()
    }

  }

  override suspend fun readRefreshToken(value: String): RefreshTokenDo? {
    val refreshKey = generateRefreshKey(value)
    val json = redisTemplate.opsForValue().get(refreshKey).awaitSingleOrNull() ?: ""
    if (json.isBlank()) {
      return null
    }
    return JsonUtils.parse(json, RefreshTokenDo::class.java)
  }

  override suspend fun cleanAllToken(clientId: String, userId: Long) {
    // clean access token
    coroutineScope {
      val as1 = async {
        val userIdToAccessKey = generateUserIdToAccessKey(clientId, userId)
        val members = redisTemplate.opsForSet()
          .members(userIdToAccessKey)
          .collectList()
          .awaitSingleOrNull() ?: emptyList<String>()
        val as11 = async {
          redisTemplate.delete(userIdToAccessKey).awaitSingleOrNull()
        }
        val as12 = async {
          members.map {
            val accessKey = generateAccessKey(it)
            async { redisTemplate.delete(accessKey).awaitSingleOrNull() }
          }.forEach { it.await() }
        }
        as11.await()
        as12.await()
      }
      val as2 = async {
        // clean refresh token
        val userIdToRefreshKey = generateUserIdToRefreshKey(clientId, userId)
        val members = redisTemplate.opsForSet()
          .members(userIdToRefreshKey)
          .collectList()
          .awaitSingleOrNull() ?: emptyList<String>()
        val as21 = async {
          redisTemplate.delete(userIdToRefreshKey).awaitSingleOrNull()
        }
        val as22 = async {
          members.map {
            val refreshKey = generateRefreshKey(it)
            async { redisTemplate.delete(refreshKey).awaitSingleOrNull() }
          }.forEach { it.await() }
        }
        as21.await()
        as22.await()
      }
      as1.await()
      as2.await()
    }
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
