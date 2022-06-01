package com.zzsong.iam.server.application

import cn.idealframework.lang.StringUtils
import cn.idealframework.transmission.exception.BadRequestException
import cn.idealframework.transmission.exception.UnauthorizedException
import cn.idealframework.util.Asserts
import com.zzsong.iam.common.args.CreateAuthClientArgs
import com.zzsong.iam.server.domain.model.auth.AuthClientDo
import com.zzsong.iam.server.domain.model.auth.AuthClientRepository
import com.zzsong.iam.server.infrastructure.encoder.password.PasswordEncoder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

/**
 * @author 宋志宗 on 2022/2/23
 */
@Service
class AuthClientService(
  private val authClientRepository: AuthClientRepository,
  private val passwordEncoder: PasswordEncoder
) {
  companion object {
    private const val basicAuthorizationPrefix = "Basic "
    private const val basicAuthorizationPrefixLength = basicAuthorizationPrefix.length
    val log: Logger = LoggerFactory.getLogger(AuthClientService::class.java)
  }

  /** 新建 */
  suspend fun create(args: CreateAuthClientArgs): AuthClientDo {
    val platform = args.platform
    val name = args.name
    val clientId = args.clientId
    val clientSecret = args.clientSecret
    Asserts.notBlank(platform, "归属平台不能为空不能为空")
    Asserts.notBlank(name, "客户端名称不能为空");name!!
    Asserts.notBlank(clientId, "客户端唯一id不能为空");clientId!!
    Asserts.notBlank(clientSecret, "客户端密码不能为空");clientSecret!!
    authClientRepository.findByClientId(clientId)?.also {
      log.info("客户端id {} 已被使用", clientId)
      throw BadRequestException("客户端id已被使用")
    }
    val authClientDo = AuthClientDo.create(
      platform,
      name,
      clientId,
      clientSecret,
      args.accessTokenValidity,
      args.refreshTokenValidity,
      args.isAccessTokenAutoRenewal,
      args.isAcceptRepetitionLogin,
      passwordEncoder
    )
    return authClientRepository.save(authClientDo)
  }

  suspend fun loadClient(authorization: String): AuthClientDo {
    val startsWith = authorization.startsWith(basicAuthorizationPrefix)
    Asserts.assertTrue(startsWith, "Authorization token不合法, 应为Basic 开头")
    val encoded = authorization.substring(basicAuthorizationPrefixLength)
    val bytes = Base64.getDecoder().decode(encoded)
    val decoded = String(bytes, Charsets.UTF_8)
    val split = StringUtils.split(decoded, ":", 2)
    val clientId = split[0]
    val clientSecret = split[1]
    val authClientDo = authClientRepository.findByClientId(clientId)
    if (authClientDo == null) {
      log.info("客户端 {} 不存在", clientId)
      throw UnauthorizedException("无效的Authorization token")
    }
    authClientDo.authenticate(clientSecret, passwordEncoder)
    return authClientDo
  }

  /** 删除 */
  suspend fun delete(id: Long) {
    authClientRepository.findById(id)
      ?.also {
        authClientRepository.delete(it)
        log.info("成功删除客户端 [{} {}]", id, it.name)
      } ?: kotlin.run { log.info("客户端 {} 不存在", id) }
  }
}
