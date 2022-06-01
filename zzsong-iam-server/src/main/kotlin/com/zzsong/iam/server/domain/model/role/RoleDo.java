package com.zzsong.iam.server.domain.model.role;

import com.zzsong.iam.common.constants.RoleType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;

/**
 * 角色模型
 *
 * @author 宋志宗 on 2022/2/22
 */
@Slf4j
@Getter
@Setter
@Document(RoleDo.DOCUMENT_NAME)
@CompoundIndexes({
  @CompoundIndex(name = "name", def = "{name:1}"),
})
public class RoleDo {
  public static final String DOCUMENT_NAME = "zs_iam_role";

  /** 主键 */
  @Id
  private long id = -1;

  /** 角色名称 */
  @Nonnull
  private String name = "";

  /** 角色类型 */
  @Nonnull
  private RoleType type = RoleType.GENERAL;

  /** 是否启用 */
  private boolean enabled = true;

  /** 描述信息 */
  @Nullable
  private String description;

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
  public static RoleDo create(@Nonnull String name,
                              @Nullable String description) {
    RoleDo roleDo = new RoleDo();
    roleDo.setName(name);
    roleDo.setType(RoleType.GENERAL);
    roleDo.setDescription(description);
    return roleDo;
  }

  @Nonnull
  public static RoleDo createSuperAdmin(@Nonnull String name,
                                        @Nullable String description) {
    RoleDo roleDo = create(name, description);
    roleDo.setType(RoleType.SUPER_ADMIN);
    return roleDo;
  }
}
