package com.zzsong.iam.server.domain.model.user;

import com.zzsong.iam.server.domain.model.role.RoleDo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;

/**
 * 用户角色关系
 * <pre>
 *  <b>indexes:</b>
 *  UNIQUE
 *    - userId,roleId
 *  NORMAL:
 *    - roleId
 *    - tenantId
 * </pre>
 *
 * @author 宋志宗 on 2022/2/26
 */
@Getter
@Setter
@Table("iam_user_role")
public class UserRoleDo {

  /** 主键 */
  @Id
  private long id = -1;

  /** 租户id */
  private long tenantId = -1;

  /** 角色id */
  private long roleId = -1;

  /** 用户id */
  private long userId = -1;

  /** 乐观锁版本 */
  @Version
  private long version = 0;

  /** 创建时间 */
  @CreatedDate
  private LocalDateTime createdTime;

  /** 更新时间 */
  @LastModifiedDate
  private LocalDateTime updatedTime;

  @Nonnull
  public static UserRoleDo create(@Nonnull UserDo user,
                                  @Nonnull RoleDo role) {
    UserRoleDo userRoleDo = new UserRoleDo();
    userRoleDo.setTenantId(role.getTenantId());
    userRoleDo.setRoleId(role.getId());
    userRoleDo.setUserId(user.getId());
    return userRoleDo;
  }
}
