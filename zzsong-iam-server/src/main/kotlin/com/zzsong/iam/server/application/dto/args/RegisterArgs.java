package com.zzsong.iam.server.application.dto.args;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2022/2/23
 */
@Getter
@Setter
public class RegisterArgs {

  /**
   * 姓名
   */
  @Nullable
  private String name;

  /**
   * 手机号码
   *
   * @required
   */
  @Nullable
  private String phone;

  /** 账号 */
  @Nullable
  private String account;

  /** 邮箱 */
  @Nullable
  private String email;

  /**
   * 密码
   *
   * @required
   */
  @Nullable
  private String password;
}
