package com.zzsong.iam.server.domain.model.menu;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * @author 宋志宗 on 2022/2/22
 */
@Getter
@Setter
@Table("iam_terminal")
public class TerminalDo {

  /** 主键 */
  @Id
  private long id = -1;

  /** 终端名称 */
  private String name = "";

  /** 终端描述 */
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
}
