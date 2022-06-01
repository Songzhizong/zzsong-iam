package com.zzsong.iam.server.domain.model.menu;

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

import javax.annotation.Nullable;
import java.time.LocalDateTime;

/**
 * @author 宋志宗 on 2022/2/22
 */
@Slf4j
@Getter
@Setter
@Document(TerminalDo.DOCUMENT_NAME)
@CompoundIndexes({
  @CompoundIndex(name = "name", def = "{name:1}"),
})
public class TerminalDo {
  public static final String DOCUMENT_NAME = "zs_iam_terminal";

  /** 主键 */
  @Id
  private long id = -1;

  /** 终端名称 */
  private String name = "";

  /** 终端描述 */
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
