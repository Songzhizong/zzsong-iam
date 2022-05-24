package com.zzsong.iam.server.port.controller

import cn.idealframework.util.Asserts
import com.zzsong.iam.server.application.AuthClientService
import com.zzsong.iam.server.application.LoginService
import com.zzsong.iam.server.domain.model.auth.AccessToken
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange

/**
 * 登录接口
 *
 * @author 宋志宗 on 2022/2/23
 */
@RestController
@RequestMapping("/iam/login")
class LoginController(
  private val loginService: LoginService,
  private val authClientService: AuthClientService
) {

  /**
   * 用户名密码登录
   * <pre>
   *   <p><b>请求示例</b></p>
   *   POST http://localhost:9091/iam/login/password
   *   Content-Type: application/x-www-form-urlencoded
   *   Authorization: Basic test test
   *
   *   username=songzhizong&password=songzhizong@password&rememberMe=true
   *   <p><b>响应示例</b></p>
   *   {
   *     "access_token": "197909198ed448b684cf1eff49acf375",
   *     "token_type": "Bearer",
   *     "refresh_token": "9235cf9dd273408baf3f3f1e9226b48d",
   *     "expires_in": 3599,
   *     "scope": "all"
   *   }
   * </pre>
   *
   * @param username 用户名
   * @param password 密码
   * @param rememberMe 是否生成refresh token
   * @param authorization 客户端token
   */
  @PostMapping("/password")
  suspend fun passwordLogin(
    username: String?,
    password: String?,
    rememberMe: Boolean?,
    @RequestHeader(name = "Authorization", required = false)
    authorization: String?,
    exchange: ServerWebExchange
  ): AccessToken {
    val formData = exchange.formData.awaitSingle()
    val username1 = formData.getFirst("username") ?: username
    val password1 = formData.getFirst("password") ?: password
    val rememberMe1 = formData.getFirst("rememberMe")?.toBoolean() ?: rememberMe ?: false
    Asserts.notBlank(username1, "用户名不能为空");username1!!
    Asserts.notBlank(password1, "密码不能为空");password1!!
    Asserts.notBlank(authorization, "Authorization头不能为空");authorization!!
    val authClientDo = authClientService.loadClient(authorization)
    val platform = authClientDo.platform
    val accessTokenDo =
      loginService.passwordLogin(platform, username1, password1, rememberMe1, authClientDo)
    return accessTokenDo.toAccessToken()
  }
}
