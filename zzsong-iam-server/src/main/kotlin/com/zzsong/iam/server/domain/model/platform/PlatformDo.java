package com.zzsong.iam.server.domain.model.platform;

import cn.idealframework.lang.StringUtils;
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
 *  UNIQUE:
 *    - code
 *  NORMAL:
 *    - name
 * </pre>
 *
 * @author 宋志宗 on 2022/2/22
 */
@Getter
@Setter
@Table("iam_platform")
public class PlatformDo {

  /** 主键 */
  @Id
  private long id = -1;

  /** 平台编码 */
  @Nonnull
  private String code = "";

  /** 平台名称 */
  @Nonnull
  private String name = "";

  /** 平台拥有人用户id */
  private long ownerUserId;

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

  public void setDescription(@Nullable String description) {
    if (StringUtils.isBlank(description)) {
      description = "";
    }
    this.description = description;
  }
}
