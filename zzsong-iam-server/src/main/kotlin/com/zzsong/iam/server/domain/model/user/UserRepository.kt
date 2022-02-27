package com.zzsong.iam.server.domain.model.user

import cn.idealframework.transmission.exception.ResourceNotFoundException
import com.zzsong.iam.server.domain.model.user.args.QueryUserArgs
import org.springframework.data.domain.Page

/**
 * 用户仓库
 *
 * @author 宋志宗 on 2022/2/22
 */
interface UserRepository {

  /** 保存用户对象 */
  suspend fun save(userDo: UserDo): UserDo

  /** 删除用户 */
  suspend fun delete(userDo: UserDo)

  /** 主键查询 */
  suspend fun findById(id: Long): UserDo?

  /** 主键查询 */
  suspend fun findRequiredById(id: Long): UserDo {
    return findById(id) ?: throw ResourceNotFoundException("用户不存在")
  }

  /** 通过id列表批量查询 */
  suspend fun findAllById(ids: Iterable<Long>): List<UserDo>

  /** 手机号查询 */
  suspend fun findByPhone(phone: String): UserDo?

  /** 账号查询 */
  suspend fun findByAccount(account: String): UserDo?

  /** 邮箱查询 */
  suspend fun findByEmail(email: String): UserDo?

  /**
   * 通过唯一身份标识查询用户信息
   * account/email/phone
   */
  suspend fun findByUniqueIdentification(uniqueIdentification: String): UserDo?

  /** 分页查询 */
  suspend fun query(args: QueryUserArgs): Page<UserDo>
}
