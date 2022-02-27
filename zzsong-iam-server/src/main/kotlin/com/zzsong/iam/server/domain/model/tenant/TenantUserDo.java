package com.zzsong.iam.server.domain.model.tenant;

import com.zzsong.iam.server.domain.model.user.UserDo;
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
 * 租户
 * <pre>
 *  <b>indexes:</b>
 *  UNIQUE
 *    - tenantId,userId
 *  NORMAL:
 *    - userId
 *    - phone
 * </pre>
 *
 * @author 宋志宗 on 2022/2/22
 */
@Getter
@Setter
@Table("iam_tenant_user")
public class TenantUserDo {

  /** 主键 */
  @Id
  private long id = -1;

  /** 租户id */
  private long tenantId = -1;

  /** 用户id */
  private long userId = -1;

  /** 用户姓名 */
  @Nonnull
  private String name = "";

  /** 手机号码 */
  @Nonnull
  private String phone = "";

  /** 用户是否已被租户冻结 */
  private boolean frozen = false;

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
  public static TenantUserDo create(@Nonnull TenantDo tenant,
                                    @Nonnull UserDo user) {
    TenantUserDo tenantUserDo = new TenantUserDo();
    tenantUserDo.setTenantId(tenant.getId());
    tenantUserDo.setUserId(user.getId());
    tenantUserDo.setName(user.getName());
    tenantUserDo.setPhone(user.getPhone());
    return tenantUserDo;
  }
}
