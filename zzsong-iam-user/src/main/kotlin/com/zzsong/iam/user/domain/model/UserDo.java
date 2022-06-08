package com.zzsong.iam.user.domain.model;

import cn.idealframework.crypto.AES;
import cn.idealframework.date.DateTimes;
import cn.idealframework.event.tuple.EventTuple;
import cn.idealframework.lang.StringUtils;
import cn.idealframework.util.Asserts;
import cn.idealframework.util.CheckUtils;
import com.zzsong.iam.common.user.User;
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
 * 用户信息
 *
 * @author 宋志宗 on 2022/6/7
 */
@Slf4j
@Getter
@Setter
@Document(UserDo.DOCUMENT_NAME)
@CompoundIndexes({
  @CompoundIndex(name = "uk_account", def = "{account:1}", sparse = true, unique = true),
  @CompoundIndex(name = "uk_phone", def = "{phone:1}", sparse = true, unique = true),
  @CompoundIndex(name = "uk_email", def = "{email:1}", sparse = true, unique = true),
  @CompoundIndex(name = "personId", def = "{personId:1}", sparse = true),
  @CompoundIndex(name = "name", def = "{name:1}", sparse = true),
})
public class UserDo {
  public static final String DOCUMENT_NAME = "zs_iam_user";
  private static final String SEC = "AK!Cq6ywVZN.3Krd";

  /** 主键 */
  @Id
  private long id = -1;

  /** 人员id */
  @Nullable
  private Long personId = null;

  /** 账号 */
  @Nullable
  private String account = null;

  /** 手机号 */
  @Nullable
  private String phone = null;

  /** 邮箱 */
  @Nullable
  private String email = null;

  /** 密码 */
  @Nonnull
  private String password = "";

  /** 最近一次密码变更时间 */
  private LocalDateTime passwordChangedTime;

  /** 姓名 */
  @Nullable
  private String name = null;

  /** 昵称 */
  @Nullable
  private String nickname = null;

  /** 头像 */
  @Nullable
  private String profilePhoto = null;

  /** 是否已被冻结 */
  private boolean frozen = false;

  /** 版本号 */
  @Version
  private long version = 0;

  /** 创建时间 */
  @CreatedDate
  private LocalDateTime createdTime = null;

  /** 更新时间 */
  @LastModifiedDate
  private LocalDateTime updatedTime = null;

  @Nonnull
  public static EventTuple<UserDo> create(long id,
                                          @Nullable Long personId,
                                          @Nullable String account,
                                          @Nullable String phone,
                                          @Nullable String email,
                                          @Nonnull String password,
                                          @Nullable String name,
                                          @Nullable String nickname,
                                          @Nullable String profilePhoto) {
    boolean expression = StringUtils.isAllBlank(account, phone, email);
    Asserts.assertFalse(expression, "账号、手机号、邮箱不能都为空");
    UserDo userDo = new UserDo();
    userDo.setId(id);
    userDo.setPersonId(personId);
    userDo.setAccount(account);
    userDo.setPhone(phone);
    userDo.setEmail(email);
    // 设置密码之前必须要设置用户id
    userDo.setPassword(password);
    userDo.setPasswordChangedTime(DateTimes.now());
    userDo.setName(name);
    userDo.setNickname(nickname);
    userDo.setProfilePhoto(profilePhoto);
    userDo.setFrozen(false);
    return EventTuple.of(userDo);
  }

  @Nonnull
  public User toUser() {
    User user = new User();
    user.setId(getId());
    user.setPersonId(getPersonId());
    user.setAccount(getAccount());
    user.setEmail(getEmail());
    user.setPhone(getPhone());
    user.setName(getName());
    user.setNickname(getNickname());
    user.setProfilePhoto(getProfilePhoto());
    user.setFrozen(getFrozen());
    user.setCreatedTime(getCreatedTime());
    user.setUpdatedTime(getUpdatedTime());
    return user;
  }

  @Nonnull
  public static String encryptAccount(@Nonnull String text) {
    return AES.encrypt("acc:" + text, SEC);
  }

  @Nonnull
  public static String decryptAccount(@Nonnull String text) {
    return AES.decrypt(text, SEC).substring(4);
  }

  @Nonnull
  public static String encryptPhone(@Nonnull String text) {
    return AES.encrypt("pho:" + text, SEC);
  }

  @Nonnull
  public static String decryptPhone(@Nonnull String text) {
    return AES.decrypt(text, SEC).substring(4);
  }

  @Nonnull
  public static String encryptEmail(@Nonnull String text) {
    return AES.encrypt("eml:" + text, SEC);
  }

  @Nonnull
  public static String decryptEmail(@Nonnull String text) {
    return AES.decrypt(text, SEC).substring(4);
  }

  @Nullable
  public String getAccount() {
    if (StringUtils.isBlank(account)) {
      return null;
    }
    return decryptAccount(account);
  }

  public void setAccount(@Nullable String account) {
    if (StringUtils.isBlank(account)) {
      this.account = null;
      return;
    }
    Asserts.assertTrue(CheckUtils.checkAccount(account), () -> {
      log.info("无效的手机号格式: {}", account);
      return "无效的账号格式";
    });
    this.account = encryptAccount(account);
  }

  @Nullable
  public String getPhone() {
    if (StringUtils.isBlank(phone)) {
      return null;
    }
    return decryptPhone(phone);
  }

  public void setPhone(@Nullable String phone) {
    if (StringUtils.isBlank(phone)) {
      this.phone = null;
      return;
    }
    Asserts.assertTrue(CheckUtils.checkMobile(phone), () -> {
      log.info("无效的手机号格式: {}", phone);
      return "无效的手机号格式";
    });
    this.phone = encryptPhone(phone);
  }

  @Nullable
  public String getEmail() {
    if (StringUtils.isBlank(email)) {
      return null;
    }
    return decryptEmail(email);
  }

  public void setEmail(@Nullable String email) {
    if (StringUtils.isBlank(email)) {
      this.email = null;
      return;
    }
    Asserts.assertTrue(CheckUtils.checkEmail(email), () -> {
      log.info("无效的邮箱格式: {}", email);
      return "无效的邮箱格式";
    });
    this.email = encryptEmail(email);
  }

  @Nonnull
  public String getPassword() {
    String secret = AES.generateSecret(SEC + getId());
    return AES.decrypt(password, secret);
  }

  public void setPassword(@Nonnull String password) {
    Asserts.assertTrue(id > 0, "未设置用户id");
    String secret = AES.generateSecret(id + SEC + id);
    this.password = AES.encrypt(password, secret);
  }
}
