package com.zzsong.iam.server.domain.model.platform;

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
 * 平台模型
 *
 * @author 宋志宗 on 2022/2/22
 */
@Slf4j
@Getter
@Setter
@Document(PlatformDo.DOCUMENT_NAME)
@CompoundIndexes({
  @CompoundIndex(name = "code", def = "{code:1}", unique = true),
})
public class PlatformDo {
  public static final String DOCUMENT_NAME = "zs_iam_platform";

  /** 主键 */
  @Id
  private long id = -1;

  /** 平台编码 */
  @Nonnull
  private String code = "";

  /** 平台名称 */
  @Nonnull
  private String name = "";

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
}
