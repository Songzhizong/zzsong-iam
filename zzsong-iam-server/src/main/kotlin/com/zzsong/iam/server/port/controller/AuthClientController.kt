package com.zzsong.iam.server.port.controller

import cn.idealframework.transmission.Result
import cn.idealframework.util.Asserts
import com.zzsong.iam.server.application.AuthClientService
import com.zzsong.iam.server.application.dto.args.CreateAuthClientArgs
import com.zzsong.iam.server.domain.model.auth.AuthClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 客户端管理
 *
 * @author 宋志宗 on 2022/2/23
 */
@RestController
@RequestMapping("/iam/client")
class AuthClientController(private val authClientService: AuthClientService) {

  /** 创建客户端 */
  @PostMapping("/create")
  suspend fun create(@RequestBody(required = false) args: CreateAuthClientArgs?): Result<AuthClient> {
    Asserts.nonnull(args, "请求body不能为空");args!!
    val authClientDo = authClientService.create(args)
    val authClient = authClientDo.toAuthClient()
    return Result.data(authClient)
  }

  /** 删除客户端 */
  @PostMapping("/delete")
  suspend fun delete(id: Long?): Result<Void> {
    require(id != null) { "id不能为空" }
    authClientService.delete(id)
    return Result.success()
  }
}
