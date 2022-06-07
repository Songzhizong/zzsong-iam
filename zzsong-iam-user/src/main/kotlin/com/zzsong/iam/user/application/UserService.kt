package com.zzsong.iam.user.application

import cn.idealframework.event.publisher.ReactiveTransactionalEventPublisher
import cn.idealframework.lang.RandomStringUtils
import cn.idealframework.transmission.exception.BadRequestException
import cn.idealframework.transmission.exception.ResourceNotFoundException
import cn.idealframework.util.CheckUtils
import com.zzsong.iam.infrastructure.IamIDGenerator
import com.zzsong.iam.user.domain.model.UserDo
import com.zzsong.iam.user.domain.model.repository.UserRepository
import com.zzsong.iam.user.dto.args.RegisterArgs
import com.zzsong.iam.user.infrastructure.password.encoder.PasswordEncoder
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * 用户管理
 *
 * @author 宋志宗 on 2022/6/7
 */
@Service
class UserService(
  private val idGenerator: IamIDGenerator,
  private val userRepository: UserRepository,
  private val passwordEncoder: PasswordEncoder,
  private val transactionalEventPublisher: ReactiveTransactionalEventPublisher
) {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(UserService::class.java)
  }

  suspend fun register(registerArgs: RegisterArgs): UserDo {
    val account = registerArgs.account
    val phone = registerArgs.phone
    val email = registerArgs.email
    var password = registerArgs.password
    var randomPassword = false
    if (password == null || password.isBlank()) {
      val prefix = RandomStringUtils.randomAlphanumeric(4)
      val middle = RandomStringUtils.randomAscii(8)
      val postfix = RandomStringUtils.randomAlphanumeric(4)
      password = prefix + middle + postfix
      randomPassword = true
    } else {
      CheckUtils.checkPassword(password, account)
    }
    val name = registerArgs.name
    val nickname = registerArgs.nickname
    val profilePhoto = registerArgs.profilePhoto
    if (account != null && account.isNotBlank()) {
      userRepository.findByAccount(account)?.also {
        log.info("账号: {} 已被使用", account)
        throw BadRequestException("账号已被使用")
      }
    }
    if (phone != null && phone.isNotBlank()) {
      userRepository.findByPhone(phone)?.also {
        log.info("手机号: {} 已被使用", phone)
        throw BadRequestException("手机号已被使用")
      }
    }
    if (email != null && email.isNotBlank()) {
      userRepository.findByEmail(email)?.also {
        log.info("邮箱: {} 已被使用", email)
        throw BadRequestException("邮箱已被使用")
      }
    }
    val userId = idGenerator.generate()
    val encodedPassword = passwordEncoder.encode(password)
    val tuple = UserDo.create(
      userId, null, account, phone, email, encodedPassword, name, nickname, profilePhoto
    )
    var userDo = tuple.value
    userDo = userRepository.save(userDo)
    transactionalEventPublisher.publish(tuple).awaitSingle()
    if (randomPassword) {
      log.info("随机生成初始密码: {}", password)
    }
    return userDo
  }

  suspend fun getById(id: Long): UserDo {
    return userRepository.findById(id) ?: kotlin.run {
      log.info("用户: {} 不存在", id)
      throw ResourceNotFoundException("用户不存在")
    }
  }

  suspend fun findAllById(ids: Collection<Long>): List<UserDo> {
    return userRepository.findAllById(ids)
  }
}
