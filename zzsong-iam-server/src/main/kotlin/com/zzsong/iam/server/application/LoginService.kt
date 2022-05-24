package com.zzsong.iam.server.application

import cn.idealframework.transmission.exception.BadRequestException
import com.zzsong.iam.server.domain.model.auth.AccessTokenDo
import com.zzsong.iam.server.domain.model.auth.AuthClientDo
import com.zzsong.iam.server.domain.model.auth.Authentication
import com.zzsong.iam.server.domain.model.auth.TokenRepository
import com.zzsong.iam.server.domain.model.user.UserDo
import com.zzsong.iam.server.domain.model.user.UserRepository
import com.zzsong.iam.server.infrastructure.encoder.password.PasswordEncoder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * @author 宋志宗 on 2022/2/23
 */
@Service
class LoginService(
  private val userRepository: UserRepository,
  private val tokenRepository: TokenRepository,
  private val passwordEncoder: PasswordEncoder
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(LoginService::class.java)
  }

  suspend fun passwordLogin(
    platform: String,
    username: String,
    password: String,
    rememberMe: Boolean,
    authClientDo: AuthClientDo
  ): AccessTokenDo {
    val userDo = userRepository.findByUniqueIdentification(platform, username)
    if (userDo == null) {
      log.info("密码登录失败, 找不到用户 {}", username)
      throw BadRequestException("用户名或密码错误")
    }
    userDo.authenticate(password, passwordEncoder)
    return this.login(rememberMe, userDo, authClientDo)
  }

  suspend fun login(
    rememberMe: Boolean,
    userDo: UserDo,
    authClientDo: AuthClientDo
  ): AccessTokenDo {
    val clientId = authClientDo.clientId
    val platform = authClientDo.platform
    val userId = userDo.id
    val username = userDo.name
    val authentication = Authentication.create(userId, username, platform, clientId)
    if (!authClientDo.acceptRepetitionLogin) {
      tokenRepository.cleanAllToken(clientId, userId)
    }
    val accessTokenDo = AccessTokenDo.create(rememberMe, authClientDo, authentication)
    tokenRepository.save(accessTokenDo)
    return accessTokenDo
  }
}
