package com.zzsong.iam.server.domain.model.person;

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
 * 人员模型
 * <pre>
 *  <b>indexes:</b>
 *  UNIQUE
 *    - phone
 *  NORMAL:
 *    - name
 * </pre>
 *
 * @author 宋志宗 on 2022/2/22
 */
@Getter
@Setter
@Table("iam_person")
public class PersonDo {

  /** 主键 */
  @Id
  private long id = -1;

  /** 用户姓名 */
  @Nonnull
  private String name = "";

  /** 手机号码 */
  @Nonnull
  private String phone = "";

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
