package com.zzsong.iam.server.port.controller

import cn.idealframework.kotlin.toPageResult
import cn.idealframework.transmission.PageResult
import cn.idealframework.transmission.Result
import cn.idealframework.util.Asserts
import com.zzsong.iam.common.args.RegisterArgs
import com.zzsong.iam.common.pojo.User
import com.zzsong.iam.server.application.UserService
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
   * 验证账号是否可用
   *
   * @author 宋志宗 on 2022/2/23
   */
  @PostMapping("/check/account")
  suspend fun checkAccount(account: String?): Result<Void> {
    Asserts.notBlank(account, "账号不能为空");account!!
    userService.checkAccount("", account)
    return Result.success()
  }

  /**
   * 验证邮箱是否可用
   *
   * @author 宋志宗 on 2022/2/23
   */
  @PostMapping("/check/email")
  suspend fun checkEmail(email: String?): Result<Void> {
    Asserts.notBlank(email, "邮箱不能为空");email!!
    userService.checkEmail("", email)
    return Result.success()
  }

  /**
   * 验证手机号是否可用
   *
   * @author 宋志宗 on 2022/2/23
   */
  @PostMapping("/check/phone")
  suspend fun checkPhone(phone: String?): Result<Void> {
    Asserts.notBlank(phone, "手机号不能为空");phone!!
    userService.checkPhone("", phone)
    return Result.success()
  }

  /**
   * 注册
   *
   * @author 宋志宗 on 2022/2/23
   */
  @PostMapping("/register")
  suspend fun register(@RequestBody(required = false) args: RegisterArgs?): Result<User> {
    Asserts.nonnull(args, "body不能为空");args!!
    val userDo = userService.register("", args)
    val user = userDo.toUser()
    return Result.data(user)
  }

  /**
   * 冻结
   *
   * @author 宋志宗 on 2022/2/23
   */
  @PostMapping("/freeze")
  suspend fun freeze(id: Long?): Result<Void> {
    Asserts.nonnull(id, "用户id不能为空")
    userService.freeze(id!!)
    return Result.success()
  }

  /**
   * 解冻
   *
   * @author 宋志宗 on 2022/2/23
   */
  @PostMapping("/unfreeze")
  suspend fun unfreeze(id: Long?): Result<Void> {
    Asserts.nonnull(id, "用户id不能为空")
    userService.unfreeze(id!!)
    return Result.success()
  }

  /**
   * 分页查询
   *
   * @author 宋志宗 on 2022/2/23
   */
  @PostMapping("/query")
  suspend fun query(
    platform: String?,
    @RequestBody(required = false)
    args: QueryUserArgs?
  ): PageResult<User> {
    Asserts.notBlank(platform, "平台编码不能为空");platform!!
    val query = args ?: QueryUserArgs()
    val page = userService.query(platform, query)
    return page.map { it.toUser() }.toPageResult()
  }
}
