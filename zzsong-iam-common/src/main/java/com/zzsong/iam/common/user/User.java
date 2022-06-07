package com.zzsong.iam.common.user;

import cn.idealframework.util.DesensitizeUtils;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.time.LocalDateTime;

/**
 * 用户信息
 *
 * @author 宋志宗 on 2022/2/23
 */
@Getter
@Setter
public class User {

  private long id;

  /** 人员id */
  @Nullable
  private Long personId;

  /** 账号 */
  @Nullable
  private String account;

  /** 手机号 */
  @Nullable
  private String phone;

  /** 邮箱 */
  @Nullable
  private String email;

  /** 姓名 */
  @Nullable
  private String name;

  /** 昵称 */
  @Nullable
  private String nickname;

  /** 头像 */
  @Nullable
  private String profilePhoto;

  /** 是否已被冻结 */
  private boolean frozen = false;

  /** 创建时间 */
  private LocalDateTime createdTime;

  /** 更新时间 */
  private LocalDateTime updatedTime;

  /**
   * 信息脱敏
   *
   * @author 宋志宗 on 2022/6/7
   */
  public void desensitize() {
    setPhone(DesensitizeUtils.desensitizePhone(getPhone()));
    setEmail(DesensitizeUtils.desensitizeEmail(getEmail()));
    setName(DesensitizeUtils.desensitizeName(getName()));
  }
}
