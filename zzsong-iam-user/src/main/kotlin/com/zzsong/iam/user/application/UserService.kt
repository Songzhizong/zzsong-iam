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
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * 用户管理
 *
 * @author 宋志宗 on 2022/6/7
 */
@Service
@Transactional(rollbackFor = [Throwable::class])
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
    // 校验账号是否已被使用
    val account = registerArgs.account?.ifBlank { null }
      ?.also { account ->
        userRepository.findByAccount(account)?.also {
          log.info("账号: {} 已被使用", account)
          throw BadRequestException("账号已被使用")
        }
      }
    // 校验手机号是否已被使用
    val phone = registerArgs.phone?.ifBlank { null }
      ?.also { phone ->
        userRepository.findByPhone(phone)?.also {
          log.info("手机号: {} 已被使用", phone)
          throw BadRequestException("手机号已被使用")
        }
      }
    // 校验邮箱是否已被使用
    val email = registerArgs.email?.ifBlank { null }
      ?.also { email ->
        userRepository.findByEmail(email)?.also {
          log.info("邮箱: {} 已被使用", email)
          throw BadRequestException("邮箱已被使用")
        }
      }
    // 如果密码不为空则进行安全校验, 为空则随机生成一个密码
    val password = registerArgs.password?.ifBlank { null }
      ?.also { CheckUtils.checkPassword(it, account) }
      ?: let { randomPassword().also { log.info("随机生成初始密码: {}", it) } }
    val name = registerArgs.name?.ifBlank { null }
    val nickname = registerArgs.nickname?.ifBlank { null }
    val profilePhoto = registerArgs.profilePhoto?.ifBlank { null }

    val userId = idGenerator.generate()
    val encodedPassword = passwordEncoder.encode(password)
    val tuple = UserDo.create(
      userId, null, account, phone, email, encodedPassword, name, nickname, profilePhoto
    )
    var userDo = tuple.value
    userDo = userRepository.save(userDo)
    transactionalEventPublisher.publish(tuple).awaitSingle()
    return userDo
  }

  @Transactional(propagation = Propagation.SUPPORTS)
  suspend fun getById(id: Long): UserDo {
    return userRepository.findById(id) ?: kotlin.run {
      log.info("用户: {} 不存在", id)
      throw ResourceNotFoundException("用户不存在")
    }
  }

  @Transactional(propagation = Propagation.SUPPORTS)
  suspend fun findAllById(ids: Collection<Long>): List<UserDo> {
    return userRepository.findAllById(ids)
  }

  private fun randomPassword(): String {
    val prefix = RandomStringUtils.randomAlphanumeric(4)
    val middle = RandomStringUtils.randomAscii(8)
    val postfix = RandomStringUtils.randomAlphanumeric(4)
    return prefix + middle + postfix
  }
}
