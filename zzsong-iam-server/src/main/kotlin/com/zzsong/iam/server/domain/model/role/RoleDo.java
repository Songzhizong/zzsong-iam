package com.zzsong.iam.server.domain.model.role;

import cn.idealframework.lang.StringUtils;
import com.zzsong.iam.server.domain.model.tenant.TenantDo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;

/**
 * 角色模型
 * <pre>
 *  <b>indexes:</b>
 *  NORMAL:
 *    - tenantId
 *    - name
 * </pre>
 *
 * @author 宋志宗 on 2022/2/22
 */
@Getter
@Setter
@Table("iam_role")
public class RoleDo {

  /** 主键 */
  @Id
  private long id = -1;

  /** 归属的租户id */
  private long tenantId = -1;

  /** 角色名称 */
  @Nonnull
  private String name = "";

  /** 角色类型 */
  @Nonnull
  private RoleType type = RoleType.GENERAL;

  /** 是否启用 */
  private boolean enabled = true;

  /** 描述信息 */
  @Nonnull
  private String description = "";

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
  public static RoleDo create(@Nonnull TenantDo tenant,
                              @Nonnull String name,
                              @Nullable String description) {
    RoleDo roleDo = new RoleDo();
    roleDo.setTenantId(tenant.getId());
    roleDo.setName(name);
    roleDo.setType(RoleType.GENERAL);
    roleDo.setDescription(description);
    return roleDo;
  }

  @Nonnull
  public static RoleDo createSuperAdmin(@Nonnull TenantDo tenant,
                                        @Nonnull String name,
                                        @Nullable String description) {
    RoleDo roleDo = create(tenant, name, description);
    roleDo.setType(RoleType.SUPER_ADMIN);
    return roleDo;
  }

  public void setDescription(@Nullable String description) {
    if (StringUtils.isBlank(description)) {
      description = "";
    }
    this.description = description;
  }
}
