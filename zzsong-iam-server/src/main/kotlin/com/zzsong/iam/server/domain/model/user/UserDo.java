package com.zzsong.iam.server.domain.model.user;

import cn.idealframework.crypto.AES;
import cn.idealframework.lang.StringUtils;
import cn.idealframework.transmission.exception.UnauthorizedException;
import cn.idealframework.util.Asserts;
import cn.idealframework.util.CheckUtils;
import com.zzsong.iam.server.infrastructure.encoder.password.PasswordEncoder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 用户模型
 * <pre>
 *  <b>indexes:</b>
 *  NORMAL:
 *    - name
 *  UNIQUE
 *    - account
 *    - email
 *    - phone
 * </pre>
 *
 * @author 宋志宗 on 2022/2/22
 */
@Getter
@Setter
@Table("iam_user")
public class UserDo {
  private static final String AES_SEC = "AK!Cq6ywVZN.3Krd";

  /** 主键 */
  @Id
  private long id = -1;

  /** 用户姓名 */
  @Nonnull
  private String name = "";

  /** 账号 */
  @Nonnull
  private String account = "";

  /** 邮箱 */
  @Nonnull
  private String email = "";

  /** 手机号码 */
  @Nonnull
  private String phone = "";

  /** 密码 */
  @Nonnull
  private String password = "";

  /** 最近一次更新密码时间 */
  private long lastPasswordTime = -1;

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
   * 冻结用户
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
   * 验证密码
   *
   * @param rawPassword     明文密码
   * @param passwordEncoder 加密后的密码
   * @author 宋志宗 on 2022/2/23
   */
  public void authenticate(@Nonnull String rawPassword,
                           @Nonnull PasswordEncoder passwordEncoder) {
    String encodedPassword = getPassword();
    boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
    if (!matches) {
      throw new UnauthorizedException("用户名或密码错误");
    }
  }

  @Nonnull
  public static String encrypt(@Nonnull String text) {
    return AES.encrypt(text, AES_SEC);
  }

  @Nonnull
  public static String decrypt(@Nonnull String text) {
    return AES.decrypt(text, AES_SEC);
  }

  @Nonnull
  private String generateUid() {
    return "uuid:" + UUID.randomUUID().toString().replace("-", "");
  }

  @Transient
  private boolean isUid(@Nullable String uid) {
    return uid != null && uid.length() == 37 && uid.startsWith("uuid:");
  }

  @Nonnull
  public static UserDo create(@Nullable String name,
                              @Nullable String account,
                              @Nullable String email,
                              @Nullable String phone,
                              @Nonnull String password,
                              @Nonnull PasswordEncoder passwordEncoder) {
    boolean allBlank = StringUtils.isAllBlank(account, email, phone);
    Asserts.assertFalse(allBlank, "账号/邮箱/手机号不能同时为空");
    Asserts.notBlank(password, "密码不能为空");
    password = passwordEncoder.encode(password);
    UserDo userDo = new UserDo();
    userDo.setName(name);
    userDo.setAccount(account);
    userDo.setEmail(email);
    userDo.setPhone(phone);
    userDo.setPassword(password);
    userDo.setLastPasswordTime(System.currentTimeMillis());
    return userDo;
  }

  @Nonnull
  public User toUser() {
    User user = new User();
    user.setId(getId());
    user.setName(getName());
    user.setAccount(getAccount());
    user.setEmail(getEmail());
    user.setPhone(getPhone());
    user.setFrozen(getFrozen());
    user.setCreatedTime(getCreatedTime());
    user.setUpdatedTime(getUpdatedTime());
    return user;
  }

  public void setName(@Nullable String name) {
    if (StringUtils.isBlank(name)) {
      name = "";
    }
    this.name = name;
  }

  @Nonnull
  public String getAccount() {
    if (StringUtils.isBlank(account)) {
      return "";
    }
    String decrypt = decrypt(account);
    if (isUid(decrypt)) {
      return "";
    }
    return decrypt;
  }

  public void setAccount(@Nullable String account) {
    if (StringUtils.isBlank(account)) {
      if (isUid(this.getAccount())) {
        return;
      } else {
        account = generateUid();
      }
    } else {
      CheckUtils.checkAccount(account, "账号必须以英文字母开头,且长度在6~64之间");
    }
    String encrypt = encrypt(account);
    if (encrypt.equals(this.account)) {
      return;
    }
    this.account = encrypt;
  }

  @Nonnull
  public String getEmail() {
    if (StringUtils.isBlank(email)) {
      return "";
    }
    String decrypt = decrypt(email);
    if (isUid(decrypt)) {
      return "";
    }
    return decrypt;
  }

  public void setEmail(@Nullable String email) {
    if (StringUtils.isBlank(email)) {
      if (isUid(this.getEmail())) {
        return;
      } else {
        email = generateUid();
      }
    } else {
      CheckUtils.checkEmail(email, "邮箱格式不正确");
    }
    String encrypt = encrypt(email);
    if (encrypt.equals(this.email)) {
      return;
    }
    this.email = encrypt;
  }

  @Nonnull
  public String getPhone() {
    if (StringUtils.isBlank(phone)) {
      return "";
    }
    String decrypt = decrypt(phone);
    if (isUid(decrypt)) {
      return "";
    }
    return decrypt;
  }

  public void setPhone(@Nullable String phone) {
    if (StringUtils.isBlank(phone)) {
      if (isUid(this.getPhone())) {
        return;
      } else {
        phone = generateUid();
      }
    } else {
      CheckUtils.checkMobile(phone, "手机号格式错误");
    }
    String encrypt = encrypt(phone);
    if (encrypt.equals(this.phone)) {
      return;
    }
    this.phone = encrypt;
  }
}
