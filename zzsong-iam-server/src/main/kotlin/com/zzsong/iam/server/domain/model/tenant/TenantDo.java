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
import javax.annotation.Nullable;
import java.time.LocalDateTime;

/**
 * 租户
 * <pre>
 *  <b>indexes:</b>
 *  NORMAL:
 *    - pid
 *    - router
 *    - name
 * </pre>
 *
 * @author 宋志宗 on 2022/2/22
 */
@Getter
@Setter
@Table("iam_tenant")
public class TenantDo {
  public static final long NONE_TENANT_ID = 0;
  public static final String TENANT_ROUTER_CONNECTOR = "-";

  /** 主键 */
  @Id
  private long id = -1;

  /** 父租户id */
  private long pid = -1;

  /** 路由 */
  private String router = "";

  /** 租户名称 */
  private String name;

  /** 拥有人用户id */
  private long ownerUserId;

  /** 是否已被冻结 */
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

  /**
   * 冻结
   *
   * @author 宋志宗 on 2022/2/23
   */
  public void freeze() {
    this.setFrozen(true);
  }

  /**
   * 解冻
   *
   * @author 宋志宗 on 2022/2/23
   */
  public void unfreeze() {
    this.setFrozen(false);
  }

  /**
   * 变更租户拥有人
   *
   * @param owner 拥有人用户信息
   * @author 宋志宗 on 2022/2/26
   */
  public void changeOwner(@Nonnull UserDo owner) {
    this.setOwnerUserId(owner.getId());
  }

  @Nonnull
  public static TenantDo create(@Nonnull UserDo owner,
                                @Nullable TenantDo parent,
                                @Nonnull String name) {
    TenantDo tenantDo = new TenantDo();
    if (parent != null) {
      long parentId = parent.getId();
      tenantDo.setPid(parentId);
      String parentRouter = parent.getRouter();
      tenantDo.setRouter(parentRouter + TENANT_ROUTER_CONNECTOR + parentId);
    }
    tenantDo.setName(name);
    tenantDo.setOwnerUserId(owner.getId());
    tenantDo.setFrozen(owner.getFrozen());
    return tenantDo;
  }

  @Nonnull
  public Tenant toTenant() {
    Tenant tenant = new Tenant();
    tenant.setId(getId());
    tenant.setPid(getPid());
    tenant.setName(getName());
    tenant.setOwnerUserId(getOwnerUserId());
    tenant.setFrozen(getFrozen());
    tenant.setCreatedTime(getCreatedTime());
    tenant.setUpdatedTime(getUpdatedTime());
    return tenant;
  }
}
