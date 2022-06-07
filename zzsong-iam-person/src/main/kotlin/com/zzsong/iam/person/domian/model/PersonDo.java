package com.zzsong.iam.person.domian.model;

import cn.idealframework.crypto.AES;
import lombok.Getter;
import lombok.Setter;
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
 * @author 宋志宗 on 2022/6/7
 */
@Getter
@Setter
@Document(PersonDo.DOCUMENT_NAME)
@CompoundIndexes({
  @CompoundIndex(name = "uk_ident", def = "{ident:1}", unique = true),
})
public class PersonDo {
  public static final String DOCUMENT_NAME = "zs_iam_person";
  private static final String SEC = "RQaiRH8LZPL!3X6d";

  /** 主键 */
  @Id
  private long id = -1;

  /** 人员唯一识别标识 */
  @Nonnull
  private String ident = "";

  /** 姓名 */
  @Nullable
  private String name;

  /** 手机号 */
  @Nullable
  private String phone;

  /** 头像 */
  @Nullable
  private String profilePhoto;

  /** 版本号 */
  @Version
  private long version = 0;

  /** 创建时间 */
  @CreatedDate
  private LocalDateTime createdTime;

  /** 更新时间 */
  @LastModifiedDate
  private LocalDateTime updatedTime;

  @Nonnull
  public static String generatePhoneIdent(@Nonnull String phone) {
    return encrypt("phone" + phone);
  }

  @Nonnull
  public static String encrypt(@Nonnull String text) {
    return AES.encrypt(text, SEC);
  }

  @Nonnull
  public static String decrypt(@Nonnull String text) {
    return AES.decrypt(text, SEC);
  }
}
