package com.zzsong.iam.server.port.controller

import cn.idealframework.kotlin.toPageResult
import cn.idealframework.transmission.PageResult
import cn.idealframework.transmission.Result
import cn.idealframework.util.Asserts
import com.zzsong.iam.server.application.UserService
import com.zzsong.iam.server.application.dto.args.RegisterArgs
import com.zzsong.iam.server.domain.model.user.User
import com.zzsong.iam.server.domain.model.user.args.QueryUserArgs
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 用户管理
 *
 * @author 宋志宗 on 2022/2/23
 */
@RestController
@RequestMapping("/iam/user")
class UserController(private val userService: UserService) {

  /**
   * 注册
   *
   * @author 宋志宗 on 2022/2/23
   */
  @PostMapping("/register")
  suspend fun register(@RequestBody(required = false) args: RegisterArgs?): Result<User> {
    Asserts.nonnull(args, "body不能为空")
    val userDo = userService.register(args!!)
    val user = userDo.toUser()
    return Result.data(user)
  }

  /**
   * 分页查询
   *
   * @author 宋志宗 on 2022/2/23
   */
  @PostMapping("/query")
  suspend fun query(@RequestBody(required = false) args: QueryUserArgs?): PageResult<User> {
    val query = args ?: QueryUserArgs()
    val page = userService.query(query)
    return page.map { it.toUser() }.toPageResult()
  }
}
