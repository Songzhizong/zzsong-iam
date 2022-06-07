package com.zzsong.iam.user.port.open

import cn.idealframework.transmission.Result
import cn.idealframework.util.Asserts
import com.zzsong.iam.common.user.User
import com.zzsong.iam.user.application.UserService
import com.zzsong.iam.user.domain.model.repository.UserRepository
import com.zzsong.iam.user.dto.args.RegisterArgs
import org.springframework.web.bind.annotation.*

/**
 * 用户管理
 *
 * @author 宋志宗 on 2022/6/7
 */
@RestController
@RequestMapping("/iam/user")
class UserController(
  private val userService: UserService,
  private val userRepository: UserRepository
) {

  /**
   * 判断账号是否已注册
   *
   * @author 宋志宗 on 2022/6/8
   */
  @GetMapping("/exists_account")
  suspend fun existsAccount(account: String?): Result<Boolean> {
    Asserts.notBlank(account, "账号为空");account!!
    val exists = userRepository.findByAccount(account)?.let { true } ?: false
    return Result.success(exists)
  }

  /**
   * 判断手机号是否已注册
   *
   * @author 宋志宗 on 2022/6/8
   */
  @GetMapping("/exists_phone")
  suspend fun existsPhone(phone: String?): Result<Boolean> {
    Asserts.notBlank(phone, "手机号为空");phone!!
    val exists = userRepository.findByPhone(phone)?.let { true } ?: false
    return Result.success(exists)
  }

  /**
   * 判断账号是否已注册
   *
   * @author 宋志宗 on 2022/6/8
   */
  @GetMapping("/exists_email")
  suspend fun existsEmail(email: String?): Result<Boolean> {
    Asserts.notBlank(email, "邮箱为空");email!!
    val exists = userRepository.findByEmail(email)?.let { true } ?: false
    return Result.success(exists)
  }

  /**
   * 注册用户
   *
   * @author 宋志宗 on 2022/6/8
   */
  @PostMapping("/register")
  suspend fun register(@RequestBody args: RegisterArgs): Result<User> {
    val userDo = userService.register(args)
    val user = userDo.toUser()
    return Result.success(user)
  }
}
