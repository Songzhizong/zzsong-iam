package com.zzsong.iam.user.port.api

import cn.idealframework.lang.StringUtils
import cn.idealframework.transmission.Result
import cn.idealframework.util.Asserts
import com.zzsong.iam.common.user.User
import com.zzsong.iam.user.application.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 内部用户接口
 *
 * @author 宋志宗 on 2022/6/7
 * @ignore
 */
@RestController
@RequestMapping("/api/iam/user")
class UserApi(private val userService: UserService) {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(UserApi::class.java)
  }

  /**
   * 通过id获取用户信息
   *
   * @param id 用户id
   * @author 宋志宗 on 2022/6/7
   */
  @GetMapping("/id")
  suspend fun getById(id: Long?): Result<User> {
    Asserts.nonnull(id, "用户id为空");id!!
    val userDo = userService.getById(id)
    val user = userDo.toUser()
    return Result.success(user)
  }

  /**
   * 批量获取用户信息
   *
   * @param ids 用户id集合, 多个之间 , 分割
   * @author 宋志宗 on 2022/6/7
   */
  @GetMapping("/ids")
  suspend fun finAllById(ids: String?): Result<List<User>> {
    if (ids == null || ids.isBlank()) {
      log.info("ids为空")
      return Result.success(emptyList())
    }
    val idSet = StringUtils.split(ids, ",")
      .mapTo(HashSet()) { it.toLong() }
    val userDoList = userService.findAllById(idSet)
    val users = userDoList.map { it.toUser() }
    return Result.success(users)
  }
}
