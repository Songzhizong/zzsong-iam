package com.zzsong.iam.server.application

import cn.idealframework.transmission.exception.BadRequestException
import cn.idealframework.util.Asserts
import com.zzsong.iam.server.application.dto.args.RegisterArgs
import com.zzsong.iam.server.domain.model.user.UserDo
import com.zzsong.iam.server.domain.model.user.UserRepository
import com.zzsong.iam.server.domain.model.user.args.QueryUserArgs
import com.zzsong.iam.server.infrastructure.encoder.password.PasswordEncoder
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

/**
 * @author 宋志宗 on 2022/2/22
 */
@Service
class UserService(
  private val userRepository: UserRepository,
  private val passwordEncoder: PasswordEncoder
) {

  /**
   * 用户注册, 手机号与密码不能为空
   *
   * @author 宋志宗 on 2022/2/23
   */
  suspend fun register(args: RegisterArgs): UserDo {
    val phone = args.phone ?: ""
    val password = args.password ?: ""
    Asserts.notBlank(phone, "注册手机号不能为空")
    Asserts.notBlank(password, "密码不能为空")
    val name = args.name
    val account = args.account
    val email = args.email
    // 检查 账号/邮箱/手机号 是否被占用
    coroutineScope {
      val findByPhone = async { userRepository.findByPhone(phone) }
      val findByAccount = async {
        if (account == null || account.isBlank()) null else userRepository.findByAccount(account)
      }
      val findByEmail = async {
        if (email == null || email.isBlank()) null else userRepository.findByEmail(email)
      }
      findByPhone.await()?.also { throw BadRequestException("手机号已被使用") }
      findByAccount.await()?.also { throw BadRequestException("邮箱已被使用") }
      findByEmail.await()?.also { throw BadRequestException("邮箱已被使用") }
    }
    val userDo = UserDo.create(name, account, email, phone, password, passwordEncoder)
    return userRepository.save(userDo)
  }

  /**
   * 查询用户列表
   *
   * @author 宋志宗 on 2022/2/23
   */
  suspend fun query(args: QueryUserArgs): Page<UserDo> {
    return userRepository.query(args)
  }
}
