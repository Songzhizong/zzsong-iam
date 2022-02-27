package com.zzsong.iam.server.application

import cn.idealframework.transmission.exception.ResourceNotFoundException
import cn.idealframework.util.Asserts
import com.zzsong.iam.server.application.dto.args.CreateTenantArgs
import com.zzsong.iam.server.domain.model.role.RoleDo
import com.zzsong.iam.server.domain.model.role.RoleRepository
import com.zzsong.iam.server.domain.model.tenant.TenantDo
import com.zzsong.iam.server.domain.model.tenant.TenantRepository
import com.zzsong.iam.server.domain.model.tenant.TenantUserDo
import com.zzsong.iam.server.domain.model.tenant.TenantUserRepository
import com.zzsong.iam.server.domain.model.user.UserRepository
import com.zzsong.iam.server.domain.model.user.UserRoleDo
import com.zzsong.iam.server.domain.model.user.UserRoleRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author 宋志宗 on 2022/2/26
 */
@Service
@Transactional(rollbackFor = [Throwable::class])
class TenantService(
  private val userRepository: UserRepository,
  private val roleRepository: RoleRepository,
  private val tenantRepository: TenantRepository,
  private val userRoleRepository: UserRoleRepository,
  private val tenantUserRepository: TenantUserRepository
) {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(TenantService::class.java)
  }

  /**
   * 新建租户
   *
   * @author 宋志宗 on 2022/2/26
   */
  suspend fun create(args: CreateTenantArgs): TenantDo {
    val pid = args.pid
    val name = args.name
    val ownerUserId = args.ownerUserId
    Asserts.notBlank(name, "租户名称不能为空");name!!
    Asserts.nonnull(ownerUserId, "租户管理员id不能为空");ownerUserId!!
    val owner = userRepository.findRequiredById(ownerUserId)
    val parent = if (pid == null) null else {
      tenantRepository.findById(pid) ?: kotlin.run {
        log.info("指定的父租户 {} 不存在", pid)
        throw ResourceNotFoundException("指定的父租户不存在")
      }
    }
    // 创建租户
    val tenantDo = TenantDo.create(owner, parent, name)
    tenantRepository.save(tenantDo)
    // 用户加入租户
    val tenantUserDo = TenantUserDo.create(tenantDo, owner)
    tenantUserRepository.save(tenantUserDo)
    // 创建租户超管角色
    val roleDo = RoleDo.createSuperAdmin(tenantDo, "超级管理员", "租户超管")
    roleRepository.save(roleDo)
    // 为用户分配租户超管角色
    val userRoleDo = UserRoleDo.create(owner, roleDo)
    userRoleRepository.save(userRoleDo)
    return tenantDo
  }
}
