package com.zzsong.iam.server.application

import cn.idealframework.transmission.exception.BadRequestException
import cn.idealframework.util.Asserts
import cn.idealframework.util.CheckUtils
import com.zzsong.iam.server.application.dto.args.RegisterArgs
import com.zzsong.iam.server.domain.model.user.UserDo
import com.zzsong.iam.server.domain.model.user.UserRepository
import com.zzsong.iam.server.domain.model.user.args.QueryUserArgs
import com.zzsong.iam.server.infrastructure.encoder.password.PasswordEncoder
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
  companion object {
    val log: Logger = LoggerFactory.getLogger(UserService::class.java)
  }

  /** 验证账号是否可用 */
  suspend fun checkAccount(account: String) {
    CheckUtils.checkAccount(account, "无效的账号格式")
    val userDo = userRepository.findByAccount(account)
    userDo?.also {
      log.info("账号 {} 已被使用", account)
      throw BadRequestException("该账号已被使用")
    }
  }

  /** 验证邮箱是否可用 */
  suspend fun checkEmail(email: String) {
    CheckUtils.checkEmail(email, "无效的邮箱格式")
    val userDo = userRepository.findByEmail(email)
    userDo?.also {
      log.info("邮箱 {} 已被使用", email)
      throw BadRequestException("该邮箱已被使用")
    }
  }

  /** 验证手机号是否可用 */
  suspend fun checkPhone(phone: String) {
    CheckUtils.checkAccount(phone, "无效的手机号格式")
    val userDo = userRepository.findByPhone(phone)
    userDo?.also {
      log.info("手机号 {} 已被使用", phone)
      throw BadRequestException("该手机号已被使用")
    }
  }

  /**
   * 用户注册
   *
   * 手机号与密码不能为空, 账号/邮箱/手机号不能重复
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
    this.check(phone, account, email)
    val userDo = UserDo.create(name, account, email, phone, password, passwordEncoder)
    return userRepository.save(userDo)
  }

  /** 检验 账号/邮箱/手机号是否可用 */
  private suspend fun check(phone: String, account: String?, email: String?) {
    coroutineScope {
      val findByPhone = async { userRepository.findByPhone(phone) }
      val findByAccount = async {
        if (account == null || account.isBlank()) null else userRepository.findByAccount(account)
      }
      val findByEmail = async {
        if (email == null || email.isBlank()) null else userRepository.findByEmail(email)
      }
      findByPhone.await()?.also {
        log.info("手机号 {} 已被使用", phone)
        throw BadRequestException("手机号已被使用")
      }
      findByAccount.await()?.also {
        log.info("账号 {} 已被使用", account)
        throw BadRequestException("账号已被使用")
      }
      findByEmail.await()?.also {
        log.info("邮箱 {} 已被使用", email)
        throw BadRequestException("邮箱已被使用")
      }
    }
  }

  /** 冻结用户 */
  suspend fun freeze(id: Long) {
    val userDo = userRepository.findRequiredById(id)
    if (userDo.frozen) {
      log.info("用户 {} 当前已是冻结状态", id)
      return
    }
    userDo.freeze()
    userRepository.save(userDo)
    log.info("成功冻结用户 {}", id)
  }

  /** 解冻用户 */
  suspend fun unfreeze(id: Long) {
    val userDo = userRepository.findRequiredById(id)
    if (!userDo.frozen) {
      log.info("用户 {} 当前非冻结状态", id)
      return
    }
    userDo.unfreeze()
    userRepository.save(userDo)
    log.info("成功解冻用户 {}", id)
  }

  /** 查询用户列表 */
  suspend fun query(args: QueryUserArgs): Page<UserDo> {
    val page = userRepository.query(args)
    val size = page.content.size
    val totalElements = page.totalElements
    log.info("查询出用户数据 {}条, 符合条件的用户总数为 {}", size, totalElements)
    return page
  }
}
